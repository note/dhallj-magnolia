import com.softwaremill.Publish.ossPublishSettings
import com.softwaremill.SbtSoftwareMillCommon.autoImport.commonSmlBuildSettings
import com.typesafe.sbt.SbtPgp.autoImportImpl.PgpKeys
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys.{developers, name, organization, scalaVersion, testFrameworks, version}
import sbt.{Project, TestFramework}
import xerial.sbt.Sonatype.autoImport.{sonatypeProfileName, sonatypeProjectHosting}
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction

object Common {
  implicit class ProjectFrom(project: Project) {
    def commonSettings(nameArg: String, versionArg: String): Project = project.settings(
      name := nameArg,
      organization := "pl.msitko",
      version := versionArg,

      scalaVersion := "2.13.3",
      scalafmtOnCompile := true,
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,

      commonSmlBuildSettings,
      testFrameworks += new TestFramework("munit.Framework"),
      ossPublishSettings ++ Seq(
        sonatypeProfileName := "pl.msitko",
        licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
        developers := List(
          Developer(
            id = "note",
            name = "Michał Sitko",
            email = "pierwszy1@gmail.com",
            url = new URL("https://github.com/note/")
          )
        )
      )
    )
  }
}