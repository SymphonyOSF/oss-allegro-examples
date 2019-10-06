---
nav_order: 40
parent: ToDo List Example
---
# Fetch Feed Items

The Fetch Feed Items example creates a Feed, which it subscribes to the current sequence containing our ToDoItems.

A Feed is a queue, which provides at least once delivery of objects which are created in the Object Store.
A Feed can be subscribed to one or more Sequences, which means that whenever an object is created which
is a member of one of those Sequences, the new object will be placed onto the Feed to be read.

Feeds can be read by a low level API, where the calling thread is used to fetch and process messages, and the
caller is responsible for ensuring that processed messages are acknowledged (deleted from the Feed) and that
the visibility timeout is extended as necessary.
There is also a Subscriber API which takes care of visibility timeout extension and message deletion for you,
and this is the mechanism illustrated by this example.

The first part of the program is almost identical to the [Hello World Example](/HelloWorld.md), see the description
there for more details.

In this case we also need to provide the Object Store URL, since these examples use that API.

```java
public class FetchFeedItems extends CommandLineHandler implements Runnable
{
  private static final Logger log_ = LoggerFactory.getLogger(FetchFeedItems.class);
  
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
  public FetchFeedItems()
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
```

In the next part of the program we fetch the metadata for the current sequence:

```java
    ISequence currentSequence = allegroApi_.fetchSequenceMetaData(new FetchSequenceMetaDataRequest()
        .withSequenceType(SequenceType.CURRENT)
        .withContentType(ToDoItem.TYPE_ID)
      );
  
    log_.info("currentSequence is " + currentSequence.getBaseHash() + " " + currentSequence);
    
```

Next we call the __upsertFeed__ method to create the feed and its subscription to the current sequence. As
this is an upsert operation it is idempotent and safe to do on each call to read the Feed:

```java    
    IFeed feed = allegroApi_.upsertFeed(
        new UpsertFeedRequest()
          .withType(FeedType.FEED)
          .withName("myCalendarFeed")
          .withSequences(currentSequence)
          );
    
    log_.info("Feed is " + feed);
    
```

As you can see, we pass a name for the feed (feeds belong to the user calling the API, this name is unique
to the calling user only), and the sequence we wish to subscribe the feed to. We could pass multiple Sequences
if we wished.

Next we call the __createFeedSubscriber__ method to create a subscriber for the feed. This subscriber uses two
thread pools to process messages from the Feed. The subscriber thread pool provides threads to make calls
to the server for batches of up to 10 messages. When a batch is received the messages are dispached individually
to the handler thread pool. It is therefore possible (depending on the size of these two thread pools) to have many
messages being processed in parallel.

This does, or course, mean that the order in which messages are processed is somewhat indeterminate, but
one should also bear in mind that the underlying queueing service used to provide the feed does not guarantee
the order in which messages are delivered, and that the entire Symphony infrastructure is designed around
__eventual consistency__.

This method takes
- The name of the Feed to be read
- The size of the subscriber thread pool
- The size of the handler thread pool
- One or more typed message consumers
- A default consumer for messages which do not match any typed consumer.

The consumer is implemented as a lambda which simply prints received ToDoItems to standard output.


```java
    IFugueLifecycleComponent subscriber = allegroApi_.createFeedSubscriber(new CreateFeedSubscriberRequest()
        .withName("myCalendarFeed")
        .withSubscriberThreadPoolSize(10)
        .withHandlerThreadPoolSize(90)
        .withConsumer(IToDoItem.class, (message, traceContext) ->
        {
          log_.info(message.toString());
        })
        .withUnprocessableMessageConsumer((item, trace, message, cause) ->
        {
          log_.error("Failed to consume message: " + message + "\nPayload:" + item, cause);
        })
    );
```

The subscriber will not begin processing until its __start()__ method is called. Once this is done it is necessary to
prevent the main thread from exiting (the thread pools used by the subscriber create __daemon__ threads and will
not prevent the JVM from terminating.

For this example we simply wait for a newline to be typed on the standard input. When this happens we stop
the subscriber gracefully. You may notice that this shutdown process takes up to 20 seconds, because it waits
for any pending subscriber threads to return and processes any messages they bring back.

```java
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
```


When we run the program the first thing we see is some preliminary log messages:


```
16:15:15.133 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - AllegroApi constructor start
16:15:15.569 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - sbe auth....
16:15:16.243 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch podInfo_....
16:15:16.376 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - keymanager auth....
16:15:16.791 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - principalHash_ = 1XEPBZKPz3gY+sVyn1QiEoWCfYr/RUATByAtBuqzkiwBAQ==
16:15:16.791 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - userId_ = 11407433183256
16:15:16.791 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - allegroApi constructor done.
```
Then we see the sequence and feed meta data:

```
17:38:10.826 [main] INFO  com.symphony.s2.allegro.examples.calendar.FetchFeedItems - currentSequence is lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ== {
  "_type":"com.symphony.s2.model.fundamental.Sequence",
  "_version":"1.0",
  "baseHash":"lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ==",
  "createdDate":"2019-10-03T18:04:50.016Z",
  "prevHash":"lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ==",
  "securityContextHash":"Bek1dWzL0C3NXy1qOpTiEyL6QKUaJG68bAik3U0zTDMBAQ==",
  "signingKeyHash":"TpDRe0nu8GnLMCy73/9nLuMqhhsVo+L18Azk57UfMOkBAQ==",
  "type":"CURRENT"
}

17:38:11.084 [main] INFO  com.symphony.s2.allegro.examples.calendar.FetchFeedItems - Feed is {
  "_type":"com.symphony.s2.model.system.Feed",
  "_version":"1.0",
  "podId":166,
  "principalBaseHash":"1XEPBZKPz3gY+sVyn1QiEoWCfYr/RUATByAtBuqzkiwBAQ==",
  "queueName":"sym-s2-dev-s2smoke2-166-s2fwd-feed-fa2UpDgUgkLUcZTpxHbyWk",
  "type":"FEED"
}
}
```
Followed by log messages from the subscriber starting up, the message __Press RETURN to quit__ which is
printed to standard error, and ToDoItens as they are updated. (You may wish to run the 
[Update Items](/UpdateItems.md) example in another window to trigger some events on the feed).

```
17:38:11.110 [main] INFO  com.symphony.s2.allegro.examples.calendar.FetchFeedItems - Subscriber state: Initializing
17:38:11.112 [main] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriberManager - Starting AbstractPullSubscriberManager with 10 subscriber threads and 90 handler threads for a total of 1 subscriptions...
17:38:11.114 [main] INFO  com.symphony.oss.allegro.api.AllegroSubscriberManager - Subscribing to myCalendarFeed...
17:38:11.117 [main] INFO  com.symphony.oss.allegro.api.AllegroSubscriberManager - Starting subscription to myCalendarFeed...
17:38:11.118 [main] INFO  com.symphony.s2.allegro.examples.calendar.FetchFeedItems - Subscriber state: Running

17:38:11.118 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
Press RETURN to quit
17:38:11.119 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:11.239 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Blocking read for myCalendarFeed...
17:38:31.411 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Blocking read for myCalendarFeed returned 0
17:38:31.412 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:31.412 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:31.569 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Blocking read for myCalendarFeed...
17:38:51.787 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Blocking read for myCalendarFeed returned 0
17:38:51.787 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:51.787 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:51.902 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Blocking read for myCalendarFeed...
17:38:58.333 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Blocking read for myCalendarFeed returned 1
17:38:58.742 [PubSub-handler-1] INFO  com.symphony.s2.allegro.examples.calendar.FetchFeedItems - {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated to Instant.now() so we are still late!",
  "due":"2019-10-06T21:38:57.417Z",
  "title":"An example TODO Item"
}

17:38:58.810 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:58.810 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:58.935 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Non-Blocking read for myCalendarFeed returned 1
17:38:58.935 [PubSub-subscriber-2] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:58.935 [PubSub-subscriber-2] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:59.004 [PubSub-subscriber-2] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Non-Blocking read for myCalendarFeed returned 1
17:38:59.004 [PubSub-subscriber-3] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:59.004 [PubSub-subscriber-3] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:59.058 [PubSub-handler-2] INFO  com.symphony.s2.allegro.examples.calendar.FetchFeedItems - {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated to Instant.now() so we are still late!",
  "due":"2019-10-06T21:38:57.956Z",
  "title":"An example TODO Item"
}

17:38:59.180 [PubSub-subscriber-3] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - Blocking read for myCalendarFeed...
17:38:59.244 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...
17:38:59.244 [PubSub-subscriber-1] INFO  org.symphonyoss.s2.fugue.pubsub.AbstractPullSubscriber - About to read for myCalendarFeed...

```
