package pl.msitko.dhallj.generic.decoder

import magnolia.{CaseClass, SealedTrait}
import org.dhallj.codec.Decoder
import pl.msitko.dhallj.generic.{Exported, ExportedMagnolia, GenericDecoder}

object auto extends ExportedDecoder {
  type Typeclass[T] = Decoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    GenericDecoder.combine(caseClass)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    GenericDecoder.dispatch(sealedTrait)

  implicit def deriveDecoder[T]: Exported[Decoder[T]] = macro ExportedMagnolia.exportedMagnolia[Decoder, T]

  // Those are definedin an object in dhallj and therefore cannot be brought by inheritance
  // TODO: Open a PR in dhallj
  implicit val decodeLong: Decoder[Long]                    = Decoder.decodeLong
  implicit val decodeInt: Decoder[Int]                      = Decoder.decodeInt
  implicit val decodeBigInt: Decoder[BigInt]                = Decoder.decodeBigInt
  implicit val decodeDouble: Decoder[Double]                = Decoder.decodeDouble
  implicit val decodeString: Decoder[String]                = Decoder.decodeString
  implicit val decodeBoolean: Decoder[Boolean]              = Decoder.decodeBoolean
  implicit def decodeOption[A: Decoder]: Decoder[Option[A]] = Decoder.decodeOption
  implicit def decodeVector[A: Decoder]: Decoder[Vector[A]] = Decoder.decodeVector
  implicit def decodeList[A: Decoder]: Decoder[List[A]]     = Decoder.decodeList
}

trait ExportedDecoder {
  implicit def exportedDecoder[A](implicit exported: Exported[Decoder[A]]): Decoder[A] = exported.instance
}
