/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.getmessage;

import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.UpsertSmsGatewayRequest;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.models.system.canon.FeedType;
import com.symphony.oss.models.system.canon.IFeed;

/**
 * Create a feed for the caller's default feed. 
 * 
 * @author Bruce Skingle
 *
 */
public class CreateFeed extends CommandLineHandler implements Runnable
{
  private static final String ALLEGRO           = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT   = "SERVICE_ACCOUNT";
  private static final String POD_URL           = "POD_URL";
  private static final String OBJECT_STORE_URL  = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE   = "CREDENTIAL_FILE";
  private static final String TELEPHONE         = "TELEPHONE";
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private String              phoneNumber_;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public CreateFeed()
  {
    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,   (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, true,   (v) -> credentialFile_       = v);
    withFlag('t',   TELEPHONE,        ALLEGRO + TELEPHONE,        String.class,   false, false,  (v) -> phoneNumber_          = v);
  }
  
  @Override
  public void run()
  {
    allegroApi_ = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withObjectStoreUrl(objectStoreUrl_)
      .withUserName(serviceAccount_)
      .withRsaPemCredentialFile(credentialFile_)
      .withTrustedSslCertResources(IAllegroApi.SYMPHONY_DEV_QA_ROOT_CERT)
      .build();
    
//    allegroApi_.storeCredential();
//    
//    IFeed feed = allegroApi_.fetchOrCreateFeed(
//        new FetchOrCreateFeedRequest()
//          .withName("MyDataFeed")
//          .withType(FeedType.GATEWAY)
//          );
    
    IFeed feed = allegroApi_.upsertFeed(
      new UpsertSmsGatewayRequest()
        .withType(FeedType.GATEWAY)
        .withPhoneNumber(phoneNumber_)
        );
    
    System.out.println("Feed is " + feed);
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    CreateFeed program = new CreateFeed();
    
    program.process(args);
    
    program.run();
  }
}
