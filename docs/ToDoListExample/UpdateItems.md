---
nav_order: 40
parent: ToDo List Example
---
# Update Items

The Update Items example updates all items in the current sequence to have the current time as their
due date.

This program is almost identical to the [List Items Example](ListItems.md), except for the logic in the
handler lambda in the call to fetchSequence:

```java  
    allegroApi_.fetchSequence(new FetchSequenceRequest()
          .withMaxItems(10)
          .withSequenceHash(currentSequence.getBaseHash())
        ,
        (item) ->
        {
          System.out.println(item);
          
          IEntity payload = allegroApi_.open(item);
          
          if(payload instanceof IToDoItem)
            update((IToDoItem)payload);
          
        });
```

In the consumer lambda we open the item to decrypt it and then call an update routine if the item is a
ToDo item (which of course it will be).


```java 
  private void update(IToDoItem item)
  {
    IToDoItem toDoItem = new ToDoItem.Builder(item)
        .withDue(Instant.now())
        .withDescription("Updated to Instant.now() so we are still late!")
        .build();
      
    System.out.println("About to update item " + toDoItem);
    
    IFundamentalObject toDoObject = allegroApi_.newApplicationObjectUpdater(item)
        .withPayload(toDoItem)
      .build();
    
    allegroApi_.store(toDoObject);
  }
```

The update routine creates a new ToDoItem.Builder, passing the existing item as a parameter, which initialises
all fields of the builder to the values in the current object. It then updatesthe due date and description and
builds a new ToDo object.

The actual update is performed by an ApplicationObjectUpdater which uses the existing (unencrypted)
FundamentalObject to identify the object to be updated and takes the new payload. The new FundamentalObject
is then stored in the Object Store.


When we run this program we see the sequence meta data, the existing FundamentalObject and the updated payload.

```
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

{
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

About to update item {
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Updated to Instant.now() so we are still late!",
  "due":"2019-08-07T23:20:53.972Z",
  "title":"An example TODO Item"
}
```

If we run the List Items Example again now, we will see that the current sequence contains only the updated version of 
our ToDoItem but the the absolute sequence contains both versions.
 

