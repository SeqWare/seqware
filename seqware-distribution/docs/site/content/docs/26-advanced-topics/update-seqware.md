---

title:                 "Updating your SeqWare executable"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

##Updating to use the latest version of SeqWare

This guide is for updating your SeqWare Command Line Executable (CLI) executable to work with new (or old) versions of SeqWare.


##1. Download SeqWare
Download the version of SeqWare that you need from Artifactory. Make sure you copy the one JAR with "-full" at the end of its name, e.g. seqware-distribution-1.1.0-beta.1.jar
   <pre><code>#!console
user@localhost:~$ wget https://seqwaremaven.oicr.on.ca/artifactory/simple/seqware-release/com/github/seqware/seqware-distribution/1.1.0-beta.1/seqware-distribution-1.1.0-beta.1-full.jar
</code></pre>

##2.Copy the jar
Copy this jar to your seqware self-installs directory.
   <pre><code>#!console
user@localhost:~$ mv seqware-distribution-1.1.0-beta.1-full.jar ~/.seqware/self-installs
</code></pre>

##3. Edit your script
Open up your ~/bin/seqware script that you had installed previously and change the SeqWare version to the newest one by editing "DEFAULT_SEQWARE_VERSION". Make sure that the name exactly matches what the JAR is called.
   <pre><code>#!bash
#!/usr/bin/env bash
# Ensure this file is executable via `chmod a+x seqware`, then place it
# somewhere on your $PATH, like ~/bin. The rest of SeqWare will be
# installed upon first run into the ~/.seqware directory.
DEFAULT_SEQWARE_VERSION="1.1.0-beta.1"
</code></pre>

##4. Run seqware
Run `seqware` with no arguments. This will trigger SeqWare to download the sanity check file that corresponds to the new version.
   <pre><code>#!console
someUser@someHost:~$ seqware
Downloading SeqWare Check to /home/someUser/.seqware/self-installs/seqware-sanity-check-1.1.0-beta.1-jar-paired-with-distribution.jar now...
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  174k  100  174k    0     0  7158k      0 --:--:-- --:--:-- --:--:-- 7262k
<br/>
Usage: seqware [&lt;flag&gt;]
       seqware &lt;command&gt; [--help]
<br/>
Commands:
  annotate      Add arbitrary key/value pairs to seqware objects
  query         Display ad-hoc information about seqware objects
  bundle        Interact with a workflow bundle during development/admin
  copy          Copy files between local and remote file systems
  create        Create new seqware objects (e.g., study)
  files         Extract information about workflow output files
  study         Extract information about studies
  workflow      Interact with workflows
  workflow-run  Interact with workflow runs
  checkdb       Check the seqware database for convention errors
  check         Check the seqware environment for configuration issues
  dev           Advanced commands that are useful for developers or debugging
<br/>
Flags:
  --help        Print help out
  --version     Print Seqware's version
  --metadata    Print metadata environment
</code></pre>