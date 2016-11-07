name := "DDoS-Detection-System"

version := "1.0"

scalaVersion := "2.12.0"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-opt:l:classpath",
  "-deprecation", "-unchecked", "-feature", "-Xlint", "-Ywarn-infer-any")

libraryDependencies ++= Seq(
  "org.log4s" %% "log4s" % "1.3.3",
  "ch.qos.logback" % "logback-classic" % "1.0.13"
)

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Xmx1g")