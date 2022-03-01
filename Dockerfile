# App Building phase --------
FROM openjdk:11-jdk AS build

RUN mkdir /appbuild
COPY . /appbuild

WORKDIR /appbuild

RUN ./gradlew clean build installDist
# End App Building phase --------

# Container setup --------
FROM openjdk:11-jdk

EXPOSE 8080:8080
RUN mkdir /app

# Entrypoint definition
COPY --from=build /appbuild/build/install/social.aceinteract.ktor-activitypub/ /app/
COPY --from=build /appbuild/json/ /app/bin/json/
COPY --from=build /appbuild/keys/ /app/bin/keys/
WORKDIR /app/bin
CMD ["./social.aceinteract.ktor-activitypub"]
# End Container setup --------