
    alter table tbs_package_file 
       add column upload_date timestamp;

    alter table tbs_package_file
       drop column if exists uploaded;
