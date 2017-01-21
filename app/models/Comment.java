package models;

import helpers.TimeAgo;
import org.hibernate.validator.constraints.NotEmpty;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @NotNull
  @Constraints.Required
  public String username = "";

  @Column(name = "mail")
  @NotNull
  @Constraints.Email
  @Constraints.Required
  public String email = "";

  @NotEmpty
  @Constraints.Required
  public String content = "";

  @Formats.DateTime(pattern = "Y-m-d h:i:s")
  public LocalDateTime created = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "post_id", referencedColumnName = "id")
  public Post post;

  public String toAgo() {
    return TimeAgo.toDuration((LocalDateTime.now().getNano() - this.created.getNano()));
  }

  public static class CommentForm {
    public String email;
    public String username;
    public String content;

    public CommentForm() {
    }

    public CommentForm(String email, String username, String content) {
      this.email = email;
      this.username = username;
      this.content = content;
    }

    @Override
    public String toString() {
      return "CommentForm{" +
              "email='" + email + '\'' +
              ", username='" + username + '\'' +
              ", content='" + content + '\'' +
              '}';
    }

    public Comment toComment(Post post) {
      Comment comment = new Comment();
      comment.id = null;
      comment.content = this.content;
      comment.username = this.username;
      comment.email = this.email;
      comment.post = post;
      comment.created = LocalDateTime.now();
      return comment;
    }
  }
}
