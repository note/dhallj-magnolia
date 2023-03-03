package pl.msitko.dhallj.generic.decoder

import org.dhallj.codec.{Decoder, Encoder}
import org.dhallj.codec.syntax._
import org.dhallj.syntax._
import pl.msitko.dhallj.generic.example.akka.OnOrOff.Off
import pl.msitko.dhallj.generic.example.akka._
import pl.msitko.dhallj.generic.example._

trait AutoDerivedDecoderSpecHelper {
  import auto._

  lazy val appConfigDecoder  = implicitly[Decoder[AppConfig]]
  lazy val dbConfigDecoder   = implicitly[Decoder[DbConfig]]
  lazy val statusCodeDecoder = implicitly[Decoder[StatusCode]]
  lazy val errorsDecoder     = implicitly[Decoder[Errors]]
  lazy val errorDecoder      = implicitly[Decoder[Error]]
  lazy val onOrOffDecoder    = implicitly[Decoder[OnOrOff]]
  lazy val akkaDecoder       = implicitly[Decoder[Akka]]
  lazy val offDecoder        = implicitly[Decoder[OnOrOff2.Off]]
  lazy val onOrOff2Decoder   = implicitly[Decoder[OnOrOff2]]

}

class AutoDeriveDecoderSpec extends munit.FunSuite with Fixtures with DecoderSpec with AutoDerivedDecoderSpecHelper {

  // This test lives here because it doesn't work for scala 3 as Mirrors are not being generated for value classes
  // A related issue: https://github.com/softwaremill/magnolia/pull/435/file. It has been reverted in https://github.com/softwaremill/magnolia/pull/441
  test("Load value classes") {
    import auto._
    val decoded = "{ code = 404 }".decode[StatusCode]

    assertEquals(decoded, StatusCode(404))
  }
}
