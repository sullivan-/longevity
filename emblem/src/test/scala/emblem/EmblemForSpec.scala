package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[emblemFor emblemFor]] specifications */
class EmblemForSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "emblem.emblemFor"

  trait Foo extends HasEmblem

  it should "throw exception on non case class types" in {
    intercept[TypeIsNotCaseClassException[_]] {
      emblemFor[Foo]
    }
  }

  case class Bar(i: Int)(j: Int) extends HasEmblem

  it should "throw exception on case classes with multiple param lists" in {
    intercept[CaseClassHasMultipleParamListsException[_]] {
      emblemFor[Bar]
    }
  }

}
