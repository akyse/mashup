# mashup

### Prerequisites ##
- Java 8
- Maven

### Commands

#### Build executable jar 
```sh
$ mvn clean install
```

#### Start embed app
```sh
$ mvn spring-boot:run
```

### Load test
```sh
$ siege -c 20 -f siege_urls
```

### App URLs
[Swagger UI](http://localhost:8080/swagger-ui.html)

[Hystrix Dashboard](http://localhost:8080/hystrix/monitor?stream=http%3A%2F%2Flocalhost%3A8080%2Fhystrix.stream)

