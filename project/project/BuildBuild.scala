import sbt._
import sbt.Keys._

object BuildBuild {
  val settings = sbtPlugins ++ Seq(
    scalaVersion := "2.10.6",
    resolvers ++= Seq(
      Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))(Resolver.ivyStylePatterns)
    ),
    scalacOptions ++= Seq("-unchecked", "-deprecation")
  )

  def sbtPlugins = Seq(
    "com.typesafe.play" % "sbt-plugin" % "2.5.10",
    "se.marcuslonnberg" % "sbt-docker" % "1.4.0",
    "com.eed3si9n" % "sbt-buildinfo" % "0.6.1"
  ) map addSbtPlugin
}
