The test data to use for this version of the scheme can be found here :


http://192.168.11.11:8001/chef/scripts/postgresql/9.28.0/


To maintain this data :

ssh centos@192.168.11.11
cd /srv/filestore/chef/scripts/postgresql/9.28.0

If you add a new file/directory to the filestore don't forget to set the group and ownership to be apache and also execute :

chcon --recursive --type httpd_sys_content_t /srv/filestore

