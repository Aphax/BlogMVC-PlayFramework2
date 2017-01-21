package framework;

import models.Post;
import org.junit.Test;
import play.Application;
import play.data.Form;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class FrameworkTest extends WithApplication {

  @Override
  protected Application provideApplication() {
    return new GuiceApplicationBuilder()
            .configure("play.http.router", "javaguide.tests.Routes")
            .build();
  }

  @Test
  public void formFactory() throws Exception {
//    Form<Post> form = this.form.form(Post.class);
//    HashMap<String, String> data = new HashMap<>();
//    data.put("name", "post name");
//    data.put("slug", "post-slug");
//    data.put("content", "qsmlkjfd qmlfjqsdmfljqsmlfd kjqs");
//    data.put("category.id", "1");
//    data.put("author.id", "1");
//    Form<Post> bind = form.bind(data);
//    assertEquals(true, bind.hasErrors());
//    Post post = bind.get();
//    assertEquals(post.name, "post name");
  }
}
