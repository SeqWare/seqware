---

title:                 "SeqWare Pipeline: Deciders"
markdown:              advanced
toc_includes_sections: true
is_dynamic:            true

---


## Overview

So far you've seen how to write a module to wrap a given tool in a consistent way. You've also seen how to write a workflow that links together multiple modules and parametrizes them through a simple ini file. Finally, you can put these together and run the workflow using the commands you saw in the introduction with HelloWorld. But the reality of large projects dictates that you can't run each workflow manually. That's where deciders come in, they essentially are a bit of code that links up the metadata in the MetaDB with a given workflow based on a set of rules encoded in the decider. At UNC we have deciders for each of our workflows and they let us process all the TCGA data on hourly cron jobs. These deciders, for example, look in the database for all RNASeq human samples that have not previously been processed through a workflows, pulls back the needed data from the MetaDB, and triggers a workflow run for that particular lane. This is the heart of what a decider is trying to do, it links metadata to the actual execution of workflows in an automated way.

So we created the deciders for:

* automation, running workflows as a cron
* a place for nasty code, frequently changing code, other code that is site specific 

For the last point we've tried to make modules very clean and generic with little business logic, mainly focused on parametrization for behavior. The workflows are very dumb as well and really depend on parametrization. So we've essentially used the deciders as places to contain logic that might be site specific or simply frequently changing. 

## API Classes

The interface for a decider is [DeciderInterface](http://seqware.github.com/javadoc/git_0.13.4/apidocs/net/sourceforge/seqware/pipeline/decider/DeciderInterface.html). In practice, when creating your own deciders you will probably extend [BasicDecider](http://seqware.github.com/javadoc/git_0.13.4/apidocs/net/sourceforge/seqware/pipeline/deciders/BasicDecider.html)


