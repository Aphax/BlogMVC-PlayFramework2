package controllers;

import actions.Access;
import models.Category;
import models.Post;
import models.User;
import play.Configuration;
import play.Logger;
import play.cache.CacheApi;
import play.data.Form;
import play.data.FormFactory;
import play.db.Database;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import security.AdminAuthenticator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * Transactional -> All methods inherits of a JPA transaction context
 * https://www.playframework.com/documentation/2.5.x/JavaJPA#Annotating-JPA-actions-with-@Transactional
 * <p>
 * Singleton -> This Controller will never be instantiated more than once (avoid memory leaks)
 * https://www.playframework.com/documentation/2.5.x/JavaDependencyInjection#Singletons
 * <p>
 * 3 way of handling Admin control access, each one encapsulated in an authorizing-checking action
 * - Security.Authenticated -> Play out of the box Security API
 *
 * @see AdminAuthenticator for more details
 * - With(SecuredAction.class) : Custom action handling admin access
 * - @Access(type = "admin") : Custom action annotation handling admin access (almost the same as previous one)
 * <p>
 * Inject -> Play uses Dependency Injection with Guice and try to remove Global State from framework since version 2.4
 * https://www.playframework.com/documentation/2.5.x/JavaDependencyInjection
 */
@Singleton
@Transactional
//@Security.Authenticated(AdminAuthenticator.class)
//@With(SecuredAction.class)
@Access(type = "admin")
public class Admin extends Controller {
  /**
   * Form helper that will help us handle form submission and validation
   */
  @Inject
  protected FormFactory formFactory;

  /**
   * If you need to access directly to JDBC driver
   * Just call db.getConnection() in any action and you're good to go
   */
  @Inject
  protected Database db;

  @Inject
  protected JPAApi jpa;

  @Inject
  protected CacheApi cache;

  @Inject
  protected Configuration conf;

  public Result index(Integer pageNb) {
    Logger.info("Admin.index()");
    List<Post> posts = Post.findByPage(pageNb);
    int pageCnt = (int) Math.ceil(Post.countAll() / conf.getInt("app.pagination"));

    return ok(views.html.admin_index.render(pageNb, pageCnt, posts));
  }

  public Result logout() {
    Logger.info("Admin.logout()");
    session().remove("username");
    return redirect(routes.Blog.all(1));
  }

  /**
   * In order to use Play form helpers, we need to use the play.data.Form wrapper on an entity
   * Form objects are immutable, all methods that induce a state change returns a new object
   * (like Form::fill which allows to populate a Form's fields objects)
   */
  public Result postNew() {
    // emptyForm != filledForm
    Form<Post> emptyForm = formFactory.form(Post.class);
    Form<Post> filledForm = emptyForm.fill(new Post());
    filledForm.discardErrors();
    return ok(views.html.admin_edit.render(filledForm, User.findAll(), Category.findAll()));
  }

  /**
   * In order to display existing data of entity to edit, we have to call the .fill() method that returns a new Form object populated with entity data
   * @param postId
   */
  public Result postEdit(Long postId) {
    Post post = Post.find(postId);
    if (post == null) {
      return notFound();
    }
    Form<Post> formPost = formFactory.form(Post.class).fill(post);
    return ok(views.html.admin_edit.render(formPost, User.findAll(), Category.findAll()));
  }

  /**
   * Creating a new entity and persisting it into database is quite simple
   * @return
   */
  public Result postCreate() {
    Form<Post> form = formFactory.form(Post.class);
    Form<Post> boundForm = form.bindFromRequest();
    Logger.debug("form data : " + boundForm.data());
    if (boundForm.hasErrors()) {
      return badRequest(views.html.admin_edit.render(boundForm, User.findAll(), Category.findAll()));
    }
    Post post = boundForm.get(); // get the entity object from Form data object once they were validated
    jpa.em().persist(post); // persist the entity into database
    flash("success", "Article has been created");
    return redirect(routes.Admin.index(1));
  }

  /**
   * Updating an existing entity takes a bit more steps, but nothing too complicated
   * The key here is to update data from the existing entity we fetch into database
   * The entity retrieved from the Form data object is just a temporary object
   *
   * About JPA : We don't need to call jpa.persist on postExisting entity explicitly because entity is already managed by JPA when we fetch it
   * @param postId
   */
  @BodyParser.Of(BodyParser.FormUrlEncoded.class)
  public Result postSave(Long postId) {
    Form<Post> form = formFactory.form(Post.class);
    Form<Post> boundForm = form.bindFromRequest();
    Logger.debug("form data : " + boundForm.data());
    Post postExisting = Post.find(postId);
    if (postExisting == null) {
      return notFound();
    }
    if (boundForm.hasErrors()) {
      return badRequest(views.html.admin_edit.render(boundForm, User.findAll(), Category.findAll()));
    }
    Post postUpdated = boundForm.get();
    postExisting.name = postUpdated.name;
    postExisting.slug = postUpdated.slug;
    postExisting.category = postUpdated.category;
    postExisting.content = postUpdated.content;

    // We could have used the merge method too
    // Be careful tho as this method will replace ALL data from new entity, even if some properties are null or empty, this is not always what you want
    // postUpdated.id = postId;
    // jpa.em().merge(postUpdated);

    // Update sidebar cache
    cache.set("lastPosts", Post.findByPage(1));
    return redirect(routes.Admin.index(1));
  }

  public Result postRemove(Long postId) {
    Logger.info("Admin.adminDeletePost(postId: " + postId + ")");
    if (session().get("id") == null) {
      return forbidden();
    }
    JPA.em().remove(Post.find(postId));
    JPA.em().flush();
    return redirect(routes.Admin.index(1));
  }
}
