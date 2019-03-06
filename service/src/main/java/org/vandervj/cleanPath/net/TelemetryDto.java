/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 3/5/2019.
 */
package org.vandervj.cleanPath.net;

import org.vandervj.cleanPath.entity.CleanerDirection;

public class TelemetryDto {
	private int x;
	private int y;
	private CleanerDirection direction;

	public TelemetryDto() {}

	public TelemetryDto(final int x, final int y, final CleanerDirection direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public CleanerDirection getDirection() {
		return direction;
	}

	public void setDirection(CleanerDirection direction) {
		this.direction = direction;
	}
}
