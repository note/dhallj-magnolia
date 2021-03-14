import Common._
import Dependencies._

lazy val root = (project in file("."))
  .commonSettings("dhallj-magnolia")
  .settings(
    libraryDependencies ++= compileDeps(scalaVersion.value) ++ testDeps
  )
