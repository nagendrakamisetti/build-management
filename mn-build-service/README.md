Monday, January 6, 2014 2:59 PM
Gregory Zussa
Hi Shawn
happy new year
Shawn Stafford
hi
Gregory Zussa
I saw you filed bugs for the management tool on github
do you have a roadmap regarding this tool and related intiatives around the build?
Basically it looks like i won't have much to do in coreapp
Shawn Stafford
No, just a few enhancements in mind.
Gregory Zussa
or let's turn it this way. There are things to do in coreapp but we thing taht i could be of better help working on these tools
Shawn Stafford
On the service patch tools?
Gregory Zussa
so basically I am willing to help your team and help the company in order to increase code quality 
Actually i am more interested by the build report tool in general
not teh service patch
Shawn Stafford
ok, in that case I guess we would need to come up with a list of things we want to do.
Gregory Zussa
basically add the features taht developer needs
understand why they don't use the build 
The main goal is to create tools and rules that will make us ahave a clean master branch all the time
So if you have feedbacks from users on what to changes
I would like to know about it
exactly
As part of the feature I see I would like to integrate kevin and Kalistick tool
maybe change the ui and design so it's more user friendly
add more notification capabilities
authentication if needed so we deploy and access the service outside of teh vpn
stuff like taht
Gregory Zussa
If i want to clone the project do i have to use the modeln git or can i use the github one?
i prefer the github one, Is the code the same?
Shawn Stafford
use the github one.
Gregory Zussa
ok
Shawn Stafford
I thought I'd deleted the code from the modeln repo
Gregory Zussa
what kind of server to i need to run the app?
tomcat?
Shawn Stafford
I use tomcat.  Any server would probably work
I wanted to set up the maven scripts to run it from Tomcat or Jetty within the script.
Gregory Zussa
i would like to be able to develop on my linux or my laptop in order to avoid vpn connection all the time
ok
Shawn Stafford
I have it running on my mac mini (under linux)
If you want the entire app set up, there are 3 main requirements: Tomcat app server, MySQL DB, and Oracle XE (if you want to use SDTracker integration).
Gregory Zussa
oh the main db is a mysql de?
ok
Shawn Stafford
Yes, it uses MySQL as the DB.
Gregory Zussa
ok so i need to install mysql on my box as well
What should I do in oder to install mysql on my desktop?
Shawn Stafford
Yes, unless we want to try to set up some other option.  I've only tested it with MySQL.
Gregory Zussa
I believe i can use the tomcat already on my system
it will go for my desktop
i just can't work on windob

in order to push request do i have to fork on github first?
make my change on my forked repo and then push merge request
Shawn Stafford
I cloned the repository locally and then pushed back to the origin.  You'll have to ask John E for permission to push
Gregory Zussa
ok so he doesn't care about how we do this
Tuesday, January 7, 2014 11:30 AM
Gregory Zussa
sorry about yesterday I had to go in a meeting and so i could finish my conversation with you
so regarding john 
he doesn't care about how we do this
basically it depend if you want me to sak for pull request
or if you want me to directly commit to the main 
also I noticed we have hudson running. How are we using it exactly
is hudson managing builds execution (kick them off) only
and so the build management tool is then used as a reporting tool
I believe we can have the same features taht are i the build management tool directly in hudson, can't we?
Shawn Stafford
Just a sec, I'm on a call with Yilun
Gregory Zussa
ok
can we have a call after?
just send me your phone number when you are ready
Shawn Stafford
650-454-4580
Wednesday, January 8, 2014 11:54 AM
Gregory Zussa
Hi shawn
did you make sure you were able to compile before to push your code on github?
Some classes are missing and some package don't exist
Shawn Stafford
Yes, I believe so.  What error are you getting?
Gregory Zussa
The CmnActSyuiteData is importing a class called RepositoryConnection
this class is supposed to be in a folder called
com.modeln/buil.web.database
the database package doesn't exist
Shawn Stafford
What maven command are you running when you compile?
Gregory Zussa
oh it's not database it should be db
i found the class
but the package name is not correct
lol how were you able to compile??
Shawn Stafford
I'm coding right now with no compilation errors.
Gregory Zussa
well for sure there are package name error
Right now i am solving eclipse error message
i imported the project on my eclipse
i solved the issue but it's weird that you don't have the same one
some java files are importing file from package that don't exist
Shawn Stafford
The package name in the class file is correct, it's just the directory name that doesn't match
Gregory Zussa
for instance the com.modeln.buil.web. database package doesn't exist on git hub and is called instead com.modeln.build.web.db
yes
anyway i fixed it
Shawn Stafford
That wouldn't produce a compilation error.
(It's not clean and still needs to be moved to the correct directory name to match the package name, but it won't affect compilation)
Gregory Zussa
I didn't compile yet i am solving eclipse errors
Shawn Stafford
Are you compiling using maven or Eclipse?
Gregory Zussa
haven't tried yet
let me try
mvn package works 
great
Shawn Stafford
I pushed a few changes to github just now.
I don't think it will affect the classes you updated.
Gregory Zussa
sorry i was on a call
yes no problem i will sync
Are you using any specific version of tomcat?
Shawn Stafford
I'm using Tomcat 5
JDK 1.7
Gregory Zussa
ok I install tomcat via Eclipse. I am not able to publish my war on the server. It's failing. I need to take a look. Instead of using the mysql db is it possible to use a inMemory database?
Shawn Stafford
I don't know, I've never tried.  
Gregory Zussa
ok
where is the property file to configure db connections?
Shawn Stafford
mn-build-webapp/src/main/webapp/WEB-INF/config/common.cfg
Gregory Zussa
perfect 
thanks
Shawn Stafford
You can configure it to use straight JDBC or to use the container managed datasources using JNDI.  Examples of both are in the config file.
I usually use JNDI in production and JDBC locally.
Gregory Zussa
ok
does tomcat had an admin ui?
a graphical interface to manage your deployed war and ear?
Shawn Stafford
It does.  You'll have to check the Tomcat website.  I've never used it.
Gregory Zussa
ok what url do you use to access the app
localhost:8080 doesn't work for me
Shawn Stafford
Try http://localhost:8080/mn-build-webapp/command/CMnOps
Gregory Zussa
Is it the entry point of the application?
it work i see a login form
Shawn Stafford
No, there are lots of entry points
Gregory Zussa
ok so now i need to install the database
Shawn Stafford
Or configure it to connect to a db on a different host.
Gregory Zussa
ok but what's the welcome pages?
looks like you don't have any in your web.xml
ok do you have any db i can use?
Shawn Stafford
You can connect to the main production instance if you want to see the nightly build data, or you can connect to a dev instance if you just want to view some data that developers generated.
Gregory Zussa
Are developers installing the app on their dev box? Why would they have their own build management app?
Shawn Stafford
when a developer runs the nightly build Ant scripts to run a full nightly build, it logs the unit test results to the MySQL instance on pddev.modeln.com
They're not running an instance of this web app, they're just having the build write test results to the DB.
Gregory Zussa
oh really!
Shawn Stafford
Then then can go to http://pddev.modeln.com/ctrl/command/report/CMnReleaseList to view the results.
Gregory Zussa
so you have a db on this pddev box taht collect data from developers
i see
Shawn Stafford
yes
Gregory Zussa
ok so you have two instance of this app running 
one for dev and one for nightly builds
i see
Shawn Stafford
Yes, we have multiple separate MySQL instances.
There are 2 at HQ and two at MNI.
Gregory Zussa
can you send me tns info for both?
Shawn Stafford
Production is jdbc:mysql://pdbuilds.modeln.com:3306/mn_build
Gregory Zussa
Also is the login mechanism sync to IT security or is it completely separated
it looks separeted 
the app has it's own user account model
it's funny i don't remember creating a user account ever
Shawn Stafford
Dev is jdbc:mysql://pddev.modeln.com:3306/mn_build
The app authenticates against it's own internal database.
Gregory Zussa
ok
Shawn Stafford
Be default all the instances are configured to authenticate against a central instance (so accounts don't have to be managed on different systems)
Gregory Zussa
nice  
you did some very good job here 
i like this project  
Shawn Stafford
Let me know if you want to try HSQL as an in memory database option.
Gregory Zussa
why not but i won't have any data
as of now let me get familiar with all the code and features first
Shawn Stafford
ok.  I'd be interested in doing it as a way to implement integration unit tests.
Gregory Zussa
i saw some unit tests but looks like they are very simple and limited to very targetted area
Shawn Stafford
I just created one unit test today to test the password changes.  Up until now there were no unit tests.
Gregory Zussa
oh ok
Gregory Zussa
looks like the app is not able to find the jdbc driver
Shawn Stafford
It's not bundled in the war file. You have to download it and put it on the lib dir
Gregory Zussa
why do i need to add it. it looks like teh dependency is defined in the pom.xml
Shawn Stafford
It's defined as "provided" or "runtime" which means it doesn't get packaged.
Gregory Zussa
i keep getting this issue 
do you know what it is refering to?
Original stack trace: com.modeln.build.web.errors.ApplicationException: Could not retrieve a connection to the database.
at com.modeln.build.web.application.WebApplication.getRepositoryConnection(WebApplication.java:1707)
Shawn Stafford
Is that in the app log file or the tomcat log file?
Gregory Zussa
tomcat
Shawn Stafford
The app should be generating a log file
check /var/log/buildmgt/common.log
unless you changed the default log directory in the config file
Gregory Zussa
no it does
i have the same log i get on tomcat
regarding the msql connector
Shawn Stafford
can I ssh to your machine to look at the log?
Gregory Zussa
to what lib dir should i put it
yes
right now i just changed the pom to include it in the package
Shawn Stafford
what's the hostname?
Gregory Zussa
pdgzussa.modeln.com
Shawn Stafford
Caused by: java.net.ConnectException: Connection refused
MySQL is blocking the connection
Gregory Zussa
ok where did you add the connector?
Shawn Stafford
It's not an issue of a missing library.  It's a mysql access control issue.
Where is your config file so I can look at the settings?
Gregory Zussa
dev/build-management/build-management
Shawn Stafford
actually, I guess I don't need to.  The log has the DB info
let me try to connect using the mysql command-line client.
Gregory Zussa
oh maybe i didn't update the correct property
they are multiple property dor msql config
which on should i use?
the default.db one?
Shawn Stafford
ok, I see what the problem is
It's configured to use the Tomcat JDBC pool
You need to change this:  default.repository=default.jndipool
to this:  default.repository=default.db
That will tell the app to use the default.db.xxx properties to determine how to connect to the default (data) repository.
Otherwise you'd need to configure tomcat to create a data source.
Gregory Zussa
what is the account.db.XXX properties?
bugtest.db correspond to sdr tracker db i believe
patch.db to some other database related to the patch service
so it account.db the common db that is used for login
and default.db the actual instance db?
Shawn Stafford
yes, account.db is used for authentication.  default.db is where it will look for application data such as builds, etc.
This app has to pull information from 4 or 5 different databases, which is why there are so many database configs.
Gregory Zussa
ahah
Shawn Stafford
And then there's the option to configure each as jdbc, jndi, etc.
Gregory Zussa
in logs i am also getting a lot of issue like this one
********************* CommandTreeNode ***********************
Could not locate class: com.modeln.build.ctrl.command.default.US.en.CMnLogin
java.lang.ClassNotFoundException: com.modeln.build.ctrl.command.default.US.en.CMnLogin
it tries 3 location and finally find the right one the forth time
what's happening? why are we looking at some many different place everytime we do a backend call to a servlet
?
Shawn Stafford
In the app log?
Gregory Zussa
no in tomcat
app logs don't show this issue
ok it's working now the connection is good
are you using the jndi way at all?
Shawn Stafford
It's an overriding mechanism in the app.  It's a way to define a user or group specific implementation.  So the app searches for an implementation that is "most specific" and then starts falling back to progressively more generic versions until it finds a match.
It's kind of like the way that resource bundles work in Java.
But instead of property files, it's doing it for class files.
I only use JNDI in production to take advantage of connection pooling.  Locally I just use jdbc for dev/testing.
Gregory Zussa
i see
Shawn Stafford
It's more of a pain to use JNDI because you have to configure the data source in tomcat and you have to make sure the jdbc jars are in the Tomcat lib directory (instead of the war)
Gregory Zussa
when you don't have an account where should i go to create one?
yes i know what you means
I beleve i created ant files long ago in order to set this up automatically in some of my old j2ee projects
i will take a look at it and see it i can make this configuration easier
Shawn Stafford
ok, I've created an account for you
username:  gzussa
password:  modeln
Gregory Zussa
oh you can't do it through the UI?
Shawn Stafford
There's an admin page for it if you're part of the admin group
Gregory Zussa
oh i see
what db is used to save the account?
i changed the account.db to pddev
but looks like it's not stored there
Shawn Stafford
oh, i saved the account in pdbuilds.modeln.com
Gregory Zussa
ok lol
Shawn Stafford
do you want to use pddev to authenticate?
Gregory Zussa
no it's ok
Shawn Stafford
ok, try updating the config and restarting
Gregory Zussa
ok it's working now
cool
next step install my own db
do you know if mysql is already installed on pd dev boxes?
Shawn Stafford
probably not.  You'll need to use yum to install it
Gregory Zussa
ok i installed mysql-server using yum
but when i do mysql start
i get ERROR 2002 (HY000): Can't connect to local MySQL server through socket '/var/lib/mysql/mysql.sock' (2)
in /etc/my.cnf
i can see the following property
socket=/var/lib/mysql/mysql.sock
which correspond to what i see in the error message
Shawn Stafford
It just needed to be started.  I ran /etc/init.d/mysqld start
PLEASE REMEMBER TO SET A PASSWORD FOR THE MySQL root USER !
To do so, start the server, then issue the following commands:
/usr/bin/mysqladmin -u root password 'new-password'
/usr/bin/mysqladmin -u root -h pdgzussa.modeln.com password 'new-password'
do you want it to start on reboot?
Gregory Zussa
what's the difference between mysql and mysqld?
Shawn Stafford
mysql is the client.  mysqld is the daemon (server)
Gregory Zussa
d for deamon got it
no i can start it manually
while setting up my password i am getting this now?
/usr/bin/mysqladmin: connect to server at 'pdgzussa.modeln.com' failed
error: 'Access denied for user 'root'@'localhost' (using password: NO)'
apache seems to be install but when i try to start it doing /etc/init.d/httpd start i am getting the following exception Starting httpd: (13)Permission denied: make_sock: could not bind to address [::]:80
(13)Permission denied: make_sock: could not bind to address 0.0.0.0:80
no listening sockets available, shutting down
Unable to open logs
                                                          [FAILED]
Shawn Stafford
Started fine for me
Gregory Zussa
ok the apache server work i need to stat as root
Shawn Stafford
Yes, that's true of most services
Gregory Zussa
ok i was able to change my password using mysql -u root -p instad of your command
do i need to change it for pdgzussa as well?
Shawn Stafford
No
Gregory Zussa
ok i am able to connect to mysql 
Shawn Stafford
Not unless you want to connect from a different host
Gregory Zussa
it's funny i have 3 root user
one for localhost one for 127.0.0.1
and one for pdgzussa.modeln.com
Gregory Zussa
ok i have a mysql server running with phpmyadmin running on my apache and accessing my mysql deb
i had some issue with mysql passwords but it looks like i "fixed" my issues
Monday, January 13, 2014 12:05 PM
Gregory Zussa
hi shawn
how can i check if i have java 7 installed on my desktop?
when i do whereis java
i get the following locations
java: /usr/bin/java /etc/java /usr/lib/java /usr/share/java /usr/share/man/man1/java.1.gz
when i do hava -version
it tells me i am using java 6
Shawn Stafford
yum list | grep sun-jdk
It should be in /opt/java/sun-jdk-1.7
Gregory Zussa
if i install 1.7 is it going to mess up with my regular modeln rme dev environment?
Shawn Stafford
No, you can install that rpm without causing a problem
Gregory Zussa
ok it's installed
so where did it go?
Shawn Stafford
/opt/java
Gregory Zussa
nice!
and what should i change in the pom so taht it compile with the 1.7 version
?
Shawn Stafford
You need to set JAVA_HOME
Gregory Zussa
ok that's it?
no need to change stuff in pom.xml?
Shawn Stafford
No
Gregory Zussa
ok weird! I added a configuration to the pom so it force 1.7 usage
Also do you know what these table are for:  login_resource
group_permission
build_log
snapshot_vm
snapshot_os
snapshot_customer 
?
see are defined in the schema definition but they are not in the mysql dump i imported
How is that possible?
Shawn Stafford
The database schema has to be updated manually, so I probably never implemented the feature so I didn't add it to the db. I should go through and delete the tables and remove them from the SQL script 
Gregory Zussa
ok. I will do it. Anywhat the schema definition is not even in the github project
i haven't find it
i will add it and remove useless definition
also i saw 1 reference error in you definition 
i will correct it
Shawn Stafford
Ok
Gregory Zussa
check this out
https://docs.google.com/a/modeln.com/drawings/d/1C460Al2GyspeKV1RaaxRA3xdHDUC7Nz2TryqwtJ8v5Y/edit?usp=sharing
Shawn Stafford
We should use maintain documentation in GitHub using docbook or similar
Did you create that by hand in google docs or import it?
Gregory Zussa
by hand
Shawn Stafford
Ok, I'll see if I can export it as SVG or something 
Gregory Zussa
import tool such ahah. It just than by doing it by hand it force me to understand every single details
Monday, January 13, 2014 7:03 PM
Gregory Zussa
it's me again
I am looking at your CmnTable class
I am trying to understand how you are setting the primary key for the login table
managed by the LoginTable class
i see the addUser
this function calls  the execute function from the CMnTable class
this class
create a CmnQueryData object
this object takes an id which seems to be the later on table id
can i call you actually?
Shawn Stafford
sure, 650-454-4580
Thursday, January 16, 2014 9:02 AM
Gregory Zussa
shawn
I see some columns in some table that are not mapped by your entities
however the database has some data for these columns
how is taht possible
for example the login.title column
or the build. key_algorithm , build.ver_public_key or again the build.ver_private_key
Shawn Stafford
For login.title, it's possible that those users were added to the database directly (through phpMyAdmin)
Gregory Zussa
ok!
what about the build table?
Shawn Stafford
Let me check
They're probably being added by SQL statements in the build scripts.
Gregory Zussa
are they used?
i bet we don't care about login.title but maybe we do for these
Shawn Stafford
The data is being inserted by the build script build/ant/release.xml
It's not actively being used.  I had originally created it as a way to implement license keys for the Model N app.  But it's not currently used.  
Gregory Zussa
ok!
great thanks. did you read my email?
what do you think?
i hope it all sounds good if not great with you  
Shawn Stafford
Yes, that all seems fine.  I'm not familiar with those frameworks, so I'm not sure what changes that entails or how complicated it will be to support.
As far as SDTracker, I don't think it uses those frameworks.  You could check with Arun or Rahul.  It's based on an older version of the HT product.
Gregory Zussa
well the goal is to make things better so don't worry it's not supposed to make things more complexe
Shawn Stafford
I can point you to the SDTracker source code if you need to look at anything.
Gregory Zussa
ok that's fine if i can't adapt the framework to the current db i will just go for either a very permissive approach (less validations) or keep the current implementation.
Another option would be to use some ETL solution in between
but i leave this for later anyway
Shawn Stafford
My long term preference is to actually remove all queries to SDTracker completely and query git for the information instead.
Gregory Zussa
yes but what about bugs info?
SDtracker has information that are not in git
are you telling me taht we use SD db in order to get git info only?
Shawn Stafford
We have git triggers that query SDTracker on every commit.  The solution would be to create git notes on each commit that contain the SDR information.
I'm saying that the only reason we use SDTracker is to provide some "user friendly" SDR references.
So if we just has git store the SDR to commit relationship, we wouldn't really need SDTracker.
Gregory Zussa
ok so after each commit you want to trigger a task that would update the git description with SD tracker info and then have the build management sync up to git only
Shawn Stafford
Yes, but that's probably a longer term thing.  For now I'm just letting you know that it's probably not worth too much effort to fix the SDTracker integration unless it's really quick
Gregory Zussa
got it
it's a good new than  
Shawn Stafford
Since this is an open source project now, I want to try to move away from stuff that's totally proprietary, like SDTracker.
Gregory Zussa
I will take a look into this as well
completely agree with this last point!!!
Shawn Stafford
Plus it makes it easier to switch bug tracking systems, which happens every 5 years or so
Since I've worked here I've had to migrate from Remedy to Bugzilla to SDTracker, and I have a feeling SDTracker is nearing the end in the next year or two.
Gregory Zussa
oh lol are you expecting to switch from SD?
looks like a scoop info
remedy!?! Never heard about this one
ok so do you have ideas about what the next thing would be?
Shawn Stafford
It was built before my time.  It was a really generic application framework that Model N happened to use to create a bug tracking system.
Gregory Zussa
ok
interesting, btw what did we run away from bugzilla?
what =why
Shawn Stafford
It was sort of a political decision.  We used Bugzilla before we acquired Azerity.  So when we had to merge Azerity (High Tech) into Model N (Life Sciences), there was strong pressure from HT Tech Support to keep SDTracker.  There was a whole big evaluation process to determine whether to keep Bugzilla, move to SDTracker, or move to a 3rd party system.
The evaluation basically said it would cost the same regardless of the approach so HT support won and we went with SDTracker.
Gregory Zussa
Personnally as a user, i hate SD because it too process oriented and by consequence people tend to use it as either an excuse for not working (ping pong phenomena) or a a management tool (using SD as a task manager)
in one case, it kills productivity
in the other, it create communication issue between pm and pd and even qa because the tool is not something like Rally
Shawn Stafford
SDTracker violates everything we tell our customers not to do.  Don't build your own when there are industry standards out there, and don't heavily customize the tool or process.
Gregory Zussa
which is targetter for Agile team management
Shawn Stafford
We had the same problem with Bugzilla.  People kept having me customize it so heavily that eventually it became impossible to upgrade to the latest release because there were so many customizations.
Gregory Zussa
ahah Ia m sorry to hear that
all right thank for the nice talk. I am leaving you there since I have a lot of work on my plate now
Shawn Stafford
ok, later
Gregory Zussa
yep
thansk again !
Thursday, January 16, 2014 12:45 PM
Gregory Zussa
hi shawn
I see a lot of duplicate class like CMnProduct
some seems to not be used or part of an old implementation
in the class javadoc i often see comment like @hibernate.class table="release_product"
am i correct? are these classes part of a old implementation that relied on jpa/hibernate framework?
an implementation that has been deprecated in order to support yours javase+jdbc
?
Shawn Stafford
Yes, the original version of the service patch tool written by Karen for Perforce used Hybernate.
Gregory Zussa
is it right that this code is not used anymore
not even in jsp pages?
also why did we change implementation?
Shawn Stafford
I rewrote the tool when we switched to git, and I thought Hybernate was too complicated.  It was a pain to build and maintain.
And since only part of the code base was using it, I decided not to use it any more when I re-implemented the sp tool.
Gregory Zussa
ok make sense. I am not a big fan of hibernate either since it has too many features that deviate from entreprise standard
ok perfect
looks like the code is still there
do you know if the ui is using it?
Shawn Stafford
which classes are you referring to?
Gregory Zussa
CMnProduct
CMnProductVersion
CMnProductRelease
etc
CmnProductComponent
CMnPurchaseItem
CMnPurchaseOrder
almost everything contain in this package
com.modeln.build.common.data.product
Shawn Stafford
Yes, they are still used.
Those classes were used both by the service patch tool and by the web application.  Even though they had Hybernate tags in them, they were also used directly by other parts of the application.
That being said, those particular classes are not heavily used because I never really built out the application to that level of detail.
But there is still code that references them
For example, in the webapp
com/modeln/build/ctrl/command/patch/CMnCustomerEnv.java
com/modeln/build/ctrl/command/patch/CMnPatchRequest.java
Gregory Zussa
humm got that's why you have these convert function here and there
you created these on order to use old entity with the new framework
got it
like in CMnBuildData
Shawn Stafford
I think I created the conversion for a different reason.
Friday, January 17, 2014 2:14 PM
Gregory Zussa
hi shawn
could you detail the reasons why we have build_event and deploy_event whe these two tables are the same
you told me that some script are selecting what table we want to use but why are we doing this?
also both deploy_event and deploy_event_criteria are empty in my db. 
Does it mean that we are not event using this "fork" feature?
Shawn Stafford
I'll send you an email about it later. 
Gregory Zussa
ok thx
Monday, January 20, 2014 3:37 PM
Gregory Zussa
hi shawn
can we have multiple actstorymap and acttestcase for one act
in the db it looks like it's a one to one mapping
Shawn Stafford
Basically you're asking why there isn't just a "story" and "testcase" column in the act table?
Gregory Zussa
yes
Shawn Stafford
I'm not sure why it's split into multiple tables.
Gregory Zussa
ok
data in the table only seems to correspond to one to one mapping
even if the pk is a combination of 2 attributes in both tables
Shawn Stafford
Ok, here's the reason.  1 test can have multiple stories.
It makes sense if you look at the code in mn-build-core/src/main/java/com/modeln/testfw/reporting/CMnAcceptanceTestTable.java
look for the call to the addStory method.
It's iterating through an enumeration and adding entries to the table.
So for example, you might have test 12345 which satisfies the following stories:  MULTIORG123, TIMEZONE345, STORY567
So it's a one-to-many relationship
Gregory Zussa
yes it is but my initial question was to know if we really need it
because even if it's a one to many 
there is only one entry to the "many" side
based on the data i have n my instance
Shawn Stafford
I don't know how many tests actually satisfy more than one story.  I was just implementing the database schema based on what the test tags support.
Gregory Zussa
ok 
Monday, February 10, 2014 10:21 AM
Gregory Zussa
hi shawn
sorry I completely missed the meeting this morning
Shawn Stafford
That's ok, it was a terrible meeting
Gregory Zussa
lol what did you talk abour?
Shawn Stafford
People mostly argued about the objectives
Gregory Zussa
so do we have a clear list now  
Shawn Stafford
No
Gregory Zussa
indeed i guess it was unseless
regarding the build-management project i mapped mapped every tables that are in the product
42 or 49 tables (i don't remember exactly)
now we are able to run test without deploying a database
and use a derby db
just for tests
we now support locks, transaction, batch and basically everything that jpa api has to offer
I haven't done any check in since i am also writing some documentation about my work I want to commit with my changes so it can help people understand what is going on
honestly I think i have a very solid persistence layer for the app that already solve the base issue for testing and integrity issues
Also I am looking at some scaffolding UI that we could add to the super admin part of the application so we can see what is in the db without having to connect to phpmyadmin
Shawn Stafford
Ok, so what's the next step? 
Gregory Zussa
right now I am finishing all this
then I want to fix all the bugs i found in the ui
like simple bugs
links that don't work
let the user to create a account
i don't want you to interact with the db manually anymore
etc
then I want to integrate Regat
i actually already start a little bit
regarding small items taht can be fixed fairly quickly. do not hesitate to give me your feedback
Shawn Stafford
When did you want to push the persistence layer changes to github?
Gregory Zussa
honestly as soon as i can
but it won't be before one week since I also have a lot of coreapp stuff to do
we just finished the roadmap
an i have things to deliver before next week
yes
Friday, February 14, 2014 5:04 PM
Gregory Zussa
hi shawn 
did you add me as a admin of the build system?
I am not able to connect
Shawn Stafford
no, I didn't.  Did you want admin access?
Gregory Zussa
yes please I wo't touch anything I just want to looks at all the page and see the entire app
it will avoid me the pain of setting everything on my box
btw. I am done with the persistence layer implementation
Shawn Stafford
ok, try it now.  You'll probably have to log out and log back in.
Gregory Zussa
I am working on documentation. then I will show you my work
Shawn Stafford
ok
Gregory Zussa
is gzussa my user name?
what password should i use?
Shawn Stafford
yes
You'll have to use the "Forgot my password?" link on the login page.
It will e-mail you a temporary password that you can use to reset your password.
Gregory Zussa
great!
thx
how many build machine do we have currently?
oh i found the environment list
nice!
Shawn Stafford
That's not really in use.
Gregory Zussa
why?
Do we have the list of environment with the number of concurrent build running per server?
Shawn Stafford
no
Gregory Zussa
can i give you a quick call?
Shawn Stafford
Can you give me a little while? Maybe 30 minutes...
The most accurate way would be to query the act_suite and unittest_suite tables for unique host names during the past week 
And also the build table
That would cover the HQ hosts. Then you'd have to do the same query against the MNI instance as well
Gregory Zussa
oh i forgot we had multiple instances 
are the two app the same (including database schema)
I will call you next week then
Shawn Stafford
Yes, they're the same. Just the data is different
Gregory Zussa
ok great
Monday, February 17, 2014 10:05 AM
Gregory Zussa
hi shawn
would you have some time this afternoon so we can discuss about the build management project
?
Shawn Stafford
Sure
Gregory Zussa
Modeln2k13
oups sorry
do i need to set up a time in your calendar or can i just ping you around 1pm?
Shawn Stafford
Maybe.  Not sure what I'll be doing then but you can ping me and see.  You know it's a US holiday today, right?
Gregory Zussa
oh really?
i didn'tknow
ok then i ping you tomorrow
sorry for bothering you in that case
Shawn Stafford
ok, no problem.
Wednesday, February 19, 2014 11:54 AM
Gregory Zussa
hi shawn 
in the account status enum
i see active, inactive, deleted and abuse
what is the abuse enum for?
Shawn Stafford
I've never used it, but it would be for disabling an account that was hacked or something like that.
Gregory Zussa
oh ok
also what is the encryption algotithm behind the enum crypt?
Shawn Stafford
It uses the MySQL ENCRYPT function.  I believe it's the same implementation as the Unix crypt function
Gregory Zussa
oh ok 
thx
could you tell me what are the roles for the build_event, build_event_criteria, build_metric, build_progress and build_progress_group tables?
are you in the office?
Shawn Stafford
No, I work from home.  But you can call me if you want.  650-454-4580
Shawn Stafford
http://pdbuilds.modeln.com/ctrl/command/report/CMnBuildData?bid=66969
http://pdbuilds.modeln.com/ctrl/command/report/CMnBuildData?bid=62187
http://pdbuilds.modeln.com/ctrl/command/report/CMnBuildSummary?ver=%255.6.2.3%25&clop=eq&cl=e9290005f9581bcd2faf47af85379f0ed02e9ae8
Shawn Stafford
http://pdbuilds.modeln.com/ctrl/command/report/CMnBuildData?bid=62187&grp=area
Thursday, February 20, 2014 10:36 AM
Gregory Zussa
hi shawn
in the build_status table we have the enum called support with the following values active, inactive and extended
what does it correspond to
?
Shawn Stafford
It's meant to indicate whether the build is still being supported by PD (either as regular support or as special extended support that the customer would pay extra for).
I don't think that field is in use though.
Gregory Zussa
ok and what is the build status notes table for?
is it used? looks like it's just to add comments to a build status
Shawn Stafford
yes, when someone marks the build as "tested" it's a way for them to record comments about the status
Gregory Zussa
one more question. the build_event table is very key in your system. why am i not able to see this tble in the mysql dump you gave me?
sis you remove the table from the dump because it was too heavy?
Shawn Stafford
yes, typically that table is very large and the data is not super critical for historical purposes, so I probably truncated it on export to reduce the size.
There's a cron job that cleans up that table weekly
Gregory Zussa
could you export the table schema from you phpmyadmin?
i want the exact ddl definition 
since what's on the sql file is not all the time correct
Shawn Stafford
ok, I mailed you the export of 1 row.
Gregory Zussa
great!
how are do you think it would be to transform the build system from a build version to a build execution bases system?
basically like hudson
which "think" more in term of build execution rather than the version of the build use
Shawn Stafford
The ability to call out to Jenkins and create/trigger jobs already exists in the app.
But I don't think it would be valuable to try to re-implement Jenkins functionality.
Gregory Zussa
question regarding the build metrics
since you can have two server running migrations test
how do you know that both system are done so that you can add an entry to the build metric table with activity set to migrate?
also one enum value for activity is populate. ince one build_version can be deployed on multiple server at the same time. it also mean that you are populating multiple db. since you can only have one entry with activity set to populate dor one specific build_version. How do you make sure every populate step are done on every server before to set this entry in the db?
Shawn Stafford
The central "build" consists of compile/package, javadoc, populate.
Those actions are only performed once per build, and the results are archived on the FTP server.
So "populate" occurs once, and the dump files are exported and placed on the FTP server with the build binaries.
Gregory Zussa
is this central build also running unit test?
Shawn Stafford
The deployments pick up the dump and the ear file.  Then they do whatever the deployment is configured to do.  It might run migration, it might run unit tests, or it might just be there for users to log in.
Whether the build server also runs the unit tests really just depends on how the build is configured.  For service patches, it all runs on one server from the command line
Gregory Zussa
oh ok
Shawn Stafford
For everything else, the build is deployed to various other servers.
Gregory Zussa
so when are act ut and iut executed? is it on deployed server
or on build server?
basically i am not understand why you now tells me taht the build is only done in one server when yesterday you explained me that what we saw on the report page was the result of multiple build execution with different build scenario
Shawn Stafford
Think of it this way, the build can be composed of multiple components:  compilation/packaging, population, javadoc, unit testing, ACT testing.
Those actions can occur on one server or multiple servers.
Some of those actions occur in serial and some of them can occur in parallel.
For example, the compilation and packaging is required for all subsequent actions.
All other actions are optional, but they all depend on the compiled/packaging to be available (which is why they all assume that the version string can be used to identify the build).
Here's an example:
server1:  compile/package
server1:  javadoc
server2:  populate 3 schema (MN, QA, ST)
server3:  Deploy build + MN schema to 2 app server instances
then run ACT and UT in parallel on both instances
So in that example, you have ACT and unit tests running on the same server, but under two different JVMs.
In another example, we might try to create more parallel execution by using 3 physical servers, 2 JVMs per server.
Gregory Zussa
JVM or app server i though JVM required different build versions
Shawn Stafford
I'm using JVM and app server interchangably
Gregory Zussa
ok
so as soon as you deploy on your 2 app server. is it went long start to get populated to the deploy_event table instead of the build_table?
long = log
Shawn Stafford
yes
So in that sense, the build scripts are not meant to be run in parallel for a single build, whereas the deployment scripts are.
Gregory Zussa
ok good and you don't log anything on the build_metric table as soon as it hand over to deployed server
Shawn Stafford
yes
Gregory Zussa
so basically the build metric only logs sequential steps
Shawn Stafford
yes
Gregory Zussa
and how does it work with agile builds? does agile build means test executions on deployed servers (in parallel) only
Shawn Stafford
Yes
Gregory Zussa
ok now i understand why you don't have a build metric activity enum called finished
Shawn Stafford
The compilation/packaging and content population (base + product) occurs on a central server.  The deployment and unit testing occur on multiple servers in parallel.
Gregory Zussa
or you could but it would basically means deployed
Shawn Stafford
The build metrics have a start time and an end time.  The end time gets set upon completion.  So if there is no end time, the task is assumed to either be failed or in progress.
The use of an enum would not provide any additional information.
When a metric starts, the start time is set to the current time and the end time is null (or 00:00:00)
Gregory Zussa
but as of now we don't have the equivalent of build metric for deploy servers
actually we do have a table as well for deployed
Shawn Stafford
There's a deploy_metrics table.
It may or may not be actively used.
It does look like there are entries logged.
There's just no UI presenting that information in the build report
Gregory Zussa
so it is possible to add more enum there like "unittest", "act", "uit" and so one
this way we could get a better sens of what get completed on parallel execution per server
Shawn Stafford
It is.  I'm actually not sure the use of an enum in those metics tables was a good idea because it makes it hard to add new things.
Gregory Zussa
true 
Shawn Stafford
I probably should have just used a varchar or something
Gregory Zussa
it's ok we can change taht later
this is really great!
i like what you did!!