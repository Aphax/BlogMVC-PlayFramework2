import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Environment;
import play.ApplicationLoader;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import play.test.WithApplication;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.running;

/**
 * Created by aphax on 03/11/16.
 */
public class FunctionalDITest extends WithApplication {
  @Inject
  Application application;

  @Before
  public void setup() {
    Module testModule = new AbstractModule() {
      @Override
      public void configure() {
        // Install custom test binding here
      }
    };

    GuiceApplicationBuilder builder = new GuiceApplicationLoader()
            .builder(new ApplicationLoader.Context(Environment.simple()))
            .overrides(testModule);
    Guice.createInjector(builder.applicationModule()).injectMembers(this);

    Helpers.start(application);
  }

  @After
  public void teardown() {
    Helpers.stop(application);
  }

//  @Test
//  public void findById() {
//    ClassLoader classLoader = classLoader();
//    Application application = new GuiceApplicationBuilder()
//            .in(new Environment(new File("/media/documents/Sites/BlogMVC/PlayFramework"), classLoader, Mode.TEST))
//            .build();
//
//    running(application, () -> {
//      User admin = User.find(1L);
//      assertEquals("admin", admin.username);
//    });
//  }
}
