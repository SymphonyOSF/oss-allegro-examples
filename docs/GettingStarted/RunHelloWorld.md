---
nav_order: 40
parent: Getting Started
---
# Run The Hello World Example

Each example program is a stand alone Java Application, which can be launched in a number of ways. You probably
want to set up Eclipse or another IDE to experiment with the API and examples, but as a first step run the
__HelloWorld__ example from Maven like this:

```sh
$ cd hello-world
$ mvn exec:java
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] Building hello-world 0.1.3-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ hello-world ---
{
  "accountType":"SYSTEM",
  "avatars":[
    {
      "size":"original",
      "url":"../avatars/static/150/default.png"
    },
    {
      "size":"small",
      "url":"../avatars/static/50/default.png"
    }
  ],
  "company":"Symphony",
  "displayName":"Allegro Bot",
  "emailAddress":"allegroBit@symphony.com",
  "id":351775001412007,
  "roles":[
    "INDIVIDUAL",
    "USER_PROVISIONING"
  ],
  "username":"allegroBot"
}

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.866 s
[INFO] Finished at: 2020-06-09T07:48:26+01:00
[INFO] Final Memory: 23M/640M
[INFO] ------------------------------------------------------------------------
$ 
 
```

This will build the example (if necessary) and then run it. The program picks up the parameters it needs from
the environment variables we set and executes.

The HelloWorld example simply authenticates and prints out the session information. The examples all use
slf4j and log4j as their logging system so we also see some informational messages about what the API classes
are doing.

For an explanation of what this example does, see [Hello World Example](../HelloWorld.md).