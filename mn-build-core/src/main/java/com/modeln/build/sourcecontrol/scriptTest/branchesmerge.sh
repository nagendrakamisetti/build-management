#!/bin/sh

## tested here
# method integrate branches into others
# possibility to try with different merge strategies, changes to be made in the unit test (CMnGitMergeTest.java)
# interesting results at the end of the file

# 1. to be tested merging testBranch1 into testBranch
# merge with one more line

# 2. to be tested merging testBranchA into testBranchA1
# merge with one less line but using a previous commit

# 3. to be tested merging testBranchB1 into testBranchB
# merge with two different lines

# 4. to be tested merging testBranchC1 into testBranchC
# merge with one less nested line

echo "testing Git API";
echo "testing merge of two branches, with different conflict possibilities";

if [[ $GITTESTS == "" ]];
then
echo "setting environment variable GITTESTS by default"
export GITTESTS=$PWD"gitData";
fi
cd $GITTESTS;

testname="BranchesMerge";
remdir=rem$testname;
rm -rf $remdir;
echo "...";

echo "creating new folder";

mkdir $remdir;
cd $remdir;

git init;
echo "bye world" > bye.txt;
git add bye.txt;
git commit -m"sample data in bye and initial commit";

echo "creating sample data in two branches";

# to be tested merging testBranch1 into testBranch
# merge with one more line
git checkout -b testBranch;
echo "hello world" > hi.txt;
git add hi.txt;
git commit -m"sample data in hi";

git checkout -b testBranch1;
echo "HELLO" >> hi.txt;
git add hi.txt;
git commit -m"more data in hi";

git checkout master;

git checkout -b testBranchA;
echo "hello world" > hi.txt;
git add hi.txt;
git commit -m"sample data in hi";

git checkout -b testBranchA1;
echo "HELLO" >> hi.txt;
git add hi.txt;
git commit -m"more data in hi";

git checkout master;

# to be tested merging testBranchA into testBranchA1
# merge with one less line
git checkout -b testBranchA;
echo "hello world" > hi.txt;
git add hi.txt;
git commit -m"sample data in hi";

git checkout -b testBranchA1;
echo "HELLO" >> hi.txt;
git add hi.txt;
git commit -m"more data in hi";

git checkout master;

# to be tested merging testBranchB1 into testBranchB
# merge with two different lines
git checkout -b testBranchB;
echo "hello world" > hi.txt;
git add hi.txt;
git commit -m"sample data in hi";

git checkout -b testBranchB1;
echo "HELLO" >> hi.txt;
git add hi.txt;
git commit -m"more data in hi";

git checkout testBranchB;
echo "HELLO WORLD" >> hi.txt;
git add hi.txt;
git commit -m"more data in hi";


git checkout master;

# to be tested merging testBranchC1 into testBranchC
# merge with one less nested line
git checkout -b testBranchC;
echo "hello world" > hello.txt;
git add hello.txt;
git commit -m"sample data in hello";

git checkout -b testBranchC1;
echo "HELLO" >> hello.txt;
git add hello.txt;
git commit -m"more data in hello";

echo "for a to b" >> hello.txt;
git add hello.txt;
git commit -m"a bit more data in hello";

echo "wait a sec" >> hello.txt;
git add hello.txt;
git commit -m"little more data in hello";

echo "end for" >> hello.txt;
git add hello.txt;
git commit -m"even more data in hello";

git checkout testBranchC;

echo "for a to b" >> hello.txt;
git add hello.txt;
git commit -m"a bit more data in hello";

echo "wait a sec" >> hello.txt;
git add hello.txt;
git commit -m"little more data in hello";

echo "end for" >> hello.txt;
git add hello.txt;
git commit -m"even more data in hello";


cd ..;
workdir=local$testname;
rm -rf $workdir;
echo "...";

echo "creating new folder";

mkdir $workdir;
cd $workdir;

git clone ../$remdir;
cd $remdir;

git fetch origin;
git checkout -b testBranch origin/testBranch;
git checkout -b testBranch1 origin/testBranch1;
git checkout -b testBranchA origin/testBranchA;
git checkout -b testBranchA1 origin/testBranchA1;
git checkout -b testBranchB origin/testBranchB;
git checkout -b testBranchB1 origin/testBranchB1;
git checkout -b testBranchC origin/testBranchC;
git checkout -b testBranchC1 origin/testBranchC1;

echo "launching unit tests";

cd $ANT_HOME/..;

ant -f build_jgit_test.xml deploy.ant utgitmerge;



echo "tests to be done";


echo "testBranch1 merged into testBranch";
# result : fast forward merge of testBranch

echo "testBranchA merged into testBranchA1";
# result : no change in A1

echo "testBranchB1 merged into testBranchB";
# result: success
# merge made with THEIRS, it has been merged to fromBranch content
# merge made with YOURS, it has been merged to toBranch content
#commit a0b5f24b272c203c7b1df0f1abc6e1b5a10f37eb
#Merge: 122ad00 a5cb796
#Author: Vincent Malley <vmalley@modeln.com>
#Date:   Fri Dec 23 11:45:08 2011 -0800
#Merge commit 'a5cb796488eb3d764b4f265b7e0d2f2746c5eab7' into testBranchB

echo "testBranchC1 merged into testBranchC";
# result: success 
# testBranchC1 changes integrated into testBranchC with THEIRS
# no change with YOURS
# commit 9d8047ae7f6481435e7ffcc6693cf73a9ab47c26
#Merge: 2d3ca61 7d77c07
#Author: Vincent Malley <vmalley@modeln.com>
#Date:   Fri Dec 23 11:45:08 2011 -0800
#    Merge commit '7d77c07bd355f11636f0b47700646c57a94be3ae' into testBranchC



# merge behavior: use THEIRS to do the same as p4 resolve -am
# MERGE is the equivalent of p4 resolve -as