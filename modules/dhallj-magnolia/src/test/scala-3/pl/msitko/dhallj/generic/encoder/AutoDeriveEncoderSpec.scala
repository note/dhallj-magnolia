package pl.msitko.dhallj.generic.encoder

import org.dhallj.ast.{NaturalLiteral, RecordLiteral}
import org.dhallj.codec.syntax.*
import org.dhallj.codec.{Decoder, Encoder}
import org.dhallj.codec.syntax.*
import org.dhallj.core.Expr
import pl.msitko.dhallj.generic.example.akka.*
import pl.msitko.dhallj.generic.example.{AppConfig, Error, Errors, Fixtures, StatusCode}

trait AutoDerivedEncoderSpecHelper:
  self: munit.FunSuite =>

//  import org.dhallj.codec.Encoder.given
  import auto.given

  lazy val akkaEncoder      = summon[Encoder[Akka]]
  lazy val appConfigEncoder = summon[Encoder[AppConfig]]
  lazy val errorEncoder     = summon[Encoder[Error]]
  lazy val errorsEncoder    = summon[Encoder[Errors]]
  lazy val offEncoder       = summon[Encoder[OnOrOff2.Off]]
  lazy val onOrOffEncoder   = summon[Encoder[OnOrOff]]

  test("Do not invoke magnolia and use custom instance if one is provided manually") {
    import auto._

    implicit val errorEncoder: Encoder[StatusCode] = new Encoder[StatusCode] {
      override def encode(value: StatusCode, target: Option[Expr]): Expr =
        RecordLiteral(Map("myStatusCode" -> NaturalLiteral(value.code).get))

      override def dhallType(value: Option[StatusCode], target: Option[Expr]): Expr = ???
    }

    val res = StatusCode(201).asExpr.toString

    assertEquals(
      res,
      """{myStatusCode = 201}"""
    )
  }

class AutoDeriveEncoderSpec extends munit.FunSuite with Fixtures with EncoderSpec with AutoDerivedEncoderSpecHelper
