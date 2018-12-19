package com.malliina.app

import controllers.{AssetsBuilder, AssetsComponents, Home}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.filters.gzip.GzipFilter
import router.Routes

class AppLoader extends LoggingAppLoader[AppComponents] with WithAppComponents

object AppComponents {
  def urlPrefix = "/two"
}

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AssetsComponents {

  override val httpFilters = Seq(new GzipFilter(), csrfFilter, securityHeadersFilter)
  val as = new AssetsBuilder(httpErrorHandler, assetsMetadata)
  val home = new Home(as, controllerComponents)
  override val router: Router = new Routes(httpErrorHandler, home, AppComponents.urlPrefix)
}
