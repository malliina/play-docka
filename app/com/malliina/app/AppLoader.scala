package com.malliina.app

import controllers.{AssetsComponents, Home}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import router.Routes

class AppLoader extends LoggingAppLoader[AppComponents] with WithAppComponents

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with AssetsComponents {
  override lazy val httpFilters: Seq[EssentialFilter] = Seq(new HttpsRedirectFilter)
  val secretService = SecretService
  val home = new Home(controllerComponents)

  override val router: Router = new Routes(httpErrorHandler, home, assets)
}
