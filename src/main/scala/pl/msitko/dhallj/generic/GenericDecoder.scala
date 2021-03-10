package pl.msitko.dhallj.generic

import cats.Traverse
import cats.implicits.catsSyntaxEitherId
import cats.instances.either._
import cats.instances.list._
import magnolia.{CaseClass, SealedTrait}
import org.dhallj.ast._
import org.dhallj.codec.Decoder.Result
import org.dhallj.codec.{ACursor, Decoder, DecodingFailure, DownField, HCursor}
import org.dhallj.core.Expr

private[generic] object GenericDecoder {

  private[generic] def combine[T](caseClass: CaseClass[Decoder, T]): Decoder[T] = new Decoder[T] {

    private def decodeAs(c: HCursor): Either[DecodingFailure, T] =
      Traverse[List]
        .traverse(caseClass.parameters.toList) { param =>
          if (c.downField(param.label).succeeded) {
            param.typeclass.tryDecode(c.downField(param.label))
          } else {
            param.default match {
              case Some(default) => default.asRight
              case None =>
                new DecodingFailure(
                  "Attempt to decode value on failed cursor",
                  Some(c.expr),
                  DownField(param.label) :: c.history).asLeft
            }
          }
        }
        .map(ps => caseClass.rawConstruct(ps))

    override def decode(c: HCursor): Result[T] = c.expr match {
      case RecordLiteral(_) =>
        decodeAs(c)

      case FieldAccess(UnionType(_), _) =>
        decodeAs(c)

      case other =>
        DecodingFailure.failedTarget(caseClass.typeName.full, other, c.history).asLeft
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

  private[generic] def dispatch[T](sealedTrait: SealedTrait[Decoder, T]): Decoder[T] = new Decoder[T] {

    private def decodeAs(c: ACursor, subtypeName: String) =
      sealedTrait.subtypes.find(_.typeName.short == subtypeName) match {
        case Some(subtype) =>
          subtype.typeclass.tryDecode(c)
        case None =>
          c.focus match {
            case Some(expr) =>
              DecodingFailure
                .failedTarget(s"$subtypeName is not a known subtype of ${sealedTrait.typeName.full}", expr, c.history)
                .asLeft
            case None =>
              new DecodingFailure("Attempt to decode value on failed cursor", None, c.history).asLeft
          }

      }

    override def decode(c: HCursor): Result[T] = c.expr match {
      case Application(FieldAccess(UnionType(_), t), _) =>
        decodeAs(c.unionAlternative(t), t)
      case FieldAccess(UnionType(_), t) =>
        decodeAs(c.unionAlternative(t), t)
      case _ =>
        DecodingFailure.failedTarget("Is not a union", c.expr, c.history).asLeft
    }

    override def isValidType(typeExpr: Expr): Boolean = true

    override def isExactType(typeExpr: Expr): Boolean = false
  }
}
