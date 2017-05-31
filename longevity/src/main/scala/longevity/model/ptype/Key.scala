package longevity.model.ptype

import emblem.TypeKey
import emblem.emblematic.Emblem
import emblem.typeKey
import longevity.model.KVEv

/** a natural key for this persistent type. wraps a [[Prop property]] that,
 * given specific a property value, will match the corresponding member of no
 * more than one persistent object.
 * 
 * @tparam M the domain model
 * @tparam P the persistent type
 */
abstract class Key[M, P : TypeKey] private[model]() {

  /** the key value type */
  type V

  /** the property that defines the key */
  val prop: Prop[P, _] = keyValProp

  private[longevity] val keyValProp: Prop[P, V]
  private[longevity] val ev: KVEv[M, P, V]

  private[longevity] lazy val keyValTypeKey = keyValProp.propTypeKey
  private[model] lazy val keyValEmblem = Emblem(keyValTypeKey)

  override def toString = s"Key[${typeKey[P].name},${keyValTypeKey.name}]"

  override def hashCode = keyValProp.hashCode

  override def equals(that: Any) =
    that.isInstanceOf[Key[M, P]] && keyValProp == that.asInstanceOf[Key[M, P]].keyValProp

}
