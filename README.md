# console-master

```shell
cd /Users/hummel/sources/mhus/console-master
```
## Demo ausführen

```shell
mvn clean install exec:java -Dexec.mainClass="com.consolemaster.demo.BorderLayoutDemo" -pl demo
```

## Demo Debuggen

```shell
mvn clean install && MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" mvn compile exec:java -pl demo -Dexec.mainClass="com.consolemaster.demo.Graphic3DDemo"
```

Wait for debugger to attach

```shell
mvn clean install && MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005" mvn compile exec:java -pl demo -Dexec.mainClass="com.consolemaster.demo.Graphic3DDemo"
```