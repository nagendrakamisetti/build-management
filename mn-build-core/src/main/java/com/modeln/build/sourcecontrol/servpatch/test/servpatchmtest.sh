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
proj1Dir=remoteSP2;
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

# we create a new branch dev
git checkout -b dev master;

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
git commit -m"[release0] sample letters : abcd..";

# we create a new branch customer2
git checkout -b customer2 dev;

echo "54321" >> numbers.txt;
git add numbers.txt;
git commit -m"more numbers, bug fixed before releasing : 54321";

echo "END OF RELEASE" >> hello.txt;
git add hello.txt;
git commit -m"[GA] delivering to customer";

git checkout dev;

echo "gaga" > lala.txt;
git add lala.txt;
git commit -m"sample data in lala: gaga";

echo "abracadabra" >> lala.txt;
git add lala.txt;
git commit -m"more data in lala: abracadabra";

echo "6789" >> numbers.txt;
git add numbers.txt;
git commit -m"more data in numbers : 6789";

echo "kalimera" >> hello.txt;
git add hello.txt;
git commit -m"more data in hello: kalimera";

echo "buongiorno" >> hello.txt;
git add hello.txt;
git commit -m"[release1] even more data in hello : buongiorno";
# we tag the commit to apply this fix later
git tag release1;

echo "10,11" >> numbers.txt;
git add numbers.txt;
git commit -m"even more data in numbers : 10,11";

echo "12;13" >> numbers.txt;
git add numbers.txt;
git commit -m"a couple more numbers in numbers : 12;13";

echo "ola" >> hello.txt;
git add hello.txt;
git commit -m"[release2] more data in hello: ola";
git tag release2;

echo "HIJKL" >> letters.txt;
git add letters.txt;
git commit -m"more data in letters: HIJKL";

echo "mnop" >> letters.txt;
git add letters.txt;
git commit -m"more data in letters: mnop";

# we built two branches, one is dev and one is customer

echo "sample data created";
echo "";
echo "cloning a local copy";

localSP=localSP2;
cd $rootprojs;
rm -rf $localSP;
mkdir $localSP;
cd $localSP;

git clone $rootprojs/$proj1Dir;
cd $proj1Dir;

git fetch origin;
git checkout -b customer2 origin/customer2;
#git checkout -b dev origin/dev;

echo "local copy created. launching the service patch tool";

# the goal of the servpatch tool is to take some commits from the bugfixes branch and apply them to a branch from customer.


# there are many ways to find a revision (commit SHA). you can run "git help revisions" in command line about that. 


# the servpatch cmd takes the following arguments:
# 1. the root directory
# 2. the branch for the customer (existing)
# 3. the branch for the patch (to be created form previous branch)
# 4. the option for the command (-m)
# 5. the last revision to be applied
$scriptdir/../servpatch.sh $rootprojs$localSP/$proj1Dir customer2 custpatch1 -m release1;

$scriptdir/../servpatch.sh $rootprojs$localSP/$proj1Dir customer2 custpatch2 -m release2;

echo "";
echo "";
echo "...TEST results : these are the potential bugfixes, those marked with to be integrated:";
git log dev --pretty=format:'%h : %s' -15;

echo "";
echo "...TEST results: this is the log on the customer branch";
git log customer2 --pretty=format:'%h : %s' -15 --graph;

