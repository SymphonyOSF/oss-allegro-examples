/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.getmessage;

import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.models.chat.canon.facade.ThreadId;
import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.FetchRecentMessagesRequest;
import com.symphony.oss.allegro.api.IAllegroApi;

/**
 * Fetch the 5 most recent messages from the given conversation.
 * 
 * @author Bruce Skingle
 *
 */
public class FetchFiveRecentMessages extends CommandLineHandler implements Runnable
{
  private static final String ALLEGRO             = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT     = "SERVICE_ACCOUNT";
  private static final String POD_URL             = "POD_URL";
  private static final String OBJECT_STORE_URL    = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE     = "CREDENTIAL_FILE";
  private static final String THREAD_ID           = "THREAD_ID";
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private ThreadId            threadId_;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public FetchFiveRecentMessages()
  {
    withFlag('s',   SERVICE_ACCOUNT,      ALLEGRO + SERVICE_ACCOUNT,      String.class,   false, true,   (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,              ALLEGRO + POD_URL,              String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL,     ALLEGRO + OBJECT_STORE_URL,     String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,      ALLEGRO + CREDENTIAL_FILE,      String.class,   false, true,   (v) -> credentialFile_       = v);
    withFlag('t',   THREAD_ID,            ALLEGRO + THREAD_ID,            String.class,   false, true,   (v) -> threadId_             = ThreadId.newBuilder().build(v));
  }
  
  @Override
  public void run()
  {
    allegroApi_ = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withObjectStoreUrl(objectStoreUrl_)
      .withUserName(serviceAccount_)
      .withRsaPemCredentialFile(credentialFile_)
//      .withTrustedSslCertResources(IAllegroApi.SYMPHONY_DEV_QA_ROOT_CERT)
      .build();
    
    System.out.println("Fetch messages from object store...");
    allegroApi_.fetchRecentMessages(
        new FetchRecentMessagesRequest()
          .withThreadId(threadId_)
          .withMaxMessages(5),
        (message) ->
        {
          System.out.println(message);
//          System.out.println("ThreadId       = " + message.getThreadId());
//          System.out.println("PresentationML = " + message.getPresentationML());
//          System.out.println("EntityJson     = " + message.getEntityJson());
        }
        );
    
    System.out.println("Fetch messages from object pod...");
    allegroApi_.fetchRecentMessagesFromPod(
        new FetchRecentMessagesRequest()
          .withThreadId(threadId_)
          .withMaxMessages(5),
        (message) ->
        {
          System.out.println(message);
        }
        );
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    FetchFiveRecentMessages program = new FetchFiveRecentMessages();
    
    program.process(args);
    
    program.run();
  }
}
