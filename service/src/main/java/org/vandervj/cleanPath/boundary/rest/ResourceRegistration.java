/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.boundary.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Configuration
public class ResourceRegistration extends ResourceConfig {
	@PostConstruct
	public void init() {
		register(CleanerResource.class);
		register(MapResource.class);
		register(MetricsResource.class);
	}
}
