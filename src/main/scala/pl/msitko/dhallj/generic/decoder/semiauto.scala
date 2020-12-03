package pl.msitko.dhallj.generic.decoder

import magnolia.{CaseClass, Magnolia, SealedTrait}
import org.dhallj.codec.Decoder
import pl.msitko.dhallj.generic.GenericDecoder

object semiauto {
  type Typeclass[T] = Decoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    GenericDecoder.combine(caseClass)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    GenericDecoder.dispatch(sealedTrait)

  def deriveDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
