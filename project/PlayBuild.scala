import com.malliina.sbt.aws.AwsDockerKeys.codeBuildServiceRole
import com.typesafe.sbt.packager.Keys.{scriptClasspath, dist}
import play.sbt.PlayImport.PlayKeys.externalizeResources
import play.sbt.{PlayImport, PlayScala}
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object PlayBuild {

  val azure = taskKey[Unit]("Zip using dist and unzip")

  lazy val p = Project("play-docka", file("."))
    .enablePlugins(BuildInfoPlugin, PlayScala)
    .settings(commonSettings: _*)

  lazy val commonSettings = buildInfoSettings ++ dockaSettings ++ azureSettings ++ Seq(
    organization := "com.malliina",
    version := "0.1.0",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-encoding", "UTF-8"
    ),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk" % "1.11.75",
      "com.lihaoyi" %% "scalatags" % "0.6.2",
      PlayImport.specs2 % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
    ),
    externalizeResources := false,
    scriptClasspath := Seq("*")
  )

  def azureSettings = Seq(
      azure := {
      IO.unzip(dist.value, baseDirectory.value)
    }
  )

  def dockaSettings = Seq(
    codeBuildServiceRole := Option("codebuild-docka-build-service-role")
  )

  def buildInfoSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.malliina.app.build"
  )
}
