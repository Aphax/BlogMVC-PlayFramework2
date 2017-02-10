package models;

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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "comments")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Constraints.Required
  public String username = "";

  @Column(name = "mail")
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

  public String getCreatedAgo() {
    final LocalDateTime now = LocalDateTime.now();
    if (ChronoUnit.YEARS.between(created, now) > 0) {
      return ChronoUnit.YEARS.between(created, now) + " year(s) ago";
    }

    if (ChronoUnit.MONTHS.between(created, now) > 0) {
      return ChronoUnit.MONTHS.between(created, now) + " month(s) ago";
    }

    if (ChronoUnit.DAYS.between(created, now) > 0) {
      return ChronoUnit.DAYS.between(created, now) + " day(s) ago";
    }

    if (ChronoUnit.HOURS.between(created, now) > 0) {
      return ChronoUnit.HOURS.between(created, now) + " hour(s) ago";
    }

    if (ChronoUnit.MINUTES.between(created, now) > 0) {
      return ChronoUnit.MINUTES.between(created, now) + " minute(s) ago";
    }

    return "just now";
  }
}
