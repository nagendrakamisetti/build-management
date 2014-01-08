Ant Tasks
=========

# Overview #
This module contains a collection of Ant tasks.  The following tasks 
are included in this project:

## General Tasks ##

* `cat` Echo the contents of a file to stdout.

* `checkresource` Determines if a resource is available to a forked 
  instance of the JVM.

* `filecount` Counts the total number of files in a file set.

* `fileinfo` Obtains file information about a specific file and stores 
  the information in Ant properties.

* `lock` Creates a lock file to ensure that other Ant scripts cannot
  execute their tasks until the lock is removed.

* `hostname` Obtains network information about a specific host and stores 
  the information in Ant properties.

* `notification` Send e-mail notification. 

* `selectregexp` Performs regular expression search for matching text of a given file.

* `timeout` Construct a timeout listener that will terminate the build if there
  is no build activity within a specified amount of time.

* `increment` Increments the value of a property.

* `listdeps` Constructs a list of resources and the classes that depend on them.

* `sysprop` Obtain the value of a Java system property and store the value
  as an Ant property.

* `toupper` Converts the string case to all upper case characters.

* `tolower` Converts the string case to all lower case characters.

* `pdfmetadata` Reads PDF metadata from the file and save the values in properties.

* `propchop` Removes the specified string from the property value.

* `trim` Trims any leading and trailing whitespace from a property value.

* `diffreport` Generates a report of all the differences between the contents of two
  directories or Jar files.

* `repeat` Executes a set of tasks in a loop until a condition is met.


## Flex ##

* `flexmf` Generate a manifest file from the list of files provided.

* `flexconf` Generate a configuration file used when invoking the flex compiler.


## Encryption Keys ##
 
* `keygen` Generates a symetric encryption key.

* `keypairgen` Generates a public and private key pair.

* `encrypt` Uses symetric key encryption to encrypt a message.

* `decrypt` Uses symetric encryption algorithms to decrypt an encrypted message.

* `verifykey` Uses a message signature and public key to verify that the
  message has not been tampered with.

## Perforce ##

* `p4changes` Obtains information about the most recent changelist.

* `p4nsync` Obtains a list of outdated files that need to be retrieved
  from Perforce.

* `p4mncounter` Operates on a Perforce counter.

* `p4blamereport`  Registers an event listener that processes log
  errors and sends an e-mail message with a summary of the
  results if the build fails.

* `p4blamedelete` Removes all p4blamereport listeners from the build.

## Ant Event Logging ##

* `dbprogress` Parses the Ant logging events and inserts database rows
  as output lines are found to match progress targets.

* `dbreport` Parses the Ant logging events and generates a report to
  summarize the event contents.

* `livereport` Parses the Ant logging events and generates a report to 
  summarize the event contents.

* `logreport` Parses an Ant log file.



# System Requirements #

The following are required when using this project:

* Apache Ant 1.8.2+

# Installation #

The `mn-build-ant` project produces a jar file which is not intended
to be used as a stand-alone artifact.  The contents of the jar will
be used by Ant when your script references the Ant tasks.  In order
for Ant to locate these Ant tasks, a task definition must be included
in your Ant scripts. 
 
If the mn-build-ant jar is copied into the `$ANT_HOME/lib` directory
or similar classpath location, the following line can be used within
your Ant scripts:

    <taskdef resource="com/modeln/build/ant/taskdef.properties"/>

If the mn-build-ant jar is not located in the Ant classpath, the taskdef
must reference the full path of the jar:

    <taskdef resource="com/modeln/build/ant/taskdef.properties">
      <classpath>
        <pathelement location="/opt/mn/lib/mn-build-ant.jar"/>
      </classpath>
    </taskdef>


## Downloading ##

The mn-build-ant jar can be available for download from the
[project website].

## Compiling from Source ##

Run a Maven build from the root directory:

    mvn package -pl mn-build-ant -am



# References #

* The Build Management Tools [project website]
 
[project website]: http://buildmanagement.modeln.com

