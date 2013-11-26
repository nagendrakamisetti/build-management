#!/bin/sh


## tested here
# 

echo "testing Git API";
echo "testing adding files and checking them in";

if [[ $GITTESTS == "" ]];
then
echo "setting environment variable GITTESTS by default"
export GITTESTS=$PWD"gitData";
fi
cd $GITTESTS;

echo "initializing first project";

testname="Checkin";
proj1Dir=rem$testname;
rm -rf $proj1Dir;
mkdir $proj1Dir;
cd $proj1Dir;

git init;
echo "bye world" > bye.txt;
git add bye.txt;
git commit -m"sample data in bye and initial commit";

cd ..;

proj2Dir=local$testname;
rm -rf $proj2Dir;
mkdir $proj2Dir;
cd $proj2Dir;

git clone ../$proj1Dir/;

cd $proj1Dir;

git checkout -b working;
echo "bye bye" >> bye.txt;
git add bye.txt;


echo "launching tests";
echo "the tests will add the data added to bye.txt and commit the changes";

cd $ANT_HOME/..;

ant -f build_jgit_test.xml deploy.ant utgitcheckin;