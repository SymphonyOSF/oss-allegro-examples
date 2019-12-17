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

package com.symphony.s2.allegro.examples.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.s2.fugue.IFugueLifecycleComponent;
import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.request.ConsumerManager;
import com.symphony.oss.allegro.api.request.FetchPartitionRequest;
import com.symphony.oss.allegro.api.request.SubscribeFeedObjectsRequest;
import com.symphony.oss.allegro.api.request.UpsertFeedRequest;
import com.symphony.oss.allegro.examples.calendar.canon.CalendarModel;
import com.symphony.oss.allegro.examples.calendar.canon.IToDoItem;
import com.symphony.oss.allegro.examples.calendar.canon.ToDoItem;
import com.symphony.oss.models.core.canon.facade.PodAndUserId;
import com.symphony.oss.models.object.canon.IFeed;

/**
 * Retrieve all objects on the given Sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class SubscribeFeedItems extends CommandLineHandler implements Runnable
{
  private static final Logger log_ = LoggerFactory.getLogger(SubscribeFeedItems.class);
  
  private static final String ALLEGRO          = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT  = "SERVICE_ACCOUNT";
  private static final String POD_URL          = "POD_URL";
  private static final String OBJECT_STORE_URL = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE  = "CREDENTIAL_FILE";
  private static final String OWNER_USER_ID    = "OWNER_USER_ID";
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private Long                ownerId_;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public SubscribeFeedItems()
  {
    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,   (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, true,   (v) -> credentialFile_       = v);
    withFlag('u',   OWNER_USER_ID,    ALLEGRO + OWNER_USER_ID,    Long.class,     false, false,  (v) -> ownerId_              = v);
  }
  
  @Override
  public void run()
  {
    allegroApi_ = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withObjectStoreUrl(objectStoreUrl_)
      .withUserName(serviceAccount_)
      .withRsaPemCredentialFile(credentialFile_)
      .withFactories(CalendarModel.FACTORIES)
      .withTrustAllSslCerts()
      .build();
    
    PodAndUserId ownerUserId = ownerId_ == null ? allegroApi_.getUserId() : PodAndUserId.newBuilder().build(ownerId_);
    
    System.out.println("CallerId is " + allegroApi_.getUserId());
    System.out.println("OwnerId is " + ownerUserId);
      
    IFeed feed = allegroApi_.upsertFeed(
        new UpsertFeedRequest.Builder()
          .withName("myCalendarFeed")
          .withPartitionHashes(allegroApi_.getPartitionHash(
              new FetchPartitionRequest.Builder()
                .withName(ToDoItem.TYPE_ID)
                .withOwner(ownerUserId)
                .build()
              ))
          .build()
          );
    
    log_.info("Feed is " + feed);
    
    IFugueLifecycleComponent subscriber = allegroApi_.subscribeToFeed(new SubscribeFeedObjectsRequest.Builder()
        .withName("myCalendarFeed")
        .withSubscriberThreadPoolSize(10)
        .withHandlerThreadPoolSize(90)
        .withConsumerManager(new ConsumerManager.Builder()
          .withConsumer(IToDoItem.class, (message, traceContext) ->
          {
            log_.info(message.toString());
          })
          .withUnprocessableMessageConsumer((item, trace, message, cause) ->
          {
            log_.error("Failed to consume message: " + message + "\nPayload:" + item, cause);
          })
          .build()
        )
      .build()
    );

    log_.info("Subscriber state: " + subscriber.getLifecycleState());
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
    SubscribeFeedItems program = new SubscribeFeedItems();
    
    program.process(args);
    
    program.run();
  }
}
