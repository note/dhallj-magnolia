package pl.msitko.dhallj.generic.decoder

import org.dhallj.codec.Decoder
import pl.msitko.dhallj.generic.encoder.{auto, AutoDerivedEncoderSpecHelper, EncoderSpec}
import pl.msitko.dhallj.generic.example.{AppConfig, DbConfig, Error, Errors, Fixtures, StatusCode}
import pl.msitko.dhallj.generic.example.akka.{Akka, OnOrOff, OnOrOff2}

trait AutoDerivedDecoderSpecHelper:
  self: munit.FunSuite =>

  import org.dhallj.codec.Decoder.given
  import pl.msitko.dhallj.generic.decoder.auto.given

  lazy val akkaDecoder      = summon[Decoder[Akka]]
  lazy val appConfigDecoder = summon[Decoder[AppConfig]]
  lazy val dbConfigDecoder  = summon[Decoder[DbConfig]]
  lazy val errorDecoder     = summon[Decoder[Error]]
  lazy val errorsDecoder    = summon[Decoder[Errors]]
  lazy val offDecoder       = summon[Decoder[OnOrOff2.Off]]
  lazy val onOrOffDecoder   = summon[Decoder[OnOrOff]]
  lazy val onOrOff2Decoder  = summon[Decoder[OnOrOff2]]

class AutoDeriveDecoderSpec extends munit.FunSuite with Fixtures with DecoderSpec with AutoDerivedDecoderSpecHelper

object Example1:
  import pl.msitko.dhallj.generic.decoder.auto.given

  lazy val dbConfig = summon[Decoder[DbConfig]]

object Example2:
  lazy val dbConfig = pl.msitko.dhallj.generic.decoder.semiauto.deriveDecoder[DbConfig]
