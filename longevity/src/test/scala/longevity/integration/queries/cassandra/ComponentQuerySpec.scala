package longevity.integration.queries.cassandra

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.cassandra.CompoundPropInOrderingQuery
import longevity.test.QuerySpec
import longevity.integration.model.component._

class ComponentQuerySpec extends QuerySpec[Blocking, DomainModel, WithComponent](
  new LongevityContext(TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val componentProp = WithComponent.props.component

  import WithComponent.queryDsl._

  behavior of "CassandraPRepo.queryToVector"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(componentProp eqs sample.component)
  }

  it should "throw exception for an ordering query on a component" in {
    intercept[CompoundPropInOrderingQuery] {
      effect.run(repo.queryToVector(
        componentProp lt sample.component
      ))
    }
  }

}
