import com.malliina.sbt.aws.AwsDockerKeys.codeBuildServiceRole
import com.malliina.sbtplay.PlayProject
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerRepository
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object PlayBuild {
  lazy val p = PlayProject.default("play-docka")
    .enablePlugins(BuildInfoPlugin, DockerPlugin)
    .settings(commonSettings: _*)

  lazy val commonSettings = buildInfoSettings ++ Seq(
    organization := "com.malliina",
    version := "0.2.0",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-encoding", "UTF-8"
    ),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk" % "1.11.75"
    ),
    codeBuildServiceRole := Option("codebuild-docka-build-service-role"),
    dockerRepository := Option("malliina")
  )

  def buildInfoSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.malliina.app.build"
  )
}
