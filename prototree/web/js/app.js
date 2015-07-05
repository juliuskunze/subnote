(function (Kotlin) {
  'use strict';
  var _ = Kotlin.defineRootPackage(null, /** @lends _ */ {
    net: Kotlin.definePackage(null, /** @lends _.net */ {
      pureal: Kotlin.definePackage(null, /** @lends _.net.pureal */ {
        subnote: Kotlin.definePackage(null, /** @lends _.net.pureal.subnote */ {
          prototree: Kotlin.definePackage(null, /** @lends _.net.pureal.subnote.prototree */ {
            f: function (screen, location, direction, radius, ball) {
              return function () {
                var width = Kotlin.numberToDouble(screen.shape.size.x);
                var height = Kotlin.numberToDouble(screen.shape.size.y);
                if (Kotlin.numberToDouble(location.v.plus_rkhl8y$(direction.v).x) < radius)
                  direction.v = _.com.mindforge.graphics.vector(Math.abs(Kotlin.numberToDouble(direction.v.x)), direction.v.y);
                if (Kotlin.numberToDouble(location.v.plus_rkhl8y$(direction.v).x) > width - radius)
                  direction.v = _.com.mindforge.graphics.vector(-Math.abs(Kotlin.numberToDouble(direction.v.x)), direction.v.y);
                if (Kotlin.numberToDouble(location.v.plus_rkhl8y$(direction.v).y) < radius)
                  direction.v = _.com.mindforge.graphics.vector(direction.v.x, Math.abs(Kotlin.numberToDouble(direction.v.y)));
                if (Kotlin.numberToDouble(location.v.plus_rkhl8y$(direction.v).y) > height - radius)
                  direction.v = _.com.mindforge.graphics.vector(direction.v.x, -Math.abs(Kotlin.numberToDouble(direction.v.y)));
                location.v = location.v.plus_rkhl8y$(direction.v);
                ball.transform = _.com.mindforge.graphics.Transforms2.translation(location.v);
                return true;
              };
            },
            main$f: function () {
              var tmp$0;
              var screen = new _.net.pureal.graphics.js.CanvasScreen((tmp$0 = document.getElementById('canvas')) != null ? tmp$0 : Kotlin.throwNPE());
              var radius = 50;
              var ball = new _.com.mindforge.graphics.MutableTransformedElement(_.com.mindforge.graphics.coloredElement(_.com.mindforge.graphics.math.circle(radius), _.com.mindforge.graphics.Fills.solid(_.com.mindforge.graphics.Colors.red)));
              var elements = _.com.mindforge.graphics.observableArrayListOf([ball]);
              screen.content = _.com.mindforge.graphics.composed(elements);
              var direction = {v: _.com.mindforge.graphics.vector(10, 10)};
              var location = {v: _.com.mindforge.graphics.vector(radius, radius)};
              window.setInterval(_.net.pureal.subnote.prototree.f(screen, location, direction, radius, ball), 20);
            },
            main: function (args) {
              $(_.net.pureal.subnote.prototree.main$f);
            }
          })
        }),
        graphics: Kotlin.definePackage(null, /** @lends _.net.pureal.graphics */ {
          js: Kotlin.definePackage(function () {
            this.hexDigits = Kotlin.modules['stdlib'].kotlin.plus_84aay$(new Kotlin.CharRange('0', '9'), new Kotlin.CharRange('A', 'F'));
          }, /** @lends _.net.pureal.graphics.js */ {
            toHexNibble: function ($receiver) {
              return _.net.pureal.graphics.js.hexDigits.get_za3lpa$($receiver % 16);
            },
            toHexByte: function ($receiver) {
              return _.net.pureal.graphics.js.toHexNibble($receiver / 16 | 0).toString() + _.net.pureal.graphics.js.toHexNibble($receiver);
            },
            get_htmlCode: {value: function ($receiver) {
              var $receiver_0 = Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([$receiver.r, $receiver.g, $receiver.b]);
              var destination = new Kotlin.ArrayList(Kotlin.modules['stdlib'].kotlin.collectionSizeOrDefault_pjxt3m$($receiver_0, 10));
              var tmp$0;
              tmp$0 = $receiver_0.iterator();
              while (tmp$0.hasNext()) {
                var item = tmp$0.next();
                destination.add_za3rmp$(_.net.pureal.graphics.js.toHexByte(Kotlin.numberToDouble(item) * 255 | 0));
              }
              return Kotlin.modules['stdlib'].kotlin.joinToString_aw7tsx$(destination, '', '#');
            }},
            CanvasScreen: Kotlin.createClass(function () {
              return [_.com.mindforge.graphics.Screen];
            }, function (canvas) {
              var tmp$0;
              this.canvas = canvas;
              this.context = (tmp$0 = this.canvas.getContext('2d')) != null ? tmp$0 : Kotlin.throwNPE();
              window.setInterval(_.net.pureal.graphics.js.CanvasScreen.CanvasScreen$f(this), 20);
              window.onresize = _.net.pureal.graphics.js.CanvasScreen.CanvasScreen$f_0(this);
              this.$content_tfpozv$ = _.com.mindforge.graphics.composed(_.com.mindforge.graphics.observableIterable(Kotlin.modules['stdlib'].kotlin.emptyList()));
            }, /** @lends _.net.pureal.graphics.js.CanvasScreen.prototype */ {
              content: {
                get: function () {
                  return this.$content_tfpozv$;
                },
                set: function (content) {
                  this.$content_tfpozv$ = content;
                }
              },
              shape: {
                get: function () {
                  return _.com.mindforge.graphics.math.rectangle(_.com.mindforge.graphics.vector(this.canvas.width, this.canvas.height));
                }
              }
            }, /** @lends _.net.pureal.graphics.js.CanvasScreen */ {
              CanvasScreen$f: function (this$CanvasScreen) {
                return function () {
                  this$CanvasScreen.context.resetTransform();
                  this$CanvasScreen.context.fillStyle = _.net.pureal.graphics.js.get_htmlCode(_.com.mindforge.graphics.Colors.white);
                  this$CanvasScreen.context.fillRect(0.0, 0.0, this$CanvasScreen.canvas.width, this$CanvasScreen.canvas.height);
                  _.net.pureal.graphics.js.draw(this$CanvasScreen.context, this$CanvasScreen.content);
                };
              },
              CanvasScreen$f_0: function (this$CanvasScreen) {
                return function (it) {
                  var tmp$0;
                  var parent = $((tmp$0 = this$CanvasScreen.canvas.parentElement) != null ? tmp$0 : Kotlin.throwNPE());
                  this$CanvasScreen.canvas.width = Kotlin.numberToInt(parent.width());
                  this$CanvasScreen.canvas.height = Math.min(Kotlin.numberToInt(parent.height()), window.innerHeight | 0);
                  return true;
                };
              }
            }),
            draw$f_0: function (shape) {
              return function () {
                this.arc(0.0, 0.0, Kotlin.numberToDouble(shape.radius), 0.0, 2 * Math.PI, false);
              };
            },
            draw: function ($receiver, element) {
              if (Kotlin.isType(element, _.com.mindforge.graphics.Composed)) {
                var originalTransform = $receiver.currentTransform;
                var tmp$0;
                tmp$0 = element.elements.iterator();
                while (tmp$0.hasNext()) {
                  var element_0 = tmp$0.next();
                  _.net.pureal.graphics.js.transform($receiver, element_0.transform);
                  _.net.pureal.graphics.js.draw($receiver, element_0.element);
                  $receiver.currentTransform = originalTransform;
                }
              }
               else if (Kotlin.isType(element, _.com.mindforge.graphics.ColoredElement)) {
                var fill = element.fill;
                var shape = element.shape;
                if (Kotlin.isType(fill, _.com.mindforge.graphics.SolidFill))
                  $receiver.fillStyle = _.net.pureal.graphics.js.get_htmlCode(fill.color);
                else
                  throw new Kotlin.UnsupportedOperationException('Cannot draw fill ' + fill + '.');
                if (Kotlin.isType(shape, _.com.mindforge.graphics.math.Circle))
                  _.net.pureal.graphics.js.fillPath($receiver, _.net.pureal.graphics.js.draw$f_0(shape));
                else
                  throw new Kotlin.UnsupportedOperationException('Cannot draw shape ' + shape + '.');
              }
               else
                throw new Kotlin.UnsupportedOperationException('Cannot draw element ' + element + '.');
            },
            fillPath: function ($receiver, constructPath) {
              $receiver.beginPath();
              constructPath.call($receiver);
              $receiver.closePath();
              $receiver.fill();
            },
            transform: function ($receiver, transform) {
              $receiver.transform(Kotlin.numberToDouble(transform.matrix.a), Kotlin.numberToDouble(transform.matrix.d), Kotlin.numberToDouble(transform.matrix.b), Kotlin.numberToDouble(transform.matrix.e), Kotlin.numberToDouble(transform.matrix.c), Kotlin.numberToDouble(transform.matrix.f));
            }
          })
        })
      })
    }),
    com: Kotlin.definePackage(null, /** @lends _.com */ {
      mindforge: Kotlin.definePackage(null, /** @lends _.com.mindforge */ {
        graphics: Kotlin.definePackage(function () {
          this.Fills = Kotlin.createObject(null, function () {
            this.invisible = _.com.mindforge.graphics.invisible$f();
          }, {
            solid: function (color) {
              return _.com.mindforge.graphics.solid$f(color);
            }
          });
          this.Colors = Kotlin.createObject(null, function () {
            this.black = _.com.mindforge.graphics.color();
            this.red = _.com.mindforge.graphics.color(1);
            this.green = _.com.mindforge.graphics.color(void 0, 1);
            this.blue = _.com.mindforge.graphics.color(void 0, void 0, 1);
            this.gray = this.gray_1(0.5);
            this.white = _.com.mindforge.graphics.color(1, 1, 1);
            this.transparent = _.com.mindforge.graphics.color(void 0, void 0, void 0, 0);
          }, {
            gray_1: function (brightness) {
              return _.com.mindforge.graphics.color(brightness, brightness, brightness);
            }
          });
          this.Transforms2 = Kotlin.createObject(null, null, {
            identity: {
              get: function () {
                return _.com.mindforge.graphics.transform(_.com.mindforge.graphics.identityMatrix3);
              }
            },
            translation: function (vector) {
              return _.com.mindforge.graphics.transform(_.com.mindforge.graphics.matrix_1(1, 0, vector.get_za3lpa$(0), 0, 1, vector.get_za3lpa$(1), 0, 0, 1));
            },
            rotation: function (angle) {
              var a = Kotlin.numberToDouble(angle);
              return this.linear(_.com.mindforge.graphics.matrix(Math.cos(a), -Math.sin(a), Math.sin(a), Math.cos(a)));
            },
            scale_1: function (factorX, factorY) {
              return this.linear(_.com.mindforge.graphics.matrix(factorX, 0, 0, factorY));
            },
            scale: function (factor) {
              return this.scale_1(factor, factor);
            },
            reflection: function (axisAngle) {
              var d = 2 * Kotlin.numberToDouble(axisAngle);
              return this.linear(_.com.mindforge.graphics.matrix(Math.cos(d), Math.sin(d), Math.sin(d), -Math.cos(d)));
            },
            linear: function (matrix) {
              return _.com.mindforge.graphics.transform(_.com.mindforge.graphics.matrix_1(matrix.get_vux9f0$(0, 0), matrix.get_vux9f0$(1, 0), 0, matrix.get_vux9f0$(0, 1), matrix.get_vux9f0$(1, 1), 0, 0, 0, 1));
            }
          });
          this.identityMatrix2 = _.com.mindforge.graphics.matrix(1, 0, 0, 1);
          this.identityMatrix3 = _.com.mindforge.graphics.matrix_1(1, 0, 0, 0, 1, 0, 0, 0, 1);
          this.zeroVector2 = _.com.mindforge.graphics.vector(0, 0);
          this.zeroVector3 = _.com.mindforge.graphics.vector_1(0, 0, 0);
        }, /** @lends _.com.mindforge.graphics */ {
          InvalidateFun: function () {
            throw new Kotlin.UnsupportedOperationException();
          },
          insistRemove: function ($receiver, element) {
            if (!$receiver.remove_za3rmp$(element))
              throw new Kotlin.IllegalStateException("Element '" + element + "' not found.");
          },
          observed: function ($receiver, initial, changed) {
            return Kotlin.createObject(function () {
              return [Kotlin.modules['stdlib'].kotlin.properties.ReadWriteProperty];
            }, function () {
              this.value_ot0ned$ = initial;
            }, {
              get_1tsekc$: function (thisRef, desc) {
                return this.value_ot0ned$;
              },
              set_1z3uih$: function (thisRef, desc, value) {
                this.value_ot0ned$ = value;
                _.com.mindforge.graphics.invoke(changed);
              }
            });
          },
          observed_1: function ($receiver, initial, onChanged) {
            return Kotlin.createObject(function () {
              return [Kotlin.modules['stdlib'].kotlin.properties.ReadWriteProperty];
            }, function () {
              this.value_ot0ned$ = initial;
            }, {
              get_1tsekc$: function (thisRef, desc) {
                return this.value_ot0ned$;
              },
              set_1z3uih$: function (thisRef, desc, value) {
                var old = this.value_ot0ned$;
                this.value_ot0ned$ = value;
                onChanged(old, value);
              }
            });
          },
          Fill: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.Fill.prototype */ {
            transform_npjzs3$: function (transform) {
              return _.com.mindforge.graphics.Fill.transform_npjzs3$f(this, transform);
            }
          }, /** @lends _.com.mindforge.graphics.Fill */ {
            transform_npjzs3$f: function (this$Fill, transform) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.TransformedFill];
              }, function () {
                this.$original_ej6c6e$ = this$Fill;
                this.$transform_pzxkqx$ = transform;
              }, {
                original: {
                  get: function () {
                    return this.$original_ej6c6e$;
                  }
                },
                transform: {
                  get: function () {
                    return this.$transform_pzxkqx$;
                  }
                }
              });
            }
          }),
          invisible$f: function () {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.InvisibleFill];
            }, null);
          },
          solid$f: function (color) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.SolidFill];
            }, function () {
              this.$color_fr21to$ = color;
            }, {
              color: {
                get: function () {
                  return this.$color_fr21to$;
                }
              }
            });
          },
          TransformedFill: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.Fill];
          }, /** @lends _.com.mindforge.graphics.TransformedFill.prototype */ {
            colorAt_rkhl8y$: function (location) {
              return this.original.colorAt_rkhl8y$(this.transform.inverse().invoke_rkhl8y$(location));
            }
          }),
          InvisibleFill: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.SolidFill];
          }, /** @lends _.com.mindforge.graphics.InvisibleFill.prototype */ {
            color: {
              get: function () {
                return _.com.mindforge.graphics.Colors.transparent;
              }
            }
          }),
          SolidFill: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.Fill];
          }, /** @lends _.com.mindforge.graphics.SolidFill.prototype */ {
            colorAt_rkhl8y$: function (location) {
              return this.color;
            }
          }),
          TextElement: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.ColoredElement];
          }, /** @lends _.com.mindforge.graphics.TextElement.prototype */ {
            shape: {
              get: function () {
                return this.font.shape_nhrjpk$(this.content, this.lineHeight);
              }
            }
          }),
          TextElementImpl: Kotlin.createClass(function () {
            return [_.com.mindforge.graphics.TextElement];
          }, function (content, font, lineHeight, fill) {
            this.onChanged_6fsbev$ = _.com.mindforge.graphics.trigger();
            this.$changed_i017c$ = _.com.mindforge.graphics.trigger();
            this.content$delegate = _.com.mindforge.graphics.observed(Kotlin.modules['stdlib'].kotlin.properties.Delegates, content, this.onChanged_6fsbev$);
            this.font$delegate = _.com.mindforge.graphics.observed(Kotlin.modules['stdlib'].kotlin.properties.Delegates, font, this.onChanged_6fsbev$);
            this.lineHeight$delegate = _.com.mindforge.graphics.observed(Kotlin.modules['stdlib'].kotlin.properties.Delegates, lineHeight, this.onChanged_6fsbev$);
            this.fill$delegate = _.com.mindforge.graphics.observed(Kotlin.modules['stdlib'].kotlin.properties.Delegates, fill, this.onChanged_6fsbev$);
            this.onChanged_6fsbev$.addObserver_otxks8$(_.com.mindforge.graphics.TextElementImpl.TextElementImpl$f(this));
            this.shapeValue_qzlyss$ = Kotlin.callGetter(this, _.com.mindforge.graphics.TextElement, 'shape');
          }, /** @lends _.com.mindforge.graphics.TextElementImpl.prototype */ {
            changed: {
              get: function () {
                return this.$changed_i017c$;
              }
            },
            content: {
              get: function () {
                return this.content$delegate.get_1tsekc$(this, new Kotlin.PropertyMetadata('content'));
              },
              set: function (content) {
                this.content$delegate.set_1z3uih$(this, new Kotlin.PropertyMetadata('content'), content);
              }
            },
            font: {
              get: function () {
                return this.font$delegate.get_1tsekc$(this, new Kotlin.PropertyMetadata('font'));
              },
              set: function (font) {
                this.font$delegate.set_1z3uih$(this, new Kotlin.PropertyMetadata('font'), font);
              }
            },
            lineHeight: {
              get: function () {
                return this.lineHeight$delegate.get_1tsekc$(this, new Kotlin.PropertyMetadata('lineHeight'));
              },
              set: function (lineHeight) {
                this.lineHeight$delegate.set_1z3uih$(this, new Kotlin.PropertyMetadata('lineHeight'), lineHeight);
              }
            },
            fill: {
              get: function () {
                return this.fill$delegate.get_1tsekc$(this, new Kotlin.PropertyMetadata('fill'));
              },
              set: function (fill) {
                this.fill$delegate.set_1z3uih$(this, new Kotlin.PropertyMetadata('fill'), fill);
              }
            },
            shape: {
              get: function () {
                return this.shapeValue_qzlyss$;
              }
            }
          }, /** @lends _.com.mindforge.graphics.TextElementImpl */ {
            TextElementImpl$f: function (this$TextElementImpl) {
              return function (it) {
                this$TextElementImpl.shapeValue_qzlyss$ = Kotlin.callGetter(this, _.com.mindforge.graphics.TextElement, 'shape');
                _.com.mindforge.graphics.invoke(this$TextElementImpl.changed);
              };
            }
          }),
          LineShape: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.math.Shape];
          }),
          TextShape: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.math.Shape];
          }, /** @lends _.com.mindforge.graphics.TextShape.prototype */ {
            size: function () {
              var tmp$0;
              var $receiver = this.lines;
              var destination = new Kotlin.ArrayList(Kotlin.modules['stdlib'].kotlin.collectionSizeOrDefault_pjxt3m$($receiver, 10));
              var tmp$1;
              tmp$1 = $receiver.iterator();
              while (tmp$1.hasNext()) {
                var item = tmp$1.next();
                destination.add_za3rmp$(Kotlin.numberToDouble(item.width));
              }
              return _.com.mindforge.graphics.vector((tmp$0 = Kotlin.modules['stdlib'].kotlin.max_77rvyy$(destination)) != null ? tmp$0 : 0.0, Kotlin.numberToDouble(this.lineHeight) * _.com.mindforge.graphics.lineCount(this.text) + Kotlin.numberToDouble(this.leading) * (_.com.mindforge.graphics.lineCount(this.text) - 1));
            },
            box: function () {
              return _.com.mindforge.graphics.math.rectangle_1(this.size(), 4);
            }
          }, /** @lends _.com.mindforge.graphics.TextShape */ {
          }),
          Font: Kotlin.createTrait(null),
          lineCount: function ($receiver) {
            var tmp$0;
            var count = 0;
            tmp$0 = Kotlin.modules['stdlib'].kotlin.iterator_gw00vq$($receiver);
            while (tmp$0.hasNext()) {
              var element = tmp$0.next();
              if (element === '\n') {
                count++;
              }
            }
            return count + 1;
          },
          Screen: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.Screen.prototype */ {
            elementsAt_rkhl8y$: function (location) {
              return this.content.elementsAt_rkhl8y$(location);
            }
          }),
          ColoredElement: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.Element];
          }, /** @lends _.com.mindforge.graphics.ColoredElement.prototype */ {
            colorAt_rkhl8y$: function (location) {
              return this.shape.contains_rkhl8y$(location) ? this.fill.colorAt_rkhl8y$(location) : null;
            }
          }),
          coloredElement_1: function (content, shape, fill, changed) {
            if (changed === void 0)
              changed = _.com.mindforge.graphics.observable([]);
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.ColoredElement];
            }, function () {
              this.$content_yddnjf$ = content;
              this.$shape_ryi771$ = shape;
              this.$fill_f4pqdt$ = fill;
              this.$changed_uut6ee$ = changed;
            }, {
              content: {
                get: function () {
                  return this.$content_yddnjf$;
                }
              },
              shape: {
                get: function () {
                  return this.$shape_ryi771$;
                }
              },
              fill: {
                get: function () {
                  return this.$fill_f4pqdt$;
                }
              },
              changed: {
                get: function () {
                  return this.$changed_uut6ee$;
                }
              }
            });
          },
          coloredElement: function (shape, fill, changed) {
            if (changed === void 0)
              changed = _.com.mindforge.graphics.observable([]);
            return _.com.mindforge.graphics.coloredElement_1(Kotlin.modules['builtins'].kotlin.Unit, shape, fill, changed);
          },
          ObservableList: Kotlin.createTrait(function () {
            return [Kotlin.modules['builtins'].kotlin.MutableList, _.com.mindforge.graphics.ObservableIterable];
          }),
          observableArrayListOf$f: function (it) {
            return it;
          },
          observableArrayListOf: function (elements) {
            return new _.com.mindforge.graphics.ObservableArrayList(Kotlin.modules['stdlib'].kotlin.map_rie7ol$(elements, _.com.mindforge.graphics.observableArrayListOf$f));
          },
          ObservableArrayList: Kotlin.createClass(function () {
            return [_.com.mindforge.graphics.ObservableList, Kotlin.ArrayList];
          }, function $fun(elements) {
            if (elements === void 0)
              elements = Kotlin.modules['stdlib'].kotlin.listOf();
            $fun.baseInitializer.call(this);
            Kotlin.ArrayList.prototype.addAll_4fm7v2$.call(this, Kotlin.modules['stdlib'].kotlin.map_m3yiqg$(elements, _.com.mindforge.graphics.ObservableArrayList.ObservableArrayList$f));
            this.$added_8cwyzb$ = _.com.mindforge.graphics.trigger();
            this.$removed_ur5o21$ = _.com.mindforge.graphics.trigger();
            this.$addedAt_6afjlw$ = _.com.mindforge.graphics.trigger();
            this.$removedAt_63ge1o$ = _.com.mindforge.graphics.trigger();
          }, /** @lends _.com.mindforge.graphics.ObservableArrayList.prototype */ {
            added: {
              get: function () {
                return this.$added_8cwyzb$;
              }
            },
            removed: {
              get: function () {
                return this.$removed_ur5o21$;
              }
            },
            addedAt: {
              get: function () {
                return this.$addedAt_6afjlw$;
              }
            },
            removedAt: {
              get: function () {
                return this.$removedAt_63ge1o$;
              }
            },
            add_za3rmp$: function (e) {
              Kotlin.ArrayList.prototype.add_za3rmp$.call(this, e);
              this.added.invoke_za3rmp$(e);
              return true;
            },
            addAll_4fm7v2$: function (c) {
              var operation = _.com.mindforge.graphics.ObservableArrayList.addAll_4fm7v2$f(this);
              var tmp$0;
              tmp$0 = c.iterator();
              while (tmp$0.hasNext()) {
                var element = tmp$0.next();
                operation(element);
              }
              return Kotlin.modules['stdlib'].kotlin.any_ir3nkc$(c);
            },
            remove_za3rmp$: function (o) {
              if (!Kotlin.ArrayList.prototype.remove_za3rmp$.call(this, o)) {
                return false;
              }
              this.removed.invoke_za3rmp$(o);
              return true;
            },
            removeAll_4fm7v2$: function (c) {
              var tmp$0;
              var result = false;
              tmp$0 = Kotlin.modules['stdlib'].kotlin.reversed_lufotp$(Kotlin.modules['stdlib'].kotlin.get_indices_4m3c68$(c)).iterator();
              while (tmp$0.hasNext()) {
                var index = tmp$0.next();
                result = this.remove_za3rmp$(Kotlin.modules['stdlib'].kotlin.elementAt_pjxt3m$(c, index)) || result;
              }
              return result;
            },
            clear: function () {
              this.removeAll_4fm7v2$(this);
            },
            clearAndAddAll: function (newElements) {
              this.clear();
              Kotlin.modules['stdlib'].kotlin.addAll_p6ac9a$(this, newElements);
            },
            addAll_9cca64$: function (index, c) {
              var tmp$0;
              tmp$0 = c.iterator();
              while (tmp$0.hasNext()) {
                var e = tmp$0.next();
                this.add_vux3hl$(index, e);
              }
              return Kotlin.modules['stdlib'].kotlin.any_ir3nkc$(c);
            },
            add_vux3hl$: function (index, element) {
              Kotlin.ArrayList.prototype.add_vux3hl$.call(this, index, element);
              this.addedAt.invoke_za3rmp$(new Kotlin.modules['stdlib'].kotlin.IndexedValue(index, element));
            },
            remove_za3lpa$: function (index) {
              var o = Kotlin.ArrayList.prototype.remove_za3lpa$.call(this, index);
              this.removedAt.invoke_za3rmp$(new Kotlin.modules['stdlib'].kotlin.IndexedValue(index, o));
              return o;
            },
            retainAll_4fm7v2$: function (c) {
              throw new Kotlin.UnsupportedOperationException();
            },
            set_vux3hl$: function (index, element) {
              throw new Kotlin.UnsupportedOperationException();
            }
          }, /** @lends _.com.mindforge.graphics.ObservableArrayList */ {
            ObservableArrayList$f: function (it) {
              return it;
            },
            addAll_4fm7v2$f: function (this$ObservableArrayList) {
              return function (it) {
                this$ObservableArrayList.add_za3rmp$(it);
              };
            }
          }),
          Trigger: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.Observable];
          }, /** @lends _.com.mindforge.graphics.Trigger.prototype */ {
            invoke_za3rmp$: function (info) {
              this.notifyObservers_za3rmp$(info);
            }
          }),
          trigger: function () {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Trigger];
            }, function () {
              this.observers = Kotlin.modules['stdlib'].kotlin.hashSetOf_9mqe4v$([]);
            });
          },
          invoke: function ($receiver) {
            $receiver.invoke_za3rmp$(Kotlin.modules['builtins'].kotlin.Unit);
          },
          Color: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.Color.prototype */ {
            equals_za3rmp$: function (other) {
              return Kotlin.isType(other, _.com.mindforge.graphics.Color) ? Kotlin.numberToDouble(this.r) === Kotlin.numberToDouble(other.r) && Kotlin.numberToDouble(this.g) === Kotlin.numberToDouble(other.g) && Kotlin.numberToDouble(this.b) === Kotlin.numberToDouble(other.b) && Kotlin.numberToDouble(this.a) === Kotlin.numberToDouble(other.a) : false;
            },
            times_3p81yu$: function (factor) {
              var f = Kotlin.numberToDouble(factor);
              return _.com.mindforge.graphics.color(Kotlin.numberToDouble(this.r) * f, Kotlin.numberToDouble(this.g) * f, Kotlin.numberToDouble(this.b) * f, Kotlin.numberToDouble(this.a) * f);
            },
            plus_1vxmee$: function (other) {
              return _.com.mindforge.graphics.color(Kotlin.numberToDouble(this.r) + Kotlin.numberToDouble(other.r), Kotlin.numberToDouble(this.g) + Kotlin.numberToDouble(other.g), Kotlin.numberToDouble(this.b) + Kotlin.numberToDouble(other.b), Kotlin.numberToDouble(this.a) + Kotlin.numberToDouble(other.a));
            },
            toString: function () {
              return 'color(r=' + Kotlin.numberToDouble(this.r) + ', g=' + Kotlin.numberToDouble(this.g) + ', b=' + Kotlin.numberToDouble(this.b) + ', a=' + Kotlin.numberToDouble(this.a) + ')';
            }
          }),
          color: function (r, g, b, a) {
            if (r === void 0)
              r = 0;
            if (g === void 0)
              g = 0;
            if (b === void 0)
              b = 0;
            if (a === void 0)
              a = 1;
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Color];
            }, function () {
              this.$r_35ltt7$ = r;
              this.$g_35ltti$ = g;
              this.$b_35lttn$ = b;
              this.$a_35ltto$ = a;
            }, {
              r: {
                get: function () {
                  return this.$r_35ltt7$;
                }
              },
              g: {
                get: function () {
                  return this.$g_35ltti$;
                }
              },
              b: {
                get: function () {
                  return this.$b_35lttn$;
                }
              },
              a: {
                get: function () {
                  return this.$a_35ltto$;
                }
              }
            });
          },
          Element: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.interaction.Interactive];
          }, /** @lends _.com.mindforge.graphics.Element.prototype */ {
            changed: {
              get: function () {
                return _.com.mindforge.graphics.observable([]);
              }
            }
          }),
          TransformedElement: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.TransformedElement.prototype */ {
            transformed_npjzs3$: function (transform) {
              return _.com.mindforge.graphics.transformedElement(this.element, this.transform.before_npjzs3$(transform));
            }
          }),
          transformedElement: function (element, transform) {
            if (transform === void 0)
              transform = _.com.mindforge.graphics.Transforms2.identity;
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.TransformedElement];
            }, function () {
              this.$element_rtjrav$ = element;
              this.$transform_l6dmc9$ = transform;
              this.$transformChanged_kb1i4t$ = _.com.mindforge.graphics.observable([]);
            }, {
              element: {
                get: function () {
                  return this.$element_rtjrav$;
                }
              },
              transform: {
                get: function () {
                  return this.$transform_l6dmc9$;
                }
              },
              transformChanged: {
                get: function () {
                  return this.$transformChanged_kb1i4t$;
                }
              }
            });
          },
          MutableTransformedElement: Kotlin.createClass(function () {
            return [_.com.mindforge.graphics.TransformedElement];
          }, function (element, transform) {
            if (transform === void 0)
              transform = _.com.mindforge.graphics.Transforms2.identity;
            this.$element_h96e48$ = element;
            this.transformChangedTrigger_hs4nwc$ = _.com.mindforge.graphics.trigger();
            this.transform$delegate = _.com.mindforge.graphics.observed(Kotlin.modules['stdlib'].kotlin.properties.Delegates, transform, this.transformChangedTrigger_hs4nwc$);
            this.$transformChanged_f73dw$ = this.transformChangedTrigger_hs4nwc$;
          }, /** @lends _.com.mindforge.graphics.MutableTransformedElement.prototype */ {
            element: {
              get: function () {
                return this.$element_h96e48$;
              }
            },
            transform: {
              get: function () {
                return this.transform$delegate.get_1tsekc$(this, new Kotlin.PropertyMetadata('transform'));
              },
              set: function (transform) {
                this.transform$delegate.set_1z3uih$(this, new Kotlin.PropertyMetadata('transform'), transform);
              }
            },
            transformChanged: {
              get: function () {
                return this.$transformChanged_f73dw$;
              }
            }
          }),
          Composed: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.Element];
          }, /** @lends _.com.mindforge.graphics.Composed.prototype */ {
            elementsAt_rkhl8y$: function (location) {
              var $receiver = Kotlin.modules['stdlib'].kotlin.toArrayList_ir3nkc$(this.elements);
              var destination = new Kotlin.ArrayList();
              var tmp$0;
              tmp$0 = $receiver.iterator();
              while (tmp$0.hasNext()) {
                var element = tmp$0.next();
                var tmp$3, tmp$1;
                var locationRelativeToElement = element.transform.inverse().invoke_rkhl8y$(location);
                var element_0 = element.element;
                if (Kotlin.isType(element_0, _.com.mindforge.graphics.Composed)) {
                  var $receiver_0 = element_0.elementsAt_rkhl8y$(locationRelativeToElement);
                  var destination_0 = new Kotlin.ArrayList(Kotlin.modules['stdlib'].kotlin.collectionSizeOrDefault_pjxt3m$($receiver_0, 10));
                  var tmp$2;
                  tmp$2 = $receiver_0.iterator();
                  while (tmp$2.hasNext()) {
                    var item = tmp$2.next();
                    destination_0.add_za3rmp$(_.com.mindforge.graphics.transformedElement(item.element, element.transform.before_npjzs3$(item.transform)));
                  }
                  tmp$3 = destination_0;
                }
                 else
                  tmp$3 = Kotlin.modules['stdlib'].kotlin.listOf();
                var subElements = tmp$3;
                if (Kotlin.isType(element_0, _.com.mindforge.graphics.Composed))
                  tmp$1 = Kotlin.modules['stdlib'].kotlin.any_ir3nkc$(subElements);
                else
                  tmp$1 = element_0.shape.contains_rkhl8y$(locationRelativeToElement);
                var list = Kotlin.modules['stdlib'].kotlin.plus_84aay$(subElements, tmp$1 ? Kotlin.modules['stdlib'].kotlin.listOf_za3rmp$(element) : Kotlin.modules['stdlib'].kotlin.listOf());
                Kotlin.modules['stdlib'].kotlin.addAll_p6ac9a$(destination, list);
              }
              return destination;
            },
            shape: {
              get: function () {
                return _.com.mindforge.graphics.math.shape(_.com.mindforge.graphics.Composed.shape$f(this));
              }
            },
            allElements: function () {
              var tmp$0 = Kotlin.modules['stdlib'].kotlin.setOf_za3rmp$(this);
              var $receiver = this.elements;
              var destination = new Kotlin.ArrayList();
              var tmp$1;
              tmp$1 = $receiver.iterator();
              while (tmp$1.hasNext()) {
                var element = tmp$1.next();
                var list;
                allElements$f$break: {
                  if (Kotlin.isType(element.element, _.com.mindforge.graphics.Composed)) {
                    list = element.element.allElements();
                    break allElements$f$break;
                  }
                   else {
                    list = Kotlin.modules['stdlib'].kotlin.setOf_za3rmp$(element.element);
                    break allElements$f$break;
                  }
                }
                Kotlin.modules['stdlib'].kotlin.addAll_p6ac9a$(destination, list);
              }
              return Kotlin.modules['stdlib'].kotlin.union_84aay$(tmp$0, destination);
            },
            containsRecursively_rqkt1a$: function (element) {
              var $receiver = this.elements;
              var destination = new Kotlin.ArrayList(Kotlin.modules['stdlib'].kotlin.collectionSizeOrDefault_pjxt3m$($receiver, 10));
              var tmp$1;
              tmp$1 = $receiver.iterator();
              while (tmp$1.hasNext()) {
                var item = tmp$1.next();
                destination.add_za3rmp$(item.element);
              }
              var elements = destination;
              var tmp$0 = elements.contains_za3rmp$(element);
              if (!tmp$0) {
                any_azvtw4$break: {
                  var tmp$2;
                  tmp$2 = elements.iterator();
                  while (tmp$2.hasNext()) {
                    var element_0 = tmp$2.next();
                    if (Kotlin.isType(element_0, _.com.mindforge.graphics.Composed) && element_0.containsRecursively_rqkt1a$(element)) {
                      tmp$0 = true;
                      break any_azvtw4$break;
                    }
                  }
                  tmp$0 = false;
                }
                tmp$0 = tmp$0;
              }
              return tmp$0;
            },
            pathTo_rqkt1a$: function (recursiveElement) {
              var $receiver = this.elements;
              var destination = new Kotlin.ArrayList();
              var tmp$0;
              tmp$0 = $receiver.iterator();
              while (tmp$0.hasNext()) {
                var element = tmp$0.next();
                var element_0 = element.element;
                var list = recursiveElement === element_0 ? Kotlin.modules['stdlib'].kotlin.listOf_za3rmp$(element) : Kotlin.isType(element_0, _.com.mindforge.graphics.Composed) && element_0.containsRecursively_rqkt1a$(recursiveElement) ? Kotlin.modules['stdlib'].kotlin.plus_84aay$(Kotlin.modules['stdlib'].kotlin.listOf_za3rmp$(element), element_0.pathTo_rqkt1a$(recursiveElement)) : Kotlin.modules['stdlib'].kotlin.listOf();
                Kotlin.modules['stdlib'].kotlin.addAll_p6ac9a$(destination, list);
              }
              return destination;
            },
            totalTransform_rqkt1a$: function (recursiveElement) {
              var path = this.pathTo_rqkt1a$(recursiveElement);
              var tmp$0;
              var accumulator = _.com.mindforge.graphics.Transforms2.identity;
              tmp$0 = path.iterator();
              while (tmp$0.hasNext()) {
                var element = tmp$0.next();
                accumulator = accumulator.before_npjzs3$(element.transform);
              }
              return accumulator;
            }
          }, /** @lends _.com.mindforge.graphics.Composed */ {
            shape$f: function (this$Composed) {
              return function (it) {
                return Kotlin.modules['stdlib'].kotlin.any_ir3nkc$(this$Composed.elementsAt_rkhl8y$(it));
              };
            }
          }),
          composed_1: function (content, elements, changed) {
            if (changed === void 0)
              changed = _.com.mindforge.graphics.observable([]);
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Composed];
            }, function () {
              this.$content_knhk3x$ = content;
              this.$elements_8ap48t$ = elements;
              this.$changed_o6218y$ = changed;
            }, {
              content: {
                get: function () {
                  return this.$content_knhk3x$;
                }
              },
              elements: {
                get: function () {
                  return this.$elements_8ap48t$;
                }
              },
              changed: {
                get: function () {
                  return this.$changed_o6218y$;
                }
              }
            });
          },
          composed_2: function (content, elements, changed) {
            if (changed === void 0)
              changed = _.com.mindforge.graphics.observable([]);
            return _.com.mindforge.graphics.composed_1(content, _.com.mindforge.graphics.observableIterable(elements), changed);
          },
          composed: function (elements, changed) {
            if (changed === void 0)
              changed = _.com.mindforge.graphics.observable([]);
            return _.com.mindforge.graphics.composed_1(Kotlin.modules['builtins'].kotlin.Unit, elements, changed);
          },
          Observable: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.Observable.prototype */ {
            addObserver_otxks8$: function (action) {
              var observer = _.com.mindforge.graphics.Observable.addObserver_otxks8$f(action, this);
              this.observers.add_za3rmp$(observer);
              if (Kotlin.modules['stdlib'].kotlin.count_4m3c68$(this.observers) > 16) {
                throw new Kotlin.IllegalStateException('Too many observers.');
              }
              return observer;
            },
            notifyObservers_za3rmp$: function (info) {
              var tmp$0;
              tmp$0 = Kotlin.modules['stdlib'].kotlin.toList_ir3nkc$(this.observers).iterator();
              while (tmp$0.hasNext()) {
                var observer = tmp$0.next();
                observer.invoke_za3rmp$(info);
              }
            }
          }, /** @lends _.com.mindforge.graphics.Observable */ {
            addObserver_otxks8$f: function (action, this$Observable) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.ObserverAction];
              }, null, {
                invoke_za3rmp$: function (value) {
                  action.call(this, value);
                },
                stop: function () {
                  if (!this$Observable.observers.remove_za3rmp$(this)) {
                    throw new Kotlin.IllegalStateException('Stop can only be called once.');
                  }
                }
              });
            }
          }),
          ObserverAction: Kotlin.createTrait(function () {
            return [_.com.mindforge.graphics.Observer];
          }),
          Observer: Kotlin.createTrait(null),
          f: function (transform, this$) {
            return function (it) {
              this$.invoke_za3rmp$(transform(it));
            };
          },
          innerObservers$f: function (transform, this$) {
            return function (it) {
              return it.addObserver_otxks8$(_.com.mindforge.graphics.f(transform, this$));
            };
          },
          addObserver_otxks8$f: function (observables, transform, action) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.ObserverAction];
            }, function () {
              var transform = _.com.mindforge.graphics.innerObservers$f(transform, this);
              var destination = new Kotlin.ArrayList(Kotlin.modules['stdlib'].kotlin.collectionSizeOrDefault_pjxt3m$(observables, 10));
              var tmp$0;
              tmp$0 = observables.iterator();
              while (tmp$0.hasNext()) {
                var item = tmp$0.next();
                destination.add_za3rmp$(transform(item));
              }
              this.innerObservers = destination;
            }, {
              invoke_za3rmp$: function (value) {
                action.call(this, value);
              },
              stop: function () {
                var $receiver = this.innerObservers;
                var tmp$0;
                tmp$0 = $receiver.iterator();
                while (tmp$0.hasNext()) {
                  var element = tmp$0.next();
                  element.stop();
                }
              }
            });
          },
          observable_3: function (observables, transform) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Observable];
            }, function () {
              this.observers = Kotlin.modules['stdlib'].kotlin.hashSetOf_9mqe4v$([]);
            }, {
              addObserver_otxks8$: function (action) {
                return _.com.mindforge.graphics.addObserver_otxks8$f(observables, transform, action);
              }
            });
          },
          observable_2: function (observable, transform) {
            return _.com.mindforge.graphics.observable_3(Kotlin.modules['stdlib'].kotlin.listOf_za3rmp$(observable), transform);
          },
          observable_1$f: function (it) {
            return it;
          },
          observable_1: function (observables) {
            return _.com.mindforge.graphics.observable_3(observables, _.com.mindforge.graphics.observable_1$f);
          },
          observable: function (observables) {
            return _.com.mindforge.graphics.observable_1(Kotlin.createObject(function () {
              return [Kotlin.modules['builtins'].kotlin.Iterable];
            }, null, {
              iterator: function () {
                return Kotlin.arrayIterator(observables);
              }
            }));
          },
          ObservableIterable: Kotlin.createTrait(function () {
            return [Kotlin.modules['builtins'].kotlin.Iterable];
          }, /** @lends _.com.mindforge.graphics.ObservableIterable.prototype */ {
            mapObservable_z22aos$: function (transform) {
              return _.com.mindforge.graphics.ObservableIterable.mapObservable_z22aos$f(this, transform);
            }
          }, /** @lends _.com.mindforge.graphics.ObservableIterable */ {
            addedAt$f: function (transform) {
              return function (it) {
                return new Kotlin.modules['stdlib'].kotlin.IndexedValue(it.index, transform(it.value));
              };
            },
            removedAt$f: function (transform) {
              return function (it) {
                return new Kotlin.modules['stdlib'].kotlin.IndexedValue(it.index, transform(it.value));
              };
            },
            mapObservable_z22aos$f: function (this$ObservableIterable, transform) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.ObservableIterable];
              }, function () {
                this.$added_gntybq$ = _.com.mindforge.graphics.observable_2(this$ObservableIterable.added, transform);
                this.$removed_3r9u6y$ = _.com.mindforge.graphics.observable_2(this$ObservableIterable.removed, transform);
                this.$addedAt_u89049$ = _.com.mindforge.graphics.observable_2(this$ObservableIterable.addedAt, _.com.mindforge.graphics.ObservableIterable.addedAt$f(transform));
                this.$removedAt_bj3v09$ = _.com.mindforge.graphics.observable_2(this$ObservableIterable.removedAt, _.com.mindforge.graphics.ObservableIterable.removedAt$f(transform));
              }, {
                added: {
                  get: function () {
                    return this.$added_gntybq$;
                  }
                },
                removed: {
                  get: function () {
                    return this.$removed_3r9u6y$;
                  }
                },
                addedAt: {
                  get: function () {
                    return this.$addedAt_u89049$;
                  }
                },
                removedAt: {
                  get: function () {
                    return this.$removedAt_bj3v09$;
                  }
                },
                iterator: function () {
                  return Kotlin.modules['stdlib'].kotlin.map_m3yiqg$(this$ObservableIterable, transform).iterator();
                }
              });
            }
          }),
          f_0: function (observer) {
            return function (it) {
              observer(it);
            };
          },
          f_1: function (observer) {
            return function (it) {
              observer(it);
            };
          },
          startKeepingAllObserved$f_0: function (observersByElement, observer) {
            return function (it) {
              observersByElement.put_wn2jw4$(it, it.addObserver_otxks8$(_.com.mindforge.graphics.f_1(observer)));
            };
          },
          startKeepingAllObserved$f_1: function (observersByElement) {
            return function (it) {
              var tmp$0;
              ((tmp$0 = observersByElement.remove_za3rmp$(it)) != null ? tmp$0 : Kotlin.throwNPE()).stop();
            };
          },
          startKeepingAllObserved: function ($receiver, observer) {
            var destination = new Kotlin.ArrayList(Kotlin.modules['stdlib'].kotlin.collectionSizeOrDefault_pjxt3m$($receiver, 10));
            var tmp$0;
            tmp$0 = $receiver.iterator();
            while (tmp$0.hasNext()) {
              var item = tmp$0.next();
              destination.add_za3rmp$(Kotlin.modules['stdlib'].kotlin.to_l1ob02$(item, item.addObserver_otxks8$(_.com.mindforge.graphics.f_0(observer))));
            }
            var observersByElement = Kotlin.modules['stdlib'].kotlin.hashMapOf_eoa9s7$(Kotlin.copyToArray(destination));
            var o1 = $receiver.added.addObserver_otxks8$(_.com.mindforge.graphics.startKeepingAllObserved$f_0(observersByElement, observer));
            var o2 = $receiver.removed.addObserver_otxks8$(_.com.mindforge.graphics.startKeepingAllObserved$f_1(observersByElement));
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Observer];
            }, null, {
              stop: function () {
                var $receiver = observersByElement.values();
                var tmp$0;
                tmp$0 = $receiver.iterator();
                while (tmp$0.hasNext()) {
                  var element = tmp$0.next();
                  element.stop();
                }
                o1.stop();
                o2.stop();
              }
            });
          },
          observableIterable: function (elements) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.ObservableIterable];
            }, function () {
              this.$added_443qcp$ = _.com.mindforge.graphics.observable([]);
              this.$removed_9su6x5$ = _.com.mindforge.graphics.observable([]);
              this.$addedAt_o6one2$ = _.com.mindforge.graphics.observable([]);
              this.$removedAt_sa0lqi$ = _.com.mindforge.graphics.observable([]);
            }, {
              added: {
                get: function () {
                  return this.$added_443qcp$;
                }
              },
              removed: {
                get: function () {
                  return this.$removed_9su6x5$;
                }
              },
              addedAt: {
                get: function () {
                  return this.$addedAt_o6one2$;
                }
              },
              removedAt: {
                get: function () {
                  return this.$removedAt_sa0lqi$;
                }
              },
              iterator: function () {
                return elements.iterator();
              }
            });
          },
          Transform2: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.Transform2.prototype */ {
            invoke_rkhl8y$: function (vector) {
              var v = this.matrix.times_rkhl8z$(_.com.mindforge.graphics.vector_1(vector.get_za3lpa$(0), vector.get_za3lpa$(1), 1));
              return _.com.mindforge.graphics.vector(v.get_za3lpa$(0), v.get_za3lpa$(1));
            },
            before_npjzs3$: function (other) {
              return _.com.mindforge.graphics.transform(other.matrix.times_z4uajv$(this.matrix));
            },
            at_rkhl8y$: function (location) {
              return _.com.mindforge.graphics.Transforms2.translation(location.minus()).before_npjzs3$(this).before_npjzs3$(_.com.mindforge.graphics.Transforms2.translation(location));
            },
            inverse: function () {
              var tmp$0;
              return _.com.mindforge.graphics.transform((tmp$0 = this.matrix.inverse()) != null ? tmp$0 : Kotlin.throwNPE());
            },
            equals_za3rmp$: function (other) {
              var tmp$0;
              return Kotlin.isType(other, _.com.mindforge.graphics.Transform2) && ((tmp$0 = this.matrix) != null ? tmp$0.equals_za3rmp$(other.matrix) : null);
            },
            toString: function () {
              return 'transform(' + this.matrix.toString() + ')';
            }
          }),
          transform: function (matrix) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Transform2];
            }, function () {
              if (!matrix.isInvertible)
                throw new Kotlin.IllegalArgumentException('A transformation matrix must be invertible.');
              this.$matrix_i644ft$ = matrix;
            }, {
              matrix: {
                get: function () {
                  return this.$matrix_i644ft$;
                }
              }
            });
          },
          Matrix2: Kotlin.createTrait(function () {
            return [Kotlin.modules['builtins'].kotlin.Iterable];
          }, /** @lends _.com.mindforge.graphics.Matrix2.prototype */ {
            times_z4uajw$: function (other) {
              return _.com.mindforge.graphics.matrix2(_.com.mindforge.graphics.Matrix2.times_z4uajw$f(other, this));
            },
            times_rkhl8y$: function (other) {
              return _.com.mindforge.graphics.vector2(_.com.mindforge.graphics.Matrix2.times_rkhl8y$f(other, this));
            },
            times_3p81yu$: function (other) {
              return _.com.mindforge.graphics.matrix2(_.com.mindforge.graphics.Matrix2.times_3p81yu$f(other, this));
            },
            div_3p81yu$: function (other) {
              return this.times_3p81yu$(1.0 / Kotlin.numberToDouble(other));
            },
            get_vux9f0$: function (x, y) {
              if (y === 0)
                if (x === 0)
                  return this.a;
                else if (x === 1)
                  return this.b;
                else
                  throw new Kotlin.IllegalArgumentException();
              else if (y === 1)
                if (x === 0)
                  return this.c;
                else if (x === 1)
                  return this.d;
                else
                  throw new Kotlin.IllegalArgumentException();
              else
                throw new Kotlin.IllegalArgumentException();
            },
            determinant: {
              get: function () {
                return Kotlin.numberToDouble(this.a) * Kotlin.numberToDouble(this.d) - Kotlin.numberToDouble(this.b) * Kotlin.numberToDouble(this.c);
              }
            },
            inverse: function () {
              return _.com.mindforge.graphics.matrix(Kotlin.numberToDouble(this.d), -Kotlin.numberToDouble(this.b), -Kotlin.numberToDouble(this.c), Kotlin.numberToDouble(this.a)).times_3p81yu$(1 / Kotlin.numberToDouble(this.determinant));
            },
            row_za3lpa$: function (y) {
              return _.com.mindforge.graphics.vector(this.get_vux9f0$(0, y), this.get_vux9f0$(1, y));
            },
            column_za3lpa$: function (x) {
              return _.com.mindforge.graphics.vector(this.get_vux9f0$(x, 0), this.get_vux9f0$(x, 1));
            },
            iterator: function () {
              return Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([this.a, this.b, this.c, this.d]).iterator();
            },
            equals_za3rmp$: function (other) {
              return Kotlin.isType(other, _.com.mindforge.graphics.Matrix2) && (Kotlin.equals(this.a, other.a) && Kotlin.equals(this.b, this.b) && Kotlin.equals(this.c, this.c) && Kotlin.equals(this.d, other.d));
            },
            toString: function () {
              return 'matrix(' + Kotlin.numberToDouble(this.a) + ', ' + Kotlin.numberToDouble(this.b) + ', ' + Kotlin.numberToDouble(this.c) + ', ' + Kotlin.numberToDouble(this.d) + ')';
            }
          }, /** @lends _.com.mindforge.graphics.Matrix2 */ {
            times_z4uajw$f: function (other, this$Matrix2) {
              return function (x, y) {
                return other.column_za3lpa$(y).times_rkhl8y$(this$Matrix2.row_za3lpa$(x));
              };
            },
            times_rkhl8y$f: function (other, this$Matrix2) {
              return function (it) {
                return other.times_rkhl8y$(this$Matrix2.row_za3lpa$(it));
              };
            },
            times_3p81yu$f: function (other, this$Matrix2) {
              return function (x, y) {
                return Kotlin.numberToDouble(other) * Kotlin.numberToDouble(this$Matrix2.get_vux9f0$(x, y));
              };
            }
          }),
          matrix: function (a, b, c, d) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Matrix2];
            }, function () {
              this.$a_s94ldg$ = a;
              this.$b_s94ldf$ = b;
              this.$c_s94lde$ = c;
              this.$d_s94ldd$ = d;
            }, {
              a: {
                get: function () {
                  return this.$a_s94ldg$;
                }
              },
              b: {
                get: function () {
                  return this.$b_s94ldf$;
                }
              },
              c: {
                get: function () {
                  return this.$c_s94lde$;
                }
              },
              d: {
                get: function () {
                  return this.$d_s94ldd$;
                }
              }
            });
          },
          matrix2: function (get) {
            return _.com.mindforge.graphics.matrix(get(0, 0), get(1, 0), get(0, 1), get(1, 1));
          },
          Matrix3: Kotlin.createTrait(function () {
            return [Kotlin.modules['builtins'].kotlin.Iterable];
          }, /** @lends _.com.mindforge.graphics.Matrix3.prototype */ {
            times_z4uajv$: function (other) {
              return _.com.mindforge.graphics.matrix3(_.com.mindforge.graphics.Matrix3.times_z4uajv$f(other, this));
            },
            times_rkhl8z$: function (other) {
              return _.com.mindforge.graphics.vector3(_.com.mindforge.graphics.Matrix3.times_rkhl8z$f(other, this));
            },
            times_3p81yu$: function (other) {
              return _.com.mindforge.graphics.matrix3(_.com.mindforge.graphics.Matrix3.times_3p81yu$f(other, this));
            },
            div_3p81yu$: function (other) {
              return this.times_3p81yu$(1.0 / Kotlin.numberToDouble(other));
            },
            get_vux9f0$: function (x, y) {
              if (x === 0)
                if (y === 0)
                  return this.a;
                else if (y === 1)
                  return this.b;
                else if (y === 2)
                  return this.c;
                else
                  throw new Kotlin.IllegalArgumentException();
              else if (x === 1)
                if (y === 0)
                  return this.d;
                else if (y === 1)
                  return this.e;
                else if (y === 2)
                  return this.f;
                else
                  throw new Kotlin.IllegalArgumentException();
              else if (x === 2)
                if (y === 0)
                  return this.g;
                else if (y === 1)
                  return this.h;
                else if (y === 2)
                  return this.i;
                else
                  throw new Kotlin.IllegalArgumentException();
              else
                throw new Kotlin.IllegalArgumentException();
            },
            determinant: {
              get: function () {
                return Kotlin.numberToDouble(this.a) * Kotlin.numberToDouble(this.e) * Kotlin.numberToDouble(this.i) + Kotlin.numberToDouble(this.b) * Kotlin.numberToDouble(this.f) * Kotlin.numberToDouble(this.g) + Kotlin.numberToDouble(this.c) * Kotlin.numberToDouble(this.d) * Kotlin.numberToDouble(this.h) - (Kotlin.numberToDouble(this.c) * Kotlin.numberToDouble(this.e) * Kotlin.numberToDouble(this.g) + Kotlin.numberToDouble(this.a) * Kotlin.numberToDouble(this.f) * Kotlin.numberToDouble(this.h) + Kotlin.numberToDouble(this.b) * Kotlin.numberToDouble(this.d) * Kotlin.numberToDouble(this.i));
              }
            },
            isInvertible: {
              get: function () {
                return !Kotlin.equals(this.determinant, 0.0);
              }
            },
            transpose: function () {
              return _.com.mindforge.graphics.matrix3(_.com.mindforge.graphics.Matrix3.transpose$f(this));
            },
            inverse: function () {
              return this.isInvertible ? this.adjugate().div_3p81yu$(this.determinant) : null;
            },
            adjugate: function () {
              return _.com.mindforge.graphics.matrix3(_.com.mindforge.graphics.Matrix3.adjugate$f(this));
            },
            row_za3lpa$: function (x) {
              return _.com.mindforge.graphics.vector_1(this.get_vux9f0$(x, 0), this.get_vux9f0$(x, 1), this.get_vux9f0$(x, 2));
            },
            column_za3lpa$: function (y) {
              return _.com.mindforge.graphics.vector_1(this.get_vux9f0$(0, y), this.get_vux9f0$(1, y), this.get_vux9f0$(2, y));
            },
            subMatrix_vux9f0$: function (exceptX, exceptY) {
              return _.com.mindforge.graphics.matrix2(_.com.mindforge.graphics.Matrix3.subMatrix_vux9f0$f(this, exceptX, exceptY));
            },
            iterator: function () {
              return Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i]).iterator();
            },
            equals_za3rmp$: function (other) {
              return Kotlin.isType(other, _.com.mindforge.graphics.Matrix3) && (Kotlin.numberToDouble(this.a) === Kotlin.numberToDouble(other.a) && Kotlin.numberToDouble(this.b) === Kotlin.numberToDouble(other.b) && Kotlin.numberToDouble(this.c) === Kotlin.numberToDouble(other.c) && Kotlin.numberToDouble(this.d) === Kotlin.numberToDouble(other.d) && Kotlin.numberToDouble(this.e) === Kotlin.numberToDouble(other.e) && Kotlin.numberToDouble(this.f) === Kotlin.numberToDouble(other.f) && Kotlin.numberToDouble(this.g) === Kotlin.numberToDouble(other.g) && Kotlin.numberToDouble(this.h) === Kotlin.numberToDouble(other.h) && Kotlin.numberToDouble(this.i) === Kotlin.numberToDouble(other.i));
            },
            toString: function () {
              return 'matrix(' + Kotlin.numberToDouble(this.a) + ', ' + Kotlin.numberToDouble(this.b) + ', ' + Kotlin.numberToDouble(this.c) + ', ' + Kotlin.numberToDouble(this.d) + ', ' + Kotlin.numberToDouble(this.e) + ', ' + Kotlin.numberToDouble(this.f) + ', ' + Kotlin.numberToDouble(this.g) + ', ' + Kotlin.numberToDouble(this.h) + ', ' + Kotlin.numberToDouble(this.i) + ')';
            },
            hashCode: function () {
              var $receiver = this;
              var tmp$0;
              var sum = 0;
              tmp$0 = $receiver.iterator();
              while (tmp$0.hasNext()) {
                var element = tmp$0.next();
                sum += Kotlin.hashCode(element);
              }
              return sum;
            }
          }, /** @lends _.com.mindforge.graphics.Matrix3 */ {
            times_z4uajv$f: function (other, this$Matrix3) {
              return function (x, y) {
                return other.column_za3lpa$(y).times_rkhl8z$(this$Matrix3.row_za3lpa$(x));
              };
            },
            times_rkhl8z$f: function (other, this$Matrix3) {
              return function (index) {
                return other.times_rkhl8z$(this$Matrix3.row_za3lpa$(index));
              };
            },
            times_3p81yu$f: function (other, this$Matrix3) {
              return function (x, y) {
                return Kotlin.numberToDouble(other) * Kotlin.numberToDouble(this$Matrix3.get_vux9f0$(x, y));
              };
            },
            transpose$f: function (this$Matrix3) {
              return function (x, y) {
                return this$Matrix3.get_vux9f0$(y, x);
              };
            },
            adjugate$f: function (this$Matrix3) {
              return function (x, y) {
                return Kotlin.numberToDouble(this$Matrix3.subMatrix_vux9f0$(y, x).determinant) * ((x + y) % 2 === 0 ? 1 : -1);
              };
            },
            subMatrix_vux9f0$f: function (this$Matrix3, exceptX, exceptY) {
              return function (x, y) {
                var $receiver = new Kotlin.NumberRange(0, 2);
                var destination = new Kotlin.ArrayList();
                var tmp$1;
                tmp$1 = $receiver.iterator();
                while (tmp$1.hasNext()) {
                  var element = tmp$1.next();
                  if (element !== exceptX) {
                    destination.add_za3rmp$(element);
                  }
                }
                var tmp$0 = destination.get_za3lpa$(x);
                var $receiver_0 = new Kotlin.NumberRange(0, 2);
                var destination_0 = new Kotlin.ArrayList();
                var tmp$2;
                tmp$2 = $receiver_0.iterator();
                while (tmp$2.hasNext()) {
                  var element_0 = tmp$2.next();
                  if (element_0 !== exceptY) {
                    destination_0.add_za3rmp$(element_0);
                  }
                }
                return this$Matrix3.get_vux9f0$(tmp$0, destination_0.get_za3lpa$(y));
              };
            }
          }),
          matrix_1: function (a, b, c, d, e, f, g, h, i) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Matrix3];
            }, function () {
              this.$a_s94ldg$ = a;
              this.$b_s94ldf$ = b;
              this.$c_s94lde$ = c;
              this.$d_s94ldd$ = d;
              this.$e_s94ldc$ = e;
              this.$f_s94ldb$ = f;
              this.$g_s94lda$ = g;
              this.$h_s94ld9$ = h;
              this.$i_s94ld8$ = i;
            }, {
              a: {
                get: function () {
                  return this.$a_s94ldg$;
                }
              },
              b: {
                get: function () {
                  return this.$b_s94ldf$;
                }
              },
              c: {
                get: function () {
                  return this.$c_s94lde$;
                }
              },
              d: {
                get: function () {
                  return this.$d_s94ldd$;
                }
              },
              e: {
                get: function () {
                  return this.$e_s94ldc$;
                }
              },
              f: {
                get: function () {
                  return this.$f_s94ldb$;
                }
              },
              g: {
                get: function () {
                  return this.$g_s94lda$;
                }
              },
              h: {
                get: function () {
                  return this.$h_s94ld9$;
                }
              },
              i: {
                get: function () {
                  return this.$i_s94ld8$;
                }
              }
            });
          },
          matrix3: function (get) {
            return _.com.mindforge.graphics.matrix_1(get(0, 0), get(0, 1), get(0, 2), get(1, 0), get(1, 1), get(1, 2), get(2, 0), get(2, 1), get(2, 2));
          },
          Vector2: Kotlin.createTrait(function () {
            return [Kotlin.modules['builtins'].kotlin.Iterable];
          }, /** @lends _.com.mindforge.graphics.Vector2.prototype */ {
            plus_rkhl8y$: function (other) {
              return _.com.mindforge.graphics.vector(Kotlin.numberToDouble(this.x) + Kotlin.numberToDouble(other.x), Kotlin.numberToDouble(this.y) + Kotlin.numberToDouble(other.y));
            },
            times_3p81yu$: function (other) {
              var s = Kotlin.numberToDouble(other);
              return _.com.mindforge.graphics.vector(Kotlin.numberToDouble(this.x) * s, Kotlin.numberToDouble(this.y) * s);
            },
            plus: function () {
              return this;
            },
            minus: function () {
              return this.times_3p81yu$(-1);
            },
            minus_rkhl8y$: function (other) {
              return this.plus_rkhl8y$(other.minus());
            },
            div_3p81yu$: function (other) {
              return this.times_3p81yu$(1 / Kotlin.numberToDouble(other));
            },
            get_za3lpa$: function (i) {
              if (i === 0)
                return this.x;
              else if (i === 1)
                return this.y;
              else {
                throw new Kotlin.IllegalArgumentException();
              }
            },
            lengthSquared: {
              get: function () {
                var x = Kotlin.numberToDouble(this.x);
                var y = Kotlin.numberToDouble(this.y);
                return x * x + y * y;
              }
            },
            length: {
              get: function () {
                return Math.sqrt(Kotlin.numberToDouble(this.lengthSquared));
              }
            },
            argument: {
              get: function () {
                return Math.atan2(Kotlin.numberToDouble(this.y), Kotlin.numberToDouble(this.x));
              }
            },
            times_rkhl8y$: function (other) {
              return Kotlin.numberToDouble(this.x) * Kotlin.numberToDouble(other.x) + Kotlin.numberToDouble(this.y) * Kotlin.numberToDouble(other.y);
            },
            iterator: function () {
              return Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([this.x, this.y]).iterator();
            },
            equals_za3rmp$: function (other) {
              return Kotlin.isType(other, _.com.mindforge.graphics.Vector2) && (Kotlin.numberToDouble(this.x) === Kotlin.numberToDouble(other.x) && Kotlin.numberToDouble(this.y) === Kotlin.numberToDouble(other.y));
            },
            toString: function () {
              return 'vector(' + Kotlin.numberToDouble(this.x) + ', ' + Kotlin.numberToDouble(this.y) + ')';
            },
            xComponent: function () {
              return _.com.mindforge.graphics.vector(this.x, 0);
            },
            yComponent: function () {
              return _.com.mindforge.graphics.vector(0, this.y);
            },
            mirrorX: function () {
              return this.yComponent().minus_rkhl8y$(this.xComponent());
            },
            mirrorY: function () {
              return this.xComponent().minus_rkhl8y$(this.yComponent());
            }
          }),
          vector: function (x, y) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Vector2];
            }, function () {
              this.$x_u1adup$ = x;
              this.$y_u1aduq$ = y;
            }, {
              x: {
                get: function () {
                  return this.$x_u1adup$;
                }
              },
              y: {
                get: function () {
                  return this.$y_u1aduq$;
                }
              }
            });
          },
          vector2: function (get) {
            return _.com.mindforge.graphics.vector(get(0), get(1));
          },
          Vector3: Kotlin.createTrait(function () {
            return [Kotlin.modules['builtins'].kotlin.Iterable];
          }, /** @lends _.com.mindforge.graphics.Vector3.prototype */ {
            plus_rkhl8z$: function (other) {
              return _.com.mindforge.graphics.vector_1(Kotlin.numberToDouble(this.x) + Kotlin.numberToDouble(other.x), Kotlin.numberToDouble(this.y) + Kotlin.numberToDouble(other.y), Kotlin.numberToDouble(this.z) + Kotlin.numberToDouble(other.z));
            },
            times_3p81yu$: function (other) {
              var s = Kotlin.numberToDouble(other);
              return _.com.mindforge.graphics.vector_1(Kotlin.numberToDouble(this.x) * s, Kotlin.numberToDouble(this.y) * s, Kotlin.numberToDouble(this.z) * s);
            },
            plus: function () {
              return this;
            },
            minus: function () {
              return this.times_3p81yu$(-1);
            },
            minus_rkhl8z$: function (other) {
              return this.plus_rkhl8z$(other.minus());
            },
            div_3p81yu$: function (other) {
              return this.times_3p81yu$(1 / Kotlin.numberToDouble(other));
            },
            get_za3lpa$: function (i) {
              if (i === 0)
                return this.x;
              else if (i === 1)
                return this.y;
              else if (i === 2)
                return this.z;
              else {
                throw new Kotlin.IllegalArgumentException();
              }
            },
            lengthSquared: {
              get: function () {
                var x = Kotlin.numberToDouble(this.x);
                var y = Kotlin.numberToDouble(this.y);
                var z = Kotlin.numberToDouble(this.z);
                return x * x + y * y + z * z;
              }
            },
            length: {
              get: function () {
                return Math.sqrt(Kotlin.numberToDouble(this.lengthSquared));
              }
            },
            dotProduct_rkhl8z$: function (other) {
              return Kotlin.numberToDouble(this.x) * Kotlin.numberToDouble(other.x) + Kotlin.numberToDouble(this.y) * Kotlin.numberToDouble(other.y) + Kotlin.numberToDouble(this.z) * Kotlin.numberToDouble(other.z);
            },
            iterator: function () {
              return Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([this.x, this.y, this.z]).iterator();
            },
            equals_za3rmp$: function (other) {
              return Kotlin.isType(other, _.com.mindforge.graphics.Vector3) && (Kotlin.numberToDouble(this.x) === Kotlin.numberToDouble(other.x) && Kotlin.numberToDouble(this.y) === Kotlin.numberToDouble(other.y) && Kotlin.numberToDouble(this.z) === Kotlin.numberToDouble(other.z));
            },
            toString: function () {
              return 'vector(' + Kotlin.numberToDouble(this.x) + ', ' + Kotlin.numberToDouble(this.y) + ', ' + Kotlin.numberToDouble(this.z) + ')';
            },
            times_rkhl8z$: function (other) {
              return this.dotProduct_rkhl8z$(other);
            },
            crossProduct_rkhl8z$: function (other) {
              return _.com.mindforge.graphics.vector_1(Kotlin.numberToDouble(this.y) * Kotlin.numberToDouble(other.z) - Kotlin.numberToDouble(this.z) * Kotlin.numberToDouble(other.y), Kotlin.numberToDouble(this.z) * Kotlin.numberToDouble(other.x) - Kotlin.numberToDouble(this.x) * Kotlin.numberToDouble(other.z), Kotlin.numberToDouble(this.x) * Kotlin.numberToDouble(other.y) - Kotlin.numberToDouble(this.y) * Kotlin.numberToDouble(other.x));
            }
          }),
          vector_1: function (x, y, z) {
            return Kotlin.createObject(function () {
              return [_.com.mindforge.graphics.Vector3];
            }, function () {
              this.$x_u1adup$ = x;
              this.$y_u1aduq$ = y;
              this.$z_u1adur$ = z;
            }, {
              x: {
                get: function () {
                  return this.$x_u1adup$;
                }
              },
              y: {
                get: function () {
                  return this.$y_u1aduq$;
                }
              },
              z: {
                get: function () {
                  return this.$z_u1adur$;
                }
              }
            });
          },
          vector3: function (get) {
            return _.com.mindforge.graphics.vector_1(get(0), get(1), get(2));
          },
          interaction: Kotlin.definePackage(function () {
            this.KeyDefinitions = Kotlin.createObject(null, null, {
              left: function (command, alternativeCommands) {
                if (alternativeCommands === void 0)
                  alternativeCommands = Kotlin.modules['stdlib'].kotlin.listOf();
                return _.com.mindforge.graphics.interaction.keyDefinition(command, alternativeCommands, 'left ' + command.name);
              },
              right: function (command, alternativeCommands) {
                if (alternativeCommands === void 0)
                  alternativeCommands = Kotlin.modules['stdlib'].kotlin.listOf();
                return _.com.mindforge.graphics.interaction.keyDefinition(command, alternativeCommands, 'right ' + command.name);
              },
              numPad: function (command, alternativeCommands) {
                if (alternativeCommands === void 0)
                  alternativeCommands = Kotlin.modules['stdlib'].kotlin.listOf();
                return _.com.mindforge.graphics.interaction.keyDefinition(command, alternativeCommands, 'num pad ' + command.name);
              }
            });
            this.Commands = Kotlin.createObject(null, function () {
              this.Keyboard = _.com.mindforge.graphics.interaction.Keyboard();
              this.Mouse = _.com.mindforge.graphics.interaction.Mouse();
              this.Touch = _.com.mindforge.graphics.interaction.Touch();
              this.Navigation = _.com.mindforge.graphics.interaction.Navigation();
              this.Media = _.com.mindforge.graphics.interaction.Media();
              this.Power = _.com.mindforge.graphics.interaction.Power();
            });
          }, /** @lends _.com.mindforge.graphics.interaction */ {
            Draggable: Kotlin.createClass(function () {
              return [_.com.mindforge.graphics.interaction.PointersElement, _.com.mindforge.graphics.Composed];
            }, function (element, dragLocation) {
              if (dragLocation === void 0)
                dragLocation = _.com.mindforge.graphics.zeroVector2;
              this.element = element;
              this.$changed_enrtfe$ = _.com.mindforge.graphics.trigger();
              this.dragLocation$delegate = _.com.mindforge.graphics.observed(Kotlin.modules['stdlib'].kotlin.properties.Delegates, dragLocation, this.changed);
              this.$elements_4ghlm9$ = _.com.mindforge.graphics.observableIterable(Kotlin.modules['stdlib'].kotlin.listOf_za3rmp$(_.com.mindforge.graphics.interaction.Draggable.elements$f(this)));
              this.dropped = _.com.mindforge.graphics.trigger();
              this.moved = _.com.mindforge.graphics.trigger();
              this.observers_xye1wd$ = new Kotlin.ArrayList();
            }, /** @lends _.com.mindforge.graphics.interaction.Draggable.prototype */ {
              changed: {
                get: function () {
                  return this.$changed_enrtfe$;
                }
              },
              dragLocation: {
                get: function () {
                  return this.dragLocation$delegate.get_1tsekc$(this, new Kotlin.PropertyMetadata('dragLocation'));
                },
                set: function (dragLocation) {
                  this.dragLocation$delegate.set_1z3uih$(this, new Kotlin.PropertyMetadata('dragLocation'), dragLocation);
                }
              },
              content: {
                get: function () {
                  return this.element.content;
                }
              },
              elements: {
                get: function () {
                  return this.$elements_4ghlm9$;
                }
              },
              onMoved_2huul7$: function (pointerKey) {
                this.dragLocation = pointerKey.pointer.location;
                this.moved.invoke_za3rmp$(pointerKey);
              },
              onPointerKeyPressed_2huul7$: function (pointerKey) {
                this.startDrag_2huul7$(pointerKey);
              },
              startDrag_2huul7$: function (pointerKey) {
                this.observers_xye1wd$.add_za3rmp$(pointerKey.pointer.moved.addObserver_otxks8$(_.com.mindforge.graphics.interaction.Draggable.startDrag_2huul7$f(pointerKey, this)));
                pointerKey.key.released.addObserver_otxks8$(_.com.mindforge.graphics.interaction.Draggable.startDrag_2huul7$f_0(this, pointerKey));
              }
            }, /** @lends _.com.mindforge.graphics.interaction.Draggable */ {
              elements$f: function (this$Draggable) {
                return Kotlin.createObject(function () {
                  return [_.com.mindforge.graphics.TransformedElement];
                }, function () {
                  this.$element_h2h9yk$ = this$Draggable.element;
                  this.$transformChanged_w4zow$ = this$Draggable.changed;
                }, {
                  element: {
                    get: function () {
                      return this.$element_h2h9yk$;
                    }
                  },
                  transform: {
                    get: function () {
                      return _.com.mindforge.graphics.Transforms2.translation(this$Draggable.dragLocation);
                    }
                  },
                  transformChanged: {
                    get: function () {
                      return this.$transformChanged_w4zow$;
                    }
                  }
                });
              },
              startDrag_2huul7$f: function (pointerKey, this$Draggable) {
                return function (it) {
                  this$Draggable.onMoved_2huul7$(pointerKey);
                };
              },
              startDrag_2huul7$f_0: function (this$Draggable, pointerKey) {
                return function (it) {
                  this.stop();
                  var tmp$0;
                  tmp$0 = this$Draggable.observers_xye1wd$.iterator();
                  while (tmp$0.hasNext()) {
                    var element = tmp$0.next();
                    element.stop();
                  }
                  this$Draggable.observers_xye1wd$.clear();
                  this$Draggable.dropped.invoke_za3rmp$(pointerKey);
                };
              }
            }),
            Scrollable: Kotlin.createClass(function () {
              return [_.com.mindforge.graphics.interaction.PointersElement, _.com.mindforge.graphics.Composed];
            }, function (element) {
              this.element = element;
              this.$changed_c4zaca$ = _.com.mindforge.graphics.trigger();
              this.scrollLocation = _.com.mindforge.graphics.zeroVector2;
              this.$elements_q2i0hx$ = _.com.mindforge.graphics.observableIterable(Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([_.com.mindforge.graphics.interaction.Scrollable.elements$f(this), _.com.mindforge.graphics.transformedElement(_.com.mindforge.graphics.coloredElement(_.com.mindforge.graphics.math.rectangle(_.com.mindforge.graphics.vector(10000, 10000)), _.com.mindforge.graphics.Fills.solid(_.com.mindforge.graphics.Colors.white)))]));
              this.lastLocation_b7c5mv$ = null;
            }, /** @lends _.com.mindforge.graphics.interaction.Scrollable.prototype */ {
              content: {
                get: function () {
                  return this.element.content;
                }
              },
              changed: {
                get: function () {
                  return this.$changed_c4zaca$;
                }
              },
              elements: {
                get: function () {
                  return this.$elements_q2i0hx$;
                }
              },
              onPointerKeyPressed_2huul7$: function (pointerKey) {
                this.lastLocation_b7c5mv$ = pointerKey.pointer.location;
              },
              onPointerKeyReleased_2huul7$: function (pointerKey) {
                this.lastLocation_b7c5mv$ = null;
              },
              onPointerMoved_ijd4m4$: function (pointer) {
                var last = this.lastLocation_b7c5mv$;
                var current = pointer.location;
                if (last != null) {
                  this.scrollLocation = this.scrollLocation.plus_rkhl8y$(current.minus_rkhl8y$(last));
                  this.lastLocation_b7c5mv$ = current;
                  _.com.mindforge.graphics.invoke(this.changed);
                }
              }
            }, /** @lends _.com.mindforge.graphics.interaction.Scrollable */ {
              elements$f: function (this$Scrollable) {
                return Kotlin.createObject(function () {
                  return [_.com.mindforge.graphics.TransformedElement];
                }, function () {
                  this.$element_w0pm68$ = this$Scrollable.element;
                  this.$transformChanged_wu20mc$ = this$Scrollable.changed;
                }, {
                  element: {
                    get: function () {
                      return this.$element_w0pm68$;
                    }
                  },
                  transform: {
                    get: function () {
                      return _.com.mindforge.graphics.Transforms2.translation(this$Scrollable.scrollLocation);
                    }
                  },
                  transformChanged: {
                    get: function () {
                      return this.$transformChanged_wu20mc$;
                    }
                  }
                });
              }
            }),
            Stackable: Kotlin.createClass(null, function (element, shape) {
              this.element = element;
              this.sizeChangedTrigger_61p1fu$ = _.com.mindforge.graphics.trigger();
              this.shape$delegate = _.com.mindforge.graphics.observed(Kotlin.modules['stdlib'].kotlin.properties.Delegates, shape, this.sizeChangedTrigger_61p1fu$);
              this.shapeChanged = this.sizeChangedTrigger_61p1fu$;
            }, /** @lends _.com.mindforge.graphics.interaction.Stackable.prototype */ {
              shape: {
                get: function () {
                  return this.shape$delegate.get_1tsekc$(this, new Kotlin.PropertyMetadata('shape'));
                },
                set: function (shape) {
                  this.shape$delegate.set_1z3uih$(this, new Kotlin.PropertyMetadata('shape'), shape);
                }
              }
            }),
            horizontalStack: function (elements, align) {
              if (align === void 0)
                align = true;
              return new _.com.mindforge.graphics.interaction.Stack(elements, true, align);
            },
            verticalStack: function (elements, align) {
              if (align === void 0)
                align = true;
              return new _.com.mindforge.graphics.interaction.Stack(elements, false, align);
            },
            Stack: Kotlin.createClass(function () {
              return [_.com.mindforge.graphics.Composed];
            }, function (stackElements, horizontal, alignToAxis) {
              if (alignToAxis === void 0)
                alignToAxis = true;
              this.stackElements = stackElements;
              this.horizontal = horizontal;
              this.alignToAxis = alignToAxis;
              this.$elements_kv8m8m$ = new _.com.mindforge.graphics.ObservableArrayList();
              this.$content_ipebqy$ = Kotlin.modules['builtins'].kotlin.Unit;
              this.$changed_f6tulx$ = _.com.mindforge.graphics.trigger();
              this.initTransforms();
              this.observer1_mgwwis$ = _.com.mindforge.graphics.startKeepingAllObserved(this.stackElements.mapObservable_z22aos$(_.com.mindforge.graphics.interaction.Stack.observer1_mgwwis$f), _.com.mindforge.graphics.interaction.Stack.observer1_mgwwis$f_0(this));
              this.observer2_mgwwir$ = this.stackElements.added.addObserver_otxks8$(_.com.mindforge.graphics.interaction.Stack.observer2_mgwwir$f(this));
              this.observer3_mgwwiq$ = this.stackElements.removed.addObserver_otxks8$(_.com.mindforge.graphics.interaction.Stack.observer3_mgwwiq$f(this));
            }, /** @lends _.com.mindforge.graphics.interaction.Stack.prototype */ {
              partialTranslation: function ($receiver) {
                return this.horizontal ? $receiver.shape.size.xComponent() : $receiver.shape.size.yComponent().minus();
              },
              offset: function ($receiver) {
                var total = $receiver.shape.center.minus_rkhl8y$((this.horizontal ? $receiver.shape.size : $receiver.shape.size.xComponent().minus_rkhl8y$($receiver.shape.size.yComponent())).div_3p81yu$(2));
                return this.alignToAxis ? total : this.horizontal ? total.xComponent() : total.yComponent();
              },
              elements: {
                get: function () {
                  return this.$elements_kv8m8m$;
                }
              },
              content: {
                get: function () {
                  return this.$content_ipebqy$;
                }
              },
              changed: {
                get: function () {
                  return this.$changed_f6tulx$;
                }
              },
              removeObservers: function () {
                this.observer1_mgwwis$.stop();
                this.observer2_mgwwir$.stop();
                this.observer3_mgwwiq$.stop();
              },
              initTransforms: function () {
                var tmp$0;
                this.elements.clear();
                var partialTransformation = _.com.mindforge.graphics.zeroVector2;
                tmp$0 = this.stackElements.iterator();
                while (tmp$0.hasNext()) {
                  var e = tmp$0.next();
                  this.elements.add_za3rmp$(new _.com.mindforge.graphics.MutableTransformedElement(e.element, _.com.mindforge.graphics.Transforms2.translation(partialTransformation.minus_rkhl8y$(this.offset(e)))));
                  partialTransformation = partialTransformation.plus_rkhl8y$(this.partialTranslation(e));
                }
                _.com.mindforge.graphics.invoke(this.changed);
              },
              length: function () {
                var $receiver = this.stackElements;
                var transform = _.com.mindforge.graphics.interaction.Stack.length$f(this);
                var destination = new Kotlin.ArrayList(Kotlin.modules['stdlib'].kotlin.collectionSizeOrDefault_pjxt3m$($receiver, 10));
                var tmp$0;
                tmp$0 = $receiver.iterator();
                while (tmp$0.hasNext()) {
                  var item = tmp$0.next();
                  destination.add_za3rmp$(transform(item));
                }
                return Kotlin.modules['stdlib'].kotlin.sum_z1slkf$(destination);
              }
            }, /** @lends _.com.mindforge.graphics.interaction.Stack */ {
              observer1_mgwwis$f: function (it) {
                return it.shapeChanged;
              },
              observer1_mgwwis$f_0: function (this$Stack) {
                return function (it) {
                  this$Stack.initTransforms();
                };
              },
              observer2_mgwwir$f: function (this$Stack) {
                return function (it) {
                  this$Stack.elements.add_za3rmp$(new _.com.mindforge.graphics.MutableTransformedElement(it.element));
                  this$Stack.initTransforms();
                };
              },
              observer3_mgwwiq$f: function (this$Stack) {
                return function (it) {
                  this$Stack.elements.remove_za3rmp$(new _.com.mindforge.graphics.MutableTransformedElement(it.element));
                  this$Stack.initTransforms();
                };
              },
              length$f: function (this$Stack) {
                return function (it) {
                  var size = it.shape.size;
                  return Kotlin.numberToDouble(this$Stack.horizontal ? size.x : size.y);
                };
              }
            }),
            KeyCombination: Kotlin.createTrait(null),
            Button: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.Composed, _.com.mindforge.graphics.interaction.PointersElement];
            }, /** @lends _.com.mindforge.graphics.interaction.Button.prototype */ {
              onPointerKeyPressed_2huul7$: function (pointerKey) {
                _.com.mindforge.graphics.invoke(this.content);
              }
            }),
            button$f: function (it) {
            },
            f: function (onClick) {
              return function (it) {
                onClick();
              };
            },
            button: function (shape, elements, changed, trigger, onLongPressed, onClick) {
              if (changed === void 0)
                changed = _.com.mindforge.graphics.observable([]);
              if (trigger === void 0)
                trigger = _.com.mindforge.graphics.trigger();
              if (onLongPressed === void 0)
                onLongPressed = _.com.mindforge.graphics.interaction.button$f;
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.interaction.Button];
              }, function () {
                this.$content_pron2h$ = trigger;
                this.$shape_velexr$ = shape;
                this.$changed_ta947i$ = changed;
                this.$elements_896bk7$ = elements;
                trigger.addObserver_otxks8$(_.com.mindforge.graphics.interaction.f(onClick));
              }, {
                content: {
                  get: function () {
                    return this.$content_pron2h$;
                  }
                },
                shape: {
                  get: function () {
                    return this.$shape_velexr$;
                  }
                },
                changed: {
                  get: function () {
                    return this.$changed_ta947i$;
                  }
                },
                elements: {
                  get: function () {
                    return this.$elements_896bk7$;
                  }
                },
                onPointerKeyPressed_2huul7$: function (pointerKey) {
                  _.com.mindforge.graphics.interaction.Button.prototype.onPointerKeyPressed_2huul7$.call(this, pointerKey);
                },
                onPointerKeyReleased_2huul7$: function (pointerKey) {
                },
                onPointerKeyLongPressed: function (pointerKey) {
                  onLongPressed(pointerKey);
                }
              });
            },
            textRectangleButton$f: function (it) {
            },
            textRectangleButton: function (inner, onLongPressed, onClick) {
              if (onLongPressed === void 0)
                onLongPressed = _.com.mindforge.graphics.interaction.textRectangleButton$f;
              return _.com.mindforge.graphics.interaction.button(inner.shape.box(), _.com.mindforge.graphics.observableIterable(Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([_.com.mindforge.graphics.transformedElement(inner), _.com.mindforge.graphics.transformedElement(_.com.mindforge.graphics.coloredElement(inner.shape.box(), _.com.mindforge.graphics.Fills.solid(_.com.mindforge.graphics.Colors.black.times_3p81yu$(0.1)))), _.com.mindforge.graphics.transformedElement(_.com.mindforge.graphics.coloredElement(_.com.mindforge.graphics.math.rectangle(_.com.mindforge.graphics.vector(10, 1)), _.com.mindforge.graphics.Fills.solid(_.com.mindforge.graphics.Colors.blue))), _.com.mindforge.graphics.transformedElement(_.com.mindforge.graphics.coloredElement(_.com.mindforge.graphics.math.rectangle(_.com.mindforge.graphics.vector(1, 10)), _.com.mindforge.graphics.Fills.solid(_.com.mindforge.graphics.Colors.blue)))])), void 0, void 0, onLongPressed, onClick);
            },
            coloredButton$f: function () {
            },
            coloredButton: function (shape, fill, changed, trigger, onClick) {
              if (changed === void 0)
                changed = _.com.mindforge.graphics.observable([]);
              if (trigger === void 0)
                trigger = _.com.mindforge.graphics.trigger();
              if (onClick === void 0)
                onClick = _.com.mindforge.graphics.interaction.coloredButton$f;
              return _.com.mindforge.graphics.interaction.button(shape, _.com.mindforge.graphics.observableIterable(Kotlin.modules['stdlib'].kotlin.listOf_za3rmp$(_.com.mindforge.graphics.transformedElement(_.com.mindforge.graphics.coloredElement(shape, fill)))), changed, trigger, void 0, onClick);
            },
            PointersElement: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.Element];
            }, /** @lends _.com.mindforge.graphics.interaction.PointersElement.prototype */ {
              onPointerKeyPressed_2huul7$: function (pointerKey) {
              },
              onPointerKeyReleased_2huul7$: function (pointerKey) {
              },
              onPointerMoved_ijd4m4$: function (pointer) {
              },
              onPointerEntered_ijd4m4$: function (pointer) {
              },
              onPointerLeaved_ijd4m4$: function (pointer) {
              }
            }),
            KeysElement: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.Element];
            }, /** @lends _.com.mindforge.graphics.interaction.KeysElement.prototype */ {
              onKeyPressed_nqvshy$: function (key) {
              },
              onKeyReleased_nqvshy$: function (key) {
              },
              onGotKeysFocus_9jcpwb$: function (keys) {
              },
              onLostKeysFocus_9jcpwb$: function (keys) {
              }
            }),
            Interactive: Kotlin.createTrait(null),
            KeyDefinition: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.interaction.KeyDefinition.prototype */ {
              alternativeCommands: {
                get: function () {
                  return Kotlin.modules['stdlib'].kotlin.listOf();
                }
              },
              name: {
                get: function () {
                  return this.command.name;
                }
              }
            }),
            keyDefinition: function (command, alternativeCommands, name) {
              if (alternativeCommands === void 0)
                alternativeCommands = Kotlin.modules['stdlib'].kotlin.listOf();
              if (name === void 0)
                name = command.name;
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.interaction.KeyDefinition];
              }, function () {
                this.$command_l5jepn$ = command;
                this.$alternativeCommands_11xnwl$ = alternativeCommands;
                this.$name_1yli8r$ = name;
              }, {
                command: {
                  get: function () {
                    return this.$command_l5jepn$;
                  }
                },
                alternativeCommands: {
                  get: function () {
                    return this.$alternativeCommands_11xnwl$;
                  }
                },
                name: {
                  get: function () {
                    return this.$name_1yli8r$;
                  }
                }
              });
            },
            Key: Kotlin.createTrait(null),
            Command: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.interaction.Command.prototype */ {
              equals_za3rmp$: function (other) {
                return Kotlin.isType(other, _.com.mindforge.graphics.interaction.Command) ? Kotlin.equals(this.name, other.name) : false;
              }
            }),
            command: function (name) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.interaction.Command];
              }, function () {
                this.$name_vte49g$ = name;
              }, {
                name: {
                  get: function () {
                    return this.$name_vte49g$;
                  }
                }
              });
            },
            CharacterCommand: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.interaction.Command];
            }),
            character$f: function (char) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.interaction.CharacterCommand];
              }, function () {
                this.$name_twm3e7$ = char.toString();
                this.$character_x8kwyl$ = char;
              }, {
                name: {
                  get: function () {
                    return this.$name_twm3e7$;
                  }
                },
                character: {
                  get: function () {
                    return this.$character_x8kwyl$;
                  }
                }
              });
            },
            specialWithCharacter$f: function (name, character) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.interaction.CharacterCommand];
              }, function () {
                this.$name_5uprfe$ = name;
                this.$character_saxkfi$ = character;
              }, {
                name: {
                  get: function () {
                    return this.$name_5uprfe$;
                  }
                },
                character: {
                  get: function () {
                    return this.$character_saxkfi$;
                  }
                }
              });
            },
            Keyboard: function () {
              return Kotlin.createObject(null, function () {
                this.control = _.com.mindforge.graphics.interaction.command('control');
                this.alt = _.com.mindforge.graphics.interaction.command('alt');
                this.shift = _.com.mindforge.graphics.interaction.command('shift');
                this.altGr = _.com.mindforge.graphics.interaction.command('alt gr');
                this.start = _.com.mindforge.graphics.interaction.command('start');
                this.menu = _.com.mindforge.graphics.interaction.command('menu');
                this.escape = _.com.mindforge.graphics.interaction.command('escape');
                this.printScreen = _.com.mindforge.graphics.interaction.command('print screen');
                this.scrollLock = _.com.mindforge.graphics.interaction.command('scroll lock');
                this.numLock = _.com.mindforge.graphics.interaction.command('num lock');
                this.capsLock = _.com.mindforge.graphics.interaction.command('caps lock');
                this.space = this.specialWithCharacter('space', ' ');
                this.tab = this.specialWithCharacter('tab', '\t');
                this.enter = this.specialWithCharacter('enter', '\n');
                this.delete = _.com.mindforge.graphics.interaction.command('delete');
                this.backspace = _.com.mindforge.graphics.interaction.command('backspace');
                this.insert = _.com.mindforge.graphics.interaction.command('insert');
                this.f1 = _.com.mindforge.graphics.interaction.command('f17');
                this.f2 = _.com.mindforge.graphics.interaction.command('f2');
                this.f3 = _.com.mindforge.graphics.interaction.command('f3');
                this.f4 = _.com.mindforge.graphics.interaction.command('f4');
                this.f5 = _.com.mindforge.graphics.interaction.command('f5');
                this.f6 = _.com.mindforge.graphics.interaction.command('f6');
                this.f7 = _.com.mindforge.graphics.interaction.command('f7');
                this.f8 = _.com.mindforge.graphics.interaction.command('f8');
                this.f9 = _.com.mindforge.graphics.interaction.command('f9');
                this.f10 = _.com.mindforge.graphics.interaction.command('f10');
                this.f11 = _.com.mindforge.graphics.interaction.command('f11');
                this.f12 = _.com.mindforge.graphics.interaction.command('f12');
              }, {
                character: function (char) {
                  return _.com.mindforge.graphics.interaction.character$f(char);
                },
                specialWithCharacter: function (name, character) {
                  return _.com.mindforge.graphics.interaction.specialWithCharacter$f(name, character);
                }
              });
            },
            Mouse: function () {
              return Kotlin.createObject(null, function () {
                this.primary = _.com.mindforge.graphics.interaction.command('primary mouse button');
                this.secondary = _.com.mindforge.graphics.interaction.command('secondary mouse button');
                this.middle = _.com.mindforge.graphics.interaction.command('middle mouse button');
              });
            },
            Touch: function () {
              return Kotlin.createObject(null, function () {
                this.touch = _.com.mindforge.graphics.interaction.command('touch');
              });
            },
            Navigation: function () {
              return Kotlin.createObject(null, function () {
                this.left = _.com.mindforge.graphics.interaction.command('left');
                this.right = _.com.mindforge.graphics.interaction.command('right');
                this.up = _.com.mindforge.graphics.interaction.command('up');
                this.down = _.com.mindforge.graphics.interaction.command('down');
                this.home = _.com.mindforge.graphics.interaction.command('home');
                this.end = _.com.mindforge.graphics.interaction.command('end');
                this.pageUp = _.com.mindforge.graphics.interaction.command('page up');
                this.pageDown = _.com.mindforge.graphics.interaction.command('page down');
                this.backward = _.com.mindforge.graphics.interaction.command('backward');
                this.forward = _.com.mindforge.graphics.interaction.command('forward');
              });
            },
            Media: function () {
              return Kotlin.createObject(null, function () {
                this.playOrPause = _.com.mindforge.graphics.interaction.command('play or pause');
                this.increaseVolume = _.com.mindforge.graphics.interaction.command('increase volume');
                this.decreaseVolume = _.com.mindforge.graphics.interaction.command('decrease volume');
                this.mute = _.com.mindforge.graphics.interaction.command('mute');
              });
            },
            Power: function () {
              return Kotlin.createObject(null, function () {
                this.power = _.com.mindforge.graphics.interaction.command('power');
                this.sleep = _.com.mindforge.graphics.interaction.command('sleep');
                this.wake = _.com.mindforge.graphics.interaction.command('wake');
              });
            },
            Pointer: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.interaction.Pointer.prototype */ {
              transformed_npjzs3$: function (transform) {
                return _.com.mindforge.graphics.interaction.Pointer.transformed_npjzs3$f(this, transform);
              }
            }, /** @lends _.com.mindforge.graphics.interaction.Pointer */ {
              moved$f: function (this$) {
                return function (it) {
                  return this$;
                };
              },
              transformed_npjzs3$f: function (this$Pointer, transform) {
                return Kotlin.createObject(function () {
                  return [_.com.mindforge.graphics.interaction.Pointer];
                }, function () {
                  this.$moved_r5z1z9$ = _.com.mindforge.graphics.observable_2(this$Pointer.moved, _.com.mindforge.graphics.interaction.Pointer.moved$f(this));
                }, {
                  moved: {
                    get: function () {
                      return this.$moved_r5z1z9$;
                    }
                  },
                  location: {
                    get: function () {
                      return transform.inverse().invoke_rkhl8y$(this$Pointer.location);
                    }
                  }
                });
              }
            }),
            PointerKey: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.interaction.PointerKey.prototype */ {
              transformed_npjzs3$: function (transform) {
                return _.com.mindforge.graphics.interaction.pointerKey(this.pointer.transformed_npjzs3$(transform), this.key);
              }
            }),
            pointerKey: function (pointer, key) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.interaction.PointerKey];
              }, function () {
                this.$pointer_gbi88l$ = pointer;
                this.$key_wvli4z$ = key;
              }, {
                pointer: {
                  get: function () {
                    return this.$pointer_gbi88l$;
                  }
                },
                key: {
                  get: function () {
                    return this.$key_wvli4z$;
                  }
                }
              });
            },
            PointerKeys: Kotlin.createTrait(null),
            pressed$f: function (it) {
              return it.pressed;
            },
            pressed$f_0: function (pointer) {
              return function (it) {
                return _.com.mindforge.graphics.interaction.pointerKey(pointer, it);
              };
            },
            released$f: function (it) {
              return it.released;
            },
            released$f_0: function (pointer) {
              return function (it) {
                return _.com.mindforge.graphics.interaction.pointerKey(pointer, it);
              };
            },
            pointerKeys: function (pointer, keys) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.interaction.PointerKeys];
              }, function () {
                this.$pointer_p1vvzm$ = pointer;
                this.$keys_jocl19$ = keys;
                this.$pressed_nov0zx$ = _.com.mindforge.graphics.observable_3(Kotlin.modules['stdlib'].kotlin.map_m3yiqg$(keys, _.com.mindforge.graphics.interaction.pressed$f), _.com.mindforge.graphics.interaction.pressed$f_0(pointer));
                this.$released_c3ywtw$ = _.com.mindforge.graphics.observable_3(Kotlin.modules['stdlib'].kotlin.map_m3yiqg$(keys, _.com.mindforge.graphics.interaction.released$f), _.com.mindforge.graphics.interaction.released$f_0(pointer));
              }, {
                pointer: {
                  get: function () {
                    return this.$pointer_p1vvzm$;
                  }
                },
                keys: {
                  get: function () {
                    return this.$keys_jocl19$;
                  }
                },
                pressed: {
                  get: function () {
                    return this.$pressed_nov0zx$;
                  }
                },
                released: {
                  get: function () {
                    return this.$released_c3ywtw$;
                  }
                }
              });
            },
            Scroll: Kotlin.createTrait(null)
          }),
          math: Kotlin.definePackage(null, /** @lends _.com.mindforge.graphics.math */ {
            Ellipse: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.math.Shape];
            }, /** @lends _.com.mindforge.graphics.math.Ellipse.prototype */ {
              boundRectangle: {
                get: function () {
                  return _.com.mindforge.graphics.math.rectangle(this.size);
                }
              },
              contains_rkhl8y$: function (location) {
                var tmp$0;
                if (Kotlin.equals(this.boundRectangle.halfSize.x, 0))
                  tmp$0 = (Kotlin.equals(location.x, 0) && Math.abs(Kotlin.numberToDouble(location.y)) <= Kotlin.numberToDouble(this.boundRectangle.halfSize.y));
                else {
                  var transformedLocation = _.com.mindforge.graphics.vector(Kotlin.numberToDouble(location.x) / Kotlin.numberToDouble(this.boundRectangle.halfSize.x) * Kotlin.numberToDouble(this.boundRectangle.halfSize.y), Kotlin.numberToDouble(location.y));
                  return Kotlin.numberToDouble(transformedLocation.lengthSquared) <= Kotlin.numberToDouble(this.boundRectangle.halfSize.y) * Kotlin.numberToDouble(this.boundRectangle.halfSize.y);
                }
                return tmp$0;
              }
            }),
            ellipse: function (size) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.math.Ellipse];
              }, function () {
                this.$size_7ivfot$ = size;
              }, {
                size: {
                  get: function () {
                    return this.$size_7ivfot$;
                  }
                }
              });
            },
            Rectangle: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.math.Shape];
            }, /** @lends _.com.mindforge.graphics.math.Rectangle.prototype */ {
              halfSize: {
                get: function () {
                  return this.size.div_3p81yu$(2);
                }
              },
              contains_rkhl8y$: function (location) {
                var $receiver = location.minus_rkhl8y$(this.center);
                var f = _.com.mindforge.graphics.math.Rectangle.contains_rkhl8y$f(this);
                return f($receiver);
              },
              translated_rkhl8y$: function (offset) {
                return _.com.mindforge.graphics.math.rectangle(this.size, this.center.plus_rkhl8y$(offset));
              }
            }, /** @lends _.com.mindforge.graphics.math.Rectangle */ {
              contains_rkhl8y$f: function (this$Rectangle) {
                return function (it) {
                  var $receiver = Kotlin.modules['stdlib'].kotlin.listOf_9mqe4v$([Kotlin.modules['stdlib'].kotlin.to_l1ob02$(it.x, this$Rectangle.halfSize.x), Kotlin.modules['stdlib'].kotlin.to_l1ob02$(it.y, this$Rectangle.halfSize.y)]);
                  var tmp$0;
                  tmp$0 = $receiver.iterator();
                  while (tmp$0.hasNext()) {
                    var element = tmp$0.next();
                    if (!(Math.abs(Kotlin.numberToDouble(element.first)) <= Kotlin.numberToDouble(element.second))) {
                      return false;
                    }
                  }
                  return true;
                };
              }
            }),
            rectangle: function (size, center) {
              if (center === void 0)
                center = _.com.mindforge.graphics.zeroVector2;
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.math.Rectangle];
              }, function () {
                this.$size_70mneq$ = size;
                this.$center_3i238u$ = center;
              }, {
                size: {
                  get: function () {
                    return this.$size_70mneq$;
                  }
                },
                center: {
                  get: function () {
                    return this.$center_3i238u$;
                  }
                }
              });
            },
            rectangle_1: function (size, quadrant) {
              var $receiver = size.div_3p81yu$(2);
              var rectangle_1$f$result;
              rectangle_1$f$break: {
                if (quadrant === 1) {
                  rectangle_1$f$result = $receiver;
                  break rectangle_1$f$break;
                }
                 else if (quadrant === 2) {
                  rectangle_1$f$result = $receiver.mirrorX();
                  break rectangle_1$f$break;
                }
                 else if (quadrant === 3) {
                  rectangle_1$f$result = $receiver.minus();
                  break rectangle_1$f$break;
                }
                 else if (quadrant === 4) {
                  rectangle_1$f$result = $receiver.mirrorY();
                  break rectangle_1$f$break;
                }
                 else
                  throw new Kotlin.IllegalArgumentException('Quadrant must be 1, 2, 3 or 4.');
              }
              return _.com.mindforge.graphics.math.rectangle(size, rectangle_1$f$result);
            },
            Shape: Kotlin.createTrait(null, /** @lends _.com.mindforge.graphics.math.Shape.prototype */ {
              transformed_npjzs3$: function (transform) {
                return _.com.mindforge.graphics.math.Shape.transformed_npjzs3$f(this, transform);
              }
            }, /** @lends _.com.mindforge.graphics.math.Shape */ {
              transformed_npjzs3$f: function (this$Shape, transform) {
                return Kotlin.createObject(function () {
                  return [_.com.mindforge.graphics.math.TransformedShape];
                }, function () {
                  this.$original_b0adtp$ = this$Shape;
                  this.$transform_6sry9s$ = transform;
                }, {
                  original: {
                    get: function () {
                      return this.$original_b0adtp$;
                    }
                  },
                  transform: {
                    get: function () {
                      return this.$transform_6sry9s$;
                    }
                  }
                });
              }
            }),
            shape: function (contains) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.math.Shape];
              }, null, {
                contains_rkhl8y$: function (location) {
                  return contains(location);
                }
              });
            },
            concatenatedShape$f: function (shapes) {
              return function (location) {
                var tmp$0;
                tmp$0 = shapes.iterator();
                while (tmp$0.hasNext()) {
                  var element = tmp$0.next();
                  if (element.contains_rkhl8y$(location)) {
                    return true;
                  }
                }
                return false;
              };
            },
            concatenatedShape: function (shapes) {
              return _.com.mindforge.graphics.math.shape(_.com.mindforge.graphics.math.concatenatedShape$f(shapes));
            },
            TransformedShape: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.math.Shape];
            }, /** @lends _.com.mindforge.graphics.math.TransformedShape.prototype */ {
              contains_rkhl8y$: function (location) {
                return this.original.contains_rkhl8y$(this.transform.inverse().invoke_rkhl8y$(location));
              }
            }),
            Circle: Kotlin.createTrait(function () {
              return [_.com.mindforge.graphics.math.Ellipse];
            }, /** @lends _.com.mindforge.graphics.math.Circle.prototype */ {
              size: {
                get: function () {
                  return _.com.mindforge.graphics.vector(2 * Kotlin.numberToDouble(this.radius), 2 * Kotlin.numberToDouble(this.radius));
                }
              }
            }),
            circle: function (radius) {
              return Kotlin.createObject(function () {
                return [_.com.mindforge.graphics.math.Circle];
              }, function () {
                this.$radius_oma9y6$ = radius;
              }, {
                radius: {
                  get: function () {
                    return this.$radius_oma9y6$;
                  }
                }
              });
            }
          })
        })
      })
    })
  });
  Kotlin.defineModule('app', _);
  _.net.pureal.subnote.prototree.main([]);
}(Kotlin));

//@ sourceMappingURL=app.js.map
