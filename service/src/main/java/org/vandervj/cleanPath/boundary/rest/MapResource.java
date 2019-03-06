/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.boundary.rest;

import org.springframework.stereotype.Component;
import org.vandervj.cleanPath.boundary.dto.MapDto;
import org.vandervj.cleanPath.control.MapControl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Named
@Path("/maps")
public class MapResource {

	@Inject
	private MapControl mapControl;

	@Inject
	private CleanerResource cleanerResource;

	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON })
	public MapDto getCurrentFloorMap(@PathParam("id") final int mapId) {
		final MapDto map = mapControl.getMap(mapId);

		if (map == null) {
			throw new NotFoundException("Map with id: " + mapId + " doesn't exist");
		}

		return map;
	}

	@PUT
	@Path("/{id}")
	@Consumes({MediaType.APPLICATION_JSON })
	@Produces({MediaType.APPLICATION_JSON })
	public MapDto setCurrentFloorMap(@PathParam("id") final int mapId, MapDto newMap) {
		mapControl.setMap(mapId, newMap);
		return newMap;
	}

	@Path("/{id}/cleaners")
	public CleanerResource handleCleanerResource(@PathParam("id") final int mapId) {
		return cleanerResource.setMapId(mapId);
	}
}
