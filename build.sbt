organization := "com.theguardian"

name := "shame-api"

version := "1.0-SNAPSHOT"

resolvers += "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-releases"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.gu" %% "management-play" % "5.26" exclude("javassist", "javassist") exclude("org.scala-stm", "scala-stm_2.10.0"),
  "com.gu.openplatform" %% "content-api-client" % "2.9",
  "org.jsoup" % "jsoup" % "1.7.3"
)

play.Project.playScalaSettings
