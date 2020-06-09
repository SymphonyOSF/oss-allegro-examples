---
nav_order: 20
parent: Getting Started
---
# Clone The Example Programs Repo and Build

Create a directory somewhere comvenient, we use __/tmp/allegro__ in the examples which follow but you probably
want to choose somewhere more permanent.

Change to the new directory and clone the git repo, then cd into the cloned directory and run
maven to build the examples. 

```sh
$ mkdir /tmp/allegro
$ cd /tmp/allegro
$ git clone https://github.com/SymphonyOSF/oss-allegro-examples.git
Cloning into 'oss-allegro-examples'...
remote: Enumerating objects: 138, done.
remote: Counting objects: 100% (138/138), done.
remote: Compressing objects: 100% (75/75), done.
remote: Total 138 (delta 53), reused 108 (delta 33), pack-reused 0
Receiving objects: 100% (138/138), 33.11 KiB | 8.28 MiB/s, done.
Resolving deltas: 100% (53/53), done.
$ cd oss-allegro-examples
$ mvn clean package
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO] 
[INFO] oss-allegro-examples
[INFO] hello-world
[INFO] get-message
[INFO] calendar
[INFO] json-objects
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] Building oss-allegro-examples 0.1.3-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:3.0.0:clean (default-clean) @ oss-allegro-examples ---
[INFO] 
[INFO] --- maven-remote-resources-plugin:1.5:process (process-resource-bundles) @ oss-allegro-examples ---
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] Building hello-world 0.1.3-SNAPSHOT
[INFO] ------------------------------------------------------------------------

```

Many lines of log output not shown

```sh
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] oss-allegro-examples ............................... SUCCESS [  0.849 s]
[INFO] hello-world ........................................ SUCCESS [  3.183 s]
[INFO] get-message ........................................ SUCCESS [  1.080 s]
[INFO] calendar ........................................... SUCCESS [ 30.745 s]
[INFO] json-objects ....................................... SUCCESS [  0.656 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 36.721 s
[INFO] Finished at: 2020-06-09T07:43:37+01:00
[INFO] Final Memory: 70M/260M
[INFO] ------------------------------------------------------------------------
$ 
```