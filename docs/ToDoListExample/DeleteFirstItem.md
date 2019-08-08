---
nav_order: 50
parent: ToDo List Example
---
# Delete First Item

The Delete First Item example logically deletes the first item on the current sequence.

This program is almost identical to the [List Items Example](ListItems.md), except for the logic in the
handler lambda in the call to fetchSequence:

```java    
    allegroApi_.fetchSequence(new FetchSequenceRequest()
          .withMaxItems(1)
          .withSequenceHash(currentSequence.getBaseHash())
        ,
        (item) ->
        {
          System.out.println(item);
          
          allegroApi_.delete(item, DeletionType.LOGICAL);
        });
```

In this case we pass an item limit of 1 to select only the first item, and in the consumer lambda we call
the delete method with the LOGICAL delete type.

The logical delete operation actually updates the object with a new version which is an instance of
DeletedObject. This object acts as a "tombstone" which indicates that the object should be treated as 
having been deleted.

If we run the List Items Example again now we will see that the absolute sequence containse all thress versions
of our ToDoItem (the original version, the updated version and the delete marker) whereas the current sequence
is empty. The logically deleted object has been removed from the current sequence entirely.

```



```

## Use Cases for Deleted Objects
As we have seen, a deleted object is removed from a current sequence, but appears as an additional version which
is a DeletedObject on an absolute sequence.

If we attempt to read the deleted object with an fetchAbsolute giving the absolute hash of the DeletedObject marker
then the DeletedObject will be returned. The caller would need to understand the meaning of the DeletedObject.
Note that the DeletedObject is a signed object and represents evidence of when, and by whom, the object was
deleted.

If we attempt to read the deleted object with a fetchCurrent giving the baseHash, then the server will respond 
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
