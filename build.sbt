import com.malliina.sbtplay.PlayProject

import scala.sys.process.Process
import scala.util.Try

lazy val p = PlayProject.default("play-docka")
  .enablePlugins(BuildInfoPlugin, DockerPlugin)

val gitHash = settingKey[String]("Git hash")

organization := "com.malliina"
version := "0.3.0"
scalaVersion := "2.12.6"
scalacOptions ++= Seq(
  "-encoding", "UTF-8"
)
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-elasticbeanstalk" % "1.11.360" % Test
)
dockerRepository := Option("malliina")

gitHash := Try(Process("git rev-parse --short HEAD").lineStream.head).toOption
  .orElse(sys.env.get("CODEBUILD_RESOLVED_SOURCE_VERSION").map(_.take(7)))
  .orElse(sys.env.get("CODEBUILD_SOURCE_VERSION").map(_.take(7)))
  .getOrElse("latest")

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, "gitHash" -> gitHash.value)
buildInfoPackage := "com.malliina.app.build"

dependencyOverrides ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-actor" % "2.5.11"
)
