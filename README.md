# dhallj-magnolia

This library provides generic derivation of [dhallj](https://github.com/travisbrown/dhallj) `Encoder` and `Decoder` typeclasses. 

## Examples

Consider following ADT:

```
final case class Foo(a: Int, b: String, bar: Bar)
sealed trait Bar
final case class Bar1(a: Int)
final case class Bar2(b: String)
```

### Derive Encoder

```


val input = Foo(12, "abc", Bar2("abcd"))


```

### Derive Decoder

```
```
