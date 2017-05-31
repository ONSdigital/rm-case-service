The test data to use for this version of the scheme can be found here :
http://filestore.rmdev.onsdigital.uk/chef/scripts/postgresql/9.28.0/


To maintain this data :
ssh into artifactory server - files are held on mounted ebs volume
cd /data/nginx/html/chef/scripts/postgresql/9.28.0

If you add a new file/directory to the filestore don't forget to set the group and ownership to be apache and also execute :

chcon --recursive --type httpd_sys_content_t /srv/filestore
