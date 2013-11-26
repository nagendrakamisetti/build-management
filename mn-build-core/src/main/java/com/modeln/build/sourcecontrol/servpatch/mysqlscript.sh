#!/bin/sh

if [ $# -lt 1 ]
then
echo "";
else
sqlstring="SELECT version_ctrl_id FROM build WHERE build_version = '$1'";
# add column version_ctrl_root to get the depot name

# params for the DB
SQL_SCRIPT="/var/tmp/getcommitfrombuild.sql";
BUILD_DB=mn_build;
MYSQL_HOSTNAME=pddev.modeln.com;
MYSQL_USERNAME=mndist;
MYSQL_PASSWORD=mndist;

# store the request in a sql script
echo $sqlstring > $SQL_SCRIPT;

# get data from DB
result=`/usr/bin/mysql --host=$MYSQL_HOSTNAME --user="$MYSQL_USERNAME" --password="$MYSQL_PASSWORD" $BUILD_DB < $SQL_SCRIPT`;

# prints the commit (depot would be the third word of the result)
echo $result | cut -d \  -f 2;
fi
