package models;

import org.pegdown.PegDownProcessor;
import play.Configuration;
import play.api.Play;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.JPA;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "posts")
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Constraints.Required
  public String name = "";

  @Constraints.Required
  public String slug = "";

  public String content = "";

  // Map Java 8 DateTime to DATETIME's database format
  @Formats.DateTime(pattern = "Y-m-d h:i:s")
  public LocalDateTime created = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "category_id", referencedColumnName = "id")
  @Constraints.Required
  public Category category;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @Constraints.Required
  public User user;


  @OneToMany(mappedBy = "post")
  @OrderBy("created DESC")
  public List<Comment> comments = new ArrayList<>();

  public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E d MMM y");

  public Post() {
  }

  /**
   * Here we use JPQL (JPA Query Language) syntax to retrieve matching entries
   * https://en.wikibooks.org/wiki/Java_Persistence/JPQL
   * Using the JOIN FETCH assignment tells Hibernate to fetch data eagerly
   * Else category and user relational data would have been fetched lazily in another statement
   */
  public static Post find(Long id) {
    try {
      return JPA.em().createQuery("SELECT p FROM Post p \n" +
              "JOIN FETCH p.user \n" +
              "JOIN FETCH p.category \n" +
              "WHERE p.id = :id", Post.class)
              .setParameter("id", id)
              .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  /**
   * Here we use JPQL (JPA Query Language) syntax to retrieve matching entries
   * https://en.wikibooks.org/wiki/Java_Persistence/JPQL
   * Using the JOIN FETCH assignment tells Hibernate to fetch data eagerly
   * Else category and user relational data would have been fetched lazily in another statement
   */
  public static Post find(String slug) {
    try {
      return JPA.em().createQuery("SELECT p FROM Post p \n" +
              "JOIN FETCH p.category \n" +
              "JOIN FETCH p.user \n" +
              "WHERE p.slug = :slug", Post.class)
              .setParameter("slug", slug)
              .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public static Long countAll() {
    return JPA.em().createQuery("SELECT COUNT(p) FROM Post p", Long.class).getSingleResult();
  }

  public static Long countBy(User author) {
    return JPA.em().createQuery("SELECT COUNT(p) FROM Post p WHERE p.user = :user", Long.class)
            .setParameter("user", author)
            .getSingleResult();
  }

  public static Long countBy(Category category) {
    return JPA.em().createQuery("SELECT COUNT(p) FROM Post p WHERE p.category = :category", Long.class)
            .setParameter("category", category)
            .getSingleResult();
  }

  /**
   * Using JPA CriteriaBuilder to build Query
   * EntityManager.getCriteriaBuilder() -> CriteriaBuilder
   * <p>
   * CriteriaBuilder : Used to construct criteria queries, compound selections, expressions, predicates, orderings.
   * It serves as the main factory of criteria queries and criteria query elements
   * https://docs.oracle.com/javaee/6/api/index.html
   * CriteriaBuilder.createQuery(Class.class) -> CriteriaQuery
   * <p>
   * CriteriaQuery : Used to build the query, creates high level query functionality like : select,from,where,groupby,having...
   * https://docs.oracle.com/javaee/6/api/index.html
   * CriteriaQuery.from -> Root
   * <p>
   * A Root instance is created to define a range variable in the FROM clause.
   * The range variable is also used in the SELECT clause as the query result expression.
   */
  public static List<Post> findByXPaginate(Integer pageNum, Long refId, Class<?> refClass) {
    EntityManager em = JPA.em();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Post> cq = cb.createQuery(Post.class);
    Root<Post> root = cq.from(Post.class);

    cq.select(root);

    // Acceptable references to build WHERE expression
    if (!refClass.isAssignableFrom(User.class) && !refClass.isAssignableFrom(Category.class)) {
      return new ArrayList<>();
    }

    // If byClass is Category.class -> check from Category.id
    // If byClass is User.class -> check from User.id
    Root<?> rootBy = cq.from(refClass);
    cq.where(cb.equal(rootBy.get("id"), refId));
    TypedQuery<Post> q = em.createQuery(cq);

    try {
      return Post.paginate(q, pageNum);
    } catch (NoResultException e) {
      return new ArrayList<>();
    }
  }

  /**
   * Dedicated method for category pagination by their slug
   * <p>
   * Here we use JPQL (JPA Query Language) syntax to retrieve matching entries
   * https://en.wikibooks.org/wiki/Java_Persistence/JPQL
   * Using the JOIN FETCH assignment tells Hibernate to fetch data eagerly
   * Else category and user relational data would have been fetched lazily in another statement
   */
  public static List<Post> findByCategory(String categorySlug, Integer pageNum) {
    try {
      return Post.paginate(JPA.em().createQuery("SELECT p FROM Post p\n" +
                      "JOIN FETCH p.category\n" +
                      "JOIN FETCH p.user\n" +
                      "WHERE p.category.slug = :slug ORDER BY p.created DESC ", Post.class)
                      .setParameter("slug", categorySlug),
              pageNum);
    } catch (NoResultException e) {
      return new ArrayList<>();
    }
  }

  /**
   * Here we use JPQL (JPA Query Language) syntax to retrieve matching entries
   * https://en.wikibooks.org/wiki/Java_Persistence/JPQL
   * Using the JOIN FETCH assignment tells Hibernate to fetch data eagerly
   * Else category and user relational data would have been fetched lazily in another statement
   */
  public static List<Post> findByPage(Integer pageNum) {
    try {
      return Post.paginate(JPA.em().createQuery("SELECT p FROM Post p \n" +
                      "JOIN FETCH p.category\n" +
                      "JOIN FETCH p.user\n" +
                      "ORDER BY p.created DESC", Post.class),
              pageNum);
    } catch (NoResultException e) {
      return new ArrayList<>();
    }
  }

  private static List<Post> paginate(TypedQuery<Post> query, Integer pageNum) {
    Integer pagination = Play.current().injector().instanceOf(Configuration.class).getInt("app.pagination");
    return query.setFirstResult((pageNum - 1) * pagination)
            .setMaxResults(pagination)
            .getResultList();
  }

  public String getCreated() {
    return created.format(dtf);
  }

  public String getResume(Integer length) {
    if (length == null) {
      length = 300;
    }
    return this.content.length() > length ? this.content.substring(0, length) + "..." : this.content + "...";
  }

  @Override
  public String toString() {
    return "Post{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", slug='" + slug + '\'' +
            ", content='" + content + '\'' +
            ", created=" + created +
            ", category=" + Optional.ofNullable(category).map(c -> c.name).orElse("null") +
            ", user=" + Optional.ofNullable(user).map(u -> u.username).orElse("null") +
            '}';
  }

  /**
   * Override getter so we get parsed Html content when calling @post.content from scala views
   */
  public String getContentAsHtml() {
    return new PegDownProcessor().markdownToHtml(this.content);
  }

  /**
   * Method called by Form wrapper when Form<Post>::hasErrors is called
   * It can return multiple types of format wether we need to return errors one by one or by list
   */
  public Object validate() {
    return validateOneByOne();
//    return validateByList();
  }

  /**
   * Sample validate method returning one error message only stored in form.globalErrors
   */
  private String validateOneByOne() {
    if (name.trim().isEmpty()) {
      return "Invalid post name";
    }
    // validation succeeded
    return null;
  }

  /**
   * Sample validate method returning multiple error messages stored in form("fieldName").errors
   */
  private List<ValidationError> validateByList() {
    ArrayList<ValidationError> errors = new ArrayList<>();
    if (name.trim().isEmpty()) {
      errors.add(new ValidationError("name", "Invalid name"));
    }
    if (slug.trim().isEmpty()) {
      errors.add(new ValidationError("slug", "Invalid slug"));
    }
    if (content.trim().isEmpty()) {
      errors.add(new ValidationError("content", "Invalid content"));
    }
    return errors.isEmpty() ? null : errors;
  }
}
