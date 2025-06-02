# How to run?

## Install Asyc-profiler

Clone Async-profiler repository:

```shell
git clone https://github.com/async-profiler/async-profiler.git
```

Run `make` to build the Async-profiler library:

```shell
make
```

Run `maven` to build the Async-profile converter:

```shell
mvn package -f pom-converter.xml
```

Add Environment Variables to the Async-profiler lib path and converter path:

```shell
export ASYNC_PROFILER_LIB_PATH=[REPO_CLONE_PATH]/build/lib/
export ASYNC_PROFILER_CONVERTER_PATH=[REPO_CLONE_PATH]/target
```

## Async-profiler as a Java Agent

### Measure Memory Allocs    

```shell
java -agentpath:$ASYNC_PROFILER_LIB_PATH/libasyncProfiler.dylib=start,alloc=1,total,event=alloc,file=alloc.jfr -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:-UseTLAB -Xmx1G -Xms1G -XX:+AlwaysPreTouch -jar target/quarkus-app/quarkus-run.jar

java -cp $ASYNC_PROFILER_CONVERTER_PATH/async-profiler-converter-3.0.jar jfr2flame alloc.jfr --alloc --total --lines alloc.html
```

### Measure CPU

```shell
java -agentpath:$ASYNC_PROFILER_LIB_PATH/libasyncProfiler.dylib=start,event=cpu,interval=1000000,file=cpu.jfr,file=cpu.jfr -Xmx1G -Xms1G -XX:+AlwaysPreTouch -jar target/quarkus-app/quarkus-run.jar

java -cp $ASYNC_PROFILER_CONVERTER_PATH/converter.jar jfr2flame cpu.jfr --classify cpu.html
```

## Run as Docker

```shell
 docker run -i --rm -p 8080:8080 -e JAVA_OPTS="-agentpath:/home/jboss/async-profiler/build/lib/libasyncProfiler.so=start,alloc=1,total,event=alloc,file=/home/jboss/profiler-data/alloc.jfr  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:-UseTLAB -Xmx1G -Xms1G -XX:+AlwaysPreTouch" -v ./target/profiler-data:/home/jboss/profiler-data --privileged radcortez/write-efficient-code
```
