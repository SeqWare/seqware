## Documentation Compilation

In relation to Hubflow, our procedure will be as follows. For new features that 
are still in a feature branch, documentation can be kept in that feature branch. 
Compilation should be invoked for local viewing, but do not push the results to 
the public website. New documentation meant for public release should be done on 
the 'develop' branch. 'Master' should be avoided and should only be modified 
during releases or with the Hubflow hotfix workflow.

### Docker Container

The recommended way to build the documentation is within a docker container that already has the dependencies that we need. 
See [seqware.github.com inside docker](https://github.com/SeqWare/seqware-sandbox/tree/develop/seqware.github.com_inside_docker) which builds upon [seqware inside docker](https://github.com/SeqWare/seqware-sandbox/tree/develop/seqware_inside_docker)

### Historical 

It is also possible to install everything from scratch on your development box.

Install nanoc (on Ubuntu 12.04):

For a basic version of nanoc just follow the directions on their site. This is
what I had to do to get their very nice sample website to compile:

    sudo apt-get install ruby1.9.3 ruby-rvm ruby-rdiscount ruby-nokogiri
    sudo gem install nanoc kramdown adsf mime-types compass haml coderay rubypants builder rainpress yajl-ruby pygments.rb 

You may need to install a specific version of yajl-ruby if you run into a conflict.

Source code is here:
seqware/seqware-distribution/docs/site

In order to add/use variables:
look at site/content/docs/webservice-api/seqware-webservice/metadata/db/study_get.html which uses site/lib/helpers_.rb

In order to do an include:
look at the admin guide for an example at site/content/docs/3-getting-started/admin-tutorial.md which uses site/layouts/includes/

Go into this directory and compile the site:

    nanoc compile

View the site in http://localhost:3000 :

    nanoc view

Dynamically compile and view the site at localhost:3000:

    nanoc autocompile

To run checks for broken links:

    nanoc check external_links
    nanoc check ilinks (check internal links)
    nanoc check --deploy (all checks)

Modify the index.html so the logo points to /seqware/ and the css points to /seqware/style.css

#### Update Metadb Schema

The seqware.github.com repo acts as the only store of the generated schema documentation. 
In order to update it:

1. Download the 5.0.0 schemaspy jar from http://schemaspy.sourceforge.net/ 
2. Create a postgres properties file of the following form (named mypg in this example)

	#
	# see http://schemaspy.sourceforge.net/dbtypes.html
	# for configuration / customization details
	#

	description=PostgreSQL

	connectionSpec=jdbc:postgresql://localhost/test_seqware_meta_db
	host=localhost
	db=test_seqware_meta_db

	driver=org.postgresql.Driver

	# Sample path to the postgresql drivers.
	# Use -dp to override.
	driverPath=./postgresql-9.3-1101.jdbc4.jar

	selectRowCountSql=select reltuples as row_count from pg_class where relname=:table

3. Download a postgres jdbc driver (ex: postgresql-9.3-1101.jdbc4.jar ) and modify the above accordingly
4. Generate the documentation 

    java -jar schemaSpy_5.0.0.jar -db test_seqware_meta_db -host localhost -o output -u dyuen -t ./mypg -s public -noads -nologo

5. Clone the documentation repo, copy the results from above, and push

    git clone git@github.com:SeqWare/seqware.github.com.git
    cp -R output/* seqware.github.com/metadb-schema
    cd seqware.github.com && git add metadb-schema && git push


#### Historical Troubleshooting 

I did not need to perform any of the steps below, but you may need to on your install

Additional setup for Linux Mint 13:
First, the ruby-rvm package did not install correctly
* sudo apt-get remove ruby-rvm 
* sudo apt-get install ruby1.9.1-dev
* sudo gem install yajl-ruby pygments.rb 

Hack on Linux Mint 13:

I had problems compiling the site until I commented out the following on line 23 of /var/lib/gems/1.9.1/gems/sass-3.2.3/lib/sass/importers/filesystem.rb

      # @see Base#find_relative
      def find_relative(name, base, options)
        #_find(File.dirname(base), name, options)
      end

Be careful with this, I have no idea if I broke anything by doing this.

Also, I originally installed this but I had to disable this in the code, didn't seem to work for me

sudo gem install fast-aleck 

Ran into this message:

    Gem::LoadError: Unable to activate pygments.rb-0.5.4, because yajl-ruby-1.2.0 conflicts with yajl-ruby (~> 1.1.0)

Which requires this fix:

    sudo gem uninstall yajl-ruby --version 1.2.0
