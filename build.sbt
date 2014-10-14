organization := "hr.element.clusterpoc"

name := "Cluster PoC"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster"    % "2.3.6"
, "com.typesafe.akka" %% "akka-slf4j"      % "2.3.6"
, "ch.qos.logback"    %  "logback-classic" % "1.1.2"
)
