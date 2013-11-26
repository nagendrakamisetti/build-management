Service Patch Tool
==================

# Overview #
The service patch tool is a command-line utility for creating
product service patches.  A "service patch" is a product build
intended for limited release to a limited customer audience.
For example, if a high-profile customer encounters a critical
product bug and cannot upgrade to the next product release 
(or the fix has not been released), it may be necessary to
produce a custom build to the customer.  That build would be
branched off of the released code currently available to the
customer and then the additional product fix(es) would be
integrated into the branch.

This tool helps automate some of the tedious aspects of the
service patch process, such as:

* Obtaining necessary build information from the build database
* Using a bug tracking-to-source control relationship to obtain
  the list of check-ins
* Sequencing the check-ins in the correct chronological order
* Determining which check-ins have already been included in the
  branch.
* Using template files to generate the necessary build scripts
  and configuration files.

# System Requirements #

The following are required when using this project:

* JDK 1.6+ 

# Installation #

The `mn-build-patch` project produces a jar file as it's primary 
artifact.  In order to provide a meaningful deployment package, 
additional Maven plug-ins are invoked during the build to generate 
the shell scripts used to invoking the tool from the command-line.
To facilitate an easy deployment, Maven profiles can be used to 
generate an RPM or install package appropriate for your target
platform.

To install the RPM, download the RPM to your local system and 
run either:

    rpm -i mn-build-patch.rpm

OR

    yum install mn-build-patch.rpm

 
## Downloading ##

The mn-build-patch jar, rpm, and installer can be available for 
download from the [project website].

## Compiling from Source ##

Clone the repository

    git clone ssh://pdgit.modeln.com:8081/buildmanagement.git

Run a build

    cd mn-build-patch
    mvn package

Generate an RPM (optional)

    mv package -P rpm


# References #

* The Build Management Tools [project website]
 
[project website]: http://buildmanagement.modeln.com

