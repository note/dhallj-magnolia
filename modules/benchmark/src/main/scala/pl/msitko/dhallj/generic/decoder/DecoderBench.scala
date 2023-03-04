package pl.msitko.dhallj.generic.decoder

import org.dhallj.syntax._
import org.dhallj.codec.syntax._
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import java.util.concurrent.TimeUnit

object BenchmarkInput {

  val sumDecoder = pl.msitko.dhallj.generic.decoder.semiauto.deriveDecoder[TestSum]

  val dhallInput =
    "<Use : {} | Tip : {} | Poem : {} | Weigh : {} | Foreign : {} | Discussion : {} | Badly : {} | Left : {} | Bound : {} | Result : {} | Bus : {} | Bark : {} | Buy : {} | Fight : {} | Strip : {} | Experience : {} | Proper : {} | Was : {}>.Experience {=}"

  val parsed = dhallInput.parseExpr.getOrElse(throw new RuntimeException("can't be parsed as dhall")).normalize()
}

// benchmark3/Jmh/run -i 3 -wi 3 -f1 -t1 -prof jfr pl.msitko.dhallj.generic.decoder.DecoderBench.*
// benchmark3/Jmh/run -i 3 -wi 3 -f1 -t1 pl.msitko.dhallj.generic.decoder.DecoderBench.*
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
class DecoderBench {
  import BenchmarkInput.*

  @Benchmark
  def decode(bh: Blackhole): Unit =
    bh.consume(parsed.as[TestSum](sumDecoder))

}

sealed trait TestSum
case object Left       extends TestSum
case object Weigh      extends TestSum
case object Buy        extends TestSum
case object Was        extends TestSum
case object Strip      extends TestSum
case object Foreign    extends TestSum
case object Bark       extends TestSum
case object Result     extends TestSum
case object Discussion extends TestSum
case object Bus        extends TestSum
case object Proper     extends TestSum
case object Poem       extends TestSum
case object Use        extends TestSum
case object Bound      extends TestSum
case object Fight      extends TestSum
case object Badly      extends TestSum
case object Tip        extends TestSum
case object Experience extends TestSum
