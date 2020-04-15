#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# Copyright 2020 Symphony Communication Services, LLC.
# 
# -----------------------------------------------------------------------------

scriptDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo ListItems

echo argc $#
echo argv $*

cd ${scriptDir}/../../..

pwd

defaultArgs="--o https://uat.api.symphony.com -p https://psdev.symphony.com/ -s allegroBot -f ${system_property:user.home}/keys/mykey.pem -k 2020-"
args=${*:--o https://uat.api.symphony.com -p https://psdev.symphony.com/ -s allegroBot -f ${HOME}/keys/mykey.pem -k 2020-}
#args=$defaultArgs
echo args $args

mvn exec:java -Dexec.mainClass="com.symphony.s2.allegro.examples.calendar.ListItems" -Dexec.args="$args"

#java -cp target/calendar-0.1.3-SNAPSHOT-jar-with-dependencies.jar com.symphony.s2.allegro.examples.calendar.ListItems $args




