<p class="warning"><strong>Note:</strong> This guide is useful if you want to install SeqWare from scratch on your local infrastructure. Be warned, there are rather vague sections and this guide was really written as notes for setting up our VMs. It is geared towards experienced Linux sysadmins and we cannot guarantee this guide is always up to date. </p>

If you just want to get started with SeqWare quickly please see [Installing the SeqWare VM](/docs/2-installation/#installing-with-a-local-vm) for instructions on downloading a VM that is ready for workflow development and testing. We also recommend this approach for production installs because you can connect easily these VMs to a real cluster.

## Introduction

Historically, we had a big and difficult to maintain document here detailing manual steps. However, you are now in much better hands. We have two main tools that aid us in deploying SeqWare to Ubuntu 12.04 hosts. 

First, we have [Bindle](https://github.com/CloudBindle/Bindle). This project allows you to provision VMs and then configure them using a "bag." If you need to configure SeqWare in a cloud environment (AWS or Openstack), follow the readme in order to configure vagrant and ansible.

Second, if you already have a ubuntu 12.04 host handy or are not working in a cloud environment, you can use our Ansible playbooks for configuring a host directly. We have a playbook for a [clean SeqWare host](https://github.com/SeqWare/seqware-bag) and a playbook for a [Pancancer environment](https://github.com/ICGC-TCGA-PanCancer/pancancer-bag).

## Next Steps

There are two main next steps:

### Develop and Test New Workflow Bundles

Once you can run the HelloWorlds workflow you should be ready to use this VM for the development of new modules and/or workflows or running/testing existing ones.  Take a look at the [Workflow Manuals](/docs/6-pipeline/#workflow-manuals) for information about how to develop for SeqWare Pipeline.

### Connect the VM to a Real Cluster

In order to hook up the VM to a real cluster, you will need to make sure that all SGE tools (like qsub, qstat, and qacct) are hooked up to a real SGE environment. Additionally, you will want to make sure that seqware's shared directories (released bundles, provisioned bundles, and oozie working directory) are available to the execution nodes.

Historically, if you're using this VM that has already been setup you will still want to edit the sge.pm file since it contains paths and ports that will need to change given your environment.  If you're using the VM as a self-contained environment you don't need to worry about this.  But if you're connecting to a real cluster then you need to have these setup properly.  For example you would need to customize the following section in /usr/share/perl5/vendor_perl/Globus/GRAM/JobManager/sge.pm:

	BEGIN
	{
	    $qsub        = '/usr/local/ge-6.1u6/bin/lx24-amd64/qsub';
	    $qstat       = '/usr/local/ge-6.1u6/bin/lx24-amd64/qstat';
	    $qdel        = '/usr/local/ge-6.1u6/bin/lx24-amd64/qdel';
	    #
	    $mpirun      = 'no';
	    $sun_mprun   = 'no';
	    $mpi_pe      = '';
	    #
	    if(($mpirun eq "no") && ($sun_mprun eq "no"))
	      { $supported_job_types = "(single|multiple|condor)"; }
	    else
	      { $supported_job_types = "(mpi|single|multiple|condor)"; }
	    #
	    $cat         = '/bin/cat';
	    #
	    $SGE_ROOT    = '/usr/local/ge-6.1u6';
	    $SGE_CELL    = 'default';
	    $SGE_MODE    = 'SGE';
	    $SGE_RELEASE = '6.1u6';

	    $SGE_ARCH='lx26-amd64';
	    $SGE_EXECD_PORT=6445;
	    $SGE_QMASTER_PORT=6444;
	    $ENV{"SGE_ROOT"} = $SGE_ROOT;
	    $ENV{"SGE_ARCH"}=$SGE_ARCH;
	    $ENV{"SGE_EXECD_PORT"}=$SGE_EXECD_PORT;
	    $ENV{"SGE_QMASTER_PORT"}=$SGE_QMASTER_PORT;
	 }

Make sure the paths and ports are valid for the cluster you're connecting too.  You'll also want to take a look at the other modifications made to sge.pm to see if they are valid for the new cluster you are connecting to.  For example, what you call your parallel environment (serial) and what variable you use to define the memory consumable resource differ from SGE install to SGE install.
