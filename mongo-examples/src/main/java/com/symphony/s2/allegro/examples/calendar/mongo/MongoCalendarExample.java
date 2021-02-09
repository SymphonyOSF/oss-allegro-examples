/*
 * Copyright 2021 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.calendar.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.symphony.oss.allegro.examples.calendar.canon.CalendarModel;
import com.symphony.oss.allegro2.mongo.api.IAllegro2MongoApi;
import com.symphony.oss.models.core.canon.facade.PodAndUserId;
import com.symphony.oss.models.core.canon.facade.ThreadId;

abstract class MongoCalendarExample extends MongoExample
{
  @Override
  public void run(String mongoUser, String mongoPassword, String mongoHost, PodAndUserId ownerUserId, IAllegro2MongoApi allegroPodApi, ThreadId threadId)
  {
    allegroPodApi.getModelRegistry()
      .withFactories(CalendarModel.FACTORIES);
    
    String connectionString = "mongodb+srv://" + mongoUser + ":" + mongoPassword + "@" + mongoHost + "/Calendar?retryWrites=true&w=majority";
    
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      run(new CalendarContext(mongoClient, ownerUserId, allegroPodApi, threadId));
    }
  }

  protected abstract void run(CalendarContext context);
}
