# MySQL test scripts

This directory contains scripts for manually testing against an ms-sql or
sql server database. It makes use of a MySQL server running inside a
docker container.

## Docker commands

Start mysql container in docker
```shell
docker run --name anonimatron-mysql \
       -p3306:3306 \
       -e MYSQL_ROOT_PASSWORD=anonimatron \
       -d mysql:8
```

Start the mysql command line tool in the started container (the password is listed in the line above): 

```shell
docker exec -it anonimatron-mysql mysql -uroot -p
```

## Create database and tables

Create a test database as described at https://realrolfje.github.io/anonimatron/documentation/

## Configure and run Anonimatron

Run anonimatron with the [config.xml](config.xml) configuration file. 
A run configuration in IntelliJ may be better for debugging and analysis.
