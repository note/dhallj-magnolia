package pl.msitko.dhallj.generic

import magnolia.{CaseClass, Magnolia, SealedTrait}
import org.dhallj.codec.Encoder
import org.dhallj.core.Expr
import org.dhallj.ast._

object GenericEncoder {
  type Typeclass[T] = Encoder[T]

  def combine[T](caseClass: CaseClass[Encoder, T]): Encoder[T] = new Encoder[T] {

    override def encode(value: T, target: Option[Expr]): Expr =
      RecordLiteral(
        caseClass.parameters.map { param =>
          param.label -> param.typeclass.encode(param.dereference(value))
        }.toMap
      )

    override def dhallType(value: Option[T], target: Option[Expr]): Expr =
      RecordType(
        caseClass.parameters.map { param =>
          param.label -> param.typeclass.dhallType(value.map(v => param.dereference(v)), target)
        }.toMap
      )
  }

  def dispatch[T](sealedTrait: SealedTrait[Encoder, T]): Encoder[T] = new Encoder[T] {

    override def encode(value: T, target: Option[Expr]): Expr = {
      val subtype = sealedTrait.subtypes.find(_.cast.isDefinedAt(value)).get
      val casted  = subtype.cast(value)
      Application(FieldAccess(dhallType(Some(value), target), subtype.typeName.short), subtype.typeclass.encode(casted))
    }

    override def dhallType(value: Option[T], target: Option[Expr]): Expr =
      UnionType(sealedTrait.subtypes.map { subtype =>
        subtype.typeName.short -> Some(subtype.typeclass.dhallType(None, target))
      }.toMap)
  }

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}
