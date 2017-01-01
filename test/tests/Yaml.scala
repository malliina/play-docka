package tests

import org.scalatest.FunSuite

import scala.language.implicitConversions

sealed trait YamlDoc extends YamlValue

sealed trait YamlValue

case class YamlContainer(children: Seq[YamlDoc])

case class Section(title: String, parts: Seq[YamlValue]) extends YamlDoc

case class Arr(title: String, items: Seq[ArrEntry]) extends YamlDoc

case class TitledValue(title: String, value: Entry) extends YamlDoc

case class ArrEntry(value: String) extends YamlValue

case object EmptyRow extends YamlDoc

object ArrEntry {
  implicit def fromString(s: String): ArrEntry = ArrEntry(s)
}

case class Entry(value: String)

object Entry {
  implicit def fromString(s: String): Entry = Entry(s)
}

object Yaml extends Yaml {
  val empty = ""
  val lineSep = "\n"
  val identStep = 2

  def stringifyDoc(yaml: YamlContainer): String = {
    yaml.children.map(child => stringify(child)).mkString(empty, lineSep, lineSep)
  }

  def stringify(yaml: YamlValue, ident: Int = 0): String = {
    val prefix = spaces(ident)
    val fragment = yaml match {
      case Section(title, parts) =>
        val partsStringified = parts.map(part => stringify(part, ident + identStep)).mkString(lineSep)
        s"$title:$lineSep$partsStringified"
      case Arr(title, items) =>
        val rows = items.map(item => stringify(item, ident + identStep)).mkString(lineSep)
        s"$title:$lineSep$rows"
      case TitledValue(title, value) =>
        s"$title: ${value.value}"
      case ArrEntry(value) =>
        s"- $value"
      case EmptyRow =>
        empty
    }
    val end = empty
    s"$prefix$fragment$end"
  }

  private def spaces(spaces: Int) = Seq.fill(spaces)(" ").mkString("")
}

trait Yaml {
  def doc(parts: YamlDoc*) = YamlContainer(parts)

  def section(title: String)(parts: YamlValue*) = Section(title, parts)

  def arr(title: String)(items: ArrEntry*) = Arr(title, items)

  def single(title: String, value: Entry) = TitledValue(title, value)

  def value(s: String) = Entry(s)

  def row = EmptyRow
}

class YamlTests extends FunSuite {

  import Yaml._

  test("can write yaml") {
    val yaml = doc(
      single("version", "0.1"),
      row,
      section("environment_variables")(
        section("plaintext")(
          single("JAVA_HOME", "/usr/lib/jvm/java-8-openjdk-amd64")
        )
      ),
      row,
      section("phases")(
        section("install")(
          arr("commands")(
            "apt-get update -y",
            "apt-get install -y maven"
          )
        ),
        section("pre_build")(
          arr("commands")(
            "echo Nothing to do in the pre_build phase..."
          )
        ),
        section("build")(
          arr("commands")(
            "echo Build started on `date`",
            "mvn install"
          )
        ),
        section("post_build")(
          arr("commands")(
            "echo Build completed on `date`"
          )
        )
      ),
      section("artifacts")(
        arr("files")(
          "target/messageUtil-1.0.jar"
        ),
        single("discard-paths", "yes")
      )
    )
    println(stringifyDoc(yaml))
  }
}
