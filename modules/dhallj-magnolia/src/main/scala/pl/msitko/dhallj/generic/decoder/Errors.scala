package pl.msitko.dhallj.generic.decoder

import org.dhallj.codec.DecodingFailure
import org.dhallj.core.Expr

final case class MissingRecordField(override val target: String, missingFieldName: String, override val value: Expr)
    extends DecodingFailure(target, value) {
  override def toString: String = s"Missing record field '$missingFieldName' when decoding $target"
}
