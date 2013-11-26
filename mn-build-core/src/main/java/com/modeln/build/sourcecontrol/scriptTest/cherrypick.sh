#!/bin/sh


## tested here
# use of servpatch script
# what we will do:
# create sample data in several branches: dev/bugfixes/customer
# we pick a couple bugfixes (commits of branch bugfixes)

# and we use the service patch tool:
# branching from branch customer,
# we apply the commits to the new branch 

echo "testing Git API";
echo "testing cherry-picking files from a branch to another";

if [[ $GITTESTS == "" ]];
then
echo "setting environment variable GITTESTS by default"
export GITTESTS=$PWD"gitData";
fi
cd $GITTESTS;

echo "initializing project";

testname="Cherrypick";
proj1Dir=rem$testname;
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
cd ..;

proj2Dir=local$testname;
rm -rf $proj2Dir;
mkdir $proj2Dir;
cd $proj2Dir;

git clone ../$proj1Dir/;
cd $proj1Dir;

git fetch origin;
git checkout -b customer1 origin/customer1;
git checkout -b bugfixes origin/bugfixes;

echo "local copy created. launching unit tests";

echo "the tests will cherrypick some of the commits";

cd $ANT_HOME/..;

ant -f build_jgit_test.xml deploy.ant utgitcherrypick;