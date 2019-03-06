"use strict";

var WebSocketServer = require('websocket').server;
var http = require('http');

var TelemetryWebSocketServer = function () {
	this.clients = [];
	this.telematryServer = http.createServer(function(request, response) {});
	this.listenPort = 8080;
};

TelemetryWebSocketServer.prototype.startServer = function (port) {
	this.listenPort = port;

	this.telematryServer.listen(port, this.onServiceStarted.bind(this));
	this.wsServer = new WebSocketServer({
		httpServer: this.telematryServer
	});

	this.wsServer.on('request', this.handleClientConnection.bind(this));
};

TelemetryWebSocketServer.prototype.onServiceStarted = function () {
	console.log("Web Socket Server is listening on port " + this.listenPort);
};

TelemetryWebSocketServer.prototype.handleClientConnection = function (request) {
	var connection = request.accept(null, request.origin);

	this.clients.push(connection);

	connection.on('message', this.messageRecieved.bind(this));
	connection.on('close', this.onClientClose.bind(this));
};

TelemetryWebSocketServer.prototype.messageRecieved = function (message) {
};

TelemetryWebSocketServer.prototype.onClientClose = function (connection) {
	this.clients.splice(this.clients.indexOf(connection), 1);
};

TelemetryWebSocketServer.prototype.sendMessage = function (message) {
	for (var i = 0; i < this.clients.length; i++) {
		this.clients[i].sendUTF(message, function () {
			console.log("Sent Message: " + JSON.stringify(arguments));
		});
	}
}

module.exports = TelemetryWebSocketServer;