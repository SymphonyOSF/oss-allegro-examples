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

import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.request.ConsumerManager;
import com.symphony.oss.allegro.api.request.FetchPartitionObjectsRequest;
import com.symphony.oss.allegro.examples.calendar.canon.CalendarModel;
import com.symphony.oss.allegro.examples.calendar.canon.IToDoItem;
import com.symphony.oss.allegro.examples.calendar.canon.ToDoItem;

/**
 * Retrieve all objects on the given Sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class ListItems extends CommandLineHandler implements Runnable
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
  public ListItems()
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
    
    allegroApi_.fetchPartitionObjects(new FetchPartitionObjectsRequest.Builder()
          .withName(ToDoItem.TYPE_ID)
          .withOwner(allegroApi_.getUserId())
          .withMaxItems(10)
          .withConsumerManager(new ConsumerManager.Builder()
              .withConsumer(IToDoItem.class, (item, trace) ->
              {
                System.out.println("Header:  " + item.getStoredApplicationObject().getHeader());
                System.out.println("Payload: " + item);
              })
              .build()
              )
          .build()
          );
  }
  
  /**
   * Main.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args)
  {
    ListItems program = new ListItems();
    
    program.process(args);
    
    program.run();
  }
}
