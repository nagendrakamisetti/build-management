Build Management Tools
======================

# Overview #
The build management repository contains a collection of tools and
applications for creating an enterprise build and release infrastructure.
The central focus of this project is to utilize a relational database to
capture build information.  Tools and applications are provided to populate
the database with build content, as well as to view and manage the build
content and artifacts. 

## Project Goals ##
The goal of this project is not to reinvent the type of scheduling
automation or code analysis tools already available, it is to provide
an infrastructure for capturing historical build information and tools 
for performing enterprise-level tasks using that data.  To achieve that
objective, the build management database serves as the focal point for
the project.  

* Create a scalable infrastructure for capturing and managing build 
  information.
* Provide integration with 3rd party such as source control, bug tracking, 
  continuous integration, and automated test frameworks.
* Provide visibility into historical build results and trends.

## Project Components ##
This project uses Maven to provide the structure and tools for building
and packaging project artifacts.  This is a multi-module project composed
of the following components: 

* Core API
* Ant Tasks
* Service Patch Tool
* Build Management Web Application

Each of these components can be used independenly or as a complete suite
of tools.  For details on each project, please read the README files in
each project directory, or view the project documentation found on the
project website.

## Project Documentation ##

A copy of the latest project documentation is available on the [project
website].  Project documentation can also be generated locally using Maven:

    mvn package
    mvn install
    mvn site

# System Requirements #

The following are required if building this project from source:

* Maven 3.0+ 
* JDK 1.6

Please refer to the documentation for each sub-module for the list of
requirements specific to that project.

## Pre-requisites ##

The following pre-requisites must be available in your local environment
if you wish to compile and package this project using Maven:

1. Install Oracle JDBC jar into the local Maven repository (*required*)

   Obtain a local copy of the JDBC jar (either from the installed
   database or by downloading directly from Oracle).

        mvn install:install-file -DgroupId=com.oracle -DartifactId=oracle -Dversion=11.2.0.4.0 -Dpackaging=jar -Dfile=ojdbc6.jar

2. Install the native `rpmbuild` command (*optional*) 

   Some project artificates have been configured to be generate an RPM file,
   which makes it much easier to deploy on some Linux systems.  If you would
   like to generate RPMs for projects which support it, you will need to 
   ensure that your system has the `rpmbuild` command available.  Here is an
   example of how to install that command on a RedHat system:
 
        yum install rpm-build

# Installation #

Each module within this project produces a separate build artifact.  Each
artifact will have it's own unique installation requirements.  The following
table illustrates the type of artifact produced by each module.

| Project          | Output      |
| ---------------- | ----------- |
| mn-build-core    | jar         |
| mn-build-ant     | jar         |
| mn-build-patch   | jar, rpm    |
| mn-build-webapp  | war         |


## Downloading ##

Individual project components may be available for download from the
[project website].

## Compiling from Source ##

Clone the repository

    git clone ssh://pdgit.modeln.com:8081/buildmanagement.git

Run a build

    mvn package


### Profiles ###

Maven profiles can be used to alter the behavior of the build process.
In this project, profiles are used to select the type of project 
artifact to generate during the build.  For example, by default a 
project may produce a `jar` file.  However, for ease of deployment,
the project may provide an `rpm` profile to produce an additional 
RPM file which can be used to easily deploy the project binaries to
a Linux system.

A Maven profile can be selected during the build process by using 
the following command-line syntax:

    mvn package -P <profile>

The following Maven profiles are supported:

#### rpm ####

If applicable, the `rpm` profile can be used to generate RPMs
as an artifact of the build.  To do this, ensure that you have
the rpmbuild executable available on your system (see the 
system pre-requisites listed above for details on how to 
install it).  

#### installer ####

If applicable, the `installer` profile can be used to generate
an install package.  This provides a platform-independent 
user interface for installing the project.



# References #

* The Build Management Tools [project website]
 
[project website]: http://buildmanagement.modeln.com

