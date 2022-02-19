FROM openjdk:11-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/social.aceinteract.ktor-activitypub/ /app/
WORKDIR /app/bin
CMD ["./social.aceinteract.ktor-activitypub"]