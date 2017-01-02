import java.nio.charset.StandardCharsets
import java.nio.file.Path

import Yaml._
import sbt.IO

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object BuildSpec {
  val FileName = "buildspec.yml"

  def writeForArtifact(artifact: Path, dest: Path) =
    writeYaml(forArtifact(artifact), dest)

  /**
    * @param dest destination file
    * @param yaml contents to write
    * @return the written contents
    */
  def writeYaml(yaml: YamlContainer, dest: Path): String = {
    val asString = stringifyDoc(yaml)
    IO.write(dest.toFile, asString, StandardCharsets.UTF_8)
    asString
  }

  def forArtifact(artifact: Path): YamlContainer = doc(
    single("version", "0.1"),
    row,
    section("phases")(
      section("build")(
        arr("commands")(
          "echo Packaging started on `date` ...",
          "sbt codeBuild",
          "echo Packaging completed on `date`"
        )
      )
    ),
    section("artifacts")(
      arr("files")(
        artifact.toString.replace('\\', '/')
      ),
      single("discard-paths", "yes")
    )
  )

  def beanstalkExtension(role: String = "codebuild-docka-build-service-role",
                         computeType: String = "BUILD_GENERAL1_SMALL",
                         image: String = "hseeberger/scala-sbt",
                         timeout: FiniteDuration = 60.minutes) =
    section("eb_codebuild_settings")(
      single("CodeBuildServiceRole", role),
      single("ComputeType", computeType),
      single("Image", image),
      single("Timeout", timeout.toMinutes.toString)
    )
}
