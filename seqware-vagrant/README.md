## About

This Vagrant script will setup a single-node SeqWare box configured to use the
Oozie workflow engine. We are currently focused on AWS but have also added
support for OpenStack and VirtualBox and are testing now.

The current script is designed for single-nodes but we will soon add code that
lets you launch clusters as well.

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
project and rename it vagrant_launch.conf.  Next, fill in your various
settings.  You can keep distinct settings for each of the backend types which
allows you to launch both AWS and OpenStack-based machines.

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

## Manual Running

You can use the Vagrantfile created by the launch script to manually start a
cluster node (note, you would have to run the vagrant_launch.pl at least once
before to get a target directory).  Change directory into the target dir.  This
command brings up a SeqWare VM on Amazon:

  cd target
  vagrant up --provider=aws

In case you need to re-run the provisioning script e.g. your testing changes:

  # just test shell setup
  vagrant provision --provision-with shell

## TODO

* need to setup HBase for the QueryEngine -- done
* need to edit the landing page to remove mention of Pegasus
* need to add code that will add all local drives to HDFS to maximize available storage (e.g. ephemerial drives) -- done
* need to have a cluster provisioning template that works properly and coordinates network settings somehow
* add teardown for cluster to this script
* need to add setup init.d script that will run on first boot for subsequent images of the provisioned node
* setup services with chkconfig to ensure a rebooted machine works properly -- done
* better integration with our Maven build process, perhaps automatically calling this to setup integration test environment -- done
* message of the day on login over ssh
* pass in the particular branch to use with SeqWare -- done
