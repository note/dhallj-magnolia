package pl.msitko.dhallj.generic.encoder

import org.dhallj.codec.Encoder
import org.dhallj.codec.syntax._
import org.dhallj.core.Expr
import pl.msitko.dhallj.generic.example.akka._
import pl.msitko.dhallj.generic.example.{AppConfig, Error, Error1, Error2, Errors, Fixtures, StatusCode}

trait EncoderSpec { self: munit.FunSuite with Fixtures =>
  implicit def akkaEncoder: Encoder[Akka]
  implicit def appConfigEncoder: Encoder[AppConfig]
  implicit def errorEncoder: Encoder[Error]
  implicit def errorsEncoder: Encoder[Errors]
  implicit def offEncoder: Encoder[OnOrOff2.Off]
  implicit def onOrOffEncoder: Encoder[OnOrOff]

  test("Encode case class") {
    val res = encode(someAppConfig)

    assertEquals(
      res,
      """{db = {host = "host.com", port = 5432}, api1 = {endpoint = {scheme = "http", host = "some.host", port = 80, path = None Text}}, api2 = {endpoint = {scheme = "https", host = "some.host2", port = 8080, path = Some "/"}}}""")
  }

  test("Generate dhall type for case class") {
    val res = dhallType[AppConfig]

    assertEquals(
      res,
      """{db : {host : Text, port : Natural}, api1 : {endpoint : {scheme : Text, host : Text, port : Natural, path : Optional Text}}, api2 : {endpoint : {scheme : Text, host : Text, port : Natural, path : Optional Text}}}""")
  }

  test("Encode sealed trait") {
    val res = encode(Errors(List(Error1("abc"), Error2(code = 123, code2 = 456))))

    assertEquals(
      res,
      """{errors = [<Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>.Error1 {msg = "abc"}, <Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>.Error2 {code = 123, code2 = 456}]}""")
  }

  test("Generate dhall type for sealed traits") {
    val typeExpr = dhallType[Error]

    assertEquals(
      typeExpr,
      "<Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>"
    )
  }

  // It wasn't working without ExportedMagnolia trick (see https://github.com/propensive/magnolia/issues/107#issuecomment-589289260)
  // The point is that it should use custom decoder defined in dhallj and not try to invoke magnolia derivation
  test("Work if the top level type has custom encoder") {
    val typeExpr = dhallType[List[Error]]

    assertEquals(
      typeExpr,
      "List <Error1 : {msg : Text} | Error2 : {code : Natural, code2 : Natural}>"
    )
  }

  test("Encode case object") {
    val res = encode(OnOrOff.Off: OnOrOff)

    // Another encoding would be preferred: <Off | On>.Off
    // However, it cannot be achieved with magnolia and current Encoder API
    // While magnolia's `join` has `CaseClass.isObject` which allows us to distinguish between case class and case object
    // it's too late in the invocation chain - we would need that ability in `split`. Alternatively, if Encoder
    // has the info if it's being applied on top level expression we could work around magnolia limitations by
    // returning null for case objects if it appears in non top-level expression. That null could be handled in `split` then
    assertEquals(res, "<Off : {} | On : {}>.Off {=}")
  }

  test("Encode case object within deeply nested hierarchy") {
    val res = encode(Akka(Http(Server(Preview(OnOrOff.Off)))))

    assertEquals(res, """{http = {server = {preview = {enableHttp2 = <Off : {} | On : {}>.Off {=}}}}}""")
  }

  test("Encode parameterless case class") {
    val res = encode(OnOrOff2.Off())

    assertEquals(res, "{=}")
  }

  def encode[T: Encoder](in: T): String =
    in.asExpr.toString

  def dhallType[T: Encoder]: String =
    Encoder[T].dhallType(None, None).toString

}
