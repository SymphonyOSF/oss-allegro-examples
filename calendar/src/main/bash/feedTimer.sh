#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# Copyright 2020 Symphony Communication Services, LLC.
# 
# -----------------------------------------------------------------------------

scriptDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo ListItems

cd ${scriptDir}/../../..

pwd

source ${scriptDir}/setEnv.sh

java -cp target/calendar-*-SNAPSHOT-jar-with-dependencies.jar com.symphony.s2.object.timer.feed.FeedTimer -o $API_URL -p $POD_URL -t $THREAD_ID -c 20 -n 100 --SESSION_TOKEN $SESSION_TOKEN --KEYMANAGER_TOKEN $KEYMANAGER_TOKEN

