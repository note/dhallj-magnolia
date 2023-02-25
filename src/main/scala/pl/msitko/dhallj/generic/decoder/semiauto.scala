package pl.msitko.dhallj.generic.decoder

import magnolia1._
import org.dhallj.codec.Decoder
import pl.msitko.dhallj.generic.GenericDecoder

object semiauto {
  type Typeclass[T] = Decoder[T]

  def join[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    GenericDecoder.join(caseClass)

  def split[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    GenericDecoder.split(sealedTrait)

  def deriveDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
