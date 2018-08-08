scalaVersion := "2.12.6"

resolvers ++= Seq(
  Resolver.bintrayRepo("malliina", "maven"),
  Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))(Resolver.ivyStylePatterns)
)
classpathTypes += "maven-plugin"
scalacOptions ++= Seq("-unchecked", "-deprecation")

Seq(
  "com.malliina" %% "sbt-play" % "1.3.0",
  "com.malliina" % "sbt-aws-docker" % "0.2.2"
) map addSbtPlugin
