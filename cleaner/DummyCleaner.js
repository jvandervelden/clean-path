"use strict";

var directionArray = [ "NORTH", "EAST", "SOUTH", "WEST" ];
var directionMap = {
	"NORTH": 0,
	"EAST": 1,
	"SOUTH": 2,
	"WEST": 3
};

var DummyCleaner = function () {
	this.pathIndex = 0;
	this.moving = false;
	this.intervalId = null;
	this.path = [];
	this.x = 0;
	this.y = 0;
	this.direction = "NORTH";
};

DummyCleaner.prototype.setPosition = function (x, y, direction) {
	this.x = x;
	this.y = y;
	this.direction = direction;
};

DummyCleaner.prototype.setPath = function (path) {
	this.path = path;
};

DummyCleaner.prototype.start = function (moveCallback) {
	this.moveCallback = moveCallback;
	this.moving = true;
	this.pathIndex = 0;
	this.intervalId = setInterval(this.handleMove.bind(this),200)
};

DummyCleaner.prototype.handleMove = function () {
	if (this.moveCallback) {
		this.processNextPathItem();

		if (this.moving) {
			this.moveCallback({
				x: this.x,
				y: this.y,
				direction: this.direction
			});
		} else {
			this.stop();
			//this.moving = true;
			//this.pathIndex = 0;
		}
	}
};

DummyCleaner.prototype.processNextPathItem = function () {
	var nextMove;

	if (this.path.length <= this.pathIndex) {
		this.moving = false;
		return;
	}

	nextMove = this.path[this.pathIndex++];

	if (nextMove == "TURN_LEFT") {
		this.direction = directionArray[(directionMap[this.direction] + 3) % 4];
	} else if (nextMove == "TURN_RIGHT") {
		this.direction = directionArray[(directionMap[this.direction] + 1) % 4];
	} else if (nextMove == "MOVE") {
		switch (this.direction) {
			case "NORTH":
				this.y--; break;
			case "EAST":
				this.x++; break;
			case "SOUTH":
				this.y++; break;
			case "WEST":
				this.x--; break;
		}
	}
};

DummyCleaner.prototype.stop = function () {
	clearInterval(this.intervalId);
	this.moveCallback = null;
	this.intervalId = null;
};

module.exports = DummyCleaner;