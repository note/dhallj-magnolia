package pl.msitko.dhallj.generic

import org.dhallj.codec.{Decoder, Encoder}
import org.dhallj.codec.syntax._
import pl.msitko.dhallj.generic.example.AppConfig
//import org.dhallj.codec.Encoder._
import pl.msitko.dhallj.generic.GenericEncoder._
import pl.msitko.dhallj.generic.GenericDecoder._

//class RoundtripSpec extends munit.FunSuite with Fixtures {
//  test("Should roundtrip") {
//    val res = someAppConfig.asExpr
//
//  }
//
////  def roundtrip[T: Encoder: Decoder](in: T): T =
////    in.asExpr.normalize().as[T].getOrElse(fail(s"roundtrip failed for $in"))
//}
