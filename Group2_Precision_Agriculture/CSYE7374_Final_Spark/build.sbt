
name := "CSYE7374_Final_Spark"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.4.1"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.4.1"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.4.1"

libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.4.1"

assemblyMergeStrategy in assembly := {
  case PathList("breeze", xs @ _*) => MergeStrategy.first
  case PathList("javax", xs @ _*) => MergeStrategy.first
  case PathList("org","apache", xs @ _*) => MergeStrategy.first
  case PathList("com","google", xs @ _*) => MergeStrategy.first
  case PathList("com","esotericsoftware", xs @ _*) => MergeStrategy.first
  case PathList("scala", xs @ _*) => MergeStrategy.first
  case x if x endsWith("properties") => MergeStrategy.first
//  case PathList("META-INF", "MANIFEST.MF") =>MergeStrategy.discard
  case x =>
    (assemblyMergeStrategy in assembly).value(x)
  //  x => MergeStrategy.first
}