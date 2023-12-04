

# Tic-Tac-Toe

A simple tic-tac-toe game with spring websocket

## Installation

Running with gradle and docker 

```bash
    docker build -t tictactoe:v0.0.1 --build-arg JAR="tictactoe-0.0.1.jar" .
    docker run -p 8080:8080 --name tictactoe tictactoe:v0.0.1
```

## How To Change The Board Dimension

To change the board dimension for the current implementation you have to edit the environment variable line inside the Dockerfile into any number you want

``` text
    ENV BOARD_DIMENSION=4
```

After that you need to re-build the docker image and re-run the docker container

```bash
    docker stop tictactoe # stop running container
    docker container rm tictactoe # remove old container
    docker image rm tictactoe:v0.0.1 # remove old image

    # re-build and re-run container
    docker build -t tictactoe:v0.0.1 --build-arg JAR="tictactoe-0.0.1.jar" .
    docker run -p 8080:8080 --name tictactoe tictactoe:v0.0.1
```

## How To Play Locally After Running On Docker

1. Open 2 browsers or you can forward your PC port 8080 to your LAN

2. Enter your name on each browser (don't use the same name, currently implementation use browser's local storage)

3. Play the game

4. Refresh if game ended draw or one of the player won




