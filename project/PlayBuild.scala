import com.malliina.sbt.aws.AwsDockerKeys.codeBuildServiceRole
import com.malliina.sbtplay.PlayProject
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object PlayBuild {
  lazy val p = PlayProject.default("play-docka")
    .enablePlugins(BuildInfoPlugin)
    .settings(commonSettings: _*)

  lazy val commonSettings = buildInfoSettings ++ dockaSettings ++ Seq(
    organization := "com.malliina",
    version := "0.1.0",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-encoding", "UTF-8"
    ),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk" % "1.11.75"
    )
  )

  def dockaSettings = Seq(
    codeBuildServiceRole := Option("codebuild-docka-build-service-role")
  )

  def buildInfoSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.malliina.app.build"
  )
}
