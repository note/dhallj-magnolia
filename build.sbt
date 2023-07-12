import Common._

val scala2Version = "2.13.10"
val scala3Version = "3.2.2"

lazy val root =
  project
    .in(file("."))
    .settings(commonSettings("dhallj-magnolia-root"))
    .settings(publishArtifact := false)
    .aggregate(
      dhalljMagnolia.projectRefs: _*
    )

lazy val dhalljMagnolia = (projectMatrix in file("modules/dhallj-magnolia"))
  .settings(commonSettings("dhallj-magnolia"))
  .settings(
    libraryDependencies ++= Seq(
      "org.dhallj"    %% "dhall-scala-codec" % "0.10.0-M2",
      "org.scalameta" %% "munit"             % "0.7.27"    % Test,
      "org.dhallj"    %% "dhall-javagen"     % "0.10.0-M2" % Test
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq("com.softwaremill.magnolia1_3" %% "magnolia" % "1.3.0")
        case _ =>
          Seq(
            "com.softwaremill.magnolia1_2" %% "magnolia"      % "1.1.3",
            "org.scala-lang"                % "scala-reflect" % scala2Version % Provided,
          )
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq("-Xmax-inlines", "64", "-Wunused:all", "-Yretain-trees")
        case _ =>
          Seq.empty
      }
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala2Version, scala3Version))

lazy val benchmark = (projectMatrix in file("modules/benchmark"))
  .settings(commonSettings("benchmark"))
  .settings(
    scalaVersion := scala3Version
  )
  .enablePlugins(JmhPlugin)
  .dependsOn(dhalljMagnolia)
  .jvmPlatform(scalaVersions = Seq(scala3Version))
