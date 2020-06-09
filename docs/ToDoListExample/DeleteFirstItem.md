---
nav_order: 50
parent: ToDo List Example
---
# Delete First Item

The Delete First Item example logically deletes the first item on the current sequence.

This program is almost identical to the [List Items Example](ListItems.md), except for the logic in the
Consumer lambda:

```java   
    allegroApi_.fetchPartitionObjects(new FetchPartitionObjectsRequest.Builder()
        .withQuery(new PartitionQuery.Builder()
            .withName(CalendarApp.PARTITION_NAME)
            .withMaxItems(1)
            .build()
            )
        .withConsumerManager(new ConsumerManager.Builder()
            .withConsumer(IToDoItem.class, (item, trace) ->
            {
              System.out.println("Header:  " + item.getStoredApplicationObject().getHeader());
              System.out.println("Payload: " + item);
              System.out.println("Stored:  " + item.getStoredApplicationObject());
              
              allegroApi_.delete(item.getStoredApplicationObject(), DeletionType.LOGICAL);
            })
            .build()
            )
        .build()
        );
```

In this case we pass an item limit of 1 to select only the first item, and in the consumer lambda we call
the **delete** method with the LOGICAL delete type.

The logical delete operation actually updates the object with a new version which is an instance of
DeletedObject. This object acts as a "tombstone" which indicates that the object should be treated as 
having been deleted.

When we run this program we see the details of the object which is then deleted:

```
Header:  {
  "_type":"com.symphony.s2.model.calendar.ToDoHeader",
  "_version":"1.0",
  "affectedUsers":[
    351775001412007
  ],
  "effectiveDate":"2020-05-13T14:47:15.341Z",
  "requestingUser":351775001412007
}

Payload: {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated at 2020-06-09T10:38:59.517Z, item 1",
  "due":"2020-05-13T14:47:15.309Z",
  "timeTaken":"333.33333333333331438552704639732837677001953125",
  "title":"An example TODO Item"
}

Stored:  {
  "_type":"com.symphony.s2.model.object.StoredApplicationObject",
  "_version":"1.0",
  "baseHash":"FgteH9X26U6++Ke1xQ+SaD/+M6/na0HhZqo4Up2mkGYBAQ==",
  "cipherSuiteId":"RSA2048_AES256",
  "createdDate":"2020-06-09T10:38:59.629Z",
  "encryptedPayload":"2sv/9HmPFfmrE7dpWkfdR4EBerl51yuWhFkt2F0u0Hrv5abqwbvEV283+je0FgxH3NrucYp2xPnrzuCiA4R4hRh+Jy78UoqV2AYufyfN7HBH8oXAj5+Xh7ibD2kia3riwh00kVEIwL9szi3vmvN6SFptyhvIaaSlwU4aM8Mfkz0sjnXL0jwQEqClZsWr0LEeKQTKXqcSuS4vqGoYQTOgAmeGQ1TTS3lpAs5cI5ylF5sHnlni0+7BGsK3TyH5yazyMRds88NcGRJL/Vp85AOjpnkA19yK27WamRbG6+o9m0vlmBzU1e8/wvzWngIK51fuAOwTu6ZrrCL+eh2H+J7mFVCHk8XAEZ7CedSn23lqp8Aq6owSdduTPfMPoRhc2i7+wXbRqZSWv7YaJMf3eXDJyZTtidIV4Rt7es1T8ey2xaUONPQ=",
  "hashType":1,
  "header":{
    "_type":"com.symphony.s2.model.calendar.ToDoHeader",
    "_version":"1.0",
    "affectedUsers":[
      351775001412007
    ],
    "effectiveDate":"2020-05-13T14:47:15.341Z",
    "requestingUser":351775001412007
  },
  "owner":351775001412007,
  "partitionHash":"j/vNnRtcln7uKIixRBWuqCjimkm5VxO4WLYKJVUT00oBAQ==",
  "prevHash":"FgteH9X26U6++Ke1xQ+SaD/+M6/na0HhZqo4Up2mkGYBAQ==",
  "prevSortKey":"2020-05-13T14:47:15.309Z",
  "rotationId":0,
  "sortKey":"2020-05-13T14:47:15.309Z",
  "threadId":"+ZNefHakKm9vK7WREHHLEn///pEOR60jdA=="
}
```


If we run the List Items Example again now we will see that the object has been deleted from the Partition.
This is, however, a _logical_ delete and if we run the ListObjectVersions example, passing the **baseHash** of
this object, then we will see the original version, the updated version and the delete marker.


## Use Cases for Deleted Objects
As we have seen, a deleted object is removed from a Partition, but exists as an additional version which
is a DeletedObject.

If we attempt to read the deleted object with an **fetchAbsolute** giving the absolute hash of the DeletedObject marker
then the DeletedObject will be returned. The caller would need to understand the meaning of the DeletedObject.
Note that the DeletedObject is a signed object and represents evidence of when, and by whom, the object was
deleted.

If we attempt to read the deleted object with a **fetchCurrent** giving the baseHash, then the server will respond 
with an HTTP __410 GONE__ response. The canon generated client stubs for HTTP methods will throw a 
__DeletedException__ when this response is received. This is a sub-class of __NotFoundException__ (which
is thrown when an HTTP __404 NOT FOUND__ response is received.

This means that for callers using Canon generated client stubs, if they choose to catch __DeletedException__ 
explicitly they can detect the difference between a deleted object and one which never existed, but if they 
simply catch __NotFoundException__ then they will see the same effect in both cases. Callers have the option
to deal with both cases but are therefore not required to do so.

## Physical Deletion
Chat messages, and other objects created by the Symphony chat application, are retained for a fixed period (which may
be determined by the owner of the pod). The Allegro API allows for the creation of potentially large volumes
of data, and it may be that the owner of this data decides that not only do they wish to logically delete the 
data but they wish to avoid future storage charges for that data immediately.

In order to facilitate this, Allegro provides a PHYSICAL delete mode. When an object is physically deleted it
is removed from all sequences (absolute as well as current) and it is physically removed from the indexes
making it inaccessible by most means. The purpose of the physical deletion is to avoid storage costs however
and it does not guarantee that all copies of the data will be removed or that the object will be inaccessible.

The process of physical deletion involves the client sending a signed DeletedObject to the server, which will
be retained for some period as evidence that the object was indeed removed deliberately.
