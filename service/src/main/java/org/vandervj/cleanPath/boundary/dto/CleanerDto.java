/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.boundary.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.vandervj.cleanPath.entity.CleanerDirection;

public class CleanerDto {
	private CleanerDirection direction;
	private Integer positionX;
	private Integer positionY;
	private Integer mapId;

	public CleanerDirection getDirection() {
		return direction;
	}

	public void setDirection(CleanerDirection direction) {
		this.direction = direction;
	}

	public Integer getPositionX() {
		return positionX;
	}

	public void setPositionX(Integer positionX) {
		this.positionX = positionX;
	}

	public Integer getPositionY() {
		return positionY;
	}

	public void setPositionY(Integer positionY) {
		this.positionY = positionY;
	}

	public Integer getMapId() {
		return mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}
}
