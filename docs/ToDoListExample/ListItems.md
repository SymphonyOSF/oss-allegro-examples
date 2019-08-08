---
nav_order: 30
parent: ToDo List Example
---
# List Items

The List Items example lists all the items in both sequences.

The first part of the program is almost identical to the [Hello World Example](/HelloWorld.md), see the description
there for more details.

In this case we also need to provide the Object Store URL, since these examples use that API.

```java
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
      .build();
```

In the next part of the program we fetch the metadata for the absolute sequence:

```java 
    ISequence absoluteSequence = allegroApi_.fetchSequenceMetaData(new FetchSequenceMetaDataRequest()
        .withSequenceType(SequenceType.ABSOLUTE)
        .withContentType(ToDoItem.TYPE_ID)
      );
  
    System.out.println("absoluteSequence is " + absoluteSequence.getBaseHash() + " " + absoluteSequence);
    
```

The we call the __fetchSequence__ method to retrieve the objects on the sequence. We pass a limit of
10 as a safety precaution in case the sequence is large.

This method takes a lambda as one of its parameters which is used to process the items in the sequence.
In this example we simply open (decrypt) the returned object, and print it to standard output.


```java    
    allegroApi_.fetchSequence(new FetchSequenceRequest()
          .withMaxItems(10)
          .withSequenceHash(absoluteSequence.getBaseHash())
        ,
        (item) ->
        {
          try
          {
            System.out.println(allegroApi_.open(item));
          }
          catch(Exception e)
          {
            e.printStackTrace();
            
            System.out.println(item);
          }
        });
```

We then repeat this process for the current sequence. Since we have created a single object, the absolute and current sequences contain the same thing.

When we run the program the first thig we see is some preliminary log messages:


```
16:15:15.133 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - AllegroApi constructor start
16:15:15.569 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - sbe auth....
16:15:16.243 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch podInfo_....
16:15:16.376 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - keymanager auth....
16:15:16.791 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - principalHash_ = 1XEPBZKPz3gY+sVyn1QiEoWCfYr/RUATByAtBuqzkiwBAQ==
16:15:16.791 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - userId_ = 11407433183256
16:15:16.791 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - allegroApi constructor done.
```
Then we see the absolute sequence meta data:

```
absoluteSequence is vh3O2VsObaYQHvYCUxOfdWyLExY3ypmD8I2ju0JNcb4BAQ== {
  "_type":"com.symphony.s2.model.fundamental.Sequence",
  "_version":"1.0",
  "baseHash":"vh3O2VsObaYQHvYCUxOfdWyLExY3ypmD8I2ju0JNcb4BAQ==",
  "createdDate":"2019-08-07T23:12:50.765Z",
  "prevHash":"vh3O2VsObaYQHvYCUxOfdWyLExY3ypmD8I2ju0JNcb4BAQ==",
  "securityContextHash":"Bek1dWzL0C3NXy1qOpTiEyL6QKUaJG68bAik3U0zTDMBAQ==",
  "signingKeyHash":"uaBGE9UNUeraOSmtd46mncYQb8ppsWg4CNV5im/q++4BAQ==",
  "type":"ABSOLUTE"
}
```
Followed by the contents of the sequence (just the one ToDo item at the moment):

```

{
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Since we are creating this item with a due date of Instant.now() we are already late!",
  "due":"2019-08-07T23:12:52.645Z",
  "title":"An example TODO Item"
}

```

The the process is repeated for the current sequence, with the same results.

```
currentSequence is lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ== {
  "_type":"com.symphony.s2.model.fundamental.Sequence",
  "_version":"1.0",
  "baseHash":"lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ==",
  "createdDate":"2019-08-07T23:12:51.948Z",
  "prevHash":"lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ==",
  "securityContextHash":"Bek1dWzL0C3NXy1qOpTiEyL6QKUaJG68bAik3U0zTDMBAQ==",
  "signingKeyHash":"uaBGE9UNUeraOSmtd46mncYQb8ppsWg4CNV5im/q++4BAQ==",
  "type":"CURRENT"
}

{
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Since we are creating this item with a due date of Instant.now() we are already late!",
  "due":"2019-08-07T23:12:52.645Z",
  "title":"An example TODO Item"
}


```

