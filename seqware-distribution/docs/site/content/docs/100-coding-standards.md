---

title:    "Coding Standards"
markdown: advanced

---

For developers on the actual SeqWare project, these are the proposed standards for code committed for development toward 1.1.X. 

First, an [Eclipse code formatting file](https://raw.githubusercontent.com/SeqWare/seqware/develop/seqware-eclipse-code-style.xml) is provided for users of the Eclipse IDE. Please look in the develop branch for updates. In addition, for users of the NetBeans IDE, the [Eclipse Code Formatter plugin](http://plugins.netbeans.org/plugin/50877/eclipse-code-formatter-for-java) must be configured with the same file. 

Second, a [Checkstyle configuration file](https://raw.githubusercontent.com/SeqWare/seqware/develop/checkstyle.xml) is available. This will be tightened during the run-up toward 1.1.X but currently it will do some light validation on your code and will reject a build if your issues are particularly egregious.

<!-- Checkstyle cannot currently enforce identation, the plugin is not configurable enough to match our coding standards. See https://stackoverflow.com/questions/18308208/indentation-check-not-working-properly-for-statement-label for an example -->


## Checkstyle Configuration

In some edge cases CheckStyle will incorrectly report an error. In these cases, you may choose to temporarily disable Checkstyle from your source code. Thanks to [ForgeRock](https://wikis.forgerock.org/confluence/display/devcom/Coding+Style+and+Guidelines#CodingStyleandGuidelines-UsingCheckstyletoenforcethecodingstyle) 

In order to toggle Checkstyle on and off

    // @Checkstyle:off
    ... ignored
    // @Checkstyle:on

In order to ignore the next line

    // @Checkstyle:ignore
    ... ignored
    ... checked

In order to ignore the next N lines (-ve means previous lines)


    // @Checkstyle:ignoreFor 2
    ... ignored
    ... ignored
    ... checked

## Code Formatting

Note that is is possible to turn on and off the Eclipse code formatter for sections of code that 
may be manually formatted in a way that is superior to what we can configure in Eclipse. 
The relevant tags are as follows:

    // @formatter:off
    ...
    // @formatter:on


### Eclipse Code Formatter

Using the above eclipse code formatting file, open preferences in Eclipse and search for "format".

Click on "Formatter" under Java -> Code Style. 

Click Import and and import the seqware-eclipse-code-style.xml file.

<img src="/assets/images/eclipse_format_1.png"/>

After the import is complete the SPB Java Convention will be the active profile.

Next Click on Editor -> Save Actions

<img src="/assets/images/eclipse_format_2.png"/>

Update the Save Actions preferences to look like the screenshot below.

<img src="/assets/images/eclipse_format_3.png"/>

Next Click on XML Files -> Editor and make the changes shown below.
<img src="/assets/images/eclipse_format_4.png"/>

Next search for "line" and click on Editors -> Text Editor.

Make the changes specified in the screen shot below.

<img src="/assets/images/eclipse_format_5.png"/>

### NetBeans Code Formatting (with the Eclipse Code Formatter plugin)

Follow the instructions available at [Eclipse Java Code Formatter in NetBeans Plugin Manager](https://blogs.oracle.com/geertjan/entry/eclipse_java_code_formatter_in)

