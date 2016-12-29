name := "DDoS-Detection-System"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-opt:l:classpath",
  "-deprecation", "-unchecked", "-feature", "-Xlint", "-Ywarn-infer-any")

libraryDependencies ++= Seq(
  "org.log4s" %% "log4s" % "latest.release",
  "ch.qos.logback" % "logback-classic" % "latest.release",
  "org.scalaj" %% "scalaj-http" % "latest.release",
//  "io.spray" %%  "spray-json" % "latest.release",
  "org.json4s" %% "json4s-native" % "latest.release",
  "org.specs2" %% "specs2-core" % "latest.release" % "test",
  "org.specs2" %% "specs2-gwt" % "latest.release" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Xmx1g")