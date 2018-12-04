package controllers

import com.malliina.app.build.BuildInfo
import play.api.mvc.Call
import scalatags.Text.GenericAttr
import scalatags.Text.all._

object AppTags {
  implicit val callAttr = new GenericAttr[Call]

  val appName = BuildInfo.name
  val appVersion = BuildInfo.version

  def index(msg: String) = TagPage(
    html(
      head(
        link(rel := "stylesheet", href := routes.Home.versioned("css/main.css"))
      ),
      body(
        h2(msg),
        p(s"$appName $appVersion ${BuildInfo.gitHash}")
      )
    )
  )
}
