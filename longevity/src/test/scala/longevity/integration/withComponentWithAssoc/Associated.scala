package longevity.integration.withComponentWithAssoc

import longevity.subdomain._

case class Associated(uri: String) extends RootEntity

object Associated extends RootEntityType[Associated]
