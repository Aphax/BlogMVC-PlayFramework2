import controllers.Blog;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;

public class ControllerUnitTest extends WithApplication {

  @Override
  protected Application provideApplication() {
    return new GuiceApplicationBuilder()
            .configure("play.http.router", "javaguide.tests.Routes")
            .build();
  }

  @Test
  public void testIndex() {
    Result result = new Blog().all(1);
    assertEquals(OK, result.status());
    assertEquals("text/html", result.contentType().get());
    assertEquals("utf-8", result.charset().get());
    assertTrue(contentAsString(result).contains("Welcome on my blog"));
  }

}