package emblem
 
import scala.ScalaReflectionException
import scala.reflect.ClassTag
import scala.reflect.api.Mirror
import scala.reflect.api.TypeCreator
import scala.reflect.api.Universe
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.InstanceMirror
import scala.reflect.runtime.universe.ModuleMirror
import scala.reflect.runtime.universe.ModuleSymbol
import scala.reflect.runtime.universe.Symbol
import scala.reflect.runtime.universe.TermName
import scala.reflect.runtime.universe.TermSymbol
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeTag

/** generally useful utility functions for working with Scala reflection library */
package object reflectionUtil {

  // overloaded makeTypeTag follows FixedMirrorTypeCreator in
  // https://github.com/scala/scala/blob/2.11.x/src/reflect/scala/reflect/internal/StdCreators.scala

  /** makes a type tag for a term */
  def makeTypeTag[A](term: Symbol): TypeTag[A] = makeTypeTag(term.typeSignature)

  /** makes a type tag for a type */
  def makeTypeTag[A](tpe: Type): TypeTag[A] = {
    val typeCreator = new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]): U # Type =
        if (m eq currentMirror)
          tpe.asInstanceOf[U # Type]
        else
          throw new IllegalArgumentException(
            s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
    }
    TypeTag[A](currentMirror, typeCreator)
  }

  /** makes a class tag for a type tag */
  def typeTagToClassTag[T : TypeTag]: ClassTag[T] = {
    ClassTag[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe))
  }

  /** given an instance that contains an inner module, and the name of the
   * inner module, return the inner module instance wrapped in a `Some`. returns
   * `None` if there is no such inner module.
   *
   * @param container the instance that contains the inner module
   * @param moduleName the name of the inner module
   * @return the inner module instance, if it exists
   */
  def innerModule(container: Any, moduleName: String): Option[Any] = {
    val instanceMirror: InstanceMirror = currentMirror.reflect(container)
    if (instanceMirror.symbol.isStatic) {
      try {
        val symbol: ModuleSymbol = currentMirror.staticModule(s"${instanceMirror.symbol.fullName}.$moduleName")
        val mirror: ModuleMirror = currentMirror.reflectModule(symbol)
        Some(mirror.instance)
      } catch {
        case e: ScalaReflectionException => None
      }
    } else {
      val symbol: Symbol = instanceMirror.symbol.selfType.decl(TermName(s"$moduleName$$"))
      if (!symbol.isModule) {
        None
      }
      else {
        val mirror: ModuleMirror = instanceMirror.reflectModule(symbol.asModule)
        Some(mirror.instance)
      }
    }
  }

  /** given an instance and a type, returns a set of all the `val` or `var`
   * members of the instance that match the type.
   *
   * @tparam A the type of the terms we are searching for
   * @param instance the instance to search for terms with matching type
   */
  def termsWithType[A : TypeKey](instance: Any): Set[A] = {
    val instanceMirror: InstanceMirror = currentMirror.reflect(instance)
    val symbols: Set[Symbol] = instanceMirror.symbol.selfType.decls.toSet
    val termSymbols: Set[TermSymbol] = symbols.collect {
      case s if s.isTerm => s.asTerm
    }
    val valOrVarSymbols: Set[TermSymbol] = termSymbols.filter {
      s => s.isVal || s.isVar
    }
    val matchingSymbols: Set[TermSymbol] = valOrVarSymbols.filter { symbol =>
      val tpe: Type = symbol.typeSignature
      tpe <:< typeKey[A].tpe
    }
    matchingSymbols.map { symbol =>
      val fieldMirror = instanceMirror.reflectField(symbol)
      fieldMirror.get.asInstanceOf[A]
    }
  }

}
