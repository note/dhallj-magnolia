import com.softwaremill.Publish.ossPublishSettings
import com.softwaremill.SbtSoftwareMillCommon.autoImport.commonSmlBuildSettings
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys.{name, organization, scalaVersion, testFrameworks, version}
import sbt.{Project, TestFramework}

object Common {
  implicit class ProjectFrom(project: Project) {
    def commonSettings(nameArg: String, versionArg: String): Project = project.settings(
      name := nameArg,
      organization := "pl.msitko",
      version := versionArg,

      scalaVersion := "2.13.3",
      scalafmtOnCompile := true,

      commonSmlBuildSettings,
      testFrameworks += new TestFramework("munit.Framework"),
      ossPublishSettings
    )
  }
}