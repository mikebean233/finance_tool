FROM grafana/grafana-oss:7.5.0

EXPOSE 3000

COPY dashboards/* /etc/grafana/provisioning/dashboards/
COPY datasources/* /etc/grafana/provisioning/datasources/
COPY server_root_url /etc/grafana/server_root_url
COPY server_serve_from_sub_path /etc/grafana/server_serve_from_sub_path
