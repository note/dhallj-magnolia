package pl.msitko.dhallj.generic

import org.dhallj.core.{Expr, ExternalVisitor}

import java.lang.{Iterable => JIterable}
import java.util.{Map => JMap}

private[generic] object Extractors {

  def unionExtractor = new ExternalVisitor.Constant[Option[JMap[String, Expr]]](None) {

    override def onUnionType(fields: JIterable[JMap.Entry[String, Expr]], size: Int): Option[JMap[String, Expr]] = {
      val jmap = new java.util.HashMap[String, Expr]()
      fields.forEach(i => jmap.put(i.getKey, i.getValue))
      Some(jmap)
    }
  }

  def fastUnionExtractor = new ExternalVisitor.Constant[Option[Unit]](None) {

    override def onUnionType(fields: JIterable[JMap.Entry[String, Expr]], size: Int): Option[Unit] =
      Some(())
  }

  object IsUnionType {

    def unapply(expr: Expr): Option[Unit] =
      expr.accept(fastUnionExtractor).map(_ => ())
  }

}
