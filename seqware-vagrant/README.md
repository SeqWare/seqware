## About

This Vagrant script will setup a single-node SeqWare box configured to use the
Oozie workflow engine. We are currently focused on AWS but will add support for
OpenStack and VirtualBox soon.

## Installing 

Install Vagrant using the package from their site: http://www.vagrantup.com/.
You then need to install plugins to handle AWS and OpenStack. We are focused on
AWS for now.

  vagrant plugin install vagrant-aws
  vagrant plugin install vagrant-openstack-plugin

## Configuration

Use the perl script "vagrant_launch.pl", this will prevent you from having to
manually collect files for provisioning to the new host.

You should edit the templates/Vagrantfile to reflect settings that are not
currently exposed by the launch script.

## Running with the Wrapper

We provide a wrapper script that helps to lauch an instance in different cloud
environments. It makes sure sensitive information is not stored in files that
will be checked in and also collects various files from other parts of the
SeqWare build.

  # for AWS
  perl vagrant_launch.pl --aws-key 'FILLMEIN' --aws-secret-key 'FILLMEIN' --use-aws
  # for OpenStack (not implemented yet)
  perl vagrant_launch.pl --use-openstack
  # for VirtualBox (not implemented yet)
  perl vagrant_launch.pl --use-virtualbox

## Manual Running

You can use the Vagrantfile created by the launch script to manually start a
cluster node.  Change directory into the target dir.  This command brings up a
SeqWare VM on Amazon:

  cd target
  vagrant up --provider=aws

In case you need to re-run the provisioning script e.g. your testing changes:

  # just test shell setup
  vagrant provision --provision-with shell

## TODO

* need to setup HBase for the QueryEngine
* need to edit the landing page to remove mention of Pegasus
* need to add code that will add all local drives to HDFS to maximize available storage (e.g. ephemerial drives)
* add teardown for cluster to this script
