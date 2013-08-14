#!/usr/bin/env bash

# Location of the "webapps" directory
# OOZIE_WEBAPP_DIR=...

# Location of the "conf" directory
# OOZIE_CONF_DIR=...

# Set how often the checking service will be run within Oozie
CHECK_SERVICE_PERIOD=${CHECK_SERVICE_PERIOD:=60}

# Set how often a particular workflow step will have its status checked
ACTION_RECHECK_PERIOD=${ACTION_RECHECK_PERIOD:=600}

# Set to true to emit debug logging of the oozie-sge plugin
DEBUG_LOG=${DEBUG_LOG:=false}


if [ ! -z "$OOZIE_WEBAPP_DIR" ]; then

    if [ ! -z "$OOZIE_CONF_DIR" ]; then

        mkdir -p $OOZIE_WEBAPP_DIR/WEB-INF/lib

        cd $OOZIE_WEBAPP_DIR/WEB-INF/lib

        if type -p curl >/dev/null 2>&1; then
            HTTP_CLIENT="curl $CURL_PROXY -f -L -O"
        else
            HTTP_CLIENT="wget"
        fi

        $HTTP_CLIENT "http://wrench.res.oicr.on.ca/artifactory/seqware-dependencies/io/seqware/oozie-sge/1.0.0/oozie-sge-1.0.0.jar"

        $HTTP_CLIENT "http://repo1.maven.org/maven2/org/apache/commons/commons-exec/1.1/commons-exec-1.1.jar"

        cd $OOZIE_CONF_DIR

        perl -pi -e "s/org.apache.oozie.action.email.EmailActionExecutor/io.seqware.oozie.action.sge.SgeActionExecutor,org.apache.oozie.action.email.EmailActionExecutor/;" oozie-site.xml

        perl -pi -e "s/shell-action-/sge-action-1.0.xsd,shell-action-/;" oozie-site.xml

        if [ ! -z "$ACTION_RECHECK_PERIOD" ]; then
            perl -pi -e  "s/<configuration>/<configuration>\n<property><name>oozie.service.ActionCheckerService.action.check.delay<\/name><value>${ACTION_RECHECK_PERIOD}<\/value><\/property>/;" oozie-site.xml
        fi

        if [ ! -z "$CHECK_SERVICE_PERIOD" ]; then
            perl -pi -e  "s/<configuration>/<configuration>\n<property><name>oozie.service.ActionCheckerService.action.check.interval<\/name><value>${CHECK_SERVICE_PERIOD}<\/value><\/property>/;" oozie-site.xml
        fi

        if $DEBUG_LOG; then
            echo "log4j.logger.io.seqware.oozie.action.sge=DEBUG, oozie" >> $OOZIE_CONF_DIR/oozie-log4j.properties
        fi

    else
        echo "Oozie conf directory (OOZIE_CONF_DIR) not specified. Terminating."
        exit 1
    fi
else
    echo "Oozie webapp directory (OOZIE_WEBAPP_DIR) not specified. Terminating."
    exit 1
fi
