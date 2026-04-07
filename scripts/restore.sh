#!/bin/bash

echo "Restoring MySQL database..."
mysql -u root -p < /home/project/java-database-capstone/scripts/cms_backup.sql
echo "MySQL database restored successfully!"

echo "Restoring MongoDB database..."
# Replace 'admin' with the auth database if your user is created there
mongosh "mongodb://root:XgUjL03qKh9Mjb455RYpgiiA@172.21.120.132:27017/prescriptions?authSource=admin" \
< /home/project/java-database-capstone/scripts/mongodb_setup.js
if [ $? -eq 0 ]; then
  echo "MongoDB database restored successfully!"
else
  echo "MongoDB restore failed!"
fi