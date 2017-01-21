import play.Application;
import play.GlobalSettings;
import play.test.Helpers;
import play.test.WithApplication;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by aphax on 03/11/16.
 */
public class FunctionalTest extends WithApplication {
  Application fakeApp = fakeApplication();

  Application fakeAppWithGlobal = fakeApplication(new GlobalSettings() {
    @Override
    public void onStart(Application app) {
      System.out.println("Starting FakeApplication");
    }
  });

  Application fakeAppWithMemoryDb = fakeApplication(inMemoryDatabase("test"));
}
