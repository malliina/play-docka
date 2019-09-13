import scala.sys.process.Process
import scala.util.Try

lazy val p = Project("play-docka", file("."))
  .enablePlugins(PlayScala, BuildInfoPlugin, DockerPlugin)

val gitHash = settingKey[String]("Git hash")
val dockerHttpPort = settingKey[Int]("HTTP listen port")
val Gcp = config("gcp")
val defaultPort = 9000
val gcpAppEngineHttpPort = 8080

organization := "com.malliina"
version := "1.0.0"
scalaVersion := "2.13.0"
scalacOptions ++= Seq(
  "-encoding", "UTF-8"
)
dockerRepository := Option("malliina")
dockerExposedPorts := Seq(dockerHttpPort.value)
javaOptions in Universal ++= Seq(
  "-J-Xmx256m",
  s"-Dhttp.port=${dockerHttpPort.value}"
)

stage in Gcp := {
  val gcpYaml = "app.yaml"
  IO.copyFile(baseDirectory.value / gcpYaml, (stagingDirectory in Docker).value / gcpYaml)
  stage.in(Docker).value
}
stage in Gcp := (stage in Gcp).dependsOn(stage in Docker).value

// https://stackoverflow.com/questions/14262798/how-to-change-setting-inside-sbt-command
commands += Command.command("deployGcp") { state =>
  val extracted = Project.extract(state)
  val newState = extracted.appendWithSession(Seq(dockerHttpPort := gcpAppEngineHttpPort), state)
  val (s, _) = Project.extract(newState).runTask(publish in Gcp, newState)
  s
}

publish in Gcp := {
  val exitValue = Process(s"gcloud app deploy", (stagingDirectory in Docker).value)
    .run(streams.value.log)
    .exitValue()
  if (exitValue != 0) sys.error(s"Non-zero exit code: $exitValue.")
}
publish in Gcp := (publish in Gcp).dependsOn(stage in Gcp).value

gitHash := Try(Process("git rev-parse --short HEAD").lineStream.head).toOption
  .orElse(sys.env.get("CODEBUILD_RESOLVED_SOURCE_VERSION").map(_.take(7)))
  .orElse(sys.env.get("CODEBUILD_SOURCE_VERSION").map(_.take(7)))
  .getOrElse("latest")

dockerHttpPort := sys.env.get("HTTP_PORT").map(_.toInt).getOrElse(defaultPort)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, "gitHash" -> gitHash.value)
buildInfoPackage := "com.malliina.app.build"

pipelineStages := Seq(digest, gzip)
pipelineStages in Assets := Seq(digest, gzip)

libraryDependencies ++= Seq(
  "com.zaxxer" % "HikariCP" % "3.3.1",
  "mysql" % "mysql-connector-java" % "5.1.47",
  "redis.clients" % "jedis" % "3.1.0",
  "com.lihaoyi" %% "scalatags" % "0.7.0",
  specs2 % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
)
