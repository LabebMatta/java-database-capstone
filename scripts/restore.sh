#!/bin/bash

echo "Restoring MySQL database..."
mysql -u root -p < /home/project/java-database-capstone/scripts/cms_backup.sql

echo "Restoring MongoDB database..."
mongosh < /home/project/java-database-capstone/scripts/mongodb_setup.js

echo "Done! Both databases restored successfully."
