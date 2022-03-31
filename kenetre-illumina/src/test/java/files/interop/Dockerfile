###############################################
# Dockerfile to build interop container image
# Based on Ubuntu 20.04
# Build with:
#   sudo docker build -t minimap2 .
###############################################

# Use ubuntu 20.04 base image
FROM ubuntu:20.04

# File author/maintainer info
MAINTAINER Laurent Jourdren <jourdren@biologie.ens.fr>

# set non-interactive mode
ENV DEBIAN_FRONTEND noninteractive
ARG VERSION=1.1.23

# Install dependencies
RUN apt update && \
    apt install --yes wget 
RUN cd /tmp &&\
    wget https://github.com/Illumina/interop/releases/download/v1.1.23/InterOp-$VERSION-Linux-GNU.tar.gz && \
    cd /usr/local && \
    tar -xzf /tmp/InterOp-$VERSION-Linux-GNU.tar.gz && \
    cd /usr/local/bin && \
    for f in /usr/local/InterOp-$VERSION-Linux-GNU/bin/* ; do ln -s $f ; done
