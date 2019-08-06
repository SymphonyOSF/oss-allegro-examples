---
nav_order: 20
parent: GettingStarted
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
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] Building oss-allegro-examples 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:3.0.0:clean (default-clean) @ oss-allegro-examples ---
[INFO] 
[INFO] --- maven-remote-resources-plugin:1.5:process (process-resource-bundles) @ oss-allegro-examples ---
```

Many lines of log output not shown

```sh
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] oss-allegro-examples ............................... SUCCESS [  2.890 s]
[INFO] hello-world ........................................ SUCCESS [  2.327 s]
[INFO] get-message ........................................ SUCCESS [  0.911 s]
[INFO] calendar ........................................... SUCCESS [  1.018 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 7.348 s
[INFO] Finished at: 2019-08-06T08:52:11-07:00
[INFO] Final Memory: 27M/120M
[INFO] ------------------------------------------------------------------------
$ 
```