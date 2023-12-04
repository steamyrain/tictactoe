FROM eclipse-temurin:17-alpine AS build-stage

WORKDIR /app

COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:17-alpine AS release-stage

ARG JAR
ENV BOARD_DIMENSION=4

WORKDIR /

COPY --from=build-stage /app/build/libs/$JAR ./tictactoe.jar
COPY --from=build-stage /app/docker-entrypoint.sh /usr/local/bin/

RUN chmod +x /usr/local/bin/docker-entrypoint.sh
RUN ln -s /usr/local/bin/docker-entrypoint.sh .

EXPOSE 8080

ENTRYPOINT ["docker-entrypoint.sh"]
CMD java \
	-jar ./tictactoe.jar 
