package actions;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This action can be associated to any controller action method like this :
 *
 * @With(SecuredAction.class) public Result myAction() { return ok(); }
 *
 * It will wrap the action call and execute the code action below before executing the code of the method where the annotation was added
 */
public class SecuredAction extends Action.Simple {
  /**
   * All action annotations have to return a CompletionStage<Result>
   * CompletionStage is just a wrapper for async programming, which is the doctrine of Play
   * It is mandatory here but not in controller's actions
   */
  public CompletionStage<Result> call(Http.Context ctx) {
    String username = ctx.session().get("username");
    if (!helpers.Security.sha1("admin").equals(username)) {
      return CompletableFuture.completedFuture(redirect(controllers.routes.Blog.login()));
    }
    return delegate.call(ctx); // restitute to the initial action method
  }
}
