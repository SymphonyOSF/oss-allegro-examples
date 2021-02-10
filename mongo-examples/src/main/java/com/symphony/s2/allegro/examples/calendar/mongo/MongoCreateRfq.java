/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.symphony.s2.allegro.examples.calendar.mongo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.bson.Document;

import com.symphony.oss.allegro.examples.model.fx.canon.Ccy;
import com.symphony.oss.allegro.examples.model.fx.canon.IRfq;
import com.symphony.oss.allegro.examples.model.fx.canon.Rfq;
import com.symphony.oss.allegro.examples.model.fx.canon.facade.CcyPair;
import com.symphony.oss.allegro.examples.model.fx.canon.facade.ICcyPair;

/**
 * An Allegro2Mongo example application which creates a ToDoItem, adding it to a collection.
 * 
 * @author Bruce Skingle
 *
 */
public class MongoCreateRfq extends MongoFxExample
{
  MongoCreateRfq(String[] args)
  {
    super(args);
  }

  @Override
  protected void run()
  {
    // Create the payload
    
    ICcyPair ccyPair = new CcyPair.Builder()
        .withBase(Ccy.GBP)
        .withCounter(Ccy.USD)
        .build();
    
    IRfq rfqItem = new Rfq.Builder()
      .withCcyPair(ccyPair)
      .withId(Instant.now().toString())
      .withQuantity(1000000L)
      .withStreamFor(10)
      .withExpires(Instant.now().plus(60, ChronoUnit.SECONDS))
      .build();
    
    System.out.println("About to create item " + rfqItem);
    
    // Create an encrypted StoredApplicationRecord.
    Document toDoDocument = allegro2MongoApi_.newEncryptedDocumentBuilder()
        .withThreadId(threadId_)
        .withHeader(rfqItem.getHeader())
        .withPayload(rfqItem)
      .build();
    
    System.out.println("About to store " + toDoDocument);
    
    fxItems_.insertOne(toDoDocument);
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    new MongoCreateRfq(args).run();
  }
}
