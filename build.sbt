name := "Project for GUI at VIKO - Menu"

version := "0.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation")

mainClass := Some("lt.labrencis.menu.MainApp")

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.3"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "0.8.0"

packSettings

packMain := Map("GUIMenu" -> "lt.labrencis.menu.MenuApp")
