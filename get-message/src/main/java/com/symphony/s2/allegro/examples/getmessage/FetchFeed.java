/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.getmessage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.FetchFeedMessagesRequest;
import com.symphony.oss.allegro.api.UpsertSmsGatewayRequest;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.UpsertFeedRequest;
import com.symphony.oss.models.fundamental.canon.facade.INotification;
import com.symphony.oss.models.system.canon.FeedType;
import com.symphony.oss.models.system.canon.IFeed;
import com.symphony.oss.models.system.canon.facade.IFeedMessage;

/**
 * Create a feed for the caller's default feed. 
 * 
 * @author Bruce Skingle
 *
 */
public class FetchFeed extends CommandLineHandler implements Runnable
{
  private static final String ALLEGRO           = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT   = "SERVICE_ACCOUNT";
  private static final String POD_URL           = "POD_URL";
  private static final String OBJECT_STORE_URL  = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE   = "CREDENTIAL_FILE";
  private static final String TRUST             = "TRUST";
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private String              trust_;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public FetchFeed()
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
    
//    allegroApi_.storeCredential();
//    
//    IFeed feed = allegroApi_.fetchOrCreateFeed(
//        new FetchOrCreateFeedRequest()
//          .withName("MyDataFeed")
//          .withType(FeedType.GATEWAY)
//          );
    
//    IFeed feed = allegroApi_.upsertFeed(
//      new UpsertSmsGatewayRequest()
//        .withType(FeedType.GATEWAY)
//        .withPhoneNumber(phoneNumber_)
//        );
    
    List<IFeedMessage> messages = new ArrayList<>();
    boolean done = false;
    
    while(!done)
    {

      
      System.out.println("Read " + messages.size() + " messages");
      
      FetchFeedMessagesRequest request = new FetchFeedMessagesRequest()
          .withName("myFeed")
          .withMaxMessages(2);
      
      boolean first=true;
      
      for(IFeedMessage message : messages)
      {
        if(first)
        {
          System.out.println("ACK " + message.getPayload().getPayloadId());
          
          request.withAck(message.getReceiptHandle());
          first = false;
          
          //payload = message.getPayload().getPayload().getP
        }
        else
        {
          System.out.println("NAK " + message.getPayload().getPayloadId());
          
          request.withNak(message.getReceiptHandle(), 60);
        }
      }
      
      messages = allegroApi_.fetchFeedMessages(request);
    }
      
      
      
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    FetchFeed program = new FetchFeed();
    
    program.process(args);
    
    program.run();
  }
}
