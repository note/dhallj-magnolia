import Common._
import Dependencies._

lazy val root = (project in file("."))
  .commonSettings("dhallj-magnolia", "0.1.0")
  .settings(
    libraryDependencies ++= compileDeps(scalaVersion.value) ++ testDeps,
    publishTravisSettings,
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
  )
