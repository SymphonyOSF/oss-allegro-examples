---
nav_order: 20
parent: ToDo List Example
---
# Create An Item

The Create an Item example creates a new ToDo item.

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

In the next part of the program we create the sequences we will use as our data set. 

In order to create an Object with an ID which we can look up, we need to create an ID object, which is an unencrypted
object containing some identifier which is known externally. For security reasons (to prevent certain types
of denial of service attack) all ID objects created through Allegro _must_ include the user ID of the user
who created the ID object.

The __fetchOrCreateSequenceMetaData__ method creates an ID object based upon the sequence type and the type of object 
which the sequence is intended to contain. Note that this type is just a notation to allow distinct ID objects
to be created in a consistent way and does not restrict the type of object which can be added to the sequence.
The server does not know what the type of an individual object is, since all objects are encrypted by the client 
before sending them to the server. 
 
We create two sequences, one absolute and one current so that we can see the differences between them in usage.


```java
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
```

Next we create a ToDo item, setting the due date to the current time and the title and description to 
hard coded values. 

```java
    IToDoItem toDoItem = new ToDoItem.Builder()
      .withDue(Instant.now())
      .withTitle("An example TODO Item")
      .withDescription("Since we are creating this item with a due date of Instant.now() we are already late!")
      .build();
    
    System.out.println("About to create item " + toDoItem);
```

The ToDo object is the unencrypted payload which we wish to store. Before we can store it we need to encrypt
it, which we do by creating a FundamentalObject using the __ApplicationObjectBuilder__ class.

```java
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
```
To create the encrypted FundamentalObject we pass the threadId of the conversation with which the object is
to be associated. This is used to obtain the content encryption key with which the payload will be encrypted.
Note that any member of the conversation (chat room of instant message conversation) would be able to obtain
the content key for that conversation and could read the object from the object store.

We also provide the payload created above, and the list of sequences to which the object should be added.

When we call the __build()__ method the payload is encrypted and the FundamentalObject is returned which
can then be safely stored in the cloud.

The FundamentalObject signed as well as being encrypted, and this signature is verified before the object is
accepted by the server for storage.

When we run this example we see the following output:

```
16:12:46.176 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - AllegroApi constructor start
16:12:46.696 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - sbe auth....
16:12:47.491 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch podInfo_....
16:12:47.614 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - keymanager auth....
16:12:48.038 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - principalHash_ = 1XEPBZKPz3gY+sVyn1QiEoWCfYr/RUATByAtBuqzkiwBAQ==
16:12:48.038 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - userId_ = 11407433183256
16:12:48.038 [main] INFO  com.symphony.oss.allegro.api.AllegroApi - allegroApi constructor done.
PodId is 166
16:12:49.262 [main] INFO  com.gs.ti.wpt.lc.security.cryptolib.Utils - Arch: mac os x/x86_64
16:12:49.288 [main] INFO  com.symphony.security.utils.DynamicLibraryLoader - java.library.path=/var/folders/4k/0sfqq6v1605547b2gjcldk2h0000gn/T:/Users/bruce/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
16:12:49.293 [main] INFO  com.gs.ti.wpt.lc.security.cryptolib.Utils - Arch: mac os x/x86_64
absoluteSequence is {
  "_type":"com.symphony.s2.model.fundamental.Sequence",
  "_version":"1.0",
  "baseHash":"vh3O2VsObaYQHvYCUxOfdWyLExY3ypmD8I2ju0JNcb4BAQ==",
  "createdDate":"2019-08-07T23:12:50.765Z",
  "prevHash":"vh3O2VsObaYQHvYCUxOfdWyLExY3ypmD8I2ju0JNcb4BAQ==",
  "securityContextHash":"Bek1dWzL0C3NXy1qOpTiEyL6QKUaJG68bAik3U0zTDMBAQ==",
  "signingKeyHash":"uaBGE9UNUeraOSmtd46mncYQb8ppsWg4CNV5im/q++4BAQ==",
  "type":"ABSOLUTE"
}

currentSequence is {
  "_type":"com.symphony.s2.model.fundamental.Sequence",
  "_version":"1.0",
  "baseHash":"lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ==",
  "createdDate":"2019-08-07T23:12:51.948Z",
  "prevHash":"lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ==",
  "securityContextHash":"Bek1dWzL0C3NXy1qOpTiEyL6QKUaJG68bAik3U0zTDMBAQ==",
  "signingKeyHash":"uaBGE9UNUeraOSmtd46mncYQb8ppsWg4CNV5im/q++4BAQ==",
  "type":"CURRENT"
}
```

After some preliminary log output we see the two sequence metadata objects which have been created.
Then we see the ToDo item: 

```
About to create item {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Since we are creating this item with a due date of Instant.now() we are already late!",
  "due":"2019-08-07T23:12:52.645Z",
  "title":"An example TODO Item"
}

```

And finally the encrypted and signed FundamentalObject as stored in the cloud:

```
Created {
  "_type":"com.symphony.s2.model.fundamental.FundamentalObject",
  "_version":"1.0",
  "hashType":1,
  "payload":{
    "_type":"com.symphony.s2.model.fundamental.Blob",
    "_version":"1.0",
    "baseHash":"AA==",
    "createdDate":"2019-08-07T23:12:52.925Z",
    "encryptedPayload":"/KCjv4mi6bUq6deppucc5qLtSDsrkVb8Y9cBSbwgv1K8B1eP5DdT0Mj+/a4Ahqwg3d1CpHMsZnmgFelfoD9GPLOtgRUlNkK2YW/EtOpH1gynZOoDgjkajfyi1wX42VoYvBN9LjsItcYKEKffqi0es4wymNFEGnybYa1dsjkFbD/hnkhd15x3DCVsGZPWSLzwhBcXP+OzByNdZ+TBiaflVimzld7sWM4Krlkmf+NgSqPOzSMDZIgTfL1CtIr69RitsaMcYlODDszdIgtbwhSNLoWk0Zb91YdKF0sskQbmFJf1Z31ClTBQqBPn9yuy610XkVcVsIjeI5sVL49BvkbY2L6wUIiY5KaF+n+m93E2u80jUkDHnKXbJEvQhGusgcveK6N0nsLc+PORvUY=",
    "payloadType":"com.symphony.s2.model.calendar.ToDoItem",
    "podId":166,
    "prevHash":"AA==",
    "securityContextHash":"8jeJFwLEJxSRn6IgQUt0FbMaV89mkDPE8VbqvT+2MhQBAQ==",
    "sequences":{
      "_type":"com.symphony.s2.model.fundamental.SequenceHashes",
      "_version":"1.0",
      "absolute":[
        "vh3O2VsObaYQHvYCUxOfdWyLExY3ypmD8I2ju0JNcb4BAQ=="
      ],
      "current":[
        "lABENMBAwyZYcetgaYOFRIa6TL+FwmiYsC7oGmqhZkcBAQ=="
      ],
      "hashCurrent":[]
    },
    "signingKeyHash":"uaBGE9UNUeraOSmtd46mncYQb8ppsWg4CNV5im/q++4BAQ=="
  },
  "signature":"SnUWsWVcnhKTm1k9nzrWGdkpX1zkOxFAAm6FZQR8/kqlSoM0bBRVjA+B8zjusURwTClABjmXKYoIFOV21MuSkgz8LADjsROe+e6tyZBXcxMgGktaUfeLdQ8UGfO48ZqfwrP/lKlsGbyEyVJsPuU0Xis8vP+XNwUym5hoHUnkonbSqFWiubnpQZ48S4Re7bMTah1BjsxMYiFbyud1EQRRvxyCz7w6TuqQRx8pt+PITkgGLfqNFwqvEaiQQB32r/06CEbaSL7Ljkjhu2RLD2aer3D7FPMU+CLo76mtryBQYloW03NgTK59OdjH4uLD7lvZ6uxRCtiqY/2GJkxIWn3RUA=="
}
```

