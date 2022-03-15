scalaVersion := "2.12.8"
import scoverage.ScoverageKeys._

lazy val monixVersion = "3.0.0-RC3"

lazy val root = (project in file("."))
  .settings(
    name := "uos",
    coverageEnabled := true,
    libraryDependencies  ++= Seq(
      "org.json4s" %% "json4s-native" % "3.6.0",
      "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC1",
      "com.softwaremill.sttp.client" %% "json4s" % "2.0.0-RC1",
      "com.github.andyglow" %% "websocket-scala-client" % "0.2.4",
      "io.monix" %% "monix" % monixVersion,
      "org.scala-js" %% "scalajs-dom_sjs0.6" % "0.9.7",
      "io.javalin" % "javalin" % "3.6.0",
      "org.scalanlp" %% "breeze" % "1.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalactic" %% "scalactic" % "3.2.0",
      "org.scalatest" %% "scalatest" % "3.2.0" % "test"
    )
  )

