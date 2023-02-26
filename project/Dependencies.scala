import sbt._

object Dependencies {

  object Versions {
    val Dhall    = "0.10.0-M2"
    val Magnolia = "0.17.0"
    val Munit    = "0.7.27"
  }

  def magnolia(scalaVersion: String) = Seq(
    "com.softwaremill.magnolia1_2" %% "magnolia"     % "1.1.3",
    "org.scala-lang"               % "scala-reflect" % scalaVersion % Provided
  )

  def compileDeps(scalaVersion: String) =
    Seq(
      "org.dhallj" %% "dhall-scala-codec" % Versions.Dhall
    ) ++ magnolia(scalaVersion)

  lazy val testDeps = Seq(
    "org.scalameta" %% "munit"         % Versions.Munit % Test,
    "org.dhallj"    %% "dhall-javagen" % Versions.Dhall % Test
  )
}
