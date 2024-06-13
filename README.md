# openjpa-spring-boot

provides a spring-boot starter with autoconfiguration for OpenJPA 4.0.x

## features
* includes the abandoned spring-framework JPA integration (OpenJpaVendorAdapter and OpenJpaDialect) from Spring 4.3.x
* autoconfiguration and customization options like spring-boot's hibernate integration
* default logging configuration for slf4j
* basic Tuple result support (custom projection with native queries)
* JPA 2.1 entity graph support

## usage 
add the dependency to openjpa-spring-boot-starter in your spring-boot app

```xml
<dependency>
    <groupId>io.github.mschieder</groupId>
    <artifactId>openjpa-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

add default spring-jpa or OpenJPA specific properties in your application.properties file:

```properties
logging.level.openjpa.jdbc.SQL=trace
spring.jpa.properties.openjpa.ConnectionFactoryProperties=PrettyPrint=true, PrettyPrintLineLength=80, PrintParameters=true
```

full project example: openjpa-spring-boot-sample-app

## building

```shell
mvn install
```
