Service Patch Tool
==================

# Overview #


# System Requirements #

The following are required when using this project:

* JDK 1.6+ 

## Pre-requisites ##

The following pre-requisites must be available in your local environment
if you wish to compile and package this project using Maven:

1. Install D3 JavaScript distribution into the local Maven repository (*required*)

   Obtain a local copy of the D3 zip (either from the installed
   database or by downloading directly from http://d3js.org/).

        mvn install:install-file -DgroupId=org.d3js -DartifactId=d3js -Dversion=3.4.1 -Dpackaging=zip -Dfile=/path/to/file/d3js.zip -DgeneratePom=true


# Installation #

 
## Downloading ##

The mn-build-patch war can be available for 
download from the [project website].

## Compiling from Source ##

Run a Maven build from the root directory:

    mvn package -pl mn-build-webapp -am


# References #

* The Build Management Tools [project website]
 
[project website]: http://buildmanagement.modeln.com

