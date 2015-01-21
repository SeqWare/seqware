---

title:                 "Updating your SeqWare executable"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

##Updating to use the latest version of SeqWare

This guide is for updating your SeqWare Command Line Executable (CLI) executable to work with new (or old) versions of SeqWare.


1. Download the version of SeqWare that you need from Artifactory. Artifactory houses both SeqWare releases and nightly snapshots. Make sure you copy the one JAR with "-full" at the end of its name, e.g. seqware-distribution-1.1.0-beta.1-20150103.005933-2-full.jar
   <pre><code>#!console
user@localhost:~$ wgets https://seqwaremaven.oicr.on.ca/artifactory/simple/seqware-snapshot/com/github/seqware/seqware-distribution/1.1.0-beta.1-SNAPSHOT/seqware-distribution-1.1.0-beta.1-20150103.005933-2-full.jar
</code></pre>
2. Copy this jar to your seqware self-installs directory.
   <pre><code>#!console
user@localhost:~$ mv seqware-distribution-1.1.0-beta.1-20150103.005933-2-full.jar ~/.seqware/self-installs
</code></pre>
3. Open up your ~/bin/seqware script that you had installed previously and change the SeqWare version to the newest one by editing "DEFAULT_SEQWARE_VERSION". Make sure that the name exactly matches what the JAR is called.
   <pre><code>#!bash
#!/usr/bin/env bash
# Ensure this file is executable via `chmod a+x seqware`, then place it
# somewhere on your $PATH, like ~/bin. The rest of SeqWare will be
# installed upon first run into the ~/.seqware directory.
DEFAULT_SEQWARE_VERSION="1.1.0-beta.1-20150103.005933-2"
</code></pre>

