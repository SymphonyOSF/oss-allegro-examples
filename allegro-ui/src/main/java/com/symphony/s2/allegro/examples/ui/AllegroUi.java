/*
 * Copyright 2020 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.oss.allegro.api.AllegroApi;
import com.symphony.oss.allegro.api.AllegroMultiTenantApi;
import com.symphony.oss.allegro.api.AsyncConsumerManager;
import com.symphony.oss.allegro.api.IAllegroApi;
import com.symphony.oss.allegro.api.IAllegroMultiTenantApi;
import com.symphony.oss.allegro.api.IAllegroQueryManager;
import com.symphony.oss.allegro.api.request.FeedQuery;
import com.symphony.oss.allegro.api.request.FetchFeedObjectsRequest;
import com.symphony.oss.allegro.api.request.UpsertFeedRequest;
import com.symphony.oss.fugue.cmd.CommandLineHandler;
import com.symphony.oss.fugue.server.http.resources.FugueResourceProvider;
import com.symphony.oss.fugue.server.http.ui.FugueHttpUiServerAssembly;
import com.symphony.oss.models.object.canon.IAbstractStoredApplicationObject;
import com.symphony.oss.models.object.canon.IFeed;

/**
 * Main class for the Allegro UI.
 * 
 * @author Bruce Skingle
 *
 */
public class AllegroUi extends FugueHttpUiServerAssembly
{
  private static final String    ALLEGRO            = "ALLEGRO_";
  private static final String    SERVICE_ACCOUNT    = "SERVICE_ACCOUNT";
  private static final String    POD_URL            = "POD_URL";
  private static final String    OBJECT_STORE_URL   = "OBJECT_STORE_URL";
  private static final String    PRIVATE_KEY_FILE   = "PRIVATE_KEY_FILE";
  private static final String    CREDENTIAL_FILE    = "CREDENTIAL_FILE";
  private static final String    INSTANCE_ID        = "INSTANCE_ID";
  
  private static final Logger                   log_ = LoggerFactory.getLogger(AllegroUi.class);
  
  private String                             serviceAccount_;
  private String                             podUrl_;
  private String                             objectStoreUrl_;
  private String                             credentialFile_;
  private String                             privateKeyFile_;
  private String                             instanceId_;

  private final IAllegroApi                  userApi_;
  private final IAllegroMultiTenantApi       accessApi_;
  private final PartitionProvider            partitionProvider_;
  private final PartitionObjectsViewProvider partitionObjectsViewProvider_;
  private final ObjectVersionsViewProvider   objectVersionsViewProvider_;
  private final IFeed                        feed_;
  private final IAllegroQueryManager         feedSubscriber_;
  
  /**
   * Constructor.
   * 
   * @param argv Command line arguments.
   */
  public AllegroUi(String[] argv)
  {
    this(argv, new ProjectorManager.Builder().build());
  }
  
  public AllegroUi(String[] argv, ProjectorManager projectorManager)
  {
    super("AllegroUi", 8000);
    
     new CommandLineHandler()
          .withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v)
          .withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v)
          .withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,  (v) -> serviceAccount_       = v)
          .withFlag('k',   PRIVATE_KEY_FILE, ALLEGRO + PRIVATE_KEY_FILE, String.class,   false, true,  (v) -> privateKeyFile_       = v)
          .withFlag('c',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, false,  (v) -> credentialFile_       = v)
          .withFlag('i',   INSTANCE_ID,      ALLEGRO + INSTANCE_ID,      String.class,   false, false,  (v) -> instanceId_       = v)
          .process(argv)
          ;
  
     userApi_ = new AllegroApi.Builder()
         .withPodUrl(podUrl_)
         .withObjectStoreUrl(objectStoreUrl_)
         .withUserName(serviceAccount_)
         .withRsaPemCredentialFile(privateKeyFile_)
         .withTrustAllSslCerts()
         .build();
     if(credentialFile_ == null)
     {
       accessApi_ = userApi_;
     }
     else
     {
       accessApi_ = new AllegroMultiTenantApi.Builder()
           .withObjectStoreUrl(objectStoreUrl_)
           .withPrincipalCredentialFile(credentialFile_)
           .withTrustAllSslCerts()
           .build();
     }
     
     partitionProvider_ = new PartitionProvider(accessApi_);
     partitionObjectsViewProvider_ = new PartitionObjectsViewProvider(accessApi_, userApi_);
     objectVersionsViewProvider_ = new ObjectVersionsViewProvider(accessApi_, userApi_);
    
    register(new FugueResourceProvider());
    
    ObjectExplorerPanel objectExplorerPanel = new ObjectExplorerPanel(accessApi_, userApi_);
    PartitionBlotterPanel partitionBlotterPanel = null;
    

    if(instanceId_ == null)
    {
      feed_ = null;
      feedSubscriber_ = null;
    }
    else
    {
      String feedName = ALLEGRO + instanceId_;
      
      feed_ = userApi_.upsertFeed(new UpsertFeedRequest.Builder()
          .withName(feedName)
          .build());
      
      partitionBlotterPanel = new PartitionBlotterPanel(projectorManager, feedName, partitionProvider_, partitionObjectsViewProvider_, accessApi_, userApi_);
      
      feedSubscriber_ = userApi_.fetchFeedObjects(new FetchFeedObjectsRequest.Builder()
          .withQuery(new FeedQuery.Builder()
              .withName(feedName)
              .build()
              )
          .withConsumerManager(new AsyncConsumerManager.Builder()
              .withSubscriberThreadPoolSize(10)
              .withHandlerThreadPoolSize(90)
              .withConsumer(IAbstractStoredApplicationObject.class, (abstractStoredObject, traceContext) ->
                {
                  System.err.println("IAbstractStoredApplicationObject " + abstractStoredObject.toString());
                  partitionObjectsViewProvider_.accept(abstractStoredObject);
                }
              )
              .withUnprocessableMessageConsumer((item, trace, message, cause) ->
                {
                  log_.error("Failed to consume message: " + message + "\nPayload:" + item, cause);
                }
              )
              .build()
            )
          .build()
      );
      
      feedSubscriber_.start();
    }
    
    getBuilder()
      .withDefaultPanel(new PartitionExplorerPanel(partitionBlotterPanel, partitionProvider_, partitionObjectsViewProvider_, accessApi_, userApi_))
      .withPanel(objectExplorerPanel)
      .withPanel(new ObjectVersionsPanel(objectVersionsViewProvider_, accessApi_, userApi_))
      .withPanel(new PodPanel(accessApi_, userApi_))
      .withLocalWebLogin();
    
    if(partitionBlotterPanel != null)
      getBuilder().withPanel(partitionBlotterPanel);
    
    
  }
  
  /**
   * Main.
   * 
   * @param argv Command line arguments.
   */
  public static void main(String[] argv)
  {
    try
    {
      AllegroUi init = new AllegroUi(argv);
      
      init.getServer().start();
  
      try
      {
        init.getServer().join();
      }
      finally
      {
        init.getServer().stop();
      }
    }
    catch (RuntimeException | InterruptedException e)
    {
      log_.error("Failed", e);
      System.exit(1);
    }
  }
}
