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

class SemiautoDerivedDecoderSpec extends munit.FunSuite with Fixtures with DecoderSpec:
  import semiauto.deriveDecoder

  implicit override lazy val appConfigDecoder: Decoder[AppConfig] = {
    given endpointConfigDec: Decoder[EndpointConfig] = deriveDecoder[EndpointConfig]
    given apiConfigDec: Decoder[ApiConfig]           = deriveDecoder[ApiConfig]
    deriveDecoder[AppConfig]
  }
  implicit override lazy val dbConfigDecoder: Decoder[DbConfig] = deriveDecoder[DbConfig]
  implicit override lazy val errorDecoder: Decoder[Error]       = deriveDecoder[Error]

  implicit override lazy val errorsDecoder: Decoder[Errors] =
    deriveDecoder[Errors]
  implicit override lazy val onOrOffDecoder: Decoder[OnOrOff]   = deriveDecoder[OnOrOff]
  implicit override lazy val onOrOff2Decoder: Decoder[OnOrOff2] = deriveDecoder[OnOrOff2]

  implicit override lazy val akkaDecoder: Decoder[Akka] = {
    given previewDec: Decoder[Preview] = deriveDecoder[Preview]
    given serverDec: Decoder[Server]   = deriveDecoder[Server]
    given httpDec: Decoder[Http]       = deriveDecoder[Http]
    deriveDecoder[Akka]
  }
  implicit override val offDecoder: Decoder[OnOrOff2.Off] = deriveDecoder[OnOrOff2.Off]
