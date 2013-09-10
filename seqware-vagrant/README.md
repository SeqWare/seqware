## WARNING

I'm working on multi-pass provisioning, be aware I'm probably going to break
the script quite badly at times.  You probably want to stick to the develop
branch until I merge this feature branch.

## About

This Vagrant script will setup a single-node SeqWare box configured to use the
Oozie workflow engine. We are currently focused on AWS but have also added
support for OpenStack and VirtualBox and are testing now.

The current script is designed for single-nodes but we will soon add code that
lets you launch clusters as well.

In the latest version of the script you can specify multiple OS setup scripts.
This lets you have different "profiles" that can be combined to setup different
types of servers (e.g. database vs. web server etc). In the near future this
functionality will be re-implemented as Puppet scripts instead but for now this
will let us setup different types of SeqWare boxes if we need to.

## Installing 

Install Vagrant using the package from their site: http://www.vagrantup.com/.
You then need to install plugins to handle AWS and OpenStack. Virtualbox is 
available out of the box with Vagrant.

  vagrant plugin install vagrant-aws
  vagrant plugin install vagrant-openstack-plugin

## Getting "Boxes"

If you are running using VirtualBox you need to pre-download boxes which are
images of computers ready to use.  The easiest way to do this is to find the
URL of the base box you want to use here:

http://www.vagrantbox.es/

For example, to download the base Ubuntu 12.04 box you do the following:

  vagrant box add Ubuntu_12.04 http://cloud-images.ubuntu.com/precise/current/precise-server-cloudimg-vagrant-amd64-disk1.box

## Configuration

Copy the file templates/vagrant_launch.conf.template to the root dir of this
project (seqware-vagrant) and rename it vagrant_launch.conf.  Next, fill in
your various settings.  You can keep distinct settings for each of the backend
types which allows you to launch both AWS and OpenStack-based machines with
slightly tweaked differences.

## Running with the Wrapper

We provide a wrapper script (vagrant_launch.pl) that helps to lauch an instance
in different cloud environments. It makes sure sensitive information is not
stored in files that will be checked in and also collects various files from
other parts of the SeqWare build.

  # for AWS
  perl vagrant_launch.pl --use-aws
  # for OpenStack
  perl vagrant_launch.pl --use-openstack
  # for VirtualBox
  perl vagrant_launch.pl --use-virtualbox

This script also lets you point to the config file explicitly, change the
working directory (which defaults to target, it's the location where Vagrant
puts all of its runtime files), point to different OS-specific setup script(s),
and skip the integration tests if desired:

    # example
    perl vagrant_launch.pl --use-aws --working-dir target-aws --config-files templates/server_setup_scripts/ubuntu_12.04_base_script.sh,templates/server_setup_scripts/ubuntu_12.04_database_script.sh,templates/server_setup_scripts/ubuntu_12.04_portal_script.sh --skip-it-tests

## OICR Examples

These are in flux right now but I'll try to keep the following up to date.  We're using this seqware-vagrant process for the following projects.

Keep in mind you will want to take a look at the Vagrant template (templates/Vagrantfile.template) and modify as needed for your backend (AWS, OpenStack, Virtualbox) since some params (like floating IP address) are not yet parameters.

### Single Node - All Projects

This is currently broken since I've refactored for cluster launching.  The plan is to generalize this vagrant_launch.pl script so you can choose single or cluster mode and you can choose how many worker nodes to launch.  In the mean time use the develop branch instead if you need to launch a single node otherwise use the sample commands below to launch 2 node clusters for testing.

### SeqWare - Cluster

This will launch a 2 node cluster with a worker and master node. It's designed to use Oozie-Hadoop (not Oozie-SGE but Alex did create config shell scripts for this too).

    perl vagrant_launch.pl --use-openstack --skip-it-tests --os-master-config-scripts templates/server_setup_scripts/ubuntu_12.04_master_script.sh --os-worker-config-scripts templates/server_setup_scripts/ubuntu_12.04_worker_script.sh --os-initial-config-scripts templates/server_setup_scripts/ubuntu_12.04_minimal_script.sh

The only issue with this right now is I don't think HBase is configured to work in HDFS/distributed mode.  Also, see the note above about hard-coded values in the Vagrantfile.template.

### ICGC DCC Portal - Cluster

This will spin up a standard, 2 node SeqWare cluster (using Oozie-Hadoop), will setup elasticsearch, will download a dump of the (small) elasticsearch DCC index, load the dump into elasticsearch, and launch the DCC Portal web app on port 8998.

Keep in mind you should look at the templates/Vagrantfile.template before you launch to make sure your floating IP addresses are correct.  Also, the specific index dump file and DCC Portal jar file are hard coded in the ubuntu_12.04_master_dcc_portal_script.sh script so you will want to change these if there's an update.  Also, take a look at templates/DCC/settings.yml which has the index name embedded and will need to change if the index is updated.

    perl vagrant_launch.pl --use-openstack --skip-it-tests --os-master-config-scripts templates/server_setup_scripts/ubuntu_12.04_master_script.sh,templates/server_setup_scripts/ubuntu_12.04_elasticsearch_master_script.sh,templates/server_setup_scripts/ubuntu_12.04_master_dcc_portal_script.sh --os-worker-config-scripts templates/server_setup_scripts/ubuntu_12.04_worker_script.sh,templates/server_setup_scripts/ubuntu_12.04_elasticsearch_master_script.sh --os-initial-config-scripts templates/server_setup_scripts/ubuntu_12.04_minimal_script.sh

Once this finishes launching you can browse the DCC Portal at http://<master_node_IP>:8998/.

## Debugging

If you need to debug a problem set the VAGRANT_LOG variable e.g.:

    VAGRANT_LOG=DEBUG perl vagrant_launch.pl --use-aws

Also you can use the "--skip-launch" option to just create the various launch
files not actually trigger a VM.

## Shutting Down

You can terminate your instance via the provider interface (Open Stack, AWS, or VirtualBox).

## Manual Running Vagrant

You can use the Vagrantfile created by the launch script to manually start a
cluster node (note, you would have to run the vagrant_launch.pl at least once
before to get a target directory).  Change directory into the target dir.  This
command brings up a SeqWare VM on Amazon:

  cd target
  vagrant up --provider=aws

In case you need to re-run the provisioning script e.g. you're testing changes
and want to re-run without restarting the box:

  # just test shell setup
  vagrant provision --provision-with shell

## TODO

* need to setup HBase for the QueryEngine -- done
* need to edit the landing page to remove mention of Pegasus
* need to add code that will add all local drives to HDFS to maximize available storage (e.g. ephemerial drives) -- done
* ecryptfs -- done
* need to have a cluster provisioning template that works properly and coordinates network settings somehow
* should I add glusterfs in parallel since it's POSIX compliant and will play better with SeqWare or should I just use NFS?
* add teardown for cluster to this script
* need to add setup init.d script that will run on first boot for subsequent images of the provisioned node
* setup services with chkconfig to ensure a rebooted machine works properly -- done
* better integration with our Maven build process, perhaps automatically calling this to setup integration test environment -- done
* message of the day on login over ssh
* pass in the particular branch to use with SeqWare -- done
