#!/bin/sh

# $1 should be the working root directory
# $2 should be the branch name we start from (customer branch)
# $3 should be the name of the branch we create
# $4 should be the option for the command (either -m or -cp)

# $i (i>4) are the commits to integrate (format: SHA)
# we assume the translation from a job/SDR/... to commit is done before

echo "";
echo "SP: launching Service Patch script";
echo "";
min_args=5;

# check we have enough args
if [ $# -le $min_args ]
then
echo "SP: Usage : servpatch.sh  root_dir  customer_branch_name  patch_branch_name  (-m|-cp)  commit_SHA*";
fi

# store locally the first arguments
rootdir=$1;
custbranch=$2;
patchbranch=$3;
cmdoption=$4;

##
# the following if loop allows either to merge the whole history into 
cherrypickoption="-cp";
mergeoption="-m";

commit=`./mysqlscript.sh $custbranch`;
if [[ $commit -ne "" ]];
then
custbranch=$commit;
fi

cd $rootdir;
echo "SP: branching from customer branch to patch branch"

# we create the branch
git branch $patchbranch $custbranch;
# we switch to the branch
git checkout $patchbranch;
# we configure the local copy to be able to push the new new branch to the remote repository 
git config remote.origin.push refs/heads/$patchbranch:refs/remotes/origin/$patchbranch;
# we configure the local copy to add p4merge as the mergetool 
git config mergetool.p4merge.cmd "p4merge \"\$LOCAL\" \"\$BASE\" \"\$REMOTE\" \"\$MERGED\"";
git config mergetool.p4merge.keepTemporaries false; 
git config mergetool.keepBackup false;

echo "";
#commitdescr="applying commits to customer branch "$custbranch" on specific branch "$patchbranch" :";


if [ $4 = $cherrypickoption ];
##########################
######-CHERRY-PICK-#######
##########################
then
echo "SP: applying all the commits one by one"
# we start filling the commit description

# we scan all the commits SHA
shift $(($min_args - 1));
for i in $*
do
# we append the short SHA to the commit description
commitdescr="applying "`git log --pretty=format:"%h" -1 $i`;
echo "";
echo "SP: applying "$i;
# when cherry-picking, there is an auto-commit except when conflicts happen
git cherry-pick $i;
# if there is any conflict, merge manually the files
echo "SP: launching the mergetool to resolve the possible conflicts";
commitdescr=$commitdescr" /conflicts: "`git status -s -uno`" /message : "`git log --pretty=format:"%s" -1 $i`;
git mergetool;
#we commit all the changes
git commit -m"$commitdescr";
done

elif [ $4 = $mergeoption ];
####################
######-MERGE-#######
####################
then
echo "SP: merging "$5" and previous history into "$patchbranch;
  # we merge and stop on conflicts
  git merge $5;
# if there is any conflict, merge manually the files
echo "SP: launching the mergetool to resolve the possible conflicts";
git mergetool;
commitdescr="merging bugfixes up to commit "`git log --pretty=format:"%h" -1 $5`
#we commit all the changes
git commit -m"$commitdescr";
fi

echo "";

git checkout $custbranch;
git merge $patchbranch;

echo "";
echo "SP: changes have been committed. Please push changes to the repository by running \"git push -u origin $custbranch\"";
echo "";