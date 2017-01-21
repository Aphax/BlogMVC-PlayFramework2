package models;

import play.db.jpa.JPA;
import scala.Tuple2;
import scala.collection.JavaConversions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  @Column(length = 50)
  public String name = "";

  @Column(length = 50)
  public String slug = "";

  @Column(length = 50, name = "post_count")
  public Long postCount = 0L;

  @OneToMany(mappedBy = "category")
  public List<Post> posts;

  public static Category find(Long id) {
    try {
      return JPA.em().createQuery("SELECT c FROM Category c WHERE c.id = :id", Category.class).setParameter("id", id).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public static Category find(String name) {
    try {
      return JPA.em().createQuery("SELECT c FROM Category c WHERE c.slug = :name", Category.class).setParameter("name", name).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public static List<Category> findAll() {
    try {
      return JPA.em().createQuery("SELECT c FROM Category c", Category.class).getResultList();
    } catch (NoResultException e) {
      return new ArrayList<>();
    }
  }

  @Override
  public String toString() {
    return "Category{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", slug='" + slug + '\'' +
            ", postCount=" + postCount +
            '}';
  }
}
