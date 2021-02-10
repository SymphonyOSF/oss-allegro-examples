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

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.oss.commons.dom.json.IJsonObject;
import com.symphony.oss.commons.dom.json.ImmutableJsonObject;
import com.symphony.oss.models.core.canon.facade.IApplicationRecord;

/**
 * An example application which creates a ToDoItem, adding it to a current and absolute sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class MongoListJsonFxItems extends MongoFxExample
{
  private static final Logger log_ = LoggerFactory.getLogger(MongoListJsonFxItems.class);

  MongoListJsonFxItems(String[] args)
  {
    super(args);
  }

  @Override
  public void run()
  {
    
    for(Document doc : fxItems_.find())
    {
      IApplicationRecord applicationRecord = allegro2MongoApi_.decrypt(doc);
      
      String payloadType = applicationRecord.getHeader().getJsonObject().getString("payloadType", null);
      
      if(payloadType == null)
      {
        log_.error("Unknown payload type " + applicationRecord.getHeader());
      }
      else
      {
        ImmutableJsonObject payload = applicationRecord.getPayload().getJsonObject();
        IJsonObject<?> ccyPairJson = payload.getObject("ccyPair");
        String ccyPair = ccyPairJson.getRequiredString("base") + ccyPairJson.getRequiredString("counter");
        
        switch(payloadType)
        {
          case "com.symphony.oss.allegro.examples.model.fx.Quote":
            printHeader();
            System.out.format("%-5.5s %s %8.5f %4s %-25.25s %s%n",
                "QUOTE", ccyPair, payload.getRequiredDouble("price"), "", payload.getRequiredString("id"), payload.getRequiredString("expires"));
            break;
            

          case "com.symphony.oss.allegro.examples.model.fx.Rfq":
            printHeader();
            System.out.format("%-5.5s %s %8d %4d %-25.25s %s%n",
                "RFQ", ccyPair, payload.getRequiredLong("quantity"), payload.getRequiredInteger("streamFor"), payload.getRequiredString("id"),
                payload.getRequiredString("expires"));
            break;
          
          default:
            log_.error("Unknown payload type " + applicationRecord.getHeader());
        }
      }
    }
  }

  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    new MongoListJsonFxItems(args).run();
  }
}
