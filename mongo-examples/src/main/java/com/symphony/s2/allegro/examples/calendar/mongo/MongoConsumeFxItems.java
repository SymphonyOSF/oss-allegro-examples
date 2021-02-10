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

import com.symphony.oss.allegro.examples.model.fx.canon.IFxHeader;
import com.symphony.oss.allegro.examples.model.fx.canon.IQuote;
import com.symphony.oss.allegro.examples.model.fx.canon.IRfq;
import com.symphony.oss.allegro2.mongo.api.AllegroMongoConsumerManager;
import com.symphony.oss.models.core.canon.IApplicationPayload;

/**
 * An example application which creates a ToDoItem, adding it to a current and absolute sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class MongoConsumeFxItems extends MongoFxExample
{
  MongoConsumeFxItems(String[] args)
  {
    super(args);
  }

  @Override
  public void run()
  {
    
    AllegroMongoConsumerManager consumerManager = allegro2MongoApi_.newConsumerManagerBuilder()
      .withConsumer(IFxHeader.class, IRfq.class, (record, header, rfq) ->
      {
        printRfq(rfq);
      })
      .withConsumer(IFxHeader.class, IQuote.class, (record, header, quote) ->
      {
        printQuote(quote);
      })
      .withConsumer(IApplicationPayload.class, IApplicationPayload.class, (record, header, payload) ->
      {
        System.out.println("Untyped Header:  " + header);
        System.out.println("Untyped Payload: " + payload);
        System.out.println("EncryptedApplicationRecord:  " + record);
      })
      .build();
    
    consumerManager.accept(fxItems_.find());
  }

  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    new MongoConsumeFxItems(args).run();
  }
}
