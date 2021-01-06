# dhallj-magnolia

This library provides generic derivation of [dhallj](https://github.com/travisbrown/dhallj) `Encoder` and `Decoder` typeclasses. 

## Examples

Consider the following ADT:

```scala
sealed trait Bar
final case class Bar1(a: Int) extends Bar
final case class Bar2(b: String) extends Bar
final case class Foo(a: Int, b: String, bar: Bar)
```

### Automatic Encoder derivation

```scala
import org.dhallj.codec.syntax._
import pl.msitko.dhallj.generic.encoder.auto._

val input = Foo(12, "abc", Bar2("abcd"))
input.asExpr.toString
// """{a = 12, b = "abc", bar = (<Bar1 : {a : Natural} | Bar2 : {b : Text}>.Bar2) {b = "abcd"}}"""
```

### Automatic Decoder derivation

```scala
import org.dhallj.syntax._
import pl.msitko.dhallj.generic.decoder.auto._

val input = """{a = 12, b = "abc", bar = (<Bar1 : {a : Natural} | Bar2 : {b : Text}>.Bar2) {b = "abcd"}}"""

for {
  parsed  <- input.parseExpr
  decoded <- parsed.normalize().as[Foo]
} yield decoded
// Right(Foo(12,abc,Bar2(abcd)))
```

### Semi-automatic Encoder derivation

```scala
import org.dhallj.codec.syntax._
import pl.msitko.dhallj.generic.encoder.semiauto._

implicit val barEncoder = deriveEncoder[Bar]
implicit val fooEncoder = deriveEncoder[Foo]

val input = Foo(12, "abc", Bar2("abcd"))
input.asExpr.toString
// """{a = 12, b = "abc", bar = (<Bar1 : {a : Natural} | Bar2 : {b : Text}>.Bar2) {b = "abcd"}}"""
```

### Semi-automatic Decoder derivation

```scala
import org.dhallj.syntax._
import pl.msitko.dhallj.generic.decoder.semiauto._

implicit val barDecoder = deriveDecoder[Bar]
implicit val fooDecoder = deriveDecoder[Foo]

val input = """{a = 12, b = "abc", bar = (<Bar1 : {a : Natural} | Bar2 : {b : Text}>.Bar2) {b = "abcd"}}"""

for {
  parsed  <- input.parseExpr
  decoded <- parsed.normalize().as[Foo]
} yield decoded
// Right(Foo(12,abc,Bar2(abcd)))
```
