package pl.msitko.dhallj.generic.debug

import org.dhallj.syntax._

object AstPrinter {

  def main(args: Array[String]): Unit = {
    printAst("""
               |let OnOrOff2 = < On: {} | Off: {} >
               |in OnOrOff2.Off {=}
               |""".stripMargin)

    printAst("""
               |let OnOrOff2 = < On: {} | Off: {} >
               |in OnOrOff2.Off {}
               |""".stripMargin)

    printAst("""
               |let OnOrOff2 = < On: {} | Off: {} >
               |in OnOrOff2.Off
               |""".stripMargin)
  }

  def javaCodeFromInput(in: String): String = {
    val expr = in.parseExpr.getOrElse(throw new RuntimeException("cannot parse")).normalize()
    org.dhallj.javagen.toJavaCode(expr, "example", "A")
  }

  def printAst(in: String): Unit =
    println(javaCodeFromInput(in))
}
