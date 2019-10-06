---
nav_order: 800
has_children: true
permalink: /docs/Consumers
---
# Consumers and Adaptors
There are various methods in the Allegro API which produce objects of various types from the Object Store, including
__fetchRecentMessages__,
__fetchMessages__,
__fetchSequence__,
__fetchObjectVersions__ and
__createFeedSubscriber__.

These methods pass objects back to the API caller via Consumers. The structure of objects in the object store 
can be quite complex and objects are normally encrypted, and SocialMessage objects (representing Symphony
chat messages) have individual attributes encrypted.

Allegro provides a rich vocabulary of typed Consumers which allows the caller to simply state what types
they wish to receive and allows the Allegro API to find the most specific Consumer for each object,
unwrapping and decrypting as necessary.

The JSON below shows an example of a Feed Notification message which contains a Symphony chat message 
(SocialMessage).

```json
{
  "_type":"com.symphony.s2.model.fundamental.Notification",
  "_version":"1.0",
  "contextHash":"Bek1dWzL0C3NXy1qOpTiEyL6QKUaJG68bAik3U0zTDMBAQ==",
  "createdDate":"2019-10-06T16:25:29.226Z",
  "distributionList":[],
  "final":true,
  "initialVersion":true,
  "legacyIdHash":"IOpG0gPsRowq6XFlrxyze7jNKei/ZlwtqPfNplOovi4BAQ==",
  "notificationDate":"2019-10-06T16:25:29.742212Z",
  "payload":{
    "_type":"com.symphony.s2.model.fundamental.FundamentalObject",
    "_version":"1.0",
    "hashType":1,
    "payload":{
      "_type":"com.symphony.s2.model.fundamental.Clob",
      "_version":"1.0",
      "baseHash":"IOpG0gPsRowq6XFlrxyze7jNKei/ZlwtqPfNplOovi4BAQ==",
      "createdDate":"2019-10-06T16:25:29.226Z",
      "payload":{
        "_type":"com.symphony.s2.model.chat.SocialMessage",
        "chatType":"CHATROOM",
        "clientId":"web-597fc14f-d410-4482-9eb0-7792f82fab0a",
        "clientVersionInfo":"DESKTOP-40.0.0-11395-MacOSX-10.14.6-Chrome-77.0.3865.90",
        "copyDisabled":false,
        "encrypted":true,
        "encryptedEntities":"AgAAAKYAAAAAAAAAAESpfqvBeCvHE8ebbWj5tNMFn/LlmJoMkWsY/YKwYTHgBQZukM9k7oLhxwegNOk3VMnS",
        "encryptedMedia":"AgAAAKYAAAAAAAAAAESpfqvBeCvHE8ebbWj5tNMFn/LlmJoMkWsY/YKwYTHgBVlU0Xptzqed/k8kijdb2WTgF8he2rpPL4XoMkc49JGyKFEY/Fim3nPoL9Ugm9KNUg==",
        "enforceExpressionFiltering":true,
        "enforceExpressionFilteringMinEFV3":false,
        "entities":{
        },
        "entityJSON":"AgAAAKYAAAAAAAAAAESpfqvBeCvHE8ebbWj5tNMFn/LlmJoMkWsY/YKwYTHgBQZukM9k7oLhxwegNOk3VMnS",
        "externalOrigination":false,
        "format":"com.symphony.messageml.v2",
        "from":{
          "company":"pod166",
          "emailAddress":"bruce.skingle@symphony.com",
          "firstName":"Bruce",
          "id":11407433176972,
          "imageUrl":"../avatars/static/150/default.png",
          "imageUrlSmall":"../avatars/static/50/default.png",
          "prettyName":"Bruce Skingle",
          "principalBaseHash":"5ypkFskUVGcFrg4oTp/FxAduAhod13JmP4c5canP1bQBAQ==",
          "surname":"Skingle",
          "userType":"lc",
          "username":"bruce"
        },
        "fromPod":166,
        "ignoreDLPWarning":false,
        "ingestionDate":1570379129226,
        "isBlast":false,
        "isChime":false,
        "isCopyDisabled":false,
        "isFormReply":false,
        "isImported":false,
        "isPrivate":false,
        "lastState":"8vCFowbRuxX3/jigv1xxZ3///pL/Spx8bw==",
        "malwareScanRequired":false,
        "messageId":"HouCwSzskPW/QpcAW795q3///pJeHB51bQ==",
        "msgFeatures":"7",
        "objectHash":"ZOvXfAvAKlvo4BwoO7j4kRUoNrGAilzvbtDBUFZZPqYBAQ==",
        "platform":"DESKTOP",
        "podDistribution":[
          166
        ],
        "presentationML":"AgAAAKYAAAAAAAAAAESpfqvBeCvHE8ebbWj5tNMFn/LlmJoMkWsY/YKwYTHgQh9eyDR9yr2I8RMQpXYYwDymLttv0K9Eed6+EXsZ9//tL140wpDaHl3/pzlj38FbupTXClgV8/hun1bZAedPohtqZJCLcGrTI2S+flxlPnlo48GEoZLxWArnbhSSkCTvDAdT",
        "receiptSuppressed":true,
        "reloadPolicies":false,
        "schemaVersion":1,
        "semVersion":"1.55.3-SNAPSHOT",
        "sendingApp":"lc",
        "streamAbsoluteHash":"xEPe8w+sn//d+oefjDwcGZgrrVvPU5KJ/Jv4rooM0wYBAQ==",
        "streamBaseHash":"Bek1dWzL0C3NXy1qOpTiEyL6QKUaJG68bAik3U0zTDMBAQ==",
        "suppressed":false,
        "text":"AgAAAKYAAAAAAAAAAESpfqvBeCvHE8ebbWj5tNMFn/LlmJoMkWsY/YKwYTHgSWGT4SX6Hi63SdicKwgZCCg=",
        "threadId":"LfLSIzX5F21evqvJGxB33X///pvh3HtVdA==",
        "tokenIds":[
          "7PTC92Og2YoC3MP0w3kaZ4OAEI3Sd684YvsVQ1Sth5U=",
          "TLMH+qEQPJ1jtIBlhs1XcSACID2P+tIfA0Mjg37dsjc="
        ],
        "traceId":"j23NKy",
        "uniqueMessageId":0,
        "version":"SOCIALMESSAGE"
      },
      "payloadType":"com.symphony.s2.model.chat.SocialMessage",
      "podId":166,
      "prevHash":"IOpG0gPsRowq6XFlrxyze7jNKei/ZlwtqPfNplOovi4BAQ==",
      "purgeDate":"2026-10-04T16:25:29.226Z",
      "sequences":{
        "_type":"com.symphony.s2.model.fundamental.SequenceHashes",
        "_version":"1.0",
        "absolute":[],
        "current":[
          "uENm7khaWom4FsAqzwU6lZSnGRUPrnPvXBMrfI5Zcw0BAQ=="
        ],
        "hashCurrent":[]
      }
    }
  },
  "payloadBaseHash":"IOpG0gPsRowq6XFlrxyze7jNKei/ZlwtqPfNplOovi4BAQ==",
  "payloadHash":"hymZH61mySm2nFqq4ThGWDJ9+o7BtgXZDQpxJCg0dMMBAQ==",
  "payloadId":{
    "_type":"com.symphony.s2.model.chat.MessageIdObject",
    "_version":"1.0",
    "hashType":1,
    "messageId":"HouCwSzskPW/QpcAW795q3///pJeHB51bQ==",
    "messageType":"com.symphony.s2.model.chat.SocialMessage",
    "podId":166
  },
  "payloadType":"com.symphony.s2.model.chat.SocialMessage",
  "podId":166,
  "purgeDate":"2026-10-04T16:25:29.226Z",
  "sequence":0,
  "sequences":{
    "_type":"com.symphony.s2.model.fundamental.SequenceHashes",
    "_version":"1.0",
    "absolute":[],
    "current":[
      "uENm7khaWom4FsAqzwU6lZSnGRUPrnPvXBMrfI5Zcw0BAQ=="
    ],
    "hashCurrent":[]
  },
  "subscriptionHash":"+g4aZcOJutwziyPDvzh1y6wZAB0iKZIcLD01oz6TGdsBAQ==",
  "traceContextHash":"0lEp9uYXQEjiripVCpC9xmIGrgcBpW8eesH70YmQba0BAQ=="
}
```

As you can see, this object contains a deeply nested structure which callers may wish to
process at multiple levels. The JSON objects visible in the serialised form represent 
objects with an inheritance hierarchy which can also be used to process messages
at various levels.

In the example above, the nested types which could be used to process this message include:

+ INotification - An ObjectStore notification object. This is the message delivered on a Feed.
+ IFundamentalObject - The fundamental unit of storage in the ObjectStore.
+ IFundamentalPayload - The base class of all payloads of Fundamental Objects.
+ IObjectPayload - A sub-class of IFundamentalPayload.
+ IVersionedObject - A sub-class of IObjectPayload.
+ IClob - A sub-class of IVersionedObject, visible in the JSON as the actual payload of the Fundamental Object.
+ IApplicationObject - The base class of all payloads of Clobs and Blobs (native encrypted objects in the Object Store).
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
  public void run()
  {
    allegroApi_ = new AllegroApi.Builder()
      .withPodUrl(podUrl_)
      .withObjectStoreUrl(objectStoreUrl_)
      .withUserName(serviceAccount_)
      .withRsaPemCredentialFile(credentialFile_)
      .build();
    
    allegroApi_.fetchMessages(
        new FetchMessagesRequest()
          .withThreadId(threadId_)
          .withMaxMessages(maxMessages_)
          .withScanForwards(true)
          .withConsumer(new Adaptor())
        );
  }
  
  class Adaptor extends ReceivedChatMessageAdaptor
  {
    @Override
    public void onSocialMessage(IReceivedSocialMessage message, ITraceContext trace)
    {
      System.out.println(message.getClass().getSimpleName() + ": " + message.getMessageId() + " " + message.getText());
    }
  }
```

The instance of __ReceivedChatMessageAdaptor__ provides default, NO OP, implementations of handler methods for each
sub-type of Symphony chat messages (LiveCurrentMessage). The example above overrides only the __OnSocialMessage__
method, which prints the message ID and message text as plain ASCII. Other message types will be ignored.

The Allegro Consumer infrastructure figures out which parts of the actual stored message are required, obtains the
necessary encryption keys, and decrypts the message payload automatically.

The authentication credentials given when creating the Allegro API client must represent a user who is a member
of the conversation of which the message is a part, otherwise the keys cannot be accessed. This is an example
of why it is sometimes desirable to provide multiple consumers at different levels. For example, if a 
SocialMessage consumer was provided in addition to the ReceivedChatMessageAdaptor, then it would be called
in the event that a message which cannot be decrypted is received.

If all else fails, the unprocessed serialised object will be passed, as a String, to the default consumer. If
no default consumer is passed, as in this example, then a default default consumer prints the message to the log
as a warning level message.

