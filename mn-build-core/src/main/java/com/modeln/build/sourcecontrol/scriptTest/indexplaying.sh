#!/bin/sh


## tested here
# 

echo "testing Git API";
echo "testing changes in the index";

if [[ $GITTESTS == "" ]];
then
echo "setting environment variable GITTESTS by default"
export GITTESTS=$PWD"gitData";
fi
cd $GITTESTS;

echo "creating sample data in two projects";


echo "initializing first project";

testname="Index";
proj1Dir=rem$testname;
rm -rf $proj1Dir;
mkdir $proj1Dir;
cd $proj1Dir;

git init;
echo "bye world" > bye.txt;
git add bye.txt;
git commit -m"sample data in bye and initial commit";

echo "bye bye world" > bye.txt;
git add bye.txt;
git commit -m"more data in bye";

echo "bye bye, bye bye world" > bye.txt;
git add bye.txt;
git commit -m"more data in bye";

echo "bye bye, bye bye, bye" > bye.txt;
git add bye.txt;
git commit -m"more data in bye";
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
echo "bye bye bye" >> bye.txt;


echo "launching tests";
echo "the tests will clone the first project and then merge the other into it";

cd $ANT_HOME/..;

ant -f build_jgit_test.xml deploy.ant utgitindex;