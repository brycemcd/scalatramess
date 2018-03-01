name := "reading-be"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Classpaths.typesafeReleases

val ScalatraVersion = "2.6.+"
libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s"   %% "json4s-jackson" % "3.5.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.8.v20171121" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "com.github.seratch" %% "awscala" % "0.6.+",
  "org.scalikejdbc" %% "scalikejdbc"       % "3.2.1",
  "com.h2database"  %  "h2"                % "1.4.196",
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3",
  "org.postgresql" % "postgresql" % "42.2.1",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)