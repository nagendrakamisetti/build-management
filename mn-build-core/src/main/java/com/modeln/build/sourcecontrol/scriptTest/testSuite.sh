#!/bin/sh

echo "starting Git API tests";

gitData=/gitData;

export GITTESTS=$PWD$gitData;

mkdir $GITTESTS;

for each in ./*.sh; 
do 
if [ $each != ./`basename $0` ]; then
echo "... running test "$each;
bash $each;
fi
done