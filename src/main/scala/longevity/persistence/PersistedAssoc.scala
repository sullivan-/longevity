package longevity.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.Persistent
import longevity.exceptions.subdomain.AssocIsPersistedException

/** an [[longevity.subdomain.Assoc Assoc]] to a root that has been persisted */
private[longevity] trait PersistedAssoc[P <: Persistent] extends Assoc[P] {
  def isPersisted = true
  def unpersisted = throw new AssocIsPersistedException(this)
}
