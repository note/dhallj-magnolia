package pl.msitko.dhallj.generic.encoder

import magnolia1._
import org.dhallj.codec.Encoder
import pl.msitko.dhallj.generic.GenericEncoder

object semiauto {
  type Typeclass[T] = Encoder[T]

  def join[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    GenericEncoder.join(caseClass)

  def split[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    GenericEncoder.split(sealedTrait)

  def deriveEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
