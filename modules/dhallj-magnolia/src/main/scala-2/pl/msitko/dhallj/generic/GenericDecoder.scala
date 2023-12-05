package pl.msitko.dhallj.generic

import cats.Traverse
import cats.implicits.catsSyntaxEitherId
import cats.instances.either._
import cats.instances.list._
import magnolia1._
import org.dhallj.ast._
import org.dhallj.codec.Decoder.Result
import org.dhallj.codec.{Decoder, DecodingFailure}
import org.dhallj.core.Expr
import pl.msitko.dhallj.generic.decoder.MissingRecordField

private[generic] object GenericDecoder {

  private[generic] def join[T](caseClass: CaseClass[Decoder, T]): Decoder[T] = new Decoder[T] {

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

    override def isValidType(typeExpr: Expr): Boolean = typeExpr match {
      case RecordType(recordMap) =>
        caseClass.parameters.toList.forall { param =>
          recordMap.get(param.label).exists(param.typeclass.isValidType)
        }
      case _ =>
        false
    }

    override def isExactType(typeExpr: Expr): Boolean = false
  }

  private[generic] def split[T](sealedTrait: SealedTrait[Decoder, T]): Decoder[T] = new Decoder[T] {

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
      case unexpected =>
        new DecodingFailure(s"${unexpected} is not a union", expr).asLeft
    }

    override def isValidType(typeExpr: Expr): Boolean = true

    override def isExactType(typeExpr: Expr): Boolean = false
  }
}
