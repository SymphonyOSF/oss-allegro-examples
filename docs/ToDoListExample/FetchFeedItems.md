---
nav_order: 60
parent: ToDo List Example
---
# Fetch Feed Items

The Fetch Feed Items example fetches objects from the Feed created in the [Create Feed Example](/CreateFeed.html).

Feeds can be read Synchronous Consumer, where the thread calling the **fetchFeedObjects** method is used to fetch a 
batch of objects and each object is passed to the consumer in the same thread,
or by an Asynchronous Consumer where a thread pool is used to fetch messages as rapidly as possible and Consumer methods
may be called in parallel by multiple threads.

The Synchronous Consumer is illustrated by this example.

The first part of the program is almost identical to the [Hello World Example](/HelloWorld.html), see the description
there for more details.

In this case we also need to provide the Object Store URL, since these examples use that API.

```java
public class FetchFeedItems extends CommandLineHandler implements Runnable
{
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
  public FetchFeedItems()
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
```

Next we call the **fetchFeedObjects** method to fetch objects from the feed. Since we are passing a synchronous ConsumerManager
the call will be made directly in the calling thread. If messages are available, or become available during the period of a single
long poll (20 seconds) then the appropriate consumers will be called, in the same thread. Once all messages retrieved in
a single request have been processed, or if no messages become available during the limit of a single long poll, then the
**fetchFeedObjects** call will return.

In order to make a long running process you would need to call this method in a loop, or use the 
[Async Fetch Feed Example](/AsyncFetchFeed.html) instead.

```java
    allegroApi_.fetchFeedObjects(new FetchFeedObjectsRequest.Builder()
        .withQuery(new FeedQuery.Builder()
            .withName("myCalendarFeed")
            .withOwner(ownerUserId)
            .withMaxItems(10)
            .build())
        .withConsumerManager(new ConsumerManager.Builder()
            .withConsumer(Object.class, (object, trace) ->
            {
              System.out.println(object);
            })
            .build())
        .build()
        );
```

If we were to run the program before creating the feed we would see an error message like this:


```
2020-06-09T10:53:03.703000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - AllegroApi constructor start
2020-06-09T10:53:03.920000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - sbe auth....
2020-06-09T10:53:04.591000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch podInfo_....
2020-06-09T10:53:04.709000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - keymanager auth....
2020-06-09T10:53:04.869000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - getAccountInfo....
2020-06-09T10:53:05.173000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - userId_ = 351775001412007
2020-06-09T10:53:05.177000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - allegroApi constructor done.
CallerId is 351775001412007
OwnerId is 351775001412007
Exception in thread "main" com.symphony.oss.canon.runtime.exception.NotFoundException: HTTP/1.1 404 Not Found ["No such feed"]

  at com.symphony.oss.canon.runtime.http.client.HttpRequestOrBuilder.validateResponse(HttpRequestOrBuilder.java:79)
  at com.symphony.oss.models.object.canon.FeedsFeedHashObjectsPostHttpRequest.execute(FeedsFeedHashObjectsPostHttpRequest.java:141)
  at com.symphony.oss.allegro.api.AllegroBaseApi.fetchFeedObjects(AllegroBaseApi.java:623)
  at com.symphony.oss.allegro.api.AllegroBaseApi.fetchFeedObjects(AllegroBaseApi.java:558)
  at com.symphony.s2.allegro.examples.calendar.FetchFeedItems.run(FetchFeedItems.java:80)
  at com.symphony.s2.allegro.examples.calendar.FetchFeedItems.main(FetchFeedItems.java:107)
```

When we run the Create Item Example to create a new ToDoItem and then run this program the first thing we see is some preliminary log messages:


```
2020-06-09T11:46:14.618000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - AllegroApi constructor start
2020-06-09T11:46:14.840000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - sbe auth....
2020-06-09T11:46:15.524000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch podInfo_....
2020-06-09T11:46:15.643000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - keymanager auth....
2020-06-09T11:46:15.809000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - getAccountInfo....
2020-06-09T11:46:16.115000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - userId_ = 351775001412007
2020-06-09T11:46:16.119000000Z [main               ] INFO  com.symphony.oss.allegro.api.AllegroApi - allegroApi constructor done.
CallerId is 351775001412007
OwnerId is 351775001412007
```
Followed by the received objects from the Feed:

```
{
  "_type":"com.symphony.s2.model.object.StoredApplicationObject",
  "_version":"1.0",
  "cipherSuiteId":"RSA2048_AES256",
  "createdDate":"2020-06-09T11:46:05.216Z",
  "encryptedPayload":"1QZZ7uwYK9Ohdb2G5j3S1BLcnWSj2do/BkeWZvFXEU//Guc4AhoyXfVOhksDq0EMYpknpGhVGvU+SOA8AwPrjhrZZ5KCK2bHuX41wpJKu+bnd1zyWG/uYAwx3dV1hcVPal1R4g0sk04kwL2FcQ8y5l7A+/byO0oMvYr/+i3lKRslNMjDkVabGIXse4hMM+hY8U5mGdkFZZCHDf+TojiK3nET7iaVwd2cT+EzjYskIAVq5nu5zxRS7j7gRNA/Wej5EvvJNP4yihua+KjoY3kgBKntjVwicjbGTr0F4YXfB7L2oohCqZ+NpCJHeAfT6DOtycDU52u3iXvmXlZh93WfvZjVWf2CMpYkCOQpJbAb6VjzBbZ28vPqq9dfULjlDjTJbB55zVNfSBv3q6sLqXL+OEMUxZTtXz3lKyO6TGPu0cjdCSBxGgrGyHWs7UMG7FIhgJUNoZu8O63MXggnExsKC3jy7sEvnT+GQUgPWvY=",
  "hashType":1,
  "header":{
    "_type":"com.symphony.s2.model.calendar.ToDoHeader",
    "_version":"1.0",
    "affectedUsers":[
      351775001412007
    ],
    "effectiveDate":"2020-06-09T11:46:04.495Z",
    "requestingUser":351775001412007
  },
  "owner":351775001412007,
  "partitionHash":"j/vNnRtcln7uKIixRBWuqCjimkm5VxO4WLYKJVUT00oBAQ==",
  "rotationId":0,
  "sortKey":"2020-06-09T11:46:04.460Z",
  "threadId":"+ZNefHakKm9vK7WREHHLEn///pEOR60jdA=="
}
```
