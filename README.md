# Counter service v0.0.1

## About
Used H2 database, Ktor with Kotlin, Exposed with love ♡

## Build and run service

In project root with Gradle >= 8.4 installed:

1) run 
```shell
./gradlew build
```
2) run
```shell
./gradlew run
```

## Test service
1) Using curl, Postman, Insomnia, HTTP Intellij IDEA or any other client
2) Run tests under test directory:
```shell
./gradlew test
```

## Service security
1) Access service with https://localhost:88 for TLS connection with self issued certificate
2) Access service with http://localhost:8080 for insecure connection

## Service OpenAPI
Access service Swagger documentation by https://localhost:88/swagger or http://localhost:8080/swagger