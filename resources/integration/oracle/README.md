# Oracle database test scripts

The easiest way to get Oracle running inside a docker is to clone the 
[Oracle Single Instance](https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance) github
repository and build a docker image from there:

```shell script
cd /tmp
git clone https://github.com/oracle/docker-images
cd docker-images/OracleDatabase/SingleInstance/dockerfiles
./buildDockerImage.sh -v 18.4.0 -x
mkdir /tmp/oradata
docker run --name oracle18 \
    -p 1521:1521 -p 5500:5500 \
    -e ORACLE_PWD=password \
    -v /tmp/oradata:/opt/oracle/oradata \
    oracle/database:18.4.0-xe
```

Please note that this will download many gigabytes, heats up your computer and and takes about 30 minutes
to complete (in proper Oracle style).

Start your SQuirreL SQL client with the ojdbc7.jar in its lib folder (classes12.jar is no longer supported), and
connect to `jdbc:oracle:thin:@localhost:1521:xe` with user `sys as sysdba` and password `password`.

## For issue 113:


```sql
create table CLIENTES
(
  id INTEGER PRIMARY KEY,
  nome             VARCHAR2(200),
  email            VARCHAR2(150),
  cep              VARCHAR2(50),
  rg               VARCHAR2(50),
  cnpj             VARCHAR2(70),
  local_nascimento VARCHAR2(70),
  estado_civil     VARCHAR2(30),
  numero_endereco  INTEGER
);

INSERT INTO clientes (id,nome,email,cep,rg,cnpj,local_nascimento,estado_civil,numero_endereco
) VALUES (1,'nome1','email1','cep1','rg1','cnpj1','local_nascimento1','estado_civil1',11); 
INSERT INTO clientes (id,nome,email,cep,rg,cnpj,local_nascimento,estado_civil,numero_endereco
) VALUES (2,'nome2','email2','cep2','rg2','cnpj2','local_nascimento2','estado_civil2',12); 

CREATE USER c##anonimatron IDENTIFIED BY password;
GRANT create session to c##anonimatron;
grant all on clientes to c##anonimatron;

```

mkdir -p /tmp/oradata/devcdb/devpdb2


