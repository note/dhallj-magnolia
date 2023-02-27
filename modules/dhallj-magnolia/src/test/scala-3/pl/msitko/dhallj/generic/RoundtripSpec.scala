package pl.msitko.dhallj.generic

import org.dhallj.codec.{Decoder, Encoder}
import org.dhallj.codec.syntax.*
import pl.msitko.dhallj.generic.example.{ApiConfig, AppConfig, DbConfig, EndpointConfig, Fixtures}

abstract class RoundtripSpec extends munit.FunSuite with Fixtures {

  def roundtrip[T: Encoder: Decoder](in: T): T =
    in.asExpr.normalize().as[T] match
      case Right(r)  => r
      case Left(err) => fail(s"roundtrip failed for $in with error: $err")
}

class AutoDeriveRoundtripSpec extends RoundtripSpec {

//  import org.dhallj.codec.Encoder.given
  import pl.msitko.dhallj.generic.decoder.auto.given
  import pl.msitko.dhallj.generic.encoder.auto.given

  test("Should roundtrip") {
    assertEquals(roundtrip(someAppConfig), someAppConfig)
  }
}
