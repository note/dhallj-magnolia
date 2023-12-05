package pl.msitko.dhallj.generic.decoder

import cats.Traverse
import cats.implicits.catsSyntaxEitherId
import magnolia1.*
import org.dhallj.ast.*
import org.dhallj.codec.{Decoder, DecodingFailure, Encoder}
import org.dhallj.codec.Decoder.Result
import org.dhallj.core.Expr
import scala.deriving.Mirror

object semiauto:

  inline def deriveDecoder[T](using m: Mirror.Of[T]): Decoder[T] =
    val derivation = new Derivation[Decoder]:
      override type Typeclass[T] = Decoder[T]

      override def split[T](sealedTrait: SealedTrait[Decoder, T]): Decoder[T] =
        new Decoder[T]:
          private def decodeAs(expr: Expr, subtypeName: String) =
            sealedTrait.subtypes.find(_.typeInfo.short == subtypeName) match
              case Some(subtype) =>
                subtype.typeclass.decode(expr)
              case None =>
                DecodingFailure(s"$subtypeName is not a known subtype of ${sealedTrait.typeInfo.full}", expr).asLeft

          override def decode(expr: Expr): Result[T] = expr match
            case Application(FieldAccess(UnionType(_), t), arg) =>
              decodeAs(arg, t)
            case FieldAccess(UnionType(_), t) =>
              decodeAs(expr, t)
            case unexpected =>
              DecodingFailure(s"${unexpected} is not a union", expr).asLeft

          override def isValidType(typeExpr: Expr): Boolean = true

          override def isExactType(typeExpr: Expr): Boolean = false

      override def join[T](caseClass: CaseClass[Decoder, T]): Decoder[T] =
        new Decoder[T]:
          private def decodeAs(expr: Expr, recordMap: Map[String, Expr]): Result[T] =
            Traverse[List]
              .traverse(caseClass.parameters.toList) { param =>
                recordMap.get(param.label) match
                  case Some(expr) =>
                    param.typeclass.decode(expr)
                  case None =>
                    param.default match
                      case Some(default) => Right(default)
                      case None =>
                        Left(MissingRecordField(caseClass.typeInfo.full, param.label, expr))
              }
              .map(ps => caseClass.rawConstruct(ps))

          override def decode(expr: Expr): Result[T] = expr match
            case RecordLiteral(recordMap) =>
              decodeAs(expr, recordMap)
            case FieldAccess(UnionType(_), _) =>
              decodeAs(expr, Map.empty)
            case other =>
              Left(DecodingFailure(caseClass.typeInfo.full, other))

          override def isValidType(typeExpr: Expr): Boolean = typeExpr match
            case RecordType(recordMap) =>
              caseClass.parameters.toList.forall { param =>
                recordMap.get(param.label).exists(param.typeclass.isValidType)
              }
            case _ =>
              false

          override def isExactType(typeExpr: Expr): Boolean = false

    derivation.derived
  end deriveDecoder
