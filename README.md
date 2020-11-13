# dhallj-magnolia

This library provides generic derivation of [dhallj](https://github.com/travisbrown/dhallj) `Encoder` and `Decoder` typeclasses. 

## Examples

Consider following ADT:

```
sealed trait Bar
final case class Bar1(a: Int) extends Bar
final case class Bar2(b: String) extends Bar
final case class Foo(a: Int, b: String, bar: Bar)
```

### Derive Encoder

```
import org.dhallj.codec.syntax._
import pl.msitko.dhallj.generic.GenericEncoder._

val input = Foo(12, "abc", Bar2("abcd"))
val out = input.asExpr.toString
```

### Derive Decoder

```
import org.dhallj.syntax._
import pl.msitko.dhallj.generic.GenericDecoder._

val input = """{a = 12, b = "abc", bar = (<Bar1 : {a : Natural} | Bar2 : {b : Text}>.Bar2) {b = "abcd"}}"""

for {
  parsed  <- input.parseExpr
  decoded <- parsed.normalize().as[Foo]
} yield decoded
```
