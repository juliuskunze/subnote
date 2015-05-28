package com.mindforge.app

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.android.vending.billing.IInAppBillingService
import org.jetbrains.anko.toast
import org.json.JSONObject
import kotlin.properties.Delegates

object PurchasableProductIds {
    val donation = "v1donation"
}

class DonationService(val activity: Activity, val donationIntentCode: Int) {
    var billingService : BillingService by Delegates.notNull()

    fun invoke() {
        val serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName) {
                activity.toast("Billing service disconnected.")
            }

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                billingService = BillingService(activity, IInAppBillingService.Stub.asInterface(service)!!)

                try {
                    val product = billingService.product(PurchasableProductIds.donation)
                    val purchase = billingService.purchaseInfoIfWasPurchased(product)

                    if (purchase != null) {
                        billingService.consume(purchase)
                    }

                    billingService.startPurchaseIntent(product, intentCode = donationIntentCode)
                } catch(ex: BillingException) {
                    activity.toast(ex.getMessage()!!)
                }
            }
        }

        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.setPackage("com.android.vending")

        activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun purchaseInfo(data: Intent) = billingService.purchaseInfo(data)
}

/**
 * For billing API see http://developer.android.com/google/play/billing/billing_reference.html
 */
class BillingService(val activity: Activity, val service : IInAppBillingService) {
    private val inAppBillingType = "inapp"
    private val packageName = "net.pureal.subnote" // TODO: change to activity.getPackageName() after namespace is net.pureal.subnote
    private val apiVersion = 3

    class PurchaseInfo(val productId: String, val purchaseToken: String)

    fun purchases(): List<PurchaseInfo> {
        val bundle = service.getPurchases(apiVersion, packageName, inAppBillingType, null)
        bundle.validateResponseCode("Failed to retrieve purchase history")

        return bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST").map { purchaseInfo(it) }.toArrayList()
    }

    fun purchaseInfoIfWasPurchased(product: ProductInfo) = purchases().singleOrNull { it.productId == product.id }

    private fun purchaseInfo(jsonString: String): PurchaseInfo {
        val o = JSONObject(jsonString)
        return PurchaseInfo(
                productId = o.getString("productId"),
                purchaseToken = o.getString("purchaseToken")
        )
    }

    fun startPurchaseIntent(product: ProductInfo, intentCode: Int) {
        val buyIntentBundle = service.getBuyIntent(apiVersion, packageName, product.id, inAppBillingType, "")
        buyIntentBundle.validateResponseCode("Purchase failed")

        val pendingIntent = buyIntentBundle.getParcelable<PendingIntent>("BUY_INTENT")!!

        activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                intentCode, Intent(), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0))
    }

    fun consume(purchase: PurchaseInfo) {
        val responseCode = service.consumePurchase(apiVersion, packageName, purchase.purchaseToken)

        if (responseCode != 0) throw BillingException(responseCode, "Failed to consume purchase: $responseCode).")
    }

    class ProductInfo(val id: String, val title: String, val price: String)

    fun product(productId: String): ProductInfo {
        val querySkus = Bundle()
        querySkus.putStringArrayList("ITEM_ID_LIST", arrayListOf(productId))

        val bundle = service.getSkuDetails(apiVersion, packageName, inAppBillingType, querySkus)
        bundle.validateResponseCode(extraMessageOnFail = "Failed to retrieve billing details")

        return bundle.getStringArrayList("DETAILS_LIST").map {
            val o = JSONObject(it)
            ProductInfo(
                    id = o.getString("productId"),
                    title = o.getString("title"),
                    price = o.getString("price")
            )
        }.toArrayList().single()
    }

    fun Bundle.validateResponseCode(extraMessageOnFail: String) {
        val responseCode = getInt("RESPONSE_CODE")
        if (responseCode != 0) throw BillingException(responseCode, extraMessage = extraMessageOnFail)
    }

    fun purchaseInfo(data: Intent) : PurchaseInfo {
        val responseCode = data.getIntExtra("RESPONSE_CODE", 0)
        if (responseCode != 0) throw BillingException(responseCode, "Purchase failed")

        return purchaseInfo(data.getStringExtra("INAPP_PURCHASE_DATA"))
    }
}

class BillingException(val responseCode: Int, extraMessage: String) : Exception("$extraMessage: (Error code $responseCode)")
