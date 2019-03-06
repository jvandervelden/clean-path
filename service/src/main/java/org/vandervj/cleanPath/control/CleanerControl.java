/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.vandervj.cleanPath.boundary.dto.CleanerDto;
import org.vandervj.cleanPath.boundary.dto.MapDto;
import org.vandervj.cleanPath.entity.CleanerDirection;
import org.vandervj.cleanPath.net.CleanerTelemetry;
import org.vandervj.cleanPath.net.TelemetryDto;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Named
public class CleanerControl implements ApplicationListener<ApplicationReadyEvent> {

	private static Logger logger = LoggerFactory.getLogger(CleanerControl.class);

	@Inject
	private MapControl mapControl;

	@Inject
	private CleanerTelemetry cleanerTelemetryRequest;

	public static List<CleanerDto> cleaners = new ArrayList<>();

	static {
		cleaners.add(new CleanerDto());
	}

	public CleanerDto getCleaner(final int id) {
		try {
			return cleaners.get(id);
		} catch (final IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void updateCleaner(final int id, final CleanerDto cleaner) {
		cleaners.add(id, cleaner);
		cleanerTelemetryRequest.setCleanerPosition(id, new TelemetryDto(cleaner.getPositionX(), cleaner.getPositionY(), cleaner.getDirection()));
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

		// Setup the initial cleaner on a random point in the map.

		final MapDto map = mapControl.getMap(0);
		final int maxY = map.size();
		final int maxX = maxY > 0 ? map.get(0).size() : 0;
		final Random rand = new Random();
		final CleanerDirection direction =
				CleanerDirection.values()[rand.nextInt(CleanerDirection.values().length)];

		int x = 0;
		int y = 0;

		if (maxX > 0 && maxY > 0) {
			int tries = 0;

			do {
				x = rand.nextInt(maxX);
				y = rand.nextInt(maxY);
				tries++;
			} while (map.get(y).get(x).equals("#") && tries < maxX * maxY);

			if (tries >= maxX * maxY) {
				x = y = 0;
				logger.error("Unable to find a suitable random x, y coordinate. Setting to 0,0");
			}
		}

		cleaners.get(0).setPositionX(x);
		cleaners.get(0).setPositionY(y);
		cleaners.get(0).setDirection(direction);
		cleaners.get(0).setMapId(0);

		final TelemetryDto telemetryDto = new TelemetryDto(x, y, direction);

		logger.info("Setting initial position of cleaner");

		while(!cleanerTelemetryRequest.setCleanerPosition(0, telemetryDto)) {
			logger.info("Failed to set initial position, sleeping 1 second and trying again.");
			if (Thread.currentThread().isInterrupted()) {
				Thread.currentThread().interrupt();
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				break;
			}
		}
	}
}
