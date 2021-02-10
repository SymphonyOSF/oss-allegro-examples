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

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.symphony.oss.allegro.examples.model.fx.canon.IFxHeader;
import com.symphony.oss.allegro.examples.model.fx.canon.IQuote;
import com.symphony.oss.allegro.examples.model.fx.canon.IRfq;
import com.symphony.oss.allegro.examples.model.fx.canon.Quote;
import com.symphony.oss.allegro.examples.model.fx.canon.Rfq;
import com.symphony.oss.allegro2.mongo.api.AllegroMongoConsumerManager;

/**
 * An Allegro2Mongo example application which creates a ToDoItem, adding it to a collection.
 * 
 * @author Bruce Skingle
 *
 */
public class MongoStreamPrices extends MongoFxExample
{
  private static final Random random_ = new Random();
  
  MongoStreamPrices(String[] args)
  {
    super(args);
  }

  @Override
  protected void run()
  {
    ExecutorService executor = Executors.newCachedThreadPool();
    
    
    AllegroMongoConsumerManager consumerManager = allegro2MongoApi_.newConsumerManagerBuilder()
        .withConsumer(IFxHeader.class, IRfq.class, (record, header, payload) ->
        {
          if(Instant.now().isAfter(payload.getExpires()))
          {
            printRfq(payload, "expired");
          }
          else
          {
            printRfq(payload, "STREAM");
            executor.submit(new PriceStreamer(payload));
          }
        })
        .build();
      
      consumerManager.accept(fxItems_.find(eq("header.payloadType", Rfq.TYPE_ID)));
      
      try
      {
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
  }
  
  class PriceStreamer implements Runnable
  {
    
    private double price_ = 1.0;
    private Integer streamFor_;
    private IRfq rfq_;
    
    public PriceStreamer(IRfq rfq)
    {
      switch(rfq.getCcyPair().getCode())
      {
        case "USDGBP":
          price_ = 0.72;
          break;
          
        case "GBPUSD":
          price_ = 1.42;
          break;
          
        case "USDJPY":
          price_ = 104.73;
          break;
      }
      streamFor_ = rfq.getStreamFor();
      rfq_ = rfq;
    }

    @Override
    public void run()
    {
      while(streamFor_-- > 0)
      {
        price_ += (random_.nextInt(200) - 100.0) / 1000.0;
        
        IQuote quote = new Quote.Builder()
            .withCcyPair(rfq_.getCcyPair())
            .withId(rfq_.getId())
            .withExpires(Instant.now().plus(800, ChronoUnit.MILLIS))
            .withPrice(price_)
            .build();
        
        printQuote(quote);        
        
        Document doc = allegro2MongoApi_.newEncryptedDocumentBuilder()
          .withThreadId(threadId_)
          .withHeader(quote.getHeader())
          .withPayload(quote)
        .build();
        
        fxItems_.insertOne(doc);
        
        try
        {
          Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
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
    new MongoStreamPrices(args).run();
  }
}
