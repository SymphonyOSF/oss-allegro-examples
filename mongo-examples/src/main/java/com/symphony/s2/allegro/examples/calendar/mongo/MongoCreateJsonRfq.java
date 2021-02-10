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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.symphony.oss.allegro.examples.model.fx.canon.Ccy;
import com.symphony.oss.allegro.examples.model.fx.canon.IRfq;
import com.symphony.oss.allegro.examples.model.fx.canon.Rfq;
import com.symphony.oss.allegro.examples.model.fx.canon.facade.CcyPair;
import com.symphony.oss.allegro.examples.model.fx.canon.facade.ICcyPair;

/**
 * An example application which creates a ToDoItem, adding it to a current and absolute sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class MongoCreateJsonRfq extends MongoFxExample
{
  MongoCreateJsonRfq(String[] args)
  {
    super(args);
  }

  @Override
  public void run()
  {
    ICcyPair ccyPair = new CcyPair.Builder()
        .withBase(Ccy.GBP)
        .withCounter(Ccy.USD)
        .build();
    
    IRfq rfqItem = new Rfq.Builder()
      .withCcyPair(ccyPair)
      .withId(Instant.now().toString())
      .withQuantity(1000000L)
      .withStreamFor(10)
      .build();
    
    System.out.println("About to create item " + rfqItem);

    System.out.println("About to create item " + rfqItem.getHeader());
    
    String now = Instant.now().toString();
    String item = "{\n" + 
//        "  \"_type\":\"com.symphony.oss.allegro.examples.model.fx.Rfq\",\n" + 
//        "  \"_version\":\"1.0\",\n" + 
        "  \"ccyPair\":{\n" + 
//        "    \"_type\":\"com.symphony.oss.allegro.examples.model.fx.CcyPair\",\n" + 
//        "    \"_version\":\"1.0\",\n" + 
        "    \"base\":\"GBP\",\n" + 
        "    \"counter\":\"USD\"\n" + 
        "  },\n" + 
        "  \"expires\":\"" + Instant.now().plus(30, ChronoUnit.SECONDS).toString() + "\",\n" +
        "  \"id\":\"" + now + "\",\n" + 
        "  \"quantity\":1000000,\n" + 
        "  \"streamFor\":10\n" + 
        "}"; 

    String header = "{\n" + 
//        "  \"_type\":\"com.symphony.oss.allegro.examples.model.fx.FxHeader\",\n" + 
//        "  \"_version\":\"1.0\",\n" + 
        "  \"ccyPair\":{\n" + 
//        "    \"_type\":\"com.symphony.oss.allegro.examples.model.fx.CcyPair\",\n" + 
//        "    \"_version\":\"1.0\",\n" + 
        "    \"base\":\"GBP\",\n" + 
        "    \"counter\":\"USD\"\n" + 
        "  },\n" + 
        "  \"id\":\"" + now + "\",\n" + 
        "  \"payloadType\":\"com.symphony.oss.allegro.examples.model.fx.Rfq\"\n" + 
        "}";
    
    System.out.println("About to create item " + item);
    System.out.println("with header " + header);
    
    // Create an encrypted StoredApplicationRecord.
    Document toDoDoc = allegro2MongoApi_.newEncryptedDocumentBuilder()
        .withThreadId(threadId_)
        .withHeader(header)
        .withPayload(item)
      .build();
    
    System.out.println("About to store " + toDoDoc);
    
    fxItems_.insertOne(toDoDoc);
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    new MongoCreateJsonRfq(args).run();
  }
}
