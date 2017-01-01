package com.malliina.app

import play.api.http.HeaderNames
import play.api.libs.streams.Accumulator
import play.api.mvc.{EssentialAction, EssentialFilter, Results}

/**
  * @see https://aws.amazon.com/premiumsupport/knowledge-center/redirect-http-https-elb/
  */
class HttpsRedirectFilter extends EssentialFilter {
  val Http = "http"
  val Https = "https"

  override def apply(next: EssentialAction): EssentialAction = EssentialAction { rh =>
    if (rh.headers.get(HeaderNames.X_FORWARDED_PROTO) contains Http)
      Accumulator.done(Results.Redirect(s"$Https://${rh.host}${rh.uri}"))
    else
      next(rh)
  }
}
