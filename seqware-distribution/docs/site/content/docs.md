---

title:    "Documentation"
markdown: basic

---

<p class="warning"><strong>Note:</strong> This guide is a work in progress and was designed for release 0.13.6.x of SeqWare.</p> 

This is the core documentation for the SeqWare project, including all the
various sub-projects. We wanted to create a single manual that would bring
together all of our documentation since much of it is currently spread across a
poorly-organized wiki.  Because SeqWare is a fairly complex software stack
(with some difficult-to-configure dependencies) we assume most users will start
with a VM, either a local VirtualBox VM or an Amazon Machine Image (AMI) on
Amazon's cloud. See "Installation" for directions for getting the VM.  While
this guide is organized by logical sections that flow from one to the next you
do not need to read the whole thing in order to get started.  Take a look at
the tutorials in the "Getting Started" section which will walk you through the
core functionality such as SeqWare Pipeline. The tutorials are divided into  
User, Developer, and Admin guides.  These reflect the three types of users that 
will interact with a SeqWare install.  Generally the User injects input data
into SeqWare, runs workflows, and collects the results. These users also use 
our various command line and Portal reporting tools.  The second user is a
Developer that is actually responsible for the creation and testing of new
workflows.  Finally, the Admin installs workflows, automates the launching 
of workflows, and identifies failed workflows.  This user is responsible 
for the components that allow the User and Developer to do their work and,
consequently, this role is responsible for setting up and maintaining the
various SeqWare componenents.

##Conventions

The SeqWare documentation uses a few conventions for markup:

* <i>Italic text</i> introduces new terms.
* <code>Monospaced text</code> is used for code snippets.
* <kbd>Monospaced, bold text</kbd> is used for commands that should be typed literally.
* <var>Monospaced, italic text</var> is used for text that should be replaced with user-supplied values.

The documentation also contains quite a few blocks of code snippets. These are marked up like this:

<pre title="Title of the snippet"><code class="language-ruby">class Lorem::Ipsum
  def dolor
    [ :foo, "sit amet, consectetur adipisicing elit", 123 ]
  end
end</code></pre>

Bash shell input/output are marked up in a similar way. Note that the prompt is
always included, but should never be typed. Here’s an example:

<pre title="Title of the snippet"><span class="prompt">some-dir%</span> <kbd>echo "hello" &amp;&amp; cd other-dir</kbd>
hello
<span class="prompt">other-dir%</span></pre>
