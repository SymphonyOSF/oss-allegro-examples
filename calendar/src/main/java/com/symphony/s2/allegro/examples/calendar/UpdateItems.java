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

import org.symphonyoss.s2.canon.runtime.IEntity;
import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.models.fundamental.canon.facade.IFundamentalObject;
import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.FetchSequenceMetaDataRequest;
import com.symphony.oss.allegro.api.FetchSequenceRequest;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.models.calendar.canon.CalendarModel;
import com.symphony.oss.models.calendar.canon.IToDoItem;
import com.symphony.oss.models.calendar.canon.ToDoItem;
import com.symphony.oss.models.fundmental.canon.ISequence;
import com.symphony.oss.models.fundmental.canon.SequenceType;

/**
 * Retrieve all objects on the given Sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class UpdateItems extends CommandLineHandler implements Runnable
{
  private static final String ALLEGRO          = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT  = "SERVICE_ACCOUNT";
  private static final String POD_URL          = "POD_URL";
  private static final String OBJECT_STORE_URL = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE  = "CREDENTIAL_FILE";

  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;

  private IAllegroApi         allegroApi_;

  /**
   * Constructor.
   */
  public UpdateItems()
  {
    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,   (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, true,   (v) -> credentialFile_       = v);
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
      .build();
    
    ISequence currentSequence = allegroApi_.fetchSequenceMetaData(new FetchSequenceMetaDataRequest()
        .withSequenceType(SequenceType.CURRENT)
        .withContentType(ToDoItem.TYPE_ID)
      );
  
    System.err.println("currentSequence is " + currentSequence);
    
    allegroApi_.fetchSequence(new FetchSequenceRequest()
          .withMaxItems(10)
          .withSequenceHash(currentSequence.getBaseHash())
        ,
        (item) ->
        {
          System.out.println(item);
          
          IEntity payload = allegroApi_.open(item);
          
          if(payload instanceof IToDoItem)
            update((IToDoItem)payload);
          
        });
  }
  
  private void update(IToDoItem item)
  {
    
    IToDoItem toDoItem = new ToDoItem.Builder(item)
        .withDue(Instant.now())
        .withDescription("Updated to Instant.now() so we are still late!")
        .build();
      
    System.out.println("About to update item " + toDoItem);
    
    IFundamentalObject toDoObject = allegroApi_.newApplicationObjectUpdater(item)
        .withPayload(toDoItem)
      .build();
    
    allegroApi_.store(toDoObject);
  }

  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    UpdateItems program = new UpdateItems();
    
    program.process(args);
    
    program.run();
  }
}
