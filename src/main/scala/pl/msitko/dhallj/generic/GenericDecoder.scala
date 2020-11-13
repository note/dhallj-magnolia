package pl.msitko.dhallj.generic

import magnolia.{CaseClass, Magnolia, SealedTrait}
import cats.Traverse
import cats.implicits.catsSyntaxEitherId
import org.dhallj.ast._
import org.dhallj.codec.{Decoder, DecodingFailure}
import org.dhallj.core.Expr
import org.dhallj.codec.Decoder.Result
import cats.instances.list._
import cats.instances.either._

final case class MissingRecordField(override val target: String, missingFieldName: String, override val value: Expr)
    extends DecodingFailure(target, value) {
  override def toString: String = s"Missing record field '$missingFieldName' when decoding $target"
}

object GenericDecoder {
  type Typeclass[T] = Decoder[T]

  def combine[T](caseClass: CaseClass[Decoder, T]): Decoder[T] = new Decoder[T] {

    private def decodeAs(expr: Expr, recordMap: Map[String, Expr]) =
      Traverse[List]
        .traverse(caseClass.parameters.toList) { param =>
          recordMap.get(param.label) match {
            case Some(expr) =>
              param.typeclass.decode(expr)
            case None =>
              param.default match {
                case Some(default) => Right(default)
                case None =>
                  Left(MissingRecordField(caseClass.typeName.full, param.label, expr))
              }
          }
        }
        .map(ps => caseClass.rawConstruct(ps))

    override def decode(expr: Expr): Result[T] = expr match {
      case RecordLiteral(recordMap) =>
        decodeAs(expr, recordMap)

      case FieldAccess(UnionType(_), _) =>
        decodeAs(expr, Map.empty)

      case other =>
        Left(new DecodingFailure(caseClass.typeName.full, other))
    }

    override def isValidType(typeExpr: Expr): Boolean = ???

    override def isExactType(typeExpr: Expr): Boolean = ???
  }

  def dispatch[T](sealedTrait: SealedTrait[Decoder, T]): Decoder[T] = new Decoder[T] {

    private def decodeAs(expr: Expr, subtypeName: String) =
      sealedTrait.subtypes.find(_.typeName.short == subtypeName) match {
        case Some(subtype) =>
          subtype.typeclass.decode(expr)
        case None =>
          new DecodingFailure(s"$subtypeName is not a known subtype of ${sealedTrait.typeName.full}", expr).asLeft
      }

    override def decode(expr: Expr): Result[T] = expr match {
      case Application(FieldAccess(UnionType(_), t), arg) =>
        decodeAs(arg, t)
      case FieldAccess(UnionType(_), t) =>
        decodeAs(expr, t)
      case _ =>
        new DecodingFailure("Is not a union", expr).asLeft
    }

    override def isValidType(typeExpr: Expr): Boolean = ???

    override def isExactType(typeExpr: Expr): Boolean = ???
  }

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]
}
