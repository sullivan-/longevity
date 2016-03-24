package longevity.integration.subdomain.withAssocOption

import longevity.subdomain._

case class WithAssocOption(
  uri: String,
  associated: Option[Assoc[Associated]])
extends Root

object WithAssocOption extends RootType[WithAssocOption] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
