package pl.msitko.dhallj.generic

// Workaround for issues with magnolia trying to derive instances having custom encoder/decoders
// Read more about that workaround here: https://github.com/propensive/magnolia/issues/107#issuecomment-589289260
case class Exported[A](instance: A) extends AnyVal

//import scala.language.higherKinds
import magnolia1._
import scala.reflect.macros.whitebox

object ExportedMagnolia {

  def exportedMagnolia[TC[_], A: c.WeakTypeTag](c: whitebox.Context): c.Expr[Exported[TC[A]]] = {
    val magnoliaTree = c.Expr[TC[A]](Magnolia.gen[A](c))
    c.universe.reify(Exported(magnoliaTree.splice))
  }
}
