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


[![](https://mermaid.ink/img/pako:eNp9VG1v2jAQ_iuWP21TSkNCBrG0bmL0w1RpIMqkaYo0GecAa8TObKeURfz32XnhrWn9xbnT89zdc3dOiZlMAROs4W8BgsGE07WiWaISgeyhhZGiyJagWk9OleGM51QY9JWqFFFd3e9m08eFh26Z_b5lCqiB969x5qBloRi03Nbuwj-CeuInaGN2IeeQS82NVPtT3NbzWiWT8ZcywWafQ4JJglNq6JJqaxy6GD80qLcZLcvFvrm7OxdHXINQ3RnkunSObUGW0ygktdtORRtkJMponoNC98Jwc5TTYC3rJJXUEKRBpI44o4KyDaCXzTh5mlInY4K4sCKNvSzVLH9fFzoZ31xmm0jQ6Ls0aLFRcofunxnkhkvRkeRM3EyBbSy4-hToXNqk9oNJu0-10GuFl0lR0xhIq5kcl-SJbnnq-svFSnZXUM-QaNgCM-gDWimZocIF2W1A1ZP5lqJPn1t6TbgqwDmr7ee6TuqUyJ04Ff627iqhbqp-2YQO9RerNG_h0wdU1iuVIl0wBlqviu12f-hcriYMKTOLo2sgKMHyIcFeo9ru88_ZYtoPwps0Te1KYw-vFU8xMaoAD2egMupMXLro9hlsIHPvALmHoP4kOBGOYx_LLymzlqZksd5gsqJbba0idxNq_jNHr7LbalsqC2EwCQdxVEXBpMTPmNiCenE_GATxyI-joR_2Pby3sLg3CIdR6Ef9YTQc9YPo4OF_VWK_NxoOYnvCwPdH_sdhcPgPz8-u5w?type=png)](https://mermaid.live/edit#pako:eNp9VG1v2jAQ_iuWP21TSkNCBrG0bmL0w1RpIMqkaYo0GecAa8TObKeURfz32XnhrWn9xbnT89zdc3dOiZlMAROs4W8BgsGE07WiWaISgeyhhZGiyJagWk9OleGM51QY9JWqFFFd3e9m08eFh26Z_b5lCqiB969x5qBloRi03Nbuwj-CeuInaGN2IeeQS82NVPtT3NbzWiWT8ZcywWafQ4JJglNq6JJqaxy6GD80qLcZLcvFvrm7OxdHXINQ3RnkunSObUGW0ygktdtORRtkJMponoNC98Jwc5TTYC3rJJXUEKRBpI44o4KyDaCXzTh5mlInY4K4sCKNvSzVLH9fFzoZ31xmm0jQ6Ls0aLFRcofunxnkhkvRkeRM3EyBbSy4-hToXNqk9oNJu0-10GuFl0lR0xhIq5kcl-SJbnnq-svFSnZXUM-QaNgCM-gDWimZocIF2W1A1ZP5lqJPn1t6TbgqwDmr7ee6TuqUyJ04Ff627iqhbqp-2YQO9RerNG_h0wdU1iuVIl0wBlqviu12f-hcriYMKTOLo2sgKMHyIcFeo9ru88_ZYtoPwps0Te1KYw-vFU8xMaoAD2egMupMXLro9hlsIHPvALmHoP4kOBGOYx_LLymzlqZksd5gsqJbba0idxNq_jNHr7LbalsqC2EwCQdxVEXBpMTPmNiCenE_GATxyI-joR_2Pby3sLg3CIdR6Ef9YTQc9YPo4OF_VWK_NxoOYnvCwPdH_sdhcPgPz8-u5w)