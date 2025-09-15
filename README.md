# console-master

## Demo ausführen

```shell
cd /Users/hummel/sources/mhus/console-master && mvn compile exec:java -Dexec.mainClass="com.consolemaster.BorderLayoutDemo" -pl demo
```

## Demo Debuggen

```shell
cd /Users/hummel/sources/mhus/console-master && mvn clean install
cd /Users/hummel/sources/mhus/console-master && MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" mvn compile exec:java -pl demo -Dexec.mainClass="com.consolemaster.MouseDemo"
```