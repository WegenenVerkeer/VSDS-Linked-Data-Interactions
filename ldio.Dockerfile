# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.9.6-amazoncorretto-21 AS builder

# MAVEN: application
FROM builder AS app-stage
COPY . .
RUN mvn clean install -DskipTests

#
# RUN THE APPLICATION
#
FROM amazoncorretto:21-alpine-jdk

RUN adduser -D -u 2000 ldio
USER ldio
WORKDIR /ldio

COPY --from=app-stage ldi-orchestrator/ldio-application/target/ldio-application.jar ./

COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-http-in/target/ldio-http-in-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-http-in-poller/target/ldio-http-in-poller-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-ldes-client/target/ldio-ldes-client-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-ldes-client-connector/target/ldio-ldes-client-connector-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-kafka/target/ldio-kafka-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-amqp/target/ldio-amqp-jar-with-dependencies.jar ./lib/

COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-rdf-adapter/target/ldio-rdf-adapter-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-ngsiv2-to-ld-adapter/target/ldio-ngsiv2-to-ld-adapter-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-rml-adapter/target/ldio-rml-adapter-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-json-to-ld-adapter/target/ldio-json-to-ld-adapter-jar-with-dependencies.jar ./lib/

COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-sparql-construct/target/ldio-sparql-construct-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-version-materialiser/target/ldio-version-materialiser-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-version-object-creator/target/ldio-version-object-creator-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-version-object-creator/target/ldio-version-object-creator-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-geojson-to-wkt/target/ldio-geojson-to-wkt-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-http-enricher/target/ldio-http-enricher-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-change-detection-filter/target/ldio-change-detection-filter-jar-with-dependencies.jar ./lib/

COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-console-out/target/ldio-console-out-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-http-out/target/ldio-http-out-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-noop-out/target/ldio-noop-out-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-repository-materialiser/target/ldio-repository-materialiser-jar-with-dependencies.jar ./lib/


RUN mkdir "state"
RUN chmod -R 777 ./state

CMD ["java", "-cp", "ldio-application.jar", "-Dloader.path=lib/", "org.springframework.boot.loader.launch.PropertiesLauncher"]
