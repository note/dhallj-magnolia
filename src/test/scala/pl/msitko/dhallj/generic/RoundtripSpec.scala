package pl.msitko.dhallj.generic

import org.dhallj.codec.{Decoder, Encoder}
import org.dhallj.codec.syntax._
import pl.msitko.dhallj.generic.example.{ApiConfig, AppConfig, DbConfig, EndpointConfig}

abstract class RoundtripSpec extends munit.FunSuite with Fixtures {

  def roundtrip[T: Encoder: Decoder](in: T): T =
    in.asExpr.normalize().as[T].getOrElse(fail(s"roundtrip failed for $in"))
}

class AutoDeriveRoundtripSpec extends RoundtripSpec {
  import pl.msitko.dhallj.generic.decoder.auto._
  import pl.msitko.dhallj.generic.encoder.auto._

  test("Should roundtrip") {
    assertEquals(roundtrip(someAppConfig), someAppConfig)
  }
}

class SemiautoDeriveRoundtripSpec extends RoundtripSpec {
  import pl.msitko.dhallj.generic.decoder.semiauto._
  import pl.msitko.dhallj.generic.encoder.semiauto._

  implicit val appConfigDec = {
    implicit val endpointConfigDec = deriveDecoder[EndpointConfig]
    implicit val apiConfigDec      = deriveDecoder[ApiConfig]
    implicit val dbConfigDec       = deriveDecoder[DbConfig]
    deriveDecoder[AppConfig]
  }

  implicit val appConfigEnc = {
    implicit val endpointConfigEnc = deriveEncoder[EndpointConfig]
    implicit val apiConfigEnc      = deriveEncoder[ApiConfig]
    implicit val dbConfigEnc       = deriveEncoder[DbConfig]
    deriveEncoder[AppConfig]
  }

  test("Should roundtrip") {
    assertEquals(roundtrip(someAppConfig), someAppConfig)
  }
}
