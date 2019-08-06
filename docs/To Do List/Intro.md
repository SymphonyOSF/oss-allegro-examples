---
nav_order: 200
---
# ToDo List Example
The ToDo List Example is a small set of programs which demonstrate how to store and retrieve a small collection
of data objects. In this case we will manage a list of ToDo items, each of which is represented by a JSON
data structure defined as a Canon Schema.

The schema for a ToDo item looks like this:
```
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

Each ToDo Item is stored as an encrypted object in the Symphony Object Store. All objects in the Object Store are
encrypted and immutable, but we can create new versions of an object which can replace the previous version when
we want to update an item.

Objects in the Object Store are identified by the hash of their contents, which is a short 

When a new version of an object is created the old version continues to exist