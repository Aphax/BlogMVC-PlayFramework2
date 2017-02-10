package controllers;

import models.Category;
import models.Comment;
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
import play.twirl.api.Html;
import security.AdminAuthenticator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Transactional -> All methods inherits of a JPA transaction context
 * https://www.playframework.com/documentation/2.5.x/JavaJPA#Annotating-JPA-actions-with-@Transactional
 * <p>
 * Singleton -> This Controller will never be instantiated more than once (avoid memory leaks)
 * https://www.playframework.com/documentation/2.5.x/JavaDependencyInjection#Singletons
 * <p>
 * Inject -> Play uses Dependency Injection with Guice and try to remove Global State from framework since version 2.4
 * https://www.playframework.com/documentation/2.5.x/JavaDependencyInjection
 */
@Transactional
@Singleton
public class Blog extends Controller {
  /**
   * Form helper that will help us handle form submission and validation
   */
  @Inject
  protected FormFactory formFactory;

  /**
   * Configuration object parsed from application.conf file
   */
  @Inject
  protected Configuration conf;

  /**
   * Play's cache API that will help us to store sidebar contents
   */
  @Inject
  protected CacheApi cache;

  @Inject
  protected JPAApi jpa;

  /**
   * If you need to access directly to JDBC driver
   * Just call db.getConnection() in any action and you're good to go
   */
  @Inject
  protected Database db;

  /**
   * Return Html object type from sidebar view object that will be rendered naturally into final scala template
   * <p>
   * cache.getOrElse(key, Callable<T>, duration) : gets cache index or get it from Callable and store it automatically
   * Same as : cache.get(key) ? cache.get(key) : cache.set(key, content).get(content)
   * https://www.playframework.com/documentation/2.5.x/JavaCache
   */
  private Html getSidebarContent() {
    List<Post> lastPosts = cache.getOrElse("lastPosts", () -> Post.findByPage(1));
    List<Category> categories = cache.getOrElse("categories", Category::findAll);
    return views.html.components.sidebar.render(lastPosts, categories);
  }

  // @Cached(key = "index") // Cache action result very easily
  public Result all(Integer pageNb) {
    Logger.info("Post.all(pageNb: " + pageNb + ")");
    List<Post> posts = Post.findByPage(pageNb);
    int pageCnt = (int) Math.ceil((Post.countAll() * 1f) / (conf.getInt("app.pagination") * 1f));
    return ok(views.html.posts.render(posts, pageNb, pageCnt, getSidebarContent()));
  }

  public Result byAuthor(Long authorId, Integer pageNb) {
    Logger.info("Post.byAuthor(pageNb: " + pageNb + ", authorId: " + authorId + ")");
    User author = User.find(authorId);
    if (author == null) {
      return notFound();
    }
    int pageCnt = (int) Math.ceil((Post.countBy(author) * 1f) / (conf.getInt("app.pagination") * 1f));
    List<Post> posts = Post.findByXPaginate(pageNb, authorId, User.class);
    return ok(views.html.posts.render(posts, pageNb, pageCnt, getSidebarContent()));
  }

  public Result byCategory(String slug, Integer pageNb) {
    Logger.info("Post.byCategory(pageNb: " + pageNb + ", slug: " + slug + ")");
    Category category = Category.find(slug);
    if (category == null) {
      return notFound();
    }
    int pageCnt = (int) Math.ceil((Post.countBy(category) * 1f) / (conf.getInt("app.pagination") * 1f));
    List<Post> posts = Post.findByCategory(slug, pageNb);
    return ok(views.html.posts.render(posts, pageNb, pageCnt, getSidebarContent()));
  }

  public Result byId(Long id) {
    Logger.info("Post.post(id: " + id + ")");
    Post post = Post.find(id);
    if (post == null) {
      return notFound();
    }
    return ok(views.html.post.render(post, getSidebarContent(), formFactory.form(Comment.class)));
  }

  public Result bySlug(String slugName) {
    Logger.info("Post.post(slugName: " + slugName + ")");
    Post post = Post.find(slugName);
    if (post == null) {
      return notFound();
    }
    return ok(views.html.post.render(post, getSidebarContent(), formFactory.form(Comment.class)));
  }

  public Result commentCreate(Long postId) {
    Logger.info("Post.commentCreate(postId: " + postId + ")");
    Form<Comment> commentForm = formFactory.form(Comment.class).bindFromRequest();
    Post post = Post.find(postId);
    if (commentForm.hasErrors()) {
      return badRequest(views.html.post.render(post, getSidebarContent(), commentForm));
    }
    Comment comment = commentForm.get();
    comment.post = post;
    jpa.em().persist(comment);
    return byId(postId);
  }

  public Result login() {
    Logger.info("Blog.login()");
    if (new AdminAuthenticator().getUsername(ctx()) != null) {
      return redirect(routes.Admin.index(1));
    }
    return ok(views.html.login.render());
  }

  /**
   * For this request, we will search POST parameters directly through request (there are other ways to get parameters with Play)
   * We use a BodyParser annotation, it tells Play in which format the request should be parsed
   * In that case, the below annotation doesn't change anything in the final behavior of the action
   * It's more of a convention than mandatory, knowing that Play default BodyParser detects body from content-type header
   * It still helps the developer to easily know which kind of request the action is receiving
   * https://www.playframework.com/documentation/2.5.x/JavaBodyParsers
   * BodyParsers becomes really useful when you have to parse custom request formats, which is rare.
   */
  @BodyParser.Of(BodyParser.FormUrlEncoded.class)
  public Result loginPost() {
    Map<String, String[]> parameters = request().body().asFormUrlEncoded(); // Body parser of FormUrlEncoded is called
    Logger.info("Admin.indexPostLogin() - data: " + parameters);
    String username = parameters.get("username")[0];
    String password = parameters.get("password")[0];
    User user = User.find(username, helpers.Security.sha1(password));
    if (user == null) {
      return unauthorized(views.html.login.render());
    }

    session("username", helpers.Security.sha1(username));
    return redirect(routes.Admin.index(1));
  }
}
