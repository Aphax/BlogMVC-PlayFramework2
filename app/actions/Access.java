package actions;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Defining a custom annotation
 * That way we can call directly @Admin over a Controller / Method to handle access
 */
@With(AccessAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Access {
  /**
   * Parameter that can be used in annotation : @Access(type = "admin")
   * Parameter value is retrieved via configuration.type() in Action implementation below
   */
  String type() default "admin";
}

/**
 * Implementing the custom annotation
 */
class AccessAction extends Action<Access> {
  /**
   * All action annotations have to return a CompletionStage<Result>
   * CompletionStage is just a wrapper for async programming, which is the doctrine of Play
   * It is mandatory here but not in controller's actions
   */
  public CompletionStage<Result> call(Http.Context ctx) {
    String username = ctx.session().get("username");
    if (!helpers.Security.sha1(configuration.type()).equals(username)) {
      return CompletableFuture.completedFuture(redirect(controllers.routes.Blog.login()));
    }
    return delegate.call(ctx); // restitute to the initial action method
  }
}
