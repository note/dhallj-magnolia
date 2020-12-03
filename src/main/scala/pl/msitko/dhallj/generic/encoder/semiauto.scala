package pl.msitko.dhallj.generic.encoder

import magnolia.{CaseClass, Magnolia, SealedTrait}
import org.dhallj.codec.Encoder
import pl.msitko.dhallj.generic.GenericEncoder

object semiauto {
  type Typeclass[T] = Encoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    GenericEncoder.combine(caseClass)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    GenericEncoder.dispatch(sealedTrait)

  def deriveEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
