package longevity.integration.subdomain.foreignKey

import longevity.subdomain.annotations.persistent

@persistent(
  keySet = Set(key(props.id)),
  indexSet = Set(index(props.associated)))
case class WithForeignKey(
  id: WithForeignKeyId,
  associated: AssociatedId)
