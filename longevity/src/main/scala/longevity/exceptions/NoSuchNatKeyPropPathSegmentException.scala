package longevity.exceptions

import emblem.TypeKey
import emblem.exceptions.NoSuchPropertyException

class NoSuchNatKeyPropPathSegmentException(
  val propName: String,
  val path: String,
  val rootTypeKey: TypeKey[_],
  e: NoSuchPropertyException)
extends InvalidNatKeyPropPathException(
  s"path segment $propName does not specify a property in path $path for root ${rootTypeKey.name}",
  e)

