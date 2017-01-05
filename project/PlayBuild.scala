import BeanstalkPlugin._
import play.sbt.{PlayImport, PlayScala}
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

import scala.util.Try

object PlayBuild {

  lazy val gitTag = taskKey[Option[String]]("The git tag of the currently checked out commit, if any")

  lazy val p = Project("play-docka", file("."))
    .enablePlugins(BuildInfoPlugin, PlayScala)
    .settings(commonSettings: _*)

  lazy val commonSettings = gitSettings ++ buildInfoSettings ++ dockaSettings ++ Seq(
    organization := "com.malliina",
    version := "0.0.23",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-encoding", "UTF-8"
    ),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.6.2",
      PlayImport.specs2 % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
    )
  )

  def gitSettings = Seq(
    gitTag := {
      Try(Process("git" :: "describe" :: "--exact-match" :: Nil).lines(NoopLogger()).headOption)
        .toOption.getOrElse(None)
    }
  )

  def dockaSettings = dockerSettings ++ Seq(
    codeBuildServiceRole := "codebuild-docka-build-service-role"
  )

  def buildInfoSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.malliina.app.build"
  )

  class NoopLogger extends sbt.ProcessLogger {
    override def info(s: => String) = ()

    override def error(s: => String) = ()

    override def buffer[T](f: => T) = f
  }

  object NoopLogger {
    def apply() = new NoopLogger
  }

}
