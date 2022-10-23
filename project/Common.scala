import com.softwaremill.Publish.ossPublishSettings
import com.softwaremill.SbtSoftwareMillCommon.autoImport.commonSmlBuildSettings
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys.{developers, name, organization, scalaVersion, testFrameworks, version}
import sbt.{Project, TestFramework}
import xerial.sbt.Sonatype.autoImport.{sonatypeProfileName, sonatypeProjectHosting}
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype.GitHubHosting

object Common {

  implicit class ProjectFrom(project: Project) {

    def commonSettings(nameArg: String): Project = project.settings(
      name := nameArg,
      organization := "pl.msitko",
      scalaVersion := "2.13.10",
      scalafmtOnCompile := true,
      commonSmlBuildSettings,
      testFrameworks += new TestFramework("munit.Framework"),
      ossPublishSettings ++ Seq(
        sonatypeProfileName := "pl.msitko",
        organizationHomepage := Some(url("https://github.com/note")),
        homepage := Some(url("https://github.com/note/dhallj-magnolia")),
        sonatypeProjectHosting := Some(
          GitHubHosting("note", name.value, "pierwszy1@gmail.com")
        ),
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
