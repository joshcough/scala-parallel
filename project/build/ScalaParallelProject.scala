import sbt._

class ScalaParallelProject(info: ProjectInfo) extends DefaultProject(info) with AutoCompilerPlugins {
  override def crossScalaVersions = Set("2.7.2", "2.7.3", "2.7.4", "2.7.5")

  val st = "org.scala-tools.testing" % "scalatest" % "0.9.5" % "test->default"

}