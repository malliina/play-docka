package tests

import com.malliina.app.HttpsRedirectFilter.{CFVisitor, Http, Https, Scheme}
import com.malliina.app.{AppComponents, WithAppComponents}
import org.scalatest.FunSuite
import play.api.http.HeaderNames.X_FORWARDED_PROTO
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class AppTestsScalaTest extends FunSuite with OneAppPerSuite2[AppComponents] with WithAppComponents {

  test("can access a component of the running test app") {
    assert(components.secretService.secret === 42)
  }

  test("can make request") {
    assert(withHeaders() === 200)
  }

  test("X-Forwarded-Proto 1") {
    assert(withHeaders(X_FORWARDED_PROTO -> Http) === MOVED_PERMANENTLY)
  }

  test("X-Forwarded-Proto 2") {
    assert(withHeaders(X_FORWARDED_PROTO -> Https) !== MOVED_PERMANENTLY)
  }

  test("CF-Visitor 1") {
    val responseStatus = withHeaders(
      CFVisitor -> Json.stringify(Json.obj(Scheme -> Http)),
      X_FORWARDED_PROTO -> Https)
    assert(responseStatus === MOVED_PERMANENTLY)
  }

  test("CF-Visitor 2") {
    val responseStatus = withHeaders(
      CFVisitor -> Json.stringify(Json.obj(Scheme -> Https)),
      X_FORWARDED_PROTO -> Http)
    assert(responseStatus !== MOVED_PERMANENTLY)
  }

  def withHeaders(headers: (String, String)*) = {
    val result = route(app, FakeRequest(GET, "/").withHeaders(headers: _*)).get
    status(result)
  }
}
