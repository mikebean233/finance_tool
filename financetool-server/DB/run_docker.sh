#!/usr/bin/env bash

echo "docker run --name finance-db -e 'ACCEPT_EULA=Y' -e "SA_PASSWORD=$1" -e 'MSSQL_PID=Express' -p 1433:1433 -d mcr.microsoft.com/mssql/server:2019-CTP3.2-ubuntu"
docker run --name finance-db -e 'ACCEPT_EULA=Y' -e "SA_PASSWORD=$1" -e 'MSSQL_PID=Express' -p 1433:1433 -d mcr.microsoft.com/mssql/server:2019-CTP3.2-ubuntu
