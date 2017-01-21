name := "BlogMVC-PlayFramework"

version := "1.0"

lazy val `root` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

// Project dependencies (ivy or maven artefacts)
libraryDependencies ++= Seq(
  // Internal dependencies directly handled by Play
  jdbc, javaJpa, cache, javaWs, filters, specs2 % Test,
  "org.apache.commons" % "commons-lang3" % "3.4",
  // The core O/RM functionality as provided by Hibernate
  // If you want to update the version please read first this Stack overflow answer
  // Stack overflow topic : http://stackoverflow.com/questions/38278199/play-framework-inject-error
  // Starting from 5.2.1 there is a bug between sbt and maven transitive dependencies
  // Bug report : https://github.com/sbt/sbt/issues/1431
  "org.hibernate" % "hibernate-core" % "5.2.0.Final",
  // MySQL JDBC Type 4 driver
  "mysql" % "mysql-connector-java" % "5.1.40",
  // A Java 1.6+ library providing a clean and lightweight markdown processor
  "org.pegdown" % "pegdown" % "1.6.+"
)

// Hack : Running Play in development mode while using JPA
// See : https://playframework.com/documentation/2.5.x/JavaJPA#deploying-play-with-jpa
PlayKeys.externalizeResources := false