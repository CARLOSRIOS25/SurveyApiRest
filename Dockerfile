FROM amazoncorretto:24

# define working directory inside the container
WORKDIR /app

# copy the jar file into the container
COPY target/surveysApiRest-0.0.1-SNAPSHOT.jar app.jar

# expose the port the app runs on
EXPOSE 8080

# command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
