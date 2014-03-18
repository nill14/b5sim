name := "b5sim"

scalaVersion := "2.10.3" 

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(        
  "org.scala-lang" % "scala-swing" % "2.10.3",
    "org.scala-lang" % "scala-reflect" % "2.10.3",
    "com.netflix.rxjava" % "rxjava-scala" % "0.15.1",
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "junit" % "junit" % "4.10" % "test"      
  )
  
mainClass := Some("b5sim.gui.B5app")  
  
retrieveManaged := true  
  
EclipseKeys.relativizeLibs := true

EclipseKeys.withSource := true

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource