package pl.msitko.dhallj.generic.decoder

import org.dhallj.codec.Decoder
import org.dhallj.codec.syntax._
import org.dhallj.syntax._
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
import pl.msitko.dhallj.generic.example.akka.OnOrOff.Off
import pl.msitko.dhallj.generic.example.akka.{Akka, Http, OnOrOff, OnOrOff2, Preview, Server}

class SemiautoDeriveDecoderSpec extends munit.FunSuite with Fixtures with DecoderSpec {
  import semiauto.deriveDecoder

  implicit override lazy val appConfigDecoder: Decoder[AppConfig] = {
    implicit val endpointConfigDec = deriveDecoder[EndpointConfig]
    implicit val apiConfigDec      = deriveDecoder[ApiConfig]
    deriveDecoder[AppConfig]
  }
  implicit override lazy val dbConfigDecoder: Decoder[DbConfig] = deriveDecoder[DbConfig]
  implicit lazy val statusCodeDecoder: Decoder[StatusCode]      = deriveDecoder[StatusCode]
  implicit override lazy val errorDecoder: Decoder[Error]       = deriveDecoder[Error]

  implicit override lazy val errorsDecoder: Decoder[Errors] =
    deriveDecoder[Errors]
  implicit override lazy val onOrOffDecoder: Decoder[OnOrOff]   = deriveDecoder[OnOrOff]
  implicit override lazy val onOrOff2Decoder: Decoder[OnOrOff2] = deriveDecoder[OnOrOff2]

  implicit override lazy val akkaDecoder: Decoder[Akka] = {
    implicit val previewDec = deriveDecoder[Preview]
    implicit val serverDec  = deriveDecoder[Server]
    implicit val httpDec    = deriveDecoder[Http]
    deriveDecoder[Akka]
  }
  implicit override val offDecoder: Decoder[OnOrOff2.Off] = deriveDecoder[OnOrOff2.Off]

  // This test lives here because it doesn't work for scala 3 as Mirrors are not being generated for value classes
  // A related issue: https://github.com/softwaremill/magnolia/pull/435/file. It has been reverted in https://github.com/softwaremill/magnolia/pull/441
  test("Load value classes") {
    val decoded = "{ code = 404 }".decode[StatusCode]

    assertEquals(decoded, StatusCode(404))
  }

}
