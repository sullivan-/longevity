package longevity.integration.subdomain.complexConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ComplexConstraintSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
