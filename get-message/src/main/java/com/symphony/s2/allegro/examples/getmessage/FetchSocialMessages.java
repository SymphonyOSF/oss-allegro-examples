/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.getmessage;

import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;
import org.symphonyoss.s2.fugue.core.trace.ITraceContext;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.FetchMessagesRequest;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.request.ConsumerManager;
import com.symphony.oss.allegro.api.request.FetchRecentMessagesRequest;
import com.symphony.oss.allegro.api.request.ReceivedChatMessageAdaptor;
import com.symphony.oss.models.allegro.canon.IReceivedMaestroMessage;
import com.symphony.oss.models.allegro.canon.IReceivedSocialMessage;
import com.symphony.oss.models.core.canon.facade.ThreadId;
import com.symphony.s2.allegro.examples.getmessage.FetchConversation.Adaptor;

/**
 * Fetch only social messages from the given conversation.
 * 
 * @author Bruce Skingle
 *
 */
public class FetchSocialMessages extends CommandLineHandler implements Runnable
{
  private static final String ALLEGRO             = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT     = "SERVICE_ACCOUNT";
  private static final String POD_URL             = "POD_URL";
  private static final String OBJECT_STORE_URL    = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE     = "CREDENTIAL_FILE";
  private static final String THREAD_ID           = "THREAD_ID";
  private static final String MAX_MESSAGES        = "MAX_MESSAGES";
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private ThreadId            threadId_;
  private int                 maxMessages_ = 5;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public FetchSocialMessages()
  {
    withFlag('s',   SERVICE_ACCOUNT,      ALLEGRO + SERVICE_ACCOUNT,      String.class,   false, true,   (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,              ALLEGRO + POD_URL,              String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL,     ALLEGRO + OBJECT_STORE_URL,     String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,      ALLEGRO + CREDENTIAL_FILE,      String.class,   false, true,   (v) -> credentialFile_       = v);
    withFlag('t',   THREAD_ID,            ALLEGRO + THREAD_ID,            String.class,   false, true,   (v) -> threadId_             = ThreadId.newBuilder().build(v));
    withFlag('m',   MAX_MESSAGES   ,      ALLEGRO + MAX_MESSAGES,         Integer.class,  false, false,  (v) -> maxMessages_          = v);
  }
  
  @Override
  public void run()
  {
    allegroApi_ = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withObjectStoreUrl(objectStoreUrl_)
      .withUserName(serviceAccount_)
      .withRsaPemCredentialFile(credentialFile_)
      .build();
    
    allegroApi_.fetchRecentMessagesFromPod(
        new FetchRecentMessagesRequest.Builder()
          .withThreadId(threadId_)
          .withMaxItems(maxMessages_)
          .withConsumerManager(new ConsumerManager.Builder()
            .withConsumer(new Adaptor())
            .build()
            )
          .build()
        );
  }
  
  class Adaptor extends ReceivedChatMessageAdaptor
  {
    @Override
    public void onSocialMessage(IReceivedSocialMessage message, ITraceContext trace)
    {
      System.out.println(message.getClass().getSimpleName() + ": " + message.getMessageId() + " " + message.getText());
    }
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    FetchSocialMessages program = new FetchSocialMessages();
    
    program.process(args);
    
    program.run();
  }
}
