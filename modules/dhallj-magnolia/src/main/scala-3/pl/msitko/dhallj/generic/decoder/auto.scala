package pl.msitko.dhallj.generic.decoder

import cats.Traverse
import cats.implicits.catsSyntaxEitherId
import magnolia1.*
import org.dhallj.ast.*
import org.dhallj.codec.{Decoder, DecodingFailure, Encoder}
import org.dhallj.codec.Decoder.Result
import org.dhallj.core.Expr
import scala.deriving.Mirror

object auto:

  inline given derived[T](using m: Mirror.Of[T]): Decoder[T] =
    pl.msitko.dhallj.generic.decoder.semiauto.deriveDecoder[T]

  export org.dhallj.codec.Decoder.{decodeList, decodeOption, decodeVector}
