package longevity.integration.subdomain.withSimpleConstraint

import longevity.subdomain._

case class WithSimpleConstraint(
  id: String,
  primaryEmail: Email,
  emails: Set[Email])
extends RootEntity

object WithSimpleConstraint extends RootEntityType[WithSimpleConstraint] {
  key("id")
}

