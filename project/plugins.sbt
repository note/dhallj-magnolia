val sbtSoftwareMillVer = "2.0.20"
addSbtPlugin("com.softwaremill.sbt-softwaremill" % "sbt-softwaremill-common"  % sbtSoftwareMillVer)
addSbtPlugin("com.softwaremill.sbt-softwaremill" % "sbt-softwaremill-publish" % sbtSoftwareMillVer)
addSbtPlugin("com.eed3si9n"                      % "sbt-projectmatrix"        % "0.9.1")
addSbtPlugin("pl.project13.scala"                % "sbt-jmh"                  % "0.4.6")
