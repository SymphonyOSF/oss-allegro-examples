/*
 * Copyright 2021 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.calendar.mongo.allegro;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.symphony.oss.allegro.examples.calendar.canon.CalendarModel;
import com.symphony.oss.allegro2.api.IAllegro2Api;
import com.symphony.oss.models.core.canon.facade.PodAndUserId;
import com.symphony.oss.models.core.canon.facade.ThreadId;

abstract class AllegroMongoCalendarExample extends AllegroMongoExample
{
  @Override
  public void run(String mongoUser, String mongoPassword, String mongoHost, PodAndUserId ownerUserId, IAllegro2Api allegroPodApi, ThreadId threadId)
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
