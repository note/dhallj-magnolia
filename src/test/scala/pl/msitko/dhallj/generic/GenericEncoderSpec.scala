package pl.msitko.dhallj.generic

import org.dhallj.codec.Encoder
import org.dhallj.codec.syntax._
import org.dhallj.codec.Encoder._
import GenericEncoder._
import pl.msitko.dhallj.generic.example.akka.{Akka, Http, OnOrOff, OnOrOff2, Preview, Server}
import pl.msitko.dhallj.generic.example.{AppConfig, Error, Error1, Error2, Errors, StatusCode}

class GenericEncoderSpec extends munit.FunSuite with Fixtures {
  test("Encode case class") {
    val res = someAppConfig.asExpr.toString

    assertEquals(
      res,
      """{db = {host = "host.com", port = 5432}, api1 = {endpoint = {scheme = "http", host = "some.host", port = 80, path = None Text}}, api2 = {endpoint = {scheme = "https", host = "some.host2", port = 8080, path = Some "/"}}}""")
  }

  test("Generate dhall type for case class") {
    val typeExpr = Encoder[AppConfig].dhallType(None, None)

    assertEquals(
      typeExpr.toString,
      """{db : {host : Text, port : Natural}, api1 : {endpoint : {scheme : Text, host : Text, port : Natural, path : Optional Text}}, api2 : {endpoint : {scheme : Text, host : Text, port : Natural, path : Optional Text}}}""")
  }

  test("Encode value class") {
    val res = StatusCode(404).asExpr.toString

    assertEquals(
      res,
      "{code = 404}"
    )
  }

  test("Encode sealed trait") {
    val res = Errors(List(Error1("abc"), Error2(code = 123, code2 = 456))).asExpr.toString

    assertEquals(
      res,
      """{errors = [(<Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>.Error1) {msg = "abc"}, (<Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>.Error2) {code = 123, code2 = 456}]}""")
  }

  test("Generate dhall type for sealed traits") {
    val typeExpr = Encoder[Error].dhallType(None, None)

    assertEquals(
      typeExpr.toString,
      "<Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>"
    )
  }

  test("Work if the top level type has custom encoder") {
    // BTW it wouldn't work if we don't `import org.dhallj.codec.Encoder._`
    val typeExpr = Encoder[List[Error]].dhallType(None, None)

    assertEquals(
      typeExpr.toString,
      "List <Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>"
    )
  }

  test("Encode case object") {
    val res = (OnOrOff.Off: OnOrOff).asExpr.toString

    // Another encoding would be preferred: <Off | On>.Off
    // However, it cannot be achieved with magnolia and current Encoder API
    // While magnolia's `combine` has `CaseClass.isObject` which allows us to distinguish between case class and case object
    // it's too late in the invocation chain - we would need that ability in `dispatch`. Alternatively, if Encoder
    // has the info if it's being applied on top level expression we could work around magnolia limitations by
    // returning null for case objects if it appears in non top-level expression. That null could be handled in `dispatch` then
    assertEquals(res, "(<Off : {} | On : {}>.Off) {=}")
  }

  test("Encode case object within deeply nested hierarchy") {
    val res = Akka(Http(Server(Preview(OnOrOff.Off)))).asExpr.toString

    assertEquals(res, """{http = {server = {preview = {enableHttp2 = (<Off : {} | On : {}>.Off) {=}}}}}""")
  }

  test("Encode parameterless case class") {
    val res = OnOrOff2.Off().asExpr.toString

    assertEquals(res, "{=}")
  }
}
