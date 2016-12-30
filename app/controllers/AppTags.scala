package controllers

import com.malliina.app.build.BuildInfo

import scalatags.Text.all._

object AppTags {
  val appName = BuildInfo.name
  val appVersion = BuildInfo.version

  def index(msg: String) = TagPage(
    html(
      body(
        h2(msg),
        p(s"$appName $appVersion")
      )
    )
  )
}
