/*
 * Copyright 2021 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.calendar.mongo;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.symphony.oss.allegro2.mongo.api.IAllegro2MongoApi;
import com.symphony.oss.models.core.canon.facade.PodAndUserId;
import com.symphony.oss.models.core.canon.facade.ThreadId;

class CalendarContext
{
  final MongoClient               mongoClient_;
  final PodAndUserId              ownerUserId_;
  final IAllegro2MongoApi         allegro2MongoApi_;
  final ThreadId                  threadId_;
  final MongoDatabase             db_;
  final MongoCollection<Document> todoItems_;

  CalendarContext(MongoClient mongoClient, PodAndUserId ownerUserId, IAllegro2MongoApi allegroPodApi, ThreadId threadId)
  {
    mongoClient_ = mongoClient;
    ownerUserId_ = ownerUserId;
    allegro2MongoApi_ = allegroPodApi;
    threadId_ = threadId;
    db_ = mongoClient.getDatabase("Calendar");
    todoItems_ = db_.getCollection("TodoItems");
  }

}
