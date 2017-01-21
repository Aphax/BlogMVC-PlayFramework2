import play.Environment;
import play.Mode;
import play.http.HttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class configures filters that run on every request. This
 * class is queried by Play to get a list of filters.
 * <p>
 * Play will automatically use filters from any class called
 * <code>Filters</code> that is placed the root package. You can load filters
 * from a different class by adding a `play.http.filters` setting to
 * the <code>application.conf</code> configuration file.
 */
@Singleton
public class Filters implements HttpFilters {

  private final Environment env;

  /**
   * @param env Basic environment settings for the current application.
   */
  @Inject
  public Filters(Environment env) {
    this.env = env;
  }

  @Override
  public EssentialFilter[] filters() {
    // Use the example filter if we're running development mode. If
    // we're running in production or test mode then don't use any
    // filters at all.
    if (env.mode().equals(Mode.DEV)) {
      return new EssentialFilter[]{};
    } else {
      return new EssentialFilter[]{};
    }
  }

}
