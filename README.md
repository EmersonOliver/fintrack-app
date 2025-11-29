# fintrack-app

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/fintrack-app-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)

[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)


[![](https://mermaid.ink/img/pako:eNp9VNuO2jAQ_RXLT20VWEJAIZa6rSj7UK1UELBSVUWqTDyAVWKntrMsjfj32rlw2-z6xZnROTNzZsYpcCIZYII1_M1BJDDhdKNoGqtYIHtobqTI0xWoxpNRZXjCMyoM-kYVQ1SX94fZdLH00F1iv-8SBdTAx7c4c9AyVwk03MZuwy9APfMztDbbkHPIpOZGqsM5buN5q5LJ-GsRY3PIIMYkxowauqLaGsc2xpMG9T6jYbnYnfv7S3HENQhVnUGuS5fYBmQ5tUJSue1UtEFGopRmGSj0IAw3Jzk11rLOUkkFQRoEc8QZFTTZAnrdjLOnLnUyJogLK9LYy1LN6vdtoZNx5zrbRIJGP6RBy62Se_TwkkBmuBQtSS7EzRTYxoKrT4HOpE1qPxJp96kSeqvwOimqGwOsnMlpSZ7pjjPXXy7Wsr2CaoZEww4Sgz6htZIpyl2Q_RZUNZnvDH3-0tCfFo5wU4CLUm4_11VSp0Tuxbnw93WXCXVd9esmtKi_WqV5A58-oqJaKYZ0niSg9Trf7Q7H1uWqw5AitTi6AYJiLB9j7NWq7T7_nC2nfj_oMMbsSmMPbxRnmBiVg4dTUCl1Ji5cdPsMtpC6d4DcQ1B_YhwLx7GP5ZeUaUNTMt9sMVnTnbZWnrkJ1f-Zk1fZbbUtlbkwmASDUVhGwaTAL5jYgrqR3x_0o1EvGoa9wPfwwcKi7iAIh0Fv6IfDcOT3h0cP_ysT97qjcBDZ44eBH_RDPzr-B5dkrs4?type=png)](https://mermaid.live/edit#pako:eNp9VNuO2jAQ_RXLT20VWEJAIZa6rSj7UK1UELBSVUWqTDyAVWKntrMsjfj32rlw2-z6xZnROTNzZsYpcCIZYII1_M1BJDDhdKNoGqtYIHtobqTI0xWoxpNRZXjCMyoM-kYVQ1SX94fZdLH00F1iv-8SBdTAx7c4c9AyVwk03MZuwy9APfMztDbbkHPIpOZGqsM5buN5q5LJ-GsRY3PIIMYkxowauqLaGsc2xpMG9T6jYbnYnfv7S3HENQhVnUGuS5fYBmQ5tUJSue1UtEFGopRmGSj0IAw3Jzk11rLOUkkFQRoEc8QZFTTZAnrdjLOnLnUyJogLK9LYy1LN6vdtoZNx5zrbRIJGP6RBy62Se_TwkkBmuBQtSS7EzRTYxoKrT4HOpE1qPxJp96kSeqvwOimqGwOsnMlpSZ7pjjPXXy7Wsr2CaoZEww4Sgz6htZIpyl2Q_RZUNZnvDH3-0tCfFo5wU4CLUm4_11VSp0Tuxbnw93WXCXVd9esmtKi_WqV5A58-oqJaKYZ0niSg9Trf7Q7H1uWqw5AitTi6AYJiLB9j7NWq7T7_nC2nfj_oMMbsSmMPbxRnmBiVg4dTUCl1Ji5cdPsMtpC6d4DcQ1B_YhwLx7GP5ZeUaUNTMt9sMVnTnbZWnrkJ1f-Zk1fZbbUtlbkwmASDUVhGwaTAL5jYgrqR3x_0o1EvGoa9wPfwwcKi7iAIh0Fv6IfDcOT3h0cP_ysT97qjcBDZ44eBH_RDPzr-B5dkrs4)