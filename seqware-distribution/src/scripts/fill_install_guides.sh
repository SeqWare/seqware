#! /bin/bash
git checkout docs/site/content/docs/github_readme/
git update-index --assume-unchanged docs/site/content/docs/github_readme/*.md
cat ../seqware-portal/README.md >> docs/site/content/docs/github_readme/5-portal.md
cat ../seqware-pipeline/README.md >> docs/site/content/docs/github_readme/1-pipeline.md
cat ../seqware-meta-db/README.md >> docs/site/content/docs/github_readme/3-metadb.md
cat ../seqware-queryengine/README.md >> docs/site/content/docs/github_readme/2-queryengine.md
cat ../seqware-webservice/README.md >> docs/site/content/docs/github_readme/4-webservice.md
