FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_2.13.8 as build
COPY . /services
WORKDIR /services
RUN sbt clean
RUN sbt assembly

FROM eclipse-temurin:17-jre-focal
RUN mkdir -p /opt/app
COPY --from=build /services/auth/target/auth.jar /opt/app/auth.jar
COPY --from=build /services/routing/target/routing.jar /opt/app/routing.jar
