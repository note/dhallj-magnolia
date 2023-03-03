package pl.msitko.dhallj.generic.encoder

import org.dhallj.ast.{NaturalLiteral, RecordLiteral}
import org.dhallj.codec.syntax._
import org.dhallj.codec.Encoder
import org.dhallj.codec.syntax._
import org.dhallj.core.Expr
import pl.msitko.dhallj.generic.example.akka._
import pl.msitko.dhallj.generic.example.{AppConfig, Error, Errors, Fixtures, StatusCode}

object AutoDerivedEncoderSpecHelper {
  import auto._

  lazy val appConfigEncoder  = implicitly[Encoder[AppConfig]]
  lazy val statusCodeEncoder = implicitly[Encoder[StatusCode]]
  lazy val errorsEncoder     = implicitly[Encoder[Errors]]
  lazy val errorEncoder      = implicitly[Encoder[Error]]
  lazy val onOrOffEncoder    = implicitly[Encoder[OnOrOff]]
  lazy val akkaEncoder       = implicitly[Encoder[Akka]]
  lazy val offEncoder        = implicitly[Encoder[OnOrOff2.Off]]

}

trait AutoDerivedEncoderSpecHelper2 { self: munit.FunSuite =>

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
}

class AutoDeriveEncoderSpec extends munit.FunSuite with Fixtures with EncoderSpec with AutoDerivedEncoderSpecHelper2 {
  import auto._

  implicit override lazy val appConfigEncoder: Encoder[AppConfig] = AutoDerivedEncoderSpecHelper.appConfigEncoder
  implicit override lazy val errorsEncoder: Encoder[Errors]       = AutoDerivedEncoderSpecHelper.errorsEncoder
  implicit override lazy val errorEncoder: Encoder[Error]         = AutoDerivedEncoderSpecHelper.errorEncoder
  implicit override lazy val onOrOffEncoder: Encoder[OnOrOff]     = AutoDerivedEncoderSpecHelper.onOrOffEncoder
  implicit override lazy val akkaEncoder: Encoder[Akka]           = AutoDerivedEncoderSpecHelper.akkaEncoder
  implicit override lazy val offEncoder: Encoder[OnOrOff2.Off]    = AutoDerivedEncoderSpecHelper.offEncoder

  // This test lives here because it doesn't work for scala 3 as Mirrors are not being generated for value classes
  // A related issue: https://github.com/softwaremill/magnolia/pull/435/file. It has been reverted in https://github.com/softwaremill/magnolia/pull/441
  test("Encode value class") {
    val res = encode(StatusCode(404))

    assertEquals(
      res,
      "{code = 404}"
    )
  }

}
