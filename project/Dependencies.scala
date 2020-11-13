import sbt._

object Dependencies {
	object Versions {
		val Dhall = "0.4.0"
		val Magnolia = "0.17.0"
		val Munit = "0.7.17"
	}

	def magnolia(scalaVersion: String) 	= Seq(
		"com.propensive" %% "magnolia" 			% Versions.Magnolia,
		"org.scala-lang" % "scala-reflect" 	% scalaVersion % Provided
	)

	def compileDeps(scalaVersion: String) = Seq(
		"org.dhallj" %% "dhall-scala-codec" % Versions.Dhall
	) ++ magnolia(scalaVersion)

	lazy val testDeps = Seq(
		"org.scalameta" %% "munit" 					% Versions.Munit % Test,
		"org.dhallj" 		%% "dhall-javagen" 	% Versions.Dhall
	)
}