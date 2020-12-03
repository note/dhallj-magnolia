package pl.msitko.dhallj.generic.decoder

import org.dhallj.codec.Decoder
import org.dhallj.codec.syntax._
import org.dhallj.syntax._
import pl.msitko.dhallj.generic.Fixtures
import pl.msitko.dhallj.generic.example.akka.OnOrOff.Off
import pl.msitko.dhallj.generic.example.akka._
import pl.msitko.dhallj.generic.example._

class AutoDeriveDecoderSpec extends munit.FunSuite with Fixtures {
  import auto._

  test("Load nested case classes") {
    val decoded =
      """
        |let topLevel = "com"
        |let somePort = 5000
        |in {
        |  db = {
        |    host = "host.${topLevel}",
        |    port = somePort + 432
        |  },
        |  api1 = {
        |    endpoint = {
        |      host = "some.host"
        |    }
        |  },
        |  api2 = {
        |    endpoint = {
        |      scheme = "https",
        |      host = "some.host2",
        |      -- Optionals are handled in non-consistent way currently:
        |      -- 1. To construct None you need to either omit field or set it to None explicitly
        |      -- 2. To construct Some you need to explicitly call Some
        |      path = Some "/",
        |      port = 8080
        |    }
        |  }
        |}
        |""".stripMargin.decode[AppConfig]

    assertEquals(decoded, someAppConfig)
  }

  test("Load value classes") {
    val decoded = "{ code = 404 }".decode[StatusCode]

    assertEquals(decoded, StatusCode(404))
  }

  test("Load sealed trait") {
    val decoded =
      """
        |let Error = < Error1 : { msg : Text } | Error2 : { code : Natural, code2 : Natural } >
        |in { errors = [Error.Error1 { msg = "abc"}, Error.Error2 { code = 123, code2 = 456 }] }
        |""".stripMargin.decode[Errors]

    assertEquals(decoded, Errors(List(Error1("abc"), Error2(code = 123, code2 = 456))))
  }

  test("Load union with empty parameter list as case object") {
    val decoded =
      """
        |let OnOrOff = < On: {} | Off: {} >
        |in { http = { server = { preview = { enableHttp2 = OnOrOff.Off {=} } } } }
        |""".stripMargin.decode[Akka]

    assertEquals(decoded, Akka(Http(Server(Preview(Off)))))
  }

  test("Load union without parameter list as case object") {
    val decoded =
      """
        |let OnOrOff = < On | Off >
        |in OnOrOff.Off
        |""".stripMargin.decode[OnOrOff]

    assertEquals(decoded, OnOrOff.Off)
  }

  test("Load union with empty parameter list as parameterless case class") {
    val decoded =
      """
        |let OnOrOff2 = < On: {} | Off: {} >
        |in OnOrOff2.Off {=}
        |""".stripMargin.decode[OnOrOff2]

    assertEquals(decoded, OnOrOff2.Off())
  }

  test("Works for FieldAccess only (should this fail?)") {
    val decoded =
      """
        |let OnOrOff2 = < On: {} | Off: {} >
        |in OnOrOff2.Off
        |""".stripMargin.decode[OnOrOff2]

    assertEquals(decoded, OnOrOff2.Off())
  }

  test("Load union without parameter list as parameterless case class") {
    val decoded =
      """
        |let OnOrOff2 = < On | Off >
        |in OnOrOff2.Off
        |""".stripMargin.decode[OnOrOff2]

    assertEquals(decoded, OnOrOff2.Off())
  }

  test("Decoding error should be comprehensible for deeply nested case classes".ignore) {
    val input =
      """
        |let OnOrOff = < On: {} | Off: {} >
        |in { http = { server = { preview = { enableHttp = OnOrOff.Off } } } }
        |""".stripMargin

    val parsed = input.parseExpr.getOr("Parsing failed").normalize()

    val decoded = parsed.as[Akka]

    val expectedMsg =
      "Missing field http.server.preview.[enableHttp2] when decoding org.dhallj.generic.example.akka.Preview"

    val errorMsg = decoded.left.map(_.getMessage)

    assert(errorMsg.left.map(_.contains(expectedMsg)).left.getOrElse(false))
  }

  implicit class DecodeString(s: String) {

    def decode[T: Decoder]: T =
      s.parseExpr.getOr("Parsing failed").normalize().as[T].getOr("Decoding failed")
  }

  implicit class EitherOps[L, R](v: Either[L, R]) {
    def getOr(clue: String): R = v.fold(l => fail(s"Unexpected Left when $clue: $l"), r => r)
  }
}
