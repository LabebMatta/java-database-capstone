#!/bin/bash

# Change to your actual Windows path (use forward slashes, e.g., C:/Users/labeb/...)
# Replace 'YOUR_PASSWORD' with your actual MySQL root password
echo "Restoring MySQL database..."
mysql -u root abcd1234EFGHIJK cms < C:/Users/labeb/java-database-capstone/scripts/cms_backup.sql
echo "MySQL database restored successfully!"

echo "Restoring MongoDB database..."
# Change the IP to 'localhost' since it's now on your machine
# Use the actual username/password you set for MongoDB, or leave blank if you didn't set one
mongosh "mongodb://localhost:27017/prescriptions" < C:/Users/labeb/java-database-capstone/scripts/mongodb_setup.js

if [ $? -eq 0 ]; then
  echo "MongoDB database restored successfully!"
else
  echo "MongoDB restore failed!"
fi