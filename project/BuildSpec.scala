import java.nio.charset.StandardCharsets
import java.nio.file.Path

import Yaml._
import sbt.IO

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
        artifact.toString
      )
    )
  )
}
