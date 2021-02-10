/*
 * Copyright 2021 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.calendar.mongo;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.symphony.oss.allegro.examples.model.fx.canon.FxModel;
import com.symphony.oss.allegro.examples.model.fx.canon.IQuote;
import com.symphony.oss.allegro.examples.model.fx.canon.IRfq;

abstract class MongoFxExample extends MongoExample
{
  protected final MongoClient               mongoClient_;
  protected final MongoDatabase             db_;
  protected final MongoCollection<Document> fxItems_;
  private boolean header_ = true;
  
  public MongoFxExample(String[] args)
  {
    super(args);
    
    allegro2MongoApi_.getModelRegistry().withFactories(FxModel.FACTORIES);
  
    String connectionString = "mongodb+srv://" + mongoUser_ + ":" + mongoPassword_ + "@" + mongoHost_ + "/FX?retryWrites=true&w=majority";
  
    mongoClient_ = MongoClients.create(connectionString);
    db_ = mongoClient_.getDatabase("FX");
    fxItems_ = db_.getCollection("FxItems");
  }

  protected void printHeader()
  {
    if(header_)
    {
      System.out.format("%-5.5s %6.6s %8.8s %4.4s %-25.25s %s%n",
          "TYPE", "CCY", "AMT/PRICE", "CNT", "ID", "EXPIRES");
      header_ = false;
    }
  }

  protected void printRfq(IRfq rfq)
  {
    printRfq(rfq, "");
  }

  protected void printRfq(IRfq rfq, String message)
  {
    printHeader();
    System.out.format("%-5.5s %s %8d %4d %-25.25s %s %s%n",
        "RFQ",
        rfq.getCcyPair().getCode(), rfq.getQuantity(), rfq.getStreamFor(), rfq.getId(), rfq.getExpires(), message);
  }

  protected void printQuote(IQuote quote)
  {
    printHeader();
    System.out.format("%-5.5s %s %8.5f %4s %-25.25s %s%n",
        "QUOTE", quote.getCcyPair().getCode(), quote.getPrice(), "", quote.getId(), quote.getExpires());
  }
}
