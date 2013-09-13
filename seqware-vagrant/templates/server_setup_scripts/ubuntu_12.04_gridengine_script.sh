#!/bin/bash

# see http://helms-deep.cable.nu/~rwh/blog/?p=159
# you would test this by (as seqware) doing:
# "echo hostname | qsub -cwd
# watch qstat -f"
# you should then see your job run and also see STDIN.* files get created with the hostname in them

# get packages
apt-get update
export DEBIAN_FRONTEND=noninteractive
apt-get -q -y --force-yes install gridengine-client gridengine-common gridengine-exec gridengine-master

# restart
/etc/init.d/gridengine-exec stop
/etc/init.d/gridengine-master restart
/etc/init.d/gridengine-exec start

# configure
export HOST=`hostname`
sudo -u sgeadmin qconf -am seqware
qconf -au seqware users
qconf -as $HOST

# this is interactive... how do I load from a file?
cat >/tmp/qconf-editor.sh <<EOF
#!/bin/sh
sleep 1
perl -pi -e 's/^hostname.*$/hostname $HOST/' \$1
EOF
chmod +x /tmp/qconf-editor.sh
export EDITOR=/tmp/qconf-editor.sh
qconf -ae

# now do this again
cat >/tmp/qconf-editor.sh <<EOF
#!/bin/sh
sleep 1
perl -pi -e 's/^hostlist.*$/hostlist $HOST/' \$1
EOF
chmod +x /tmp/qconf-editor.sh
export EDITOR=/tmp/qconf-editor.sh
qconf -ahgrp @allhosts
# might need to do this instead
qconf -mhgrp @allhosts

# config
qconf -aattr hostgroup hostlist $HOST @allhosts

# interactive
# uses the same editor as above
qconf -aq main.q
# same as above, may need to modify the queue instead
qconf -mq main.q

qconf -aattr queue hostlist @allhosts main.q

qconf -aattr queue slots "[$HOST=1]" main.q

# restart
/etc/init.d/gridengine-exec stop
/etc/init.d/gridengine-master restart
/etc/init.d/gridengine-exec start

