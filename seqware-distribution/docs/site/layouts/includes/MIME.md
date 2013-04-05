SeqWare Pipeline modules consume input files and settings and output files as the result. It's very important to agree on the mime type used to document the inputs and outputs, this way a given module "knows" if it can use a certain file as an input and/or output. This will allow us to use tools to build workflows visually because the input/output types of the modules can be known.  

In order to get this to work we need to agree on the mime types used in SeqWare modules. The following are used by one or more modules, please check this list before you write a new module to see if input/outputs can be annotated with one of these mime types.  Only add to this list if you're certain the MIME type does not already exist below.

For a directory of standard mime types (you should use a standard one if it exists!) see [here](http://www.feedforall.com/mime-types.htm here), [here](http://silk.nih.gov/public/zzyzzap.@www.silk.types.html here) and [here](http://www.iana.org/assignments/media-types/index.html here).  Wikipedia has more to say [here](http://en.wikipedia.org/wiki/MIME here).

For the current list of MIME types, please see the [current list](https://docs.google.com/spreadsheet/ccc?key=0An-x7dcdlF7AdGhjdjRTU0toZkJXNlNRb1NROXdfLWc).
If you wish to add a new MIME type, use the form which contributes to this list [here](https://docs.google.com/spreadsheet/viewform?formkey=dGhjdjRTU0toZkJXNlNRb1NROXdfLWc6MQ).
