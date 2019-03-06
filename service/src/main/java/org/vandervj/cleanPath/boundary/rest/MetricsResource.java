/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.boundary.rest;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

@Named
@Path("/metrics")
public class MetricsResource {
	@GET
	public List<String> getMetrics() {
		return new ArrayList<>();
	}
}
