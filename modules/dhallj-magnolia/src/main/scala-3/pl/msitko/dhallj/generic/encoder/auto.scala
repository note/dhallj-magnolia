package pl.msitko.dhallj.generic.encoder

import magnolia1.*
import org.dhallj.ast.*
import org.dhallj.codec.Encoder
import org.dhallj.core.Expr

import scala.deriving.Mirror

object auto:

  inline given derived[T](using m: Mirror.Of[T]): Encoder[T] =
    pl.msitko.dhallj.generic.encoder.semiauto.deriveEncoder[T]

  export org.dhallj.codec.Encoder.{encodeList, encodeOption, encodeVector}
