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
[INFO] Building hello-world 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ hello-world ---
21:16:18.291 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - AllegroApi constructor start
21:16:18.733 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - sbe auth....
21:16:19.516 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch podInfo_....
21:16:19.629 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - fetch 
21:16:19.629 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - keymanager auth....
21:16:20.081 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - principalHash_ = rvnLsBmjsnGaKUW2HQWfERkM+Fjv5eNvE2hePJA/vFkBAQ==
21:16:20.081 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - userId_ = 11476152617609
21:16:20.081 [com.symphony.s2.allegro.examples.helloworld.HelloWorld.main()] INFO  com.symphony.oss.allegro.api.AllegroApi - allegroApi constructor done.
{
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
  "displayName":"Allegro Bot",
  "id":11476152617609,
  "roles":[
    "INDIVIDUAL"
  ],
  "username":"allegroBot"
}

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 4.643 s
[INFO] Finished at: 2019-08-06T21:16:20-07:00
[INFO] Final Memory: 27M/577M
[INFO] ------------------------------------------------------------------------
$ 
 
```

This will build the example (if necessary) and then run it. The program picks up the parameters it needs from
the environment variables we set and executes.

The HelloWorld example simply authenticates and prints out the session information. The examples all use
slf4j and log4j as their logging system so we also see some informational messages about what the API classes
are doing.

For an explanation of what this example does, see [Hello World Example](../HelloWorld.md).