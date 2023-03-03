package pl.msitko.dhallj.generic.encoder

import magnolia1._
import org.dhallj.codec.Encoder
import pl.msitko.dhallj.generic.{Exported, ExportedMagnolia, GenericEncoder}

object auto extends ExportedEncoder {
  type Typeclass[T] = Encoder[T]

  def join[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    GenericEncoder.join(caseClass)

  def split[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    GenericEncoder.split(sealedTrait)

  implicit def deriveEncoder[T]: Exported[Encoder[T]] = macro ExportedMagnolia.exportedMagnolia[Encoder, T]

  // Those are definedin an object in dhallj and therefore cannot be brought by inheritance
  // TODO: Open a PR in dhallj
  implicit val encodeLong: Encoder[Long]                    = Encoder.encodeLong
  implicit val encodeInt: Encoder[Int]                      = Encoder.encodeInt
  implicit val encodeBigInt: Encoder[BigInt]                = Encoder.encodeBigInt
  implicit val encodeDouble: Encoder[Double]                = Encoder.encodeDouble
  implicit val encodeString: Encoder[String]                = Encoder.encodeString
  implicit val encodeBoolean: Encoder[Boolean]              = Encoder.encodeBoolean
  implicit def encodeOption[A: Encoder]: Encoder[Option[A]] = Encoder.encodeOption
  implicit def encodeVector[A: Encoder]: Encoder[Vector[A]] = Encoder.encodeVector
  implicit def encodeList[A: Encoder]: Encoder[List[A]]     = Encoder.encodeList
}

trait ExportedEncoder {
  implicit def exportedEncoder[A](implicit exported: Exported[Encoder[A]]): Encoder[A] = exported.instance
}
