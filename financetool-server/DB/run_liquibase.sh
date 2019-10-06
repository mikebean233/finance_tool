#!/usr/bin/env bash

liquibase --defaultsFile="../backend/sql/liquibase.properties" update
