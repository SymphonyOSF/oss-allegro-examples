---
nav_order: 40
parent: ToDo List Example
---
# Update Items

The Update Items example updates all items in the current sequence to have the current time in their
description.

This program is almost identical to the [List Items Example](ListItems.md), except for the logic in the
consumer:

```java  
    allegroApi_.fetchPartitionObjects(new FetchPartitionObjectsRequest.Builder()
        .withQuery(new PartitionQuery.Builder()
            .withMaxItems(10)
            .withName(CalendarApp.PARTITION_NAME)
            .withOwner(allegroApi_.getUserId())
            .build()
            )
          .withConsumerManager(new ConsumerManager.Builder()
              .withConsumer(IToDoItem.class, (item, trace) ->
              {
                System.out.println("Header:  " + item.getStoredApplicationObject().getHeader());
                System.out.println("Payload: " + item);
                System.out.println("Stored:  " + item.getStoredApplicationObject());
                
                update(item);
              })
              .build()
              )
          .build()
          );
```

In the consumer lambda we call an update routine:


```java 
  private void update(IToDoItem item)
  {
    IToDoItem toDoItem = new ToDoItem.Builder(item)
        .withDescription("Updated at " + Instant.now() + ", item " + count_++)
        .build();
      
    System.out.println("About to update item " + toDoItem);
    
    IStoredApplicationObject toDoObject = allegroApi_.newApplicationObjectUpdater(item.getStoredApplicationObject())
        .withPayload(toDoItem)
      .build();
    
    allegroApi_.store(toDoObject);
  }
```

The update routine creates a new ToDoItem.Builder, passing the existing item as a parameter, which initialises
all fields of the builder to the values in the current object. It then updates the description and
builds a new ToDo object.

The actual update is performed by an **ApplicationObjectUpdater** which uses the existing (encrypted)
StoredApplicationObject to identify the object to be updated and takes the new payload. The new StoredApplicationObject
is then stored in the Object Store.


When we run this program we see the existing StoredApplicationObject and the updated payload.

```
Stored:  {
  "_type":"com.symphony.s2.model.object.StoredApplicationObject",
  "_version":"1.0",
  "cipherSuiteId":"RSA2048_AES256",
  "createdDate":"2020-06-09T10:08:19.858Z",
  "encryptedPayload":"FkSqD5xCqpKMexH1nZw0pkGyuBMsxBdbhdo+yWwFdt1hCbheoqbryndChFUMkp0AWwGDpmftXotm7Vl+qLSAlsAVRbyVDMBUPGBNvFCgkKQ7wwDm8FeNQ9Uuw5O7aFxkmzKt602PMmJVKNeC9vh6EXT/fC3mtRCRuZPGia/2JttPUF+y3kQxEUNbE0wnTGRCfxrb84GskKq0f7uj36G6g52Tm/EL8GxC28juBb6xVBQXUXcZd7YfF2bqu8oIBQSlWkvkhOCC6JdOQ/9AvoZn3OEUowuwkOmdaX3jwNWO2MCrSqjmMEGO76XvyE6CgM67Wa+cVB8g51LTfh2z1A2sWco9C5/LM60ANSAr94n8yu3Yo3u/Vw2yeeYOpqdJ5qj2nLjh3Agnz6gN5KbgiBkW+RbbSXxyRc2nJs129X8MXeSTHEZ7Lmy2WZxtIdB6FEbinkcOcFX3004bz6ObkIrl43DXcN+VdRdz10S5Hmw=",
  "hashType":1,
  "header":{
    "_type":"com.symphony.s2.model.calendar.ToDoHeader",
    "_version":"1.0",
    "affectedUsers":[
      351775001412007
    ],
    "effectiveDate":"2020-06-09T10:08:19.112Z",
    "requestingUser":351775001412007
  },
  "owner":351775001412007,
  "partitionHash":"j/vNnRtcln7uKIixRBWuqCjimkm5VxO4WLYKJVUT00oBAQ==",
  "rotationId":0,
  "sortKey":"2020-06-09T10:08:19.092Z",
  "threadId":"+ZNefHakKm9vK7WREHHLEn///pEOR60jdA=="
}

About to update item {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated at 2020-06-09T10:39:00.311Z, item 3",
  "due":"2020-06-09T10:08:19.092Z",
  "timeTaken":"333.33333333333331438552704639732837677001953125",
  "title":"An example TODO Item"
}
```

If we run the List Items Example again now, we will see that the Partition now contains the updated version of 
each ToDoItem .
 

