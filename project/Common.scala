import com.softwaremill.SbtSoftwareMillCommon.commonSmlBuildSettings
import com.softwaremill.Publish.ossPublishSettings
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys.{developers, name, organization, testFrameworks}
import sbt.{Project, TestFramework}
import xerial.sbt.Sonatype.autoImport.{sonatypeCredentialHost, sonatypeProfileName, sonatypeProjectHosting}
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype.GitHubHosting

object Common {
  def commonSettings(nameArg: String): Seq[Def.Setting[_]] = commonSmlBuildSettings ++ Seq(
    name := nameArg,
    organization := "pl.msitko",

    scalafmtOnCompile := true,

    testFrameworks += new TestFramework("munit.Framework"),
  ) ++ ossPublishSettings ++ Seq(
    sonatypeProfileName := "pl.msitko",
    organizationHomepage := Some(url("https://github.com/note")),
    homepage := Some(url("https://github.com/note/dhallj-magnolia")),
    sonatypeProjectHosting := Some(
      GitHubHosting("note", name.value, "pierwszy1@gmail.com")
    ),
    sonatypeCredentialHost := "oss.sonatype.org",
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
}
