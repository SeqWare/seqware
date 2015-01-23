Please launch your local VM in VirtualBox or cloud AMI on Amazon now.  For the
local VM, login as user <kbd>seqware</kbd>, password <kbd>seqware</kbd> at this
time. Click on the "SeqWare Directory" link on the desktop which will open a
terminal to the location where we installed the SeqWare tools.

Alternatively, on the Amazon AMI follow the directions to log in
[here](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AccessingInstancesLinux.html).
Make sure that you launch our VM with the "cc1.4xlarge" instance type.
Also, please wait roughly 10 minutes for our startup scripts to run and fully setup your instance.

Once logging into the remote instance you need to "switch user" to
<kbd>seqware</kbd>, e.g.:

    $ sudo su - seqware

In some instances using AWS, it may be necessasry to run `sudo umount /dev/xvdc` as ubuntu user before switching to seqware user.

Both the VirtualBox VM and Amazon AMI include a start page that links to key information
for the VM such as the URLs for the installed Portal, Web Service, key file locations, etc.
On the VirtualBox VM, just click the "Start Here" link on the desktop.  For the Amazon instance
use the instance name provided by the AWS console. For example, it will look similar to:

	http://ec2-54-224-22-195.compute-1.amazonaws.com

You fill in your instance DNS name from the Amazon console in place of ec2-54-224-22-195.compute-1.amazonaws.com above. Make sure you check your security group settings to ensure port 80 (and the other ports referenced in the landing page) are open.
