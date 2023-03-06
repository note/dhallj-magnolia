val sbtSoftwareMillVer = "2.0.12"
addSbtPlugin("com.softwaremill.sbt-softwaremill" % "sbt-softwaremill-common"  % sbtSoftwareMillVer)
addSbtPlugin("com.softwaremill.sbt-softwaremill" % "sbt-softwaremill-publish" % sbtSoftwareMillVer)
addSbtPlugin("com.eed3si9n"                      % "sbt-projectmatrix"        % "0.9.0")
addSbtPlugin("pl.project13.scala"                % "sbt-jmh"                  % "0.4.4")
