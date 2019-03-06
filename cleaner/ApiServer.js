"use strict";

var express = require('express');
var bodyParser = require('body-parser');

var ApiServer = function () {
	this.expressApp = express();
	this.expressApp.use(bodyParser.json());
	this.expressApp.post('/api/cleaners/0/start', this.handleStartCleanerRequest.bind(this));
	this.expressApp.post('/api/cleaners/0', this.handleSetPositionRequest.bind(this));
};

ApiServer.prototype.startServer = function (port, startCleanerCallback, setPositionCallback) {
	this.listenPort = port;
	this.startCleanerCallback = startCleanerCallback;
	this.setPositionCallback = setPositionCallback;

	this.expressApp.listen(port, this.onServerStarted.bind(this));
};

ApiServer.prototype.onServerStarted = function () {
	console.log("Api server started on port: " + this.listenPort);
};

ApiServer.prototype.handleStartCleanerRequest = function (request, response) {
	if (this.startCleanerCallback) {
		this.startCleanerCallback(request.body);
	}

	response.send("");
};

ApiServer.prototype.handleSetPositionRequest = function (request, response) {
	if (this.setPositionCallback) {
		this.setPositionCallback(request.body);
	}

	response.send("");
};

module.exports = ApiServer;