package controllers

import com.malliina.app.build.BuildInfo
import play.api.libs.json.Json
import play.api.mvc._

class Home(comps: ControllerComponents) extends AbstractController(comps) {
  val Welcome = "Welcome"
  val App = "app"
  val Version = "version"
  val NoCache = "no-cache"

  def index = Action {
    Ok(AppTags.index(Welcome))
  }

  def health = Action {
    Ok(Json.obj(App -> BuildInfo.name, Version -> BuildInfo.version))
      .withHeaders(CACHE_CONTROL -> NoCache)
  }
}
