---
nav_order: 800
has_children: true
permalink: /docs/Consumers
---
# Consumers and Adaptors
There are various methods in the Allegro API which produce objects of various types, including
__fetchRecentMessagesFromPod__,
__fetchFeedMessages__,
__fetchFeedObjects__ and
__fetchObjectVersions__.

These methods pass objects back to the API caller via Consumers. The structure of objects in the object store 
can be quite complex, objects are normally encrypted and SocialMessage objects (representing Symphony
chat messages) have individual attributes encrypted.

Allegro provides a rich vocabulary of typed Consumers which allows the caller to simply state what types
they wish to receive and allows the Allegro API to find the most specific Consumer for each object,
unwrapping and decrypting as necessary.

The JSON below shows an example of a StoredApplicationObject which contains a ToDoItem from the examples earlier.

```json
{
  "_type":"com.symphony.s2.model.object.StoredApplicationObject",
  "_version":"1.0",
  "baseHash":"KhMLZz664tqW21eJ3psAetpzrNVxpEUTgpepUNmbH78BAQ==",
  "cipherSuiteId":"RSA2048_AES256",
  "createdDate":"2020-06-09T12:12:48.005Z",
  "encryptedPayload":"ef/xt6LUXj3++7kKsSzlcTs4NEilDbKMVWV9DOvPCRMlcBIvAsU/i8g15I8HNOlDFNxspnmxtD+jQNxfC4qLzU4b9/BmBjJ++XnGvRX+dMNbg5y1ZlZH43RyYOJQJxlvC1gD9HmQiNiIw2wJD5uY2dZ71fA21W+twrWUx8Z7vxMuvG44NC5dI/I3JMjUKTq9avO9Vj8mF8YLJneMt7ATQJDyEfTzrVkx2IkkZrC5L1GvrUr5pd2fdR39eRwaz+hI2STrvTN97Fypj4hPfdnEJ96d9dliczEL7VK9OWC6P9wfJOxUFY8EUhBtXyxhQd+MsJhLOuH9zU8756KjNHlG82X+tlitR99v7n+RUmhLHudGSUvkVh2Ctjbx8InsvWry3zVKhoa99lT7fups3PimAFfmjFV8R9kn1M2lmP7vL/kYGHM=",
  "hashType":1,
  "header":{
    "_type":"com.symphony.s2.model.calendar.ToDoHeader",
    "_version":"1.0",
    "affectedUsers":[
      351775001412007
    ],
    "effectiveDate":"2020-06-01T13:32:52.605Z",
    "requestingUser":351775001412007
  },
  "owner":351775001412007,
  "partitionHash":"j/vNnRtcln7uKIixRBWuqCjimkm5VxO4WLYKJVUT00oBAQ==",
  "prevHash":"tAfGwP+jUBcGFWWfBgfapKlmqTDYKnMe88USKEsUo6cBAQ==",
  "prevSortKey":"2020-06-01T13:32:52.585Z",
  "rotationId":0,
  "sortKey":"2020-06-01T13:32:52.585Z",
  "threadId":"+ZNefHakKm9vK7WREHHLEn///pEOR60jdA=="
}
```

The JSON objects visible in the serialised form represent 
objects with an inheritance hierarchy which can also be used to process messages
at various levels.

In the example above, the nested types which could be used to process this message include:

+ IApplicationPayload - The base class of all payloads stored in the Object Store.
+ ISystemObject - The base class of system types in the Object Store.
+ IAbstractStoredApplicationObject - An object stored in the ObjectStore which could be a deleted object marker.
+ IStoredApplicationObject - An object stored in the ObjectStore which has a payload.
+ ICalendarModel - The super interface of all objects in the Calendar schema of the example programs.
+ IToDoItem - The actual application payload type.

In cases where a chat message is returned the types include:

+ ILiveCurrentMessage - The base class of all Symphony Chat application messages
+ ISocialMessage - The type of chat messages typed by users in the Symphony chat application, visible in the JSON as the Clob payload.
+ IReceivedChatMessage - the base class of decrypted received chat messages provided by Allegro.
+ IReceivedSocialMessage - A sub-class of IReceivedChatMessage representing a decrypted SocialMessage.

So as you can see, there is a good deal of complexity in the data structures, and the purpose of
Consumers and Adaptors is to make it easy for an Allegro API caller to get what they need with the minimum
of fuss.

To give you an idea of how simple this can be, take a look at the following example, which prints the decrypted
message content of chat messages (SocialMessage objects) in a conversation: 

```
    System.out.println("Fetch messages from pod...");
    allegroApi_.fetchRecentMessagesFromPod(
        new FetchRecentMessagesRequest.Builder()
          .withThreadId(threadId_)
          .withMaxItems(maxMessages_)
          .withConsumerManager(new ConsumerManager.Builder()
              .withConsumer(IReceivedChatMessage.class, (message, trace) ->
              {
                System.out.println("M " + message.getMessageId() + " " + message.getText());
              }
              )
              .withConsumer(IReceivedSocialMessage.class, (message, trace) ->
              {
                System.out.println("S " + message.getMessageId() + " " + message.getText() + " " + message.getMentions());
                System.out.println("S " + message.getPresentationML());
              }
              )
              .build()
          )
          .build()
        );
```

The **IReceivedSocialMessage** handler will be called with the decrypted payload of all SocialMessages (chat messages)
and the **IReceivedChatMessage** handler (IReceivedChatMessage is a super interface of IReceivedSocialMessage) will
be called with all other message types such as Maestro (meta-data) messages.

The Allegro Consumer infrastructure figures out which parts of the actual stored message are required, obtains the
necessary encryption keys, and decrypts the message payload automatically.

The authentication credentials given when creating the Allegro API client must represent a user who is a member
of the conversation of which the message is a part, otherwise the keys cannot be accessed. This is an example
of why it is sometimes desirable to provide multiple consumers at different levels. For example, if a 
**SocialMessage** consumer was provided in addition to the **ReceivedChatMessageAdaptor**, then it would be called
in the event that a message which cannot be decrypted is received.

If all else fails, the unprocessed serialised object will be passed, as a String, to the default consumer. If
no default consumer is passed, as in this example, then a default default consumer prints the message to the log
as a warning level message.

