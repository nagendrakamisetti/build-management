Build Management API
====================

# Overview #
The build management core API libraries provide a common set of Java API
that can be leveraged by the other build management tools and applications.

# System Requirements #

See the system requirements and pre-requisites for the parent project.

# Installation #

The `mn-build-core` project produces a jar file which is not intended
to be used as a stand-alone artifact.  The jar will be used as a 
dependency for the other build management projects.  If you wish to
compile or download the core API portion of the project, please 
follow the instructions below.

## Downloading ##

The  may be available for download from the
[project website].

## Compiling from Source ##

Run a Maven build from the root directory:

    mvn package -pl mn-build-core -am


# References #

* The Build Management Tools [project website]
 
[project website]: http://buildmanagement.modeln.com

