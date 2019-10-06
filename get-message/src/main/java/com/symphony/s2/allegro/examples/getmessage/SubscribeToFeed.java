/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.getmessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.s2.common.fault.TransientTransactionFault;
import org.symphonyoss.s2.fugue.IFugueLifecycleComponent;
import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;
import org.symphonyoss.s2.fugue.core.trace.ITraceContext;
import org.symphonyoss.s2.fugue.pipeline.ISimpleThreadSafeConsumer;
import org.symphonyoss.s2.fugue.pipeline.IThreadSafeConsumer;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.CreateFeedSubscriberRequest;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.models.allegro.canon.facade.IReceivedChatMessage;
import com.symphony.oss.models.chat.canon.facade.ISocialMessage;
import com.symphony.oss.models.fundamental.canon.facade.INotification;

/**
 * Create a feed for the caller's default feed. 
 * 
 * @author Bruce Skingle
 *
 */
public class SubscribeToFeed extends CommandLineHandler implements Runnable
{
  private static final Logger log_ = LoggerFactory.getLogger(SubscribeToFeed.class);
  
  private static final String   ALLEGRO          = "ALLEGRO_";
  private static final String   SERVICE_ACCOUNT  = "SERVICE_ACCOUNT";
  private static final String   POD_URL          = "POD_URL";
  private static final String   OBJECT_STORE_URL = "OBJECT_STORE_URL";
  private static final String   CREDENTIAL_FILE  = "CREDENTIAL_FILE";
  private static final String   TRUST            = "TRUST";
  private static final TimeUnit RETRY_TIME_UNIT  = TimeUnit.SECONDS;
  private static final Long     RETRY_TIME       = 60L;
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private String              trust_;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public SubscribeToFeed()
  {
    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,   (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, true,   (v) -> credentialFile_       = v);
    withFlag('T',   TRUST,            ALLEGRO + TRUST,            String.class,   false, false,  (v) -> trust_                = v);
  }

  @Override
  public void run()
  {
    AllegroApi.Builder builder = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withObjectStoreUrl(objectStoreUrl_)
      .withUserName(serviceAccount_)
      .withRsaPemCredentialFile(credentialFile_)
      ;
    
    if(trust_ != null)
    {
      switch(trust_.toLowerCase())
      {
        case "symphony":
          builder.withTrustedSslCertResources(IAllegroApi.SYMPHONY_DEV_QA_ROOT_CERT);
          break;
        
        case "all":
          builder.withTrustAllSslCerts();
          break;
        
        default:
          throw new IllegalArgumentException("Invalid trust value, expected \"symphony\" or \"all\"");
      }
    }
    
    allegroApi_ = builder.build();
    
    IFugueLifecycleComponent subscriber = allegroApi_.createFeedSubscriber(new CreateFeedSubscriberRequest()
        .withName("myFeed")
        .withConsumer(INotification.class, new IThreadSafeConsumer<INotification>()
        {
          boolean ack = true;
          
          @Override
          public void consume(INotification message, ITraceContext trace)
          {
            if(ack)
            {
              System.out.println("ACK " + message.getPayloadId());
              ack = false;
            }
            else
            {
              System.out.println("NAK " + message.getPayloadId());
              ack = true;
              throw new RuntimeException("Rejected");
            }
          }

          @Override
          public void close()
          {
            System.out.println("That's all folks!");
          }
        }
        )
        .withConsumer(ISocialMessage.class, (message, trace) ->
        {
          System.out.println("ACK " + message.getMessageId());
        })
        .withConsumer(IReceivedChatMessage.class, new ISimpleThreadSafeConsumer<IReceivedChatMessage>()
        {
          boolean ack = false;
          
          @Override
          public void consume(IReceivedChatMessage message, ITraceContext trace)
          {
            if(ack)
            {
              log_.info("ACK " + message.getPresentationML());
              
              
              ack = false;
            }
            else
            {
              log_.info("NAK " + message.getPresentationML());
//              log_.info("But wait.....");
//              
//              try
//              {
//                Thread.sleep(20000);
//              }
//              catch(InterruptedException e)
//              {
//                
//              }
//              
//              log_.info("But wait.....OK carry on");
              
//              ack = true;
//              throw new TransientTransactionFault("Rejected", RETRY_TIME_UNIT, RETRY_TIME);
              throw new TransientTransactionFault("Rejected", TimeUnit.SECONDS, 3L);
              
//              throw new FatalTransactionFault("Fatally rejected");
            }
          }
        }
        )
        .withUnprocessableMessageConsumer((item, trace, message, cause) ->
        {
          log_.error("Failed to consume message: " + message + "\nPayload:" + item, cause);
        })
      );
    
    subscriber.start();
    
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    log_.info("Subscriber state: " + subscriber.getLifecycleState());
    
    System.err.println();
    System.err.println("Press RETURN to quit");
    try
    {
      in.readLine();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    log_.info("Stopping...");
    subscriber.stop();
    log_.info("Subscriber state: " + subscriber.getLifecycleState());
    
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    SubscribeToFeed program = new SubscribeToFeed();
    
    program.process(args);
    
    program.run();
  }
}
