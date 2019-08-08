---
nav_order: 200
has_children: true
permalink: /docs/ToDoListExample
---
# ToDo List Example
The ToDo List Example is a small set of programs which demonstrate how to store and retrieve a small collection
of data objects. In this case we will manage a list of ToDo items, each of which is represented by a JSON
data structure defined as a Canon Schema.

The schema for a ToDo item looks like this:

```json
      "ToDoItem": {
        "description": "A TODO item.",
        "type": "object",
        "extends": "../../../../fundamental-model/src/main/canon/fundamental.json#/components/schemas/ApplicationObject",
        "facade": false,
        "properties": {
          "due": {
            "$ref": "../../../../fundamental-model/src/main/canon/fundamental.json#/components/schemas/Instant"
          },
          "title": {
            "type": "string"
          },
          "description": {
            "type": "string"
          }
        }
```

As you can see, a ToDo item consists of:

+ A Due date (An Instant is a timestamp with up to nanosecond resolution).
+ A title
+ A description

When serialised a ToDo item looks like this:

```
{
  "_type":"com.symphony.s2.model.calendar.ToDoItem",
  "_version":"1.0",
  "description":"Since we are creating this item with a due date of Instant.now() we are already late!",
  "due":"2019-08-07T23:12:52.645Z",
  "title":"An example TODO Item"
}
```

Each ToDo Item is stored as an encrypted object in the Symphony Object Store. All objects in the Object Store are
encrypted and immutable, but we can create new versions of an object which can replace the previous version when
we want to update an item.

See [The Symphony 2.0 Object Store](https://allegro.oss.symphony.com/ObjectStore.html) on the Allegro API site
for more details about the object model.

We shall create a sequence to hold a set of ToDo items, the examples create both an absolute sequence and a
current sequence so you can see the differences between them. In generat you would use an absolute sequence
for an audit trail and a current sequence for a data set.

## Set Up a Chat Room
In order to run these examples you need to create a chat room and add your bot user to the room.
Then click on the timestamp of any message in that room to display the __Message Status__ panel:

![Message Status Panel](./message-status.png)

Make a note of the ConversationId, this needs to be presented to the example programs as the Environment
Variable __ALLEGRO_THREAD_ID__.