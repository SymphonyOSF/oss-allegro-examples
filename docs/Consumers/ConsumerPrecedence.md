---
nav_order: 10
parent: Consumers and Adaptors
---
# Consumers, Types and Precedence

When calling any method which takes consumers, you can register any number of consumers and adaptors.
Each of these is registered against a specific type (Java Class). When an object is processed, Allegro
tries to find the best available consumer or adaptor to handle the object.

To start with, if there is a consumer registered against the exact type of the received object, then it 
is selected.

If this is not the case then the registered consumers are examined on order of group precedence
as follows:

- Subclasses of IReceivedChatMessage - decrypted Symphony chat application messages.
- Subclasses of IApplicationObject - application level objects with Canon schemas.
- Other types.

Within each of these groups, the consumer or adaptor which has the most specific match to the received object
is selected. For example, if there are handlers for
- Object
- String
- Number
- Integer

Then 
- a received object of type Integer would be passed to the Integer consumer.
- a received object of type Double would be passed to the Number consumer.
- a received object of type String would be passed to the String consumer.
- a received object of type StoredApplicationObject would be passed to the Object consumer.

In cases where there is more than one consumer with the same "type distance" from a received object,
then the order in which the consumers are registered determines which will be used, with earlier registered
consumers being selected in preference to later registered ones.

It is perfectly possible to register Consumers and Adaptors in the same request. An Adaptor is just a Consumer
which contains a switch statement to route different sub-types to various __onXXX()__ methods.
The __getPayloadType()__ method of an Adaptor provides the type it will be registered under.
