/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.boundary.rest;

import org.vandervj.cleanPath.boundary.dto.CleanerDto;
import org.vandervj.cleanPath.control.CleanerControl;
import org.vandervj.cleanPath.control.PathControl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Named
@Path("/cleaners")
public class CleanerResource {

	@Inject
	private CleanerControl cleanerControl;

	@Inject
	private PathControl pathControl;

	private Integer mapId = null;

	@GET
	@Path("/{cleanerId}")
	@Produces({MediaType.APPLICATION_JSON})
	public final CleanerDto getCleaner(@PathParam("cleanerId") final int cleanerId) {
		final CleanerDto cleaner = cleanerControl.getCleaner(cleanerId);

		if (cleaner == null) {
			throw new NotFoundException("Unable to find cleaner with id " + cleanerId);
		}

		return cleaner;
	}

	@PUT
	@Path("/{cleanerId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public CleanerDto updateCleaner(@PathParam("cleanerId") final int cleanerId, final CleanerDto cleaner) {
		if (mapId != null) {
			cleaner.setMapId(mapId);
		}

		cleanerControl.updateCleaner(cleanerId, cleaner);

		return cleaner;
	}

	@POST
	@Path("/{cleanerId}/start")
	@Produces({MediaType.APPLICATION_JSON})
	public void startCleaner(@PathParam("cleanerId") final int cleanerId, final CleanerDto cleaner) {
		cleanerControl.updateCleaner(cleanerId, cleaner);
		pathControl.startCleaner(cleanerId);
	}

	public CleanerResource setMapId(int mapId) {
		this.mapId = mapId;
		return this;
	}
}
