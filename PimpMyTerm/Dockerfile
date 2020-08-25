# Use the official image as a parent image
FROM openjdk:14-jdk-slim-buster

# Set the working directory
WORKDIR /

# Copy the jar file
COPY ./build/libs/PimpMyTerm.jar .

# Copy the config file
COPY ./aCore.xml .

# Listening on https
EXPOSE 443
EXPOSE 4430

# Run the specified command within the container.
CMD [ "java", "-jar","PimpMyTerm.jar","./aCore.xml" ]

