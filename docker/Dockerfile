FROM dockerfile/elasticsearch
MAINTAINER Alex Ciminian <alex.ciminian@gmail.com>
ENV REFRESHED_AT 2014-08-17

# Mount elasticsearch.yml config
ADD config/elasticsearch.yml /elasticsearch/config/elasticsearch.yml

# install kopf and kibana
RUN /elasticsearch/bin/plugin --install lmenezes/elasticsearch-kopf/1.2
RUN /elasticsearch/bin/plugin -url https://download.elasticsearch.org/kibana/kibana/kibana-3.1.0.zip --install elasticsearch/kibana

# Expose ports.
#   - 9200: HTTP
#   - 9300: transport
EXPOSE 9200
EXPOSE 9300
