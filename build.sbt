import com.malliina.sbtplay.PlayProject

import scala.sys.process.Process
import scala.util.Try

lazy val p = PlayProject.default("play-docka")
  .enablePlugins(BuildInfoPlugin, DockerPlugin)

val gitHash = settingKey[String]("Git hash")
val httpPort = settingKey[Int]("HTTP listen port")

organization := "com.malliina"
version := "0.4.0"
scalaVersion := "2.12.8"
scalacOptions ++= Seq(
  "-encoding", "UTF-8"
)
dockerRepository := Option("malliina")
dockerExposedPorts := Seq(httpPort.value)
javaOptions in Universal ++= Seq(
  "-J-Xmx256m",
  s"-Dhttp.port=${httpPort.value}"
)

gitHash := Try(Process("git rev-parse --short HEAD").lineStream.head).toOption
  .orElse(sys.env.get("CODEBUILD_RESOLVED_SOURCE_VERSION").map(_.take(7)))
  .orElse(sys.env.get("CODEBUILD_SOURCE_VERSION").map(_.take(7)))
  .getOrElse("latest")

httpPort := sys.env.get("HTTP_PORT").map(_.toInt).getOrElse(9000)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, "gitHash" -> gitHash.value)
buildInfoPackage := "com.malliina.app.build"

pipelineStages := Seq(digest, gzip)
pipelineStages in Assets := Seq(digest, gzip)

libraryDependencies ++= Seq(
  "com.zaxxer" % "HikariCP" % "3.2.0",
  "mysql" % "mysql-connector-java" % "5.1.47",
  "redis.clients" % "jedis" % "3.0.0"
)
