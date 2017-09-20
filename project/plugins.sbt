addSbtPlugin("com.jsuereth"     % "sbt-pgp"       % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site"      % "1.3.0")
addSbtPlugin("org.scoverage"    % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages"   % "0.6.2")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

resolvers += Resolver.url(
  "scoverage-bintray",
  url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(
  Resolver.ivyStylePatterns)
