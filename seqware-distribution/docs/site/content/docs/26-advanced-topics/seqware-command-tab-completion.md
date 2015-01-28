---

title:                 "Seqware command tab-completion"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

This document will describe how to achieve tab-completion functionality for SeqWare.

##Download the tab-completion script.

Download the script from <a href="https://github.com/SeqWare/seqware/releases/download/<%= seqware_release_version %>/seqware_bash_completion">https://github.com/SeqWare/seqware/releases/download/<%= seqware_release_version %>/seqware_bash_completion</a>

From the command line, you can do it like this:
<pre><code>#!console
$ wget https://github.com/SeqWare/seqware/releases/download/<%= seqware_release_version %>/seqware_bash_completion
</code></pre>

##Install the script.
The script contains bash code that will tell your system how to perform tab-completion for the `seqware` command. You will need to execute this script to register this code with your system. To do this, you must ensure that the file is executable.

<pre><code>#!console
$ chmod u+x seqware_bash_completion
</code></pre>
 
The script needs to be installed in `/etc/bash_completion.d/`. If you don't have access to this directory, you could try installing it to `~/.bash_completion.d/` instead.
<pre><code>#!console
$ sudo mv seqware_bash_completion /etc/bash_completion.d/
</code></pre>

The script then needs to be executed.

<pre><code>#!console
$ /etc/bash_completion.d/seqware_bash_completion
</code></pre>

You should now be able to type `seqware`-<kbd>&lt;TAB&gt;</kbd> and see a list of available sub-commands (the changes might not be noticable until you open a new terminal or start a new session):

<pre><code>#!console
$ seqware 
annotate      checkdb       create        files         workflow      
bundle        copy          dev           query         workflow-run  
</code></pre>

This will work on subcommands as well!

<pre><code>#!console
$ seqware annotate 
experiment    ius           processing    study         workflow-run  
file          lane          sample        workflow
</code></pre>