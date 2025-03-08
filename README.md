# Allocs

```shell
java -agentpath:$ASYNC_PROFILER_LIB_PATH/libasyncProfiler.dylib=start,alloc=1,total,event=alloc,file=alloc.jfr -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:-UseTLAB -Xmx1G -Xms1G -XX:+AlwaysPreTouch -jar target/quarkus-app/quarkus-run.jar

java -cp $ASYNC_PROFILER_CONVERTER_PATH/async-profiler-converter-3.0.jar jfr2flame alloc.jfr --alloc --total --lines alloc.html
```

# CPU

```shell
java -agentpath:$ASYNC_PROFILER_LIB_PATH/libasyncProfiler.dylib=start,event=cpu,interval=1000000,file=cpu.jfr,file=cpu.jfr -Xmx1G -Xms1G -XX:+AlwaysPreTouch -jar target/quarkus-app/quarkus-run.jar

java -cp $ASYNC_PROFILER_CONVERTER_PATH/converter.jar jfr2flame cpu.jfr --classify cpu.html
```
