#!/bin/sh


## tested here
# use of servpatch script
# what we will do:
# create sample data in several branches: dev/bugfixes/customer
# we pick a couple bugfixes (commits of branch bugfixes)

# and we use the service patch tool:
# branching from branch customer,
# we apply the commits to the new branch 

echo "testing servpatch script";

if [[ $GITTESTS == "" ]];
then
echo "setting environment variable GITTESTS by default"
export GITTESTS=$PWD"gitData";
fi

echo $#;
if [ $# -ne 1 ]
then
rootprojs=$GITTESTS;
else
rootprojs=$1;
mkdir $rootprojs; 
fi

scriptdir=$PWD;
cd $rootprojs;

echo "";
echo "creating a sample project";
echo "";

# we create a remote project dir
proj1Dir=remoteSP;
rm -rf $proj1Dir;
mkdir $proj1Dir;
cd $proj1Dir;

# initialization of git project with some commits
git init;
echo "bye world" > bye.txt;
git add bye.txt;
git commit -m"sample data in bye and initial commit";

echo "not SO fast" >> bye.txt;
echo "see you later" >> bye.txt;
git add bye.txt;
git commit -m"some more data in bye";

echo "soon" >> seeyou.txt;
echo "later" >> seeyou.txt;
git add seeyou.txt;
git commit -m"creation, add data in seeyou";

echo "hello world" > hello.txt;
git add hello.txt;
git commit -m"sample data in hello: hello world";

echo "bonjour" >> hello.txt;
echo "hallo" >> hello.txt;
git add hello.txt;
git commit -m"some more data in hello : bonjour/hallo";


echo "12345" > numbers.txt;
git add numbers.txt;
git commit -m"sample numbers : 12345";

echo "abcdefg" > letters.txt;
git add letters.txt;
git commit -m"sample letters : abcd..";

# we create two branches : dev and bugfixes
git checkout -b dev master;
git checkout master;
git checkout -b bugfixes master;

echo "gaga" > lala.txt;
git add lala.txt;
git commit -m"[CP] sample data in lala: gaga";
# we will apply this fix later (7th to last commit on bugfixes branch)

echo "abracadabra" >> lala.txt;
git add lala.txt;
git commit -m"more data in lala: abracadabra";

echo "6789" >> numbers.txt;
git add numbers.txt;
git commit -m"[CP] more numbers : 6789";
# we will apply this fix later (last commit that edits numbers.txt)

echo "kalimera" >> hello.txt;
git add hello.txt;
git commit -m"more data in hello: kalimera";

echo "buongiorno" >> hello.txt;
git add hello.txt;
git commit -m"[CP] more in hello : buongiorno";
# we tag the commit to apply this fix later
git tag fix123;

echo "10,11" >> numbers.txt;
git add numbers.txt;
git commit -m"even more numbers : 10,11";

echo "12;13" >> numbers.txt;
git add numbers.txt;
git commit -m"[CP] more in numbers : 12;13";
# we will apply this fix later (last commit that edits numbers.txt)

git checkout dev;

echo "ola" >> hello.txt;
git add hello.txt;
git commit -m"more data in hello: ola";

git checkout -b customer1 dev;

echo "END OF FILE" >> hello.txt;
git add hello.txt;
git commit -m"[GA] delivering to customer";

git checkout dev;

echo "HIJKL" >> letters.txt;
git add letters.txt;
git commit -m"more data in letters: HIJKL";

# we built three branches, one is dev, one is bugfixes, and one is customer

echo "sample data created";
echo "";
echo "cloning a local copy";

localSP=localSP;
cd $rootprojs;
rm -rf $localSP;
mkdir $localSP;
cd $localSP;

git clone $rootprojs/$proj1Dir;
cd $proj1Dir;

git fetch origin;
git checkout -b customer1 origin/customer1;
git checkout -b bugfixes origin/bugfixes;

echo "local copy created. launching the service patch tool";

# the goal of the servpatch tool is to take some commits from the bugfixes branch and apply them to a branch from customer.


# there are many ways to find a revision (commit SHA). you can run "git help revisions" in command line about that. 
 
# the first bugfix is retrieved using the log cmd.
# in this command, the --pretty=format:"%h" means we want the partial commit SHA (that is unique to identify the commit)
# the -1 means we want only one commit. The file name limits the search to the commits that modify this file
bugfix1=`git log --pretty=format:"%h" -1 numbers.txt`

# we can also use log cmd to retrieve the commit SHA of the 5th-to-last commit on the branch "bugfixes"
bugfix2=`git log --pretty=format:"%h" -1 bugfixes~6`

bugfix3=`git log --pretty=format:"%h" -1 fix123`

bugfix4=`git log $bugfix1~2 --pretty=format:"%h" -1 numbers.txt`


# the servpatch cmd takes the following arguments:
# 1. the root directory
# 2. the branch for the customer (existing)
# 3. the branch for the patch (to be created form previous branch)
# 4. the option for the command (-cp)
# 5..n. the revisions to be applied
$scriptdir/../servpatch.sh $rootprojs$localSP/$proj1Dir customer1 custpatch1 -cp bugfixes~6 $bugfix1 fix123 $bugfix4;

echo "";
echo "";
echo "...TEST results : these are the potential bugfixes, those marked with [CP] to be integrated:";
git log bugfixes --pretty=format:'%h : %s' -8;

echo "";
echo "...TEST results: this is the log on the customer branch";
git log customer1 --pretty=format:'%h : %s' -8;

