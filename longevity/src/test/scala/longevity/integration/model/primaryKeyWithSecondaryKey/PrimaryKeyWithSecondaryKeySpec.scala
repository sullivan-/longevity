package longevity.integration.model.primaryKeyWithSecondaryKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyWithSecondaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

