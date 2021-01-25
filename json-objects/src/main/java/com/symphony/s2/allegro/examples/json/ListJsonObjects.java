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

package com.symphony.s2.allegro.examples.json;

import com.symphony.oss.allegro.api.request.FetchPartitionObjectsRequest;
import com.symphony.oss.allegro.api.request.PartitionQuery;
import com.symphony.oss.allegro.objectstore.AllegroObjectStoreApi;
import com.symphony.oss.allegro.objectstore.ConsumerManager;
import com.symphony.oss.allegro.objectstore.IAllegroObjectStoreApi;
import com.symphony.oss.fugue.cmd.CommandLineHandler;
import com.symphony.oss.models.allegro.canon.facade.AllegroConfiguration;
import com.symphony.oss.models.allegro.canon.facade.AllegroObjectStoreConfiguration;
import com.symphony.oss.models.object.canon.facade.IApplicationObjectPayload;

/**
 * Retrieve all objects on the given Sequence.
 * 
 * @author Bruce Skingle
 *
 */
public class ListJsonObjects extends CommandLineHandler implements JsonObjectExample
{
  private static final String ALLEGRO          = "ALLEGRO_";
  private static final String SERVICE_ACCOUNT  = "SERVICE_ACCOUNT";
  private static final String POD_URL          = "POD_URL";
  private static final String OBJECT_STORE_URL = "OBJECT_STORE_URL";
  private static final String CREDENTIAL_FILE  = "CREDENTIAL_FILE";
  private static final String CERT_FILE        = "CERT_FILE";
  private static final String CERT_PASSWORD    = "CERT_PASSWORD";
  private static final String SESSION_AUTH_URL = "SESSION_AUTH_URL";
  private static final String KEY_AUTH_URL     = "KEY_AUTH_URL";
  private static final String PROXY_URL        = "PROXY_URL";
  
  private String              serviceAccount_;
  private String              podUrl_;
  private String              objectStoreUrl_;
  private String              credentialFile_;
  
  private IAllegroObjectStoreApi         allegroApi_;
  private String              certFile_;
  private String              certPassword_;
  private String              sessionAuthUrl_;
  private String              keyAuthUrl_;
  private String              proxyUrl_;

  /**
   * Constructor.
   */
  public ListJsonObjects()
  {
    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, false,  (v) -> serviceAccount_       = v);
    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, false,  (v) -> credentialFile_       = v);
    withFlag(null,  CERT_FILE,        ALLEGRO + CERT_FILE,        String.class,   false, false,  (v) -> certFile_             = v);
    withFlag(null,  CERT_PASSWORD,    ALLEGRO + CERT_PASSWORD,    String.class,   false, false,  (v) -> certPassword_         = v);
    withFlag(null,  SESSION_AUTH_URL, ALLEGRO + SESSION_AUTH_URL, String.class,   false, false,  (v) -> sessionAuthUrl_       = v);
    withFlag(null,  KEY_AUTH_URL,     ALLEGRO + KEY_AUTH_URL,     String.class,   false, false,  (v) -> keyAuthUrl_           = v);
    withFlag(null,  PROXY_URL,        ALLEGRO + PROXY_URL,        String.class,   false, false,  (v) -> proxyUrl_           = v);
  }
  
  @Override
  public void run()
  {
    allegroApi_ = new AllegroObjectStoreApi.Builder()
        .withConfiguration(new AllegroObjectStoreConfiguration.Builder()
            .withApiUrl(objectStoreUrl_)
//            .withApiConnectionSettings(new ConnectionSettings.Builder()
//                .withSslTrustStrategy(SslTrustStrategy.TRUST_ALL_CERTS)
//                .build())
            .withAllegroConfiguration(new AllegroConfiguration.Builder()
                .withPodUrl(podUrl_)
                .withUserName(serviceAccount_)
                .withRsaPemCredentialFile(credentialFile_)
                .build())
            .build())
    .build();
    
  
    allegroApi_.fetchPartitionObjects(new FetchPartitionObjectsRequest.Builder()
        .withQuery(new PartitionQuery.Builder()
            .withName(PARTITION_NAME)
            .withOwner(allegroApi_.getUserId())
            .withMaxItems(10)
            .build()
            )
        .withConsumerManager(new ConsumerManager.Builder()
            .withConsumer(IApplicationObjectPayload.class, (item, trace) ->
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
    ListJsonObjects program = new ListJsonObjects();
    
    program.process(args);
    
    program.run();
  }
}
