#!/bin/bash

echo "Restoring MySQL database..."
mysql -u root -p cms < /home/project/java-database-capstone/scripts/cms_backup.sql
echo "MySQL database restored successfully!"

echo "Restoring MongoDB database..."
# Replace 'admin' with the auth database if your user is created there
mongosh "mongodb://root:ZgNe9jW8v5DEQ0DIMxL8SU2L@172.21.135.71:27017/prescriptions?authSource=admin" \
< /home/project/java-database-capstone/scripts/mongodb_setup.js
if [ $? -eq 0 ]; then
  echo "MongoDB database restored successfully!"
else
  echo "MongoDB restore failed!"
fi