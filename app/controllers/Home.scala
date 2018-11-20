package controllers

import com.malliina.app.build.BuildInfo
import controllers.Assets.Asset
import controllers.Home._
import play.api.libs.json.Json
import play.api.mvc._

object Home {
  val Welcome = "Welcome"
  val App = "app"
  val Version = "version"
  val GitHash = "gitHash"
  val NoCache = "no-cache"
}

class Home(assets: AssetsBuilder, comps: ControllerComponents) extends AbstractController(comps) {
  def index = Action {
    Ok(AppTags.index(Welcome))
  }

  def health = Action {
    Ok(Json.obj(App -> BuildInfo.name, Version -> BuildInfo.version, GitHash -> BuildInfo.gitHash))
      .withHeaders(CACHE_CONTROL -> NoCache)
  }

  def versioned(path: String, file: Asset) = assets.versioned(path, file)
}
