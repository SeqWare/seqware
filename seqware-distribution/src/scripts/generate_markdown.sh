#! /bin/bash
java -jar target/seqware-distribution-*-full.jar  -p net.sourceforge.seqware.pipeline.plugins.MarkdownPlugin -- -s WorkflowPlugin, ModuleRunner, MarkdownPlugin | tail -n +1 > docs/site/content/docs/17-plugins.md
java -jar target/seqware-distribution-*-full.jar  -p net.sourceforge.seqware.pipeline.plugins.MarkdownPlugin -- -m | tail -n +1 > docs/site/content/docs/17a-modules.md
java -jar target/seqware-distribution-*-full.jar  -p io.seqware.pipeline.plugins.UserSettingsPlugin | tail -n +1 > docs/site/layouts/includes/settings

