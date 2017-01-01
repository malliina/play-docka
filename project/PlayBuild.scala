import com.typesafe.sbt.packager.Keys.{dockerCmd, dockerEntrypoint, dockerBaseImage => _, dockerCommands => _, dockerExposedPorts => _, _}
import com.typesafe.sbt.packager.MappingsHelper
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.docker.{Cmd, CmdLike, DockerPlugin, ExecCmd}
import com.typesafe.sbt.packager.universal.Archives
import org.apache.commons.io.FilenameUtils
import play.sbt.{PlayImport, PlayScala}
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object PlayBuild {
  val dockerBaseDir = settingKey[String]("WORKDIR")
  val dockerExecutable = settingKey[String]("Docker executable script")
  val dockerZip = taskKey[File]("Zip the app")

  lazy val p = Project("play-docka", file("."))
    .enablePlugins(BuildInfoPlugin, PlayScala)
    .settings(commonSettings: _*)

  lazy val commonSettings = buildInfoSettings ++ dockerSettings ++ Seq(
    organization := "com.malliina",
    version := "0.0.8",
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

  def buildInfoSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.malliina.app.build"
  )

  // We roll the dockerCommands from scratch because we want to at least make the script executable
  def dockerSettings = Seq(
    dockerBaseDir := (defaultLinuxInstallLocation in Docker).value,
    dockerExecutable := s"${dockerBaseDir.value}/${dockerEntrypoint.value}",
    dockerExposedPorts := Seq(9000),
    dockerCommands := {
      val user = (daemonUser in Docker).value
      val group = (daemonGroup in Docker).value
      Seq(
        Cmd("FROM", dockerBaseImage.value),
        Cmd("WORKDIR", dockerBaseDir.value),
        makeAdd(dockerBaseDir.value),
        makeChown(user, group, Seq(".")),
        ExecCmd("RUN", ("chmod" :: "u+x" :: dockerEntrypoint.value.toList): _*),
        Cmd("EXPOSE", dockerExposedPorts.value.mkString(" ")),
        Cmd("USER", user),
        ExecCmd("ENTRYPOINT", dockerEntrypoint.value: _*),
        ExecCmd("CMD", dockerCmd.value: _*)
      )
    },
    dockerZip := {
      val dest = zipDir(
        (stagingDirectory in Docker).value,
        (target in Docker).value / s"${name.value}-${version.value}.zip"
      )
      streams.value.log.info(s"Created $dest")
      dest
    },
    dockerZip := (dockerZip dependsOn (stage in Docker)).value
  )

  /** Zips `sourceDir`.
    *
    * @param sourceDir directory to zip
    * @param zipTarget desired zip file
    * @return the zipped file
    */
  def zipDir(sourceDir: File, zipTarget: File): File = {
    val targetDir = Option(zipTarget.getParentFile) getOrElse new File(".")
    val name = FilenameUtils.getBaseName(zipTarget.getName)
    val mappings = MappingsHelper.contentOf(sourceDir)
    Archives.makeZip(targetDir, name, mappings, None)
  }

  private final def makeAdd(dockerBaseDirectory: String): CmdLike = {
    val files = dockerBaseDirectory.split(DockerPlugin.UnixSeparatorChar)(1)
    Cmd("ADD", s"$files /$files")
  }

  private final def makeChown(daemonUser: String, daemonGroup: String, directories: Seq[String]): CmdLike =
    ExecCmd("RUN", Seq("chown", "-R", s"$daemonUser:$daemonGroup") ++ directories: _*)
}
