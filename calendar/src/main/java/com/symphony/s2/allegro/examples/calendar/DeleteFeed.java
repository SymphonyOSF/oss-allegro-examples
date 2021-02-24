/*
 * Copyright 2020 Symphony Communication Services, LLC.
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

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.ConsumerManager;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.Permission;
import com.symphony.oss.allegro.api.ResourcePermissions;
import com.symphony.oss.allegro.api.request.FeedId;
import com.symphony.oss.allegro.api.request.FeedQuery;
import com.symphony.oss.allegro.api.request.FetchFeedObjectsRequest;
import com.symphony.oss.allegro.api.request.PartitionId;
import com.symphony.oss.allegro.api.request.UpsertFeedRequest;
import com.symphony.oss.allegro.api.request.UpsertPartitionRequest;
import com.symphony.oss.allegro.examples.calendar.canon.CalendarModel;
import com.symphony.oss.allegro.examples.calendar.canon.IToDoHeader;
import com.symphony.oss.allegro.examples.calendar.canon.IToDoItem;
import com.symphony.oss.allegro.examples.calendar.canon.ToDoHeader;
import com.symphony.oss.allegro.examples.calendar.canon.ToDoItem;
import com.symphony.oss.canon.runtime.exception.NotFoundException;
import com.symphony.oss.fugue.cmd.CommandLineHandler;
import com.symphony.oss.models.allegro.canon.SslTrustStrategy;
import com.symphony.oss.models.allegro.canon.facade.AllegroConfiguration;
import com.symphony.oss.models.allegro.canon.facade.ConnectionSettings;
import com.symphony.oss.models.core.canon.facade.PodAndUserId;
import com.symphony.oss.models.core.canon.facade.ThreadId;
import com.symphony.oss.models.object.canon.IFeed;
import com.symphony.oss.models.object.canon.facade.IPartition;
import com.symphony.oss.models.object.canon.facade.IStoredApplicationObject;

/**
 * Retrieve all objects on the given Sequence.
 * 
 * @author Geremia Longobardo
 *
 */
public class DeleteFeed extends CommandLineHandler implements Runnable
{
  private static final Logger log_             = LoggerFactory.getLogger(DeleteFeed.class);

  private static final String ALLEGRO          = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT  = "SERVICE_ACCOUNT";
  private static final String POD_URL          = "POD_URL";
  private static final String OBJECT_STORE_URL = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE  = "CREDENTIAL_FILE";
  private static final String OWNER_USER_ID    = "OWNER_USER_ID";
  private static final String OTHER_USER_ID    = "OTHER_USER_ID";
  private static final String THREAD_ID        = "THREAD_ID";

  private ThreadId            threadId_;
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private PodAndUserId        ownerId_;
  private PodAndUserId        otherUserId_;

  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public DeleteFeed()
  {
    withFlag('s', SERVICE_ACCOUNT, ALLEGRO + SERVICE_ACCOUNT, String.class, false, true, (v) -> serviceAccount_ = v);
    withFlag('p', POD_URL, ALLEGRO + POD_URL, String.class, false, true, (v) -> podUrl_ = v);
    withFlag('o', OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class, false, true, (v) -> objectStoreUrl_ = v);
    withFlag('f', CREDENTIAL_FILE, ALLEGRO + CREDENTIAL_FILE, String.class, false, true, (v) -> credentialFile_ = v);
    withFlag('t', THREAD_ID, ALLEGRO + THREAD_ID, String.class, false, true,
        (v) -> threadId_ = ThreadId.newBuilder().build(v));
    withFlag('u', OWNER_USER_ID, ALLEGRO + OWNER_USER_ID, Long.class, false, false,
        (v) -> ownerId_ = PodAndUserId.newBuilder().build(v));
    withFlag(null, OTHER_USER_ID, ALLEGRO + OTHER_USER_ID, Long.class, false, false,
        (v) -> otherUserId_ = PodAndUserId.newBuilder().build(v));
  }

  @Override
  public void run()
  {
    allegroApi_ = new AllegroApi.Builder()
            .withConfiguration(new AllegroConfiguration.Builder()
                    .withPodUrl(podUrl_)
                    .withApiUrl(objectStoreUrl_)
                    .withUserName(serviceAccount_)
                    .withRsaPemCredentialFile(credentialFile_)
                    .withApiConnectionSettings(new ConnectionSettings.Builder()
                        .withSslTrustStrategy(SslTrustStrategy.TRUST_ALL_CERTS)
                        .build())
                    .build())
            .withFactories(CalendarModel.FACTORIES)
            .build();
    
    System.out.println("CallerId is " + allegroApi_.getUserId());
    System.out.println("OwnerId is " + otherUserId_);
    System.out.println("OtherUserId is " + otherUserId_);
    
    ResourcePermissions permissions = null;
    
    if(otherUserId_ != null)
    {
      permissions = new ResourcePermissions.Builder()
          .withUser(otherUserId_, Permission.Read)
          .build()
          ;
    }
    
    IPartition partition = allegroApi_.upsertPartition(new UpsertPartitionRequest.Builder()
            .withName(CalendarApp.PARTITION_NAME)
            .withPermissions(permissions)
            .build()
          );
      
    
    UpsertFeedRequest.Builder builder1 = new UpsertFeedRequest.Builder()
        .withName(CalendarApp.FEED_NAME)
        .withPermissions(permissions)
        .withPartitionIds(
            new PartitionId.Builder()
            .withName(CalendarApp.PARTITION_NAME)
            .withOwner(ownerId_)
            .build()
            )
        ;
   
    IFeed feed = allegroApi_.upsertFeed(builder1.build());

    
    log_.info("Feed1 is " + feed);

    IToDoItem toDoItem = new ToDoItem.Builder()
    	      .withDue(Instant.now())
    	      .withTimeTaken(new BigDecimal(1000.0 / 3.0))
    	      .withTitle("An example TODO Item")
    	      .withDescription("Since we are creating this item with a due date of Instant.now() we are already late!")
    	      .build();
    	    
    	    System.out.println("About to create item " + toDoItem);
    	    
    	    IToDoHeader header = new ToDoHeader.Builder()
    	        .withRequestingUser(allegroApi_.getUserId())
    	        .withAffectedUsers(allegroApi_.getUserId())
    	        .withEffectiveDate(Instant.now())
    	        .build();
    	    
    	    IStoredApplicationObject toDoObject = allegroApi_.newApplicationObjectBuilder()
    	        .withThreadId(threadId_)
    	        .withHeader(header)
    	        .withPayload(toDoItem)
    	        .withPartition(new PartitionId.Builder()
    	            .withName(CalendarApp.PARTITION_NAME)
    	            .withOwner(ownerId_)
    	            .build()
    	            )
    	        .withSortKey(toDoItem.getDue().toString())
//    	        .withPurgeDate(Instant.now().plusMillis(60000))
    	      .build();
    	    
    	   allegroApi_.store(toDoObject);
    	    log_.info("Feed1 is " + feed);
    	    
    	    
    	    System.out.println("Created " + toDoObject);
    	    System.out.println("absoluteHash " + toDoObject.getAbsoluteHash());
    	    
    	    allegroApi_.deleteFeed(new FeedId.Builder()
    	        .withId(feed.getId())
    	        .build()
    	        );
    	    
    	    try {
    	    allegroApi_.fetchFeedObjects(new FetchFeedObjectsRequest.Builder()
    	            .withQuery(new FeedQuery.Builder()
    	                .withName(CalendarApp.FEED_NAME)
    	                .withOwner(ownerId_)
    	                .withMaxItems(10)
    	                .build())
    	            .withConsumerManager(new ConsumerManager.Builder()
    	                .withConsumer(Object.class, (object, trace) ->
    	                {
    	                  System.out.println("FEED 1 - RECEIVED --> "+object);
    	                  throw new IllegalStateException("The feed should have been already deleted!");
    	                })
    	                .build())
    	            .build()
    	            );
                }
                catch (NotFoundException e)
                {
                  System.out.println("OK, FEED NOT FOUND, SINCE IT WAS DELETED");
                }
    
  }

  /**
   * Main.
   * 
   * @param args
   *          Command line arguments.
   */
  public static void main(String[] args)
  {
    DeleteFeed program = new DeleteFeed();

    program.process(args);

    program.run();
  }
}
