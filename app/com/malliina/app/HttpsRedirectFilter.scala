package com.malliina.app

import com.malliina.app.HttpsRedirectFilter._
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.libs.streams.Accumulator
import play.api.mvc.{EssentialAction, EssentialFilter, RequestHeader, Results}

import scala.util.Try

object HttpsRedirectFilter {
  val CFVisitor = "CF-Visitor"
  val Http = "http"
  val Https = "https"
  val Scheme = "scheme"
}

/**
  * @see https://aws.amazon.com/premiumsupport/knowledge-center/redirect-http-https-elb/
  * @see https://support.cloudflare.com/hc/en-us/articles/200170986-How-does-CloudFlare-handle-HTTP-Request-headers-
  */
class HttpsRedirectFilter extends EssentialFilter {
  /** If CloudFlare terminates SSL and ELB is used, then the X-Forwared-Proto header
    * will be "http" regardless of whether the client uses HTTPS or not.
    *
    * So we first check the CloudFlare-specific CF-Visitor header for the scheme
    * and fallback to the X-Forwarded-Proto header iff no CF-Visitor header is present.
    *
    * Only redirects based on headers, because health check endpoints are still
    * HTTP-only.
    *
    * @param next the action
    * @return a possible redirection
    */
  override def apply(next: EssentialAction): EssentialAction = EssentialAction { rh =>
    val proto = cloudFlareProto(rh) orElse xForwardedProto(rh)
    val shouldRedirect = proto contains Http
    if (shouldRedirect)
      Accumulator.done(Results.MovedPermanently(s"$Https://${rh.host}${rh.uri}"))
    else
      next(rh)
  }

  /** Example CF-Visitor value: {"scheme":"https"} or {"scheme":"http"}.
    *
    * @param rh request header
    * @return the scheme, if any
    */
  def cloudFlareProto(rh: RequestHeader): Option[String] =
    for {
      visitor <- rh.headers.get(CFVisitor)
      json <- Try(Json.parse(visitor)).toOption
      proto <- (json \ Scheme).validate[String].asOpt
    } yield proto

  def xForwardedProto(rh: RequestHeader): Option[String] =
    rh.headers.get(HeaderNames.X_FORWARDED_PROTO)
}
