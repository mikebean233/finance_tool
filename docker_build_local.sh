!# /bin/sh

docker build finance-tool .
cd grafana
docker build finance-tool-grafana .
