package longevity.integration.subdomain.keyWithComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
