import com.malliina.sbtplay.PlayProject

lazy val p = PlayProject.default("play-docka")
  .enablePlugins(BuildInfoPlugin, DockerPlugin)

organization := "com.malliina"
version := "0.2.0"
scalaVersion := "2.12.6"
scalacOptions ++= Seq(
  "-encoding", "UTF-8"
)
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.11.360"
)
dockerRepository := Option("malliina")

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
buildInfoPackage := "com.malliina.app.build"

dependencyOverrides ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-actor" % "2.5.11"
)