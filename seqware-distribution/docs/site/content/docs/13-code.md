---

title:                 "Source Code"
toc_includes_sections: false
markdown:              basic

---

## Getting Source Code

Our source code is available from [GitHub](https://github.com/SeqWare/seqware) or the "Fork me on GitHub" banner at the upper right at [Home](/)

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

You can then use Maven (version 3.0.4 or greater) in order to build and test our tools via <code>mvn clean install</code>

On Mac OS, you may need to install Protocol Buffers to successfully compile and test the SeqWare Query Engine. 

<pre title="Cloning the git repository">
<span class="prompt">~$</span> <kbd>wget http://protobuf.googlecode.com/files/protobuf-2.4.1.tar.gz</kbd>
<span class="prompt">~$</span> <kbd>tar xzf protobuf-2.4.1.tar.gz</kbd>
<span class="prompt">~$</span> <kbd>cd protobuf-2.4.1</kbd>
<span class="prompt">~$</span> <kbd>./configure</kbd>
<span class="prompt">~$</span> <kbd>make</kbd>
<span class="prompt">~$</span> <kbd>make install</kbd>
</pre>
