DBNAME=focus
DATE=`date +"%Y%m%d"`
SQLFILE=$DBNAME-${DATE}.sql
mysqldump --opt --user=root --password $DBNAME > $SQLFILE
gzip $SQLFILE

