package pl.msitko.dhallj.generic.encoder

import org.dhallj.codec.Encoder
import org.dhallj.codec.syntax._
import org.dhallj.core.Expr
import pl.msitko.dhallj.generic.encoder.semiauto.deriveEncoder
import pl.msitko.dhallj.generic.example.{
  ApiConfig,
  AppConfig,
  DbConfig,
  EndpointConfig,
  Error,
  Error1,
  Error2,
  Errors,
  Fixtures,
  StatusCode
}
import pl.msitko.dhallj.generic.example.akka.{Akka, Http, OnOrOff, OnOrOff2, Preview, Server}

class SemiautoDeriveEncoderSpec extends munit.FunSuite with Fixtures with EncoderSpec {
  import semiauto._

  implicit override val appConfigEncoder: Encoder[AppConfig] = {
    implicit val endpointConfigEnc = deriveEncoder[EndpointConfig]
    implicit val apiConfigEnc      = deriveEncoder[ApiConfig]
    implicit val dbConfigEnc       = deriveEncoder[DbConfig]
    deriveEncoder[AppConfig]
  }
  implicit val statusCodeEncoder: Encoder[StatusCode] = deriveEncoder[StatusCode]
  implicit override val errorEncoder: Encoder[Error]  = deriveEncoder[Error]

  implicit override val errorsEncoder: Encoder[Errors] =
    deriveEncoder[Errors]
  implicit override val onOrOffEncoder: Encoder[OnOrOff] = deriveEncoder[OnOrOff]

  implicit override val akkaEncoder: Encoder[Akka] = {
    implicit val previewEnc = deriveEncoder[Preview]
    implicit val serverEnc  = deriveEncoder[Server]
    implicit val httpEnc    = deriveEncoder[Http]
    deriveEncoder[Akka]
  }
  implicit override val offEncoder: Encoder[OnOrOff2.Off] = deriveEncoder[OnOrOff2.Off]

//  test("Work if the top level type has custom encoder") {
//    // BTW it wouldn't work if we don't `import org.dhallj.codec.Encoder._`
//    val typeExpr = dhallType[List[Error]]
//
//    assertEquals(
//      typeExpr.toString,
//      "List <Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>"
//    )
//  }

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
