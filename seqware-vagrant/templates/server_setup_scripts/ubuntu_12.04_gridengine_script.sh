#!/bin/bash

# see http://helms-deep.cable.nu/~rwh/blog/?p=159

# get packages
apt-get update
export DEBIAN_FRONTEND=noninteractive
apt-get -q -y --force-yes install gridengine-client gridengine-common gridengine-exec gridengine-master

# configure
sudo -u sgeadmin qconf -am seqware
qconf -au seqware users
qconf -as localhost

# this is interactive... how do I load from a file?
qconf -ae
qconf -ahgrp @allhosts

# config
qconf -aattr hostgroup hostlist localhost @allhosts

# interactive
qconf -aq main.q

qconf -aattr queue hostlist @allhosts main.q

qconf -aattr queue slots "[localhost=1]" main.q


