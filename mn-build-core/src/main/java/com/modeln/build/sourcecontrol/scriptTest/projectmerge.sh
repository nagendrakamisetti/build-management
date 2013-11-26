#!/bin/sh


## tested here
# merging two projects without common history
# using two branches to be merged
# the data is totally different with similar file names
# merging startegy could be changed in the unit tests

echo "testing Git API";
echo "testing merge of two projects";

if [[ $GITTESTS == "" ]];
then
echo "setting environment variable GITTESTS by default"
export GITTESTS=$PWD"gitData";
fi
cd $GITTESTS;


echo "creating sample data in two projects";


echo "initializing first project";

testname="ProjMerge";
proj1Dir=rem1$testname;
rm -rf $proj1Dir;
mkdir $proj1Dir;
cd $proj1Dir;

git init;
echo "bye world" > bye.txt;
git add bye.txt;
git commit -m"sample data in bye and initial commit";

echo "not SO fast" >> bye.txt;
echo "see you later" >> bye.txt;
git add bye.txt;
git commit -m"some more data in bye";


echo "hello world" > hello.txt;
git add hello.txt;
git commit -m"sample data in hello";

echo "bonjour" >> hello.txt;
echo "hallo" >> hello.txt;
git add hello.txt;
git commit -m"some more data in hello";


echo "12345" > numbers.txt;
git add numbers.txt;
git commit -m"sample numbers";

echo "abcdefg" > letters.txt;
git add letters.txt;
git commit -m"sample letters";

git checkout -b otherbranch;
# we try with five conflicting files
echo "gaga" > lala.txt;
git add lala.txt;
git commit -m"sample data in lala";




cd ..;

echo "initializing second project";
proj2Dir=rem2$testname;
rm -rf $proj2Dir;
mkdir $proj2Dir;
cd $proj2Dir;

git init;
echo "bye world" > bye.txt;
git add bye.txt;
git commit -m"sample data in bye and initial commit";

echo "not TOO fast" >> bye.txt;
echo "see you later" >> bye.txt;
git add bye.txt;
git commit -m"some more data in bye";

echo "hello world" > hello.txt;
git add hello.txt;
git commit -m"sample data in hello";

echo "buenos dias" >> hello.txt;
echo "failte" >> hello.txt;
echo "bonjour" >> hello.txt;
echo "hallo" >> hello.txt;
git add hello.txt;
git commit -m"some more data in hello";

echo "98765" > numbers.txt;
git add numbers.txt;
git commit -m"sample numbers";

echo "zyxwvut" > letters.txt;
git add letters.txt;
git commit -m"sample letters";

git checkout -b otherbranch;
# we try with five conflicting files
echo "toto" > lala.txt;
git add lala.txt;
git commit -m"sample data in lala";

cd ..;

echo "initializing the directory we will merge everything into";

rootdir=local$testname;
rm -rf $rootdir;
echo "...";
echo "creating new folder in which we will merge projects";
mkdir $rootdir;
cd $rootdir;


echo "launching tests";
echo "the tests will clone the first project and then merge the other into it";

cd $ANT_HOME/..;

ant -f build_jgit_test.xml deploy.ant utgitprojmerge;



# Tests : success
# merge a repo with the branch specified (the branch names will not change in the new repository)
# if the merge option is THEIRS, the repoToMerge content will overwrite the old data
# if the merge option is YOURS, the first repo will overwrite the repoToMerge
# if the merge option is MERGE, every conflict will remain in the end. Run "git mergetool"
# in the project directory to resolve the conflicts with the mergetool configured (see updateMergeCfg() )
