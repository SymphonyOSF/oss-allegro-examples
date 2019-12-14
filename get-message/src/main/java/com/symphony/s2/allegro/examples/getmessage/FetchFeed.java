/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.getmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.s2.canon.runtime.IEntity;
import org.symphonyoss.s2.common.fault.TransientTransactionFault;
import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;
import org.symphonyoss.s2.fugue.core.trace.ITraceContext;
import org.symphonyoss.s2.fugue.pipeline.IConsumer;
import org.symphonyoss.s2.fugue.pipeline.ISimpleConsumer;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.models.allegro.canon.facade.IChatMessage;
import com.symphony.oss.models.allegro.canon.facade.IReceivedChatMessage;
import com.symphony.oss.models.chat.canon.facade.ISocialMessage;

/**
 * Create a feed for the caller's default feed. 
 * 
 * @author Bruce Skingle
 *
 */


/*
 * Temporarily withdrawn 
 */


public class FetchFeed //extends CommandLineHandler implements Runnable
{
//  private static final Logger log_ = LoggerFactory.getLogger(FetchFeed.class);
//  
//  private static final String   ALLEGRO          = "ALLEGRO_";
//  private static final String   SERVICE_ACCOUNT  = "SERVICE_ACCOUNT";
//  private static final String   POD_URL          = "POD_URL";
//  private static final String   OBJECT_STORE_URL = "OBJECT_STORE_URL";
//  private static final String   CREDENTIAL_FILE  = "CREDENTIAL_FILE";
//  private static final String   TRUST            = "TRUST";
//  private static final TimeUnit RETRY_TIME_UNIT  = TimeUnit.SECONDS;
//  private static final Long     RETRY_TIME       = 60L;
//  
//  private String              serviceAccount_;
//  private String              podUrl_;
//  private String              objectStoreUrl_;
//  private String              credentialFile_;
//  private String              trust_;
//  
//  private IAllegroApi         allegroApi_;
//
//  /**
//   * Constructor.
//   */
//  public FetchFeed()
//  {
//    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,   (v) -> serviceAccount_       = v);
//    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
//    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
//    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, true,   (v) -> credentialFile_       = v);
//    withFlag('T',   TRUST,            ALLEGRO + TRUST,            String.class,   false, false,  (v) -> trust_                = v);
//
//    
//    debug();
//  }
//
//  private void debug()
//  {
//    List<Class<?>> typeList = new ArrayList<>();
//    
//    typeList.add(IEntity.class);
//    typeList.add(IChatMessage.class);
//    typeList.add(INotification.class);
//    
//    getType(IChatMessage.class, typeList);
//    getType(INotification.class, typeList);
//
//    getType(ISocialMessage.class, typeList);
//    getType(String.class, typeList);
//    
//    System.out.println("===");
//    
//  }
//  
//  private void getType(Class<?> type, List<Class<?>> typeList)
//  {
//    Class<?> best = Object.class;
//    
//    for(Class<?> t : typeList)
//    {
//      if(!t.isAssignableFrom(type))
//        continue;
//      
//      if(best == null || best.isAssignableFrom(t))
//        best = t;
//      
//    }
//    
//    System.out.println("Best for " + type + " is " + best);
//  }
//
//  @Override
//  public void run()
//  {
//    AllegroApi.Builder builder = new AllegroApi.Builder()
//      .withPodUrl(podUrl_)
//      .withObjectStoreUrl(objectStoreUrl_)
//      .withUserName(serviceAccount_)
//      .withRsaPemCredentialFile(credentialFile_)
//      ;
//    
//    if(trust_ != null)
//    {
//      switch(trust_.toLowerCase())
//      {
//        case "symphony":
//          builder.withTrustedSslCertResources(IAllegroApi.SYMPHONY_DEV_QA_ROOT_CERT);
//          break;
//        
//        case "all":
//          builder.withTrustAllSslCerts();
//          break;
//        
//        default:
//          throw new IllegalArgumentException("Invalid trust value, expected \"symphony\" or \"all\"");
//      }
//    }
//    
//    allegroApi_ = builder.build();
//    
////    allegroApi_.storeCredential();
////    
////    IFeed feed = allegroApi_.fetchOrCreateFeed(
////        new FetchOrCreateFeedRequest()
////          .withName("MyDataFeed")
////          .withType(FeedType.GATEWAY)
////          );
//    
////    IFeed feed = allegroApi_.upsertFeed(
////      new UpsertSmsGatewayRequest()
////        .withType(FeedType.GATEWAY)
////        .withPhoneNumber(phoneNumber_)
////        );
//    
//    
//    
//    allegroApi_.fetchFeedMessages(new FetchFeedMessagesRequest()
//        .withName("myFeed")
//        .withConsumer(INotification.class, new IConsumer<INotification>()
//        {
//          boolean ack = true;
//          
//          @Override
//          public void consume(INotification message, ITraceContext traceContext)
//          {
//            if(ack)
//            {
//              System.out.println("ACK " + message.getPayloadId());
//              ack = false;
//            }
//            else
//            {
//              System.out.println("NAK " + message.getPayloadId());
//              ack = true;
//              throw new RuntimeException("Rejected");
//            }
//          }
//
//          @Override
//          public void close()
//          {
//            System.out.println("That's all folks");
//          }
//        }
//        )
//        .withConsumer(IReceivedChatMessage.class, (message, traceContext) ->
//        {
//          System.out.println("ACK " + message.getMessageId() + " " + message.getCanonType() + " " + message.getText());
//        })
////        .withConsumer(IReceivedChatMessage.class, new IConsumer<IReceivedChatMessage>()
////        {
////          boolean ack = false;
////          
////          @Override
////          public void consume(IReceivedChatMessage message, ITraceContext traceContext)
////          {
////            if(ack)
////            {
////              log_.info("ACK " + message.getPresentationML());
////              
////              
////              ack = false;
////            }
////            else
////            {
////              log_.info("NAK " + message.getPresentationML());
////              log_.info("But wait.....");
////              
////              try
////              {
////                Thread.sleep(20000);
////              }
////              catch(InterruptedException e)
////              {
////                
////              }
////              
////              log_.info("But wait.....OK carry on");
////              
//////              ack = true;
////              throw new TransientTransactionFault("Rejected", RETRY_TIME_UNIT, RETRY_TIME);
////            }
////          }
////
////          @Override
////          public void close() throws Exception
////          {
////            log_.info("That is all");
////          }
////        }
////      )
//    );
//  }
//  
//  /**
//   * Main.
//   * 
//   * @param args Command line arguments.
//   */
//  public static void main(String[] args)
//  {
//    FetchFeed program = new FetchFeed();
//    
//    program.process(args);
//    
//    program.run();
//  }
}
