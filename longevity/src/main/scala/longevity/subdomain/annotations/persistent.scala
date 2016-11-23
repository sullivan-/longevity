package longevity.subdomain.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.Index

// TODO unit tests
// TODO mos def a WIP

/** macro annotation to mark a class as a persistent component. creates a
 * companion object for the class that extends [[longevity.subdomain.PType
 * PType]]. if the class already has a companion object, then adds a parent
 * class `PType` to the existing companion object. Note that
 * this will not work if your companion object already extends an abstract
 * or concrete class, as `PType` itself is an abstract class. if this
 * happens, you will see a compiler error such as "class Foo needs to be a trait
 * to be mixed in".
 *
 * TODO describe params keys & indexes
 */
@compileTimeOnly("you must enable macro paradise for @persistent to work")
class persistent(keySet: Set[Key[_]] = null, indexSet: Set[Index[_]] = null) extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro persistent.impl

}

object persistent {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new PersistentImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class PersistentImpl {
    val c: Context
    val as: Seq[c.Tree]

    import c.universe._

    def impl = if (as.tail.isEmpty) {
      q"{ ${as.head} ; $newCompanion }"
    } else {
      q"{ ${as.head} ; $augmentedCompanion }"
    }

    private lazy val name = as.head match {
      case q"$_ class  $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case q"$_ trait  $typeName[..$_]           extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ => 
        c.error(
          c.enclosingPosition,
          s"@longevity.subdomain.persistent can only be applied to classes and traits")
        TermName("")
    }

    private lazy val termName = TermName(name.decodedName.toString)
    private lazy val typeName = TypeName(name.decodedName.toString)

    private def newCompanion =
      q"@longevity.subdomain.annotations.mprops object $termName extends $ptype { ..$keySet ; ..$indexSet }"

    private def ptype = tq"longevity.subdomain.PType[$typeName]"

    private def augmentedCompanion = {
      val q"$origMods object $n extends {..$eds} with ..$ps { $s => ..$ss }" = as.tail.head
      val q"$mpropsAnnMods object foo" = c.parse("@longevity.subdomain.annotations.mprops object foo")
      val mergedMods = Modifiers(
        origMods.flags,
        origMods.privateWithin,
        mpropsAnnMods.annotations.head :: origMods.annotations)
      q"""$mergedMods object $n extends {..$eds} with ..${ ptype +: ps.tail } {
            $s => ..$keySet ; ..$indexSet ; ..$ss
          }
       """
    }

    private lazy val (keySet, indexSet) = c.prefix.tree match {
      case q"new $persistent(keySet = $ks, indexSet = $is)" =>
        (q"override lazy val keySet = $ks", q"override lazy val indexSet = $is")
      case q"new $persistent(keySet = $ks)" =>
        (q"override lazy val keySet = $ks", EmptyTree)
      case q"new $persistent()" =>
        (EmptyTree, EmptyTree)
      case q"new $persistent(...$exprss)" =>
        val argString = exprss.map(es => s"(${es.mkString(",")})").mkString
        c.abort(
          c.enclosingPosition,
          s"@longevity.subdomain.persistent cannot take arguments $argString")
    }

  }

}
