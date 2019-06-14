# MS-SQL test scripts

THis directory contains scripts for manually testing against an ms-sql or
sql server database. It makes use of an sql server running inside a
docker container.

## Docker commands

See https://hub.docker.com/_/microsoft-mssql-server

Get the docker image
```
docker pull mcr.microsoft.com/mssql/server:2017-latest-ubuntu
```

Start the mssqql docker image listening on port 1433
```shell
docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=Anon!matron' -p 1433:1433 -d mcr.microsoft.com/mssql/server:2017-latest
```

Start the sqlcmd command line tool (focused_proskuriakova is the container name in this case)
```shell
docker exec -it focused_proskuriakova /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'Anon!matron'
```

## Create database and tables

Create a contained database (for local user management), create a schema, and
two tables, one in the non-default schema.

```roomsql
EXEC sp_configure 'CONTAINED DATABASE AUTHENTICATION'
go
EXEC sp_configure 'CONTAINED DATABASE AUTHENTICATION', 1
create database mydb containment = partial
go

use mydb
go

CREATE USER test WITH PASSWORD = 'Test.1234'
GRANT SELECT to test
grant update to test
go

CREATE TABLE TABLE1 (ID int primary key IDENTITY(1,1) NOT NULL, COL1 VARCHAR(200))
CREATE SCHEMA SCHEMA2
CREATE TABLE SCHEMA2.TABLE2 (ID int primary key IDENTITY(1,1) NOT NULL, COL1 VARCHAR(200))
go

INSERT INTO table1 (col1) VALUES ('testmail@example.com'); 
INSERT INTO schema2.table1 (col1) VALUES ('testmail@example.com'); 
go
```

## Configure and run Anonimatron

Run anonimatron with the [config.xml](config.xml) configuration file. 
A run configuration in IntelliJ may be better for debugging and analysis.
