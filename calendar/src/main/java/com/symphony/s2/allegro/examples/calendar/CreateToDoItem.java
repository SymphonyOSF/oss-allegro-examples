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

import java.time.Instant;

import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.models.calendar.canon.IToDoItem;
import com.symphony.oss.models.calendar.canon.ToDoItem;
import com.symphony.oss.models.chat.canon.facade.ThreadId;
import com.symphony.oss.models.fundamental.canon.facade.IFundamentalObject;
import com.symphony.oss.models.fundmental.canon.ISequence;
import com.symphony.oss.models.fundmental.canon.ISequenceHashes;
import com.symphony.oss.models.fundmental.canon.SequenceHashes;
import com.symphony.oss.models.fundmental.canon.SequenceType;
import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.FetchOrCreateSequenceMetaDataRequest;
import com.symphony.oss.allegro.api.IAllegroApi;

/**
 * An example application which creates a ToDoItem, adding it to a current and absolute sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class CreateToDoItem extends CommandLineHandler implements Runnable
{
  private static final String ALLEGRO         = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT = "SERVICE_ACCOUNT";
  private static final String POD_URL         = "POD_URL";
  private static final String OBJECT_STORE_URL = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE = "CREDENTIAL_FILE";
  private static final String THREAD_ID       = "THREAD_ID";
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  private ThreadId            threadId_;
  
  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public CreateToDoItem()
  {
    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,   (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, false,  (v) -> credentialFile_       = v);
    withFlag('t',   THREAD_ID,        ALLEGRO + THREAD_ID,        String.class,   false, true,   (v) -> threadId_             = ThreadId.newBuilder().build(v));
  }
  
  @Override
  public void run()
  { 
    allegroApi_ = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withObjectStoreUrl(objectStoreUrl_)
      .withUserName(serviceAccount_)
      .withRsaPemCredentialFile(credentialFile_)
      .withTrustAllSslCerts()
      .build();
    
    System.out.println("PodId is " + allegroApi_.getPodId());
    
    ISequence absoluteSequence = allegroApi_.fetchOrCreateSequenceMetaData(new FetchOrCreateSequenceMetaDataRequest()
          .withSequenceType(SequenceType.ABSOLUTE)
          .withContentType(ToDoItem.TYPE_ID)
          .withThreadId(threadId_)
        );
    
    System.out.println("absoluteSequence is " + absoluteSequence);
    
    ISequence currentSequence = allegroApi_.fetchOrCreateSequenceMetaData(new FetchOrCreateSequenceMetaDataRequest()
        .withSequenceType(SequenceType.CURRENT)
        .withContentType(ToDoItem.TYPE_ID)
        .withThreadId(threadId_)
      );
  
    System.out.println("currentSequence is " + currentSequence);
    
    IToDoItem toDoItem = new ToDoItem.Builder()
      .withDue(Instant.now())
      .withTitle("An example TODO Item")
      .withDescription("Since we are creating this item with a due date of Instant.now() we are already late!")
      .build();
    
    System.out.println("About to create item " + toDoItem);
    
    ISequenceHashes sequences = new SequenceHashes.Builder()
      .withAbsolute(absoluteSequence.getBaseHash())
      .withCurrent(currentSequence.getBaseHash())
      .build();
    
    IFundamentalObject toDoObject = allegroApi_.newApplicationObjectBuilder()
        .withThreadId(threadId_)
        .withPayload(toDoItem)
        .withSequences(sequences)
      .build();
    
    allegroApi_.store(toDoObject);
    
    System.out.println("Created " + toDoObject);
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    CreateToDoItem program = new CreateToDoItem();
    
    program.process(args);
    
    try
    {
    program.run();
    }
    catch(RuntimeException e)
    {
      e.printStackTrace();
    }
  }
}
