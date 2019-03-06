"use strict";

var TelemetryWebSocketServer = require("./TelemetryWebSocketServer");
var ApiServer = require("./ApiServer");
var DummyCleaner = require("./DummyCleaner");

var webSocketsServerPort = 8081;
var apiServerPort = 8082;

var telemetryWebSocketServer = new TelemetryWebSocketServer();
var apiServer = new ApiServer();
var dummyCleaner = new DummyCleaner();

telemetryWebSocketServer.startServer(webSocketsServerPort);
apiServer.startServer(
	apiServerPort,
	function (cleanerPath) {
		dummyCleaner.stop();
		console.log("Starting cleaner on path: " + JSON.stringify(cleanerPath));
		dummyCleaner.setPath(cleanerPath);
		dummyCleaner.start(function (data) {
			console.log("Reporting position: " + data.x + ", " + data.y + ", " + data.direction);

			telemetryWebSocketServer.sendMessage(JSON.stringify({
				x: data.x,
				y: data.y,
				direction: data.direction
			}));
		});
	},
	function (cleanerPosition) {
		console.log("Setting cleaner position: " + cleanerPosition.x + "," + cleanerPosition.y + "," + cleanerPosition.direction);
		dummyCleaner.setPosition(cleanerPosition.x, cleanerPosition.y, cleanerPosition.direction);

		telemetryWebSocketServer.sendMessage(JSON.stringify({
			x: cleanerPosition.x,
			y: cleanerPosition.y,
			direction: cleanerPosition.direction
		}));
	});
/*
dummyCleaner.setPath([ "MOVE", "TURN_LEFT", "MOVE", "MOVE", "MOVE", "TURN_LEFT", "MOVE", "MOVE", "MOVE", "TURN_LEFT", "MOVE", "MOVE", "TURN_RIGHT" ]);

dummyCleaner.start(function (data) {
	console.log("Reporting position: " + data.x + ", " + data.y + ", " + data.direction);

	telemetryWebSocketServer.sendMessage({
		x: data.x,
		y: data.y,
		direction: data.direction
	});
});
*/