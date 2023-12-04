var stompClient = null;
var game = null;
var player = null;

const sendMessage = (message) => {
    stompClient.send(`/app/${message.type}`, {}, JSON.stringify(message));
}

const makeMove = (row, cell) => {
    sendMessage({
        type: "game.move",
        moveRow: row,
        moveCell: cell,
        turn: game.turn,
        sender: player,
        gameId: game.gameId
    });
}

const messagesTypes = {
    "game.join": (message) => {
        updateGame(message);
    },
    "game.gameOver": (message) => {
        updateGame(message);
        if (message.gameStatus === 'DRAW') toastr.success(`Draw!`);
        else showWinner(message.winner);
    },
    "game.joined": (message) => {
        updateBoardMetaData(message);
        if (game !== null && game.gameId === message.gameId) return;
        player = localStorage.getItem("playerName");
        updateGame(message);
        stompClient.subscribe(`/topic/game.${message.gameId}`, function (message) {
            handleMessage(JSON.parse(message.body));
        });
    },
    "game.move": (message) => {
        updateGame(message);
    },
    "game.left": (message) => {
        updateGame(message);
        if (message.winner) showWinner(message.winner);
    },
    "error": (message) => {
        toastr.error(message.content);
    }
}

const handleMessage = (message) => {
    if (messagesTypes[message.type])
        messagesTypes[message.type](message);
}

const messageToGame = (message) => {
    return {
        gameId: message.gameId,
        board: message.board,
        turn: message.turn,
        player1: message.player1,
        player2: message.player2,
        gameStatus: message.gameStatus,
        winner: message.winner
    }
}

const showWinner = (winner) => {
    toastr.success(`The winner is ${winner}!`);
}

const joinGame = () => {
    const playerName = prompt("Enter your name:");
    localStorage.setItem("playerName", playerName);
    sendMessage({
        type: "game.join",
        playerName: playerName,
    });
}

const connect = () => {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/game.state', function (message) {
            handleMessage(JSON.parse(message.body));
        });
        loadGame();
    });
}

const loadGame = () => {
    const playerName = localStorage.getItem("playerName");
    if (playerName) {
        sendMessage({
            type: "game.join",
            playerName: playerName
        });
    } else {
        joinGame();
    }
}

const updateGame = (message) => {
    game = messageToGame(message);
    updateBoard(message.board);
    document.getElementById("player1").innerHTML = game.player1;
    document.getElementById("player2").innerHTML = game.player2 || (game.winner ? '-' : 'Waiting for player 2...');
    document.getElementById("turn").innerHTML = game.turn;
    document.getElementById("winner").innerHTML = game.winner || '-';
}

const updateBoardMetaData = (message) => {
    document.getElementById("player1").innerHTML = message.player1
    document.getElementById("player2").innerHTML = message.player2 || (message.winner ? '-' : 'Waiting for player 2...');
    document.getElementById("turn").innerHTML = message.turn;
    document.getElementById("winner").innerHTML = message.winner || '-';
}

const updateBoard = (board) => {
    board.forEach((row, rowIndex) => {
        row.forEach((cell, cellIndex) => {
            const cellElement = document.querySelector(`.row-${rowIndex} .cell-${cellIndex}`);
            cellElement.innerHTML = cell === ' ' ? '<button onclick="makeMove(' + rowIndex + ','+ cellIndex +')"> </button>' : `<span class="cell-item">${cell}</span>`;
        });
    });
}

window.onload = function () {
    connect();
}
