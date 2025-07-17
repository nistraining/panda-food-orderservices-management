FROM openjdk:17-jdk
WORKDIR /app
EXPOSE 5001
COPY target/panda-food-orderservices-management-0.0.1-SNAPSHOT.war panda-food-orderservices-management.war
ENTRYPOINT ["java", "-jar", "panda-food-orderservices-management.war"]