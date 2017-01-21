package security;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Here we extends Play authentificator default behavior in order to define admin specific authentification
 * It uses a simple per-username authentification
 */
public class AdminAuthenticator extends Security.Authenticator {
  /**
   * The method to override in order to define custom-security logic
   * Here we read the username cookie session value, if it is the SHA1-encrypted "admin" value, access is granted
   * If return value is not null, access is granted, otherwise AdminAuthenticator.onUnauthorized() is called
   * @see Security
   */
  @Override
  public String getUsername(Context ctx) {
    String username = ctx.session().get("username");
    return helpers.Security.sha1("admin").equals(username) ? username : null;
  }

  /**
   * Result to return if user authentification fails
   */
  @Override
  public Result onUnauthorized(Context ctx) {
    return redirect(controllers.routes.Blog.login());
  }
}