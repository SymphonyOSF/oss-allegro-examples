/*
 * Copyright 2019 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.allegro.examples.getmessage;

import org.symphonyoss.s2.fugue.cmd.CommandLineHandler;

/**
 * Create a feed for the caller's default feed. 
 * 
 * @author Bruce Skingle
 *
 */

/*
 * Temporarily withdrawn 
 */

public class SubscribeToFeed extends CommandLineHandler //implements Runnable
{
//  private static final Logger log_ = LoggerFactory.getLogger(SubscribeToFeed.class);
//  
//  private static final String   ALLEGRO          = "ALLEGRO_";
//  private static final String   SERVICE_ACCOUNT  = "SERVICE_ACCOUNT";
//  private static final String   POD_URL          = "POD_URL";
//  private static final String   OBJECT_STORE_URL = "OBJECT_STORE_URL";
//  private static final String   CREDENTIAL_FILE  = "CREDENTIAL_FILE";
//  private static final String   TRUST            = "TRUST";
//  private static final TimeUnit RETRY_TIME_UNIT  = TimeUnit.SECONDS;
//  private static final Long     RETRY_TIME       = 60L;
//  
//  private String              serviceAccount_;
//  private String              podUrl_;
//  private String              objectStoreUrl_;
//  private String              credentialFile_;
//  private String              trust_;
//  
//  private IAllegroApi         allegroApi_;
//
//  /**
//   * Constructor.
//   */
//  public SubscribeToFeed()
//  {
//    withFlag('s',   SERVICE_ACCOUNT,  ALLEGRO + SERVICE_ACCOUNT,  String.class,   false, true,   (v) -> serviceAccount_       = v);
//    withFlag('p',   POD_URL,          ALLEGRO + POD_URL,          String.class,   false, true,   (v) -> podUrl_               = v);
//    withFlag('o',   OBJECT_STORE_URL, ALLEGRO + OBJECT_STORE_URL, String.class,   false, true,   (v) -> objectStoreUrl_       = v);
//    withFlag('f',   CREDENTIAL_FILE,  ALLEGRO + CREDENTIAL_FILE,  String.class,   false, true,   (v) -> credentialFile_       = v);
//    withFlag('T',   TRUST,            ALLEGRO + TRUST,            String.class,   false, false,  (v) -> trust_                = v);
//  }
//
//  @Override
//  public void run()
//  {
//    AllegroApi.Builder builder = new AllegroApi.Builder()
//      .withPodUrl(podUrl_)
//      .withObjectStoreUrl(objectStoreUrl_)
//      .withUserName(serviceAccount_)
//      .withRsaPemCredentialFile(credentialFile_)
//      ;
//    
//    if(trust_ != null)
//    {
//      switch(trust_.toLowerCase())
//      {
//        case "symphony":
//          builder.withTrustedSslCertResources(IAllegroApi.SYMPHONY_DEV_QA_ROOT_CERT);
//          break;
//        
//        case "all":
//          builder.withTrustAllSslCerts();
//          break;
//        
//        default:
//          throw new IllegalArgumentException("Invalid trust value, expected \"symphony\" or \"all\"");
//      }
//    }
//    
//    allegroApi_ = builder.build();
//    
//    IFugueLifecycleComponent subscriber = allegroApi_.createFeedSubscriber(new CreateFeedSubscriberRequest()
//        .withName("myFeed")
//        .withConsumer(INotification.class, new IThreadSafeConsumer<INotification>()
//        {
//          boolean ack = true;
//          
//          @Override
//          public void consume(INotification message, ITraceContext trace)
//          {
//            if(ack)
//            {
//              System.out.println("ACK " + message.getPayloadId());
//              ack = false;
//            }
//            else
//            {
//              System.out.println("NAK " + message.getPayloadId());
//              ack = true;
//              throw new RuntimeException("Rejected");
//            }
//          }
//
//          @Override
//          public void close()
//          {
//            System.out.println("That's all folks!");
//          }
//        }
//        )
//        .withConsumer(ISocialMessage.class, (message, trace) ->
//        {
//          System.out.println("ACK " + message.getMessageId());
//          System.out.println("ACK " + message.toString());
//        })
//        .withConsumer(IReceivedChatMessage.class, (message, trace) ->
//          {
//              log_.info(message.getPresentationML().toString());
//          }
//        )
//        .withUnprocessableMessageConsumer((item, trace, message, cause) ->
//        {
//          log_.error("Failed to consume message: " + message + "\nPayload:" + item, cause);
//        })
//      );
//    
//    subscriber.start();
//    
//    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//    
//    log_.info("Subscriber state: " + subscriber.getLifecycleState());
//    
//    System.err.println();
//    System.err.println("Press RETURN to quit");
//    try
//    {
//      in.readLine();
//    }
//    catch (IOException e)
//    {
//      e.printStackTrace();
//    }
//    
//    log_.info("Stopping...");
//    subscriber.stop();
//    log_.info("Subscriber state: " + subscriber.getLifecycleState());
//    
//  }
//  
//  /**
//   * Main.
//   * 
//   * @param args Command line arguments.
//   */
//  public static void main(String[] args)
//  {
//    SubscribeToFeed program = new SubscribeToFeed();
//    
//    program.process(args);
//    
//    program.run();
//  }
}
