#!/bin/sh

## tested here
# constructor of CMnGitServer with its two features:
# -clone a project from a url into a local working directory
# -start with an configured local working directory

echo "testing Git API";
echo "testing init of a project";

if [[ $GITTESTS == "" ]];
then
echo "setting environment variable GITTESTS by default"
export GITTESTS=$PWD"gitData";
fi

cd $GITTESTS;

echo "creating a sample directory to be called";

testname="Init";
projDir=local$testname;
rm -rf $projDir;
#mkdir $projDir;



proj1Dir=rem$testname;
rm -rf $proj1Dir;
mkdir $proj1Dir;
cd $proj1Dir;

git init;
echo "bye world" > bye.txt;
git add bye.txt;
git commit -m"sample data in bye and initial commit";


echo "launching unit tests";

cd $ANT_HOME/..;

ant -f build_jgit_test.xml deploy.ant utgitinit;
