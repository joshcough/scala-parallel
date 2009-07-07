import sbt._

class ScalaParallelProject(info: ProjectInfo) extends DefaultProject(info) with AutoCompilerPlugins {
  val st = "org.scala-tools.testing" % "scalatest" % "0.9.5" % "test->default"

}