name := "HttpWebServer"

version := "1.0"

scalaVersion := "2.11.8"

javaOptions ++= Seq("--source", "1.8", "--target", "1.8")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "org.slf4j" % "slf4j-log4j12" % "1.2",
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "junit" % "junit" % "4.10"
)

mainClass in (Compile, run) := Some("com.randombulbs.webserver.WebServer")
