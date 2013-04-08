## Introduction 

This README is just a quick overview of building SeqWare. See our
[project homepage](http://seqware.github.com) for much more documentation.

This is top level of the [SeqWare Project](http://seqware.github.com).
This contains the 5 major components of the SeqWare project along with
documentation:

* seqware-meta-db
* seqware-webservice
* seqware-portal
* seqware-pipeline
* seqware-queryengine
* seqware-common
* the http://seqware.github.com website and manual
* seqware-ext-testing

The seqware-common sub-project provides a location for common code
and most of the other sub-projects have this as a dependency.

## Prerequisites 

###A Recent Linux Distribution

This pretty much goes without saying but the SeqWare project is targeted at
Linux.  You may be able to compile and use the software on MacOS X but, in all
honesty, we recommend you use a recent Linux distribution such as Debian
(Ubuntu, Linux Mint, etc) or RedHat (RedHat Enterprise, Fedora, etc).  This
software, although written in Java mostly, was never intended to work on
Windows. If you need to use Windows for development or deployment we recommend
you simply use our VirtualBox VM for both activities, see our extensive documentation
on http://seqware.github.com for more information. You can also use this same
approach on MacOS (or even another version of Linux).

###Java

SeqWare requires Oracle JDK 1.6 or greater, we primarily write and test with JDK 1.6.x.
An example of instructions on how to update your Linux installation can be found [here](https://ccp.cloudera.com/display/CDH4DOC/Before+You+Install+CDH4+on+a+Single+Node#BeforeYouInstallCDH4onaSingleNode-InstalltheOracleJavaDevelopmentKit). You will need to use the method appropriate to your distribution to install this.

## Building 

### Getting the Source Code 

Our source code is available from [GitHub](https://github.com/SeqWare/seqware) or the "Fork me on GitHub" banner at the upper right of our website

To get a copy of of our source code you will first need to install Git (<code>sudo apt-get install git</code> in Ubuntu) and then clone our repository.

<pre title="Cloning the git repository">
<span class="prompt">~$</span> <kbd>git clone git://github.com/SeqWare/seqware.git</kbd>
Cloning into 'seqware'...
remote: Counting objects: 8984, done.
remote: Compressing objects: 100% (2908/2908), done.
remote: Total 8984 (delta 4308), reused 8940 (delta 4265)
Receiving objects: 100% (8984/8984), 33.57 MiB | 392 KiB/s, done.
Resolving deltas: 100% (4308/4308), done.
</pre>


### Building and Automated Testing 

We're moving to Maven for our builds, this is currently how
you do it in the trunk directory:

    mvn clean install

Maven now runs unit tests as follows.

    mvn clean install # (runs unit tests but skips integration tests, HBase for query engine and Jetty for web service by default) 
    mvn clean install -DskipTests # (skips all unit tests and integration tests)

In order to run the integration tests on the entire project, please ensure that you have followed the steps in each of the integration testing guides for our sub-projects. This includes [MetaDB](http://seqware.github.com/docs/github_readme/3-metadb/) , [Web Service](http://seqware.github.com/docs/github_readme/4-webservice/) , and [Query Engine](http://seqware.github.com/docs/github_readme/2-queryengine/). 

When this is complete: 

    export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m" (ensures enough memory for integration tests)
    mvn clean install -DskipITs=false # (runs all unit tests and integration tests that only require postgres as a prerequisite)
    mvn clean install -DskipITs=false -P extITs # (runs all unit tests and all integration tests including those that require Condor/Globus/Pegasus)

In the last case, the extended integration tests profile is used to trigger integration tests that run our command line utilities. 
In order to point your command-line tools at the web service brought up by the integration tests, you will need to modify your SeqWare ~/.seqware/settings to include:

You can also build individual components such as the new query engine with: 

    cd seqware-queryengine
    mvn clean install

###Problems with Maven

Sometimes we run into problems when building, strange missing dependency issues
and broken packages. A lot of the time this is an issue with Maven, try
deleting your ~/.m2 directory and running the build process again.


## Installing

See http://seqware.github.com/ for detailed installation instructions
including links to a pre-configured virtual machine that can be used for
testing, development, and deployment.

## Copyright

Copyright 2008-2013 Brian D O'Connor, OICR, UNC, and Nimbus Informatics, LLC

## Contributors

Please see our [partners and contributors](http://seqware.github.com/partners/)

## License

SeqWare is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SeqWare is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SeqWare.  If not, see <http://www.gnu.org/licenses/>.


