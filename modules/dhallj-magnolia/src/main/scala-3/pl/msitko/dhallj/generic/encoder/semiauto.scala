package pl.msitko.dhallj.generic.encoder

import magnolia1.{CaseClass, Derivation, SealedTrait}
import org.dhallj.ast.{Application, FieldAccess, RecordLiteral, RecordType, UnionType}
import org.dhallj.codec.Encoder
import org.dhallj.core.Expr

import scala.deriving.Mirror

object semiauto:

  inline def deriveEncoder[T](using m: Mirror.Of[T]): Encoder[T] =
    val derivation = new Derivation[Encoder]:
      override type Typeclass[T] = Encoder[T]

      override def split[T](sealedTrait: SealedTrait[Encoder, T]): Encoder[T] =
        new Encoder[T]:
          override def encode(value: T, target: Option[Expr]): Expr =
            val subtype = sealedTrait.subtypes.find(_.cast.isDefinedAt(value)).get
            val casted  = subtype.cast(value)
            Application(
              FieldAccess(dhallType(Some(value), target), subtype.typeInfo.short),
              subtype.typeclass.encode(casted))

          override def dhallType(value: Option[T], target: Option[Expr]): Expr =
            UnionType(sealedTrait.subtypes.map { subtype =>
              subtype.typeInfo.short -> Some(subtype.typeclass.dhallType(None, target))
            }.toMap)

      override def join[T](caseClass: CaseClass[Encoder, T]): Encoder[T] = new Encoder[T]:
        override def encode(value: T, target: Option[Expr]): Expr =
          val m = caseClass.params.map { param =>
            param.label -> param.typeclass.encode(param.deref(value))
          }.toMap
          RecordLiteral(m)

        override def dhallType(value: Option[T], target: Option[Expr]): Expr =
          val m = caseClass.params.map { param =>
            param.label -> param.typeclass.dhallType(value.map(v => param.deref(v)), target)
          }.toMap
          RecordType(m)

    derivation.derived
  end deriveEncoder
