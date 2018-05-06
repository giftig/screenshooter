lazy val root = (project in file(".")).settings(
  name := "Screenshooter",
  organization := "com.xantoria",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.1",
  mainClass in Compile := Some("com.xantoria.screenshooter.Main"),
  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.0.0",
  retrieveManaged := true
)
