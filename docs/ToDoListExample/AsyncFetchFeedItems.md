---
nav_order: 70
parent: ToDo List Example
---
# Async Fetch Feed Items

The Async Fetch Feed Items example fetches objects from the Feed created in the [Create Feed Example](/CreateFeed.html).

Feeds can be read Synchronous Consumer, where the thread calling the **fetchFeedObjects** method is used to fetch a 
batch of objects and each object is passed to the consumer in the same thread,
or by an Asynchronous Consumer where a thread pool is used to fetch messages as rapidly as possible and Consumer methods
may be called in parallel by multiple threads.

The Asynchronous Consumer is illustrated by this example.

The first part of the program is almost identical to the [Hello World Example](/HelloWorld.html), see the description
there for more details.

After the program initialisation we call the **fetchFeedObjects** method to create a subscriber for the feed. 
The only difference in the call from the Synchronous example is the **AsyncConsumerManager**, when Allegro
sees that the consumer is asynchronous it creates the necessary thread pools to process messages in parallel.

This subscriber uses two
thread pools to process objects from the Feed. The subscriber thread pool provides threads to make calls
to the server for batches of up to 10 messages. When a batch is received the messages are dispatched individually
to the handler thread pool. It is therefore possible (depending on the size of these two thread pools) to have many
messages being processed in parallel.

This does, of course, mean that the order in which messages are processed is somewhat indeterminate, but
one should also bear in mind that the underlying queueing service used to provide the feed does not guarantee
the order in which messages are delivered, and that the entire Symphony infrastructure is designed around
__eventual consistency__.

The AsyncConsumerManager method takes
- The size of the subscriber thread pool
- The size of the handler thread pool
- One or more typed message consumers
- An unprocessable handler which will be called id the consumer for a message throws a fatal exception.

The consumer is implemented as a lambda which simply prints received ToDoItems to standard output.

```java 
    IAllegroQueryManager subscriber = allegroApi_.fetchFeedObjects(new FetchFeedObjectsRequest.Builder()
        .withQuery(new FeedQuery.Builder()
            .withName("myCalendarFeed")
            .withOwner(ownerUserId)
            .build()
            )
        .withConsumerManager(new AsyncConsumerManager.Builder()
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
            .build()
        )
      .build()
    );
```

The subscriber will not begin processing until its __start()__ method is called. Once this is done it is necessary to
prevent the main thread from exiting (the thread pools used by the subscriber create __daemon__ threads and will
not prevent the JVM from terminating).

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

When we run the program the first thing we see is some preliminary log messages,
followed by log messages from the subscriber starting up, the message __Press RETURN to quit__ which is
printed to standard error


```
2020-06-09T11:52:30.815000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - AllegroApi constructor start
2020-06-09T11:52:31.111000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - sbe auth....
2020-06-09T11:52:31.861000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch podInfo_....
2020-06-09T11:52:31.983000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - keymanager auth....
2020-06-09T11:52:32.149000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - getAccountInfo....
2020-06-09T11:52:32.482000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - userId_ = 351775001412007
2020-06-09T11:52:32.487000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - allegroApi constructor done.
CallerId is 351775001412007
OwnerId is 351775001412007
2020-06-09T11:52:32.527000000Z [main               ] INFO  com.symphony.s2.allegro.examples.calendar.AsyncFetchFeedItems - Subscriber state: Initializing
2020-06-09T11:52:32.528000000Z [main               ] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriberManager - Starting AbstractPullSubscriberManager with 10 subscriber threads and 90 handler threads for a total of 1 subscriptions...
2020-06-09T11:52:32.530000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroSubscriberManager - Subscribing to JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:32.533000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroSubscriberManager - Starting subscription to JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:32.535000000Z [main               ] INFO  com.symphony.s2.allegro.examples.calendar.AsyncFetchFeedItems - Subscriber state: Running
2020-06-09T11:52:32.535000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...

Press RETURN to quit
```
If we do nothing we will see repeated requests to the server for objects, each of which terminates ofter 20 seconds
with no results returned.

```
2020-06-09T11:52:32.535000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...

2020-06-09T11:52:32.537000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Pull....
2020-06-09T11:52:33.220000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 0 messages.
2020-06-09T11:52:33.220000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:33.221000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Pull....
2020-06-09T11:52:53.403000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 0 messages.
2020-06-09T11:52:53.404000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ== returned 0
2020-06-09T11:52:53.404000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:53.404000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
}
```
If we now run the 
[Update Items](/UpdateItems.md) example in another window to trigger some events on the feed, we will see those objects returned:

```
2020-06-09T11:52:58.241000000Z [PubSub-handler-1   ] INFO  com.symphony.s2.allegro.examples.calendar.AsyncFetchFeedItems - {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated at 2020-06-09T11:52:57.158Z, item 1",
  "due":"2020-06-01T13:32:52.585Z",
  "timeTaken":"333.33333333333331438552704639732837677001953125",
  "title":"An example TODO Item"
}

2020-06-09T11:52:58.350000000Z [PubSub-subscriber-2] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 1 messages.
2020-06-09T11:52:58.350000000Z [PubSub-subscriber-2] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ== returned 1
2020-06-09T11:52:58.351000000Z [PubSub-subscriber-3] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:58.351000000Z [PubSub-subscriber-3] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:58.351000000Z [PubSub-subscriber-3] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Pull....
2020-06-09T11:52:58.352000000Z [PubSub-handler-2   ] INFO  com.symphony.s2.allegro.examples.calendar.AsyncFetchFeedItems - {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated at 2020-06-09T11:52:57.882Z, item 2",
  "due":"2020-06-09T10:08:19.092Z",
  "timeTaken":"333.33333333333331438552704639732837677001953125",
  "title":"An example TODO Item"
}

2020-06-09T11:52:58.675000000Z [PubSub-subscriber-3] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 1 messages.
2020-06-09T11:52:58.675000000Z [PubSub-subscriber-3] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Non-Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ== returned 1
2020-06-09T11:52:58.675000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:58.675000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:58.675000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Pull....
2020-06-09T11:52:58.676000000Z [PubSub-handler-3   ] INFO  com.symphony.s2.allegro.examples.calendar.AsyncFetchFeedItems - {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated at 2020-06-09T11:52:58.178Z, item 3",
  "due":"2020-06-09T11:46:04.460Z",
  "timeTaken":"333.33333333333331438552704639732837677001953125",
  "title":"An example TODO Item"
}

2020-06-09T11:52:58.906000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 0 messages.
2020-06-09T11:52:58.906000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:52:58.906000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Pull....
2020-06-09T11:53:05.041000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:53:05.041000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - About to read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:53:05.041000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Pull....
2020-06-09T11:53:05.288000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 0 messages.
2020-06-09T11:53:05.289000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ==...
2020-06-09T11:53:05.289000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Pull....
```

When we press RETURN on the standard input of the AsyncFetchFeedItems program we see a graceful shutdown:

```
2020-06-09T11:53:06.477000000Z [main               ] INFO  com.symphony.s2.allegro.examples.calendar.AsyncFetchFeedItems - Stopping...
2020-06-09T11:53:25.062000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 0 messages.
2020-06-09T11:53:25.062000000Z [PubSub-subscriber-4] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ== returned 0
2020-06-09T11:53:25.476000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.allegro.api.AllegroSubscriber - Received 0 messages.
2020-06-09T11:53:25.476000000Z [PubSub-subscriber-1] INFO  com.symphony.oss.fugue.pubsub.AbstractPullSubscriber - Blocking read for JbVHG4jaA+g1ZjhwdS9qlPnDwwXiUiaJ/JRShVbRA1UBAQ== returned 0
2020-06-09T11:53:25.477000000Z [main               ] INFO  com.symphony.s2.allegro.examples.calendar.AsyncFetchFeedItems - Subscriber state: Stopped
```
