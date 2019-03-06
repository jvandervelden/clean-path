/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 3/5/2019.
 */
package org.vandervj.cleanPath.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Named
public class CleanerTelemetry {

	private static Logger logger = LoggerFactory.getLogger(CleanerTelemetry.class);

	@Value("${telemetry.service.url:http://localhost:8082}")
	private String telemetryServiceUrl;

	public boolean setCleanerPosition(final int cleanerId, final TelemetryDto telemetryDto) {
		final HttpClient client = HttpClientBuilder.create().build();
		final HttpPost request = new HttpPost(telemetryServiceUrl + "/api/cleaners/" + cleanerId);

		request.setHeader("Content-Type", MediaType.APPLICATION_JSON);
		final ObjectMapper mapper = new ObjectMapper();
		try {
			request.setEntity(new StringEntity(mapper.writeValueAsString(telemetryDto)));

			final HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() > 399) {
				logger.error("Error sending telematry data.");
				for (final String errorLine : IOUtils.readLines(response.getEntity().getContent(), "UTF-8")) {
					logger.error(errorLine);
				}

				return false;
			}

			return true;
		} catch (final JsonProcessingException | UnsupportedEncodingException e) {
			logger.error("Unable to stringify telematry data. ", e);
		} catch (final IOException e) {
			logger.error("Unable to send telematry data. ", e);
		}

		return false;
	}

	public boolean startCleaner(final int cleanerId, final List<String> path) {
		final HttpClient client = HttpClientBuilder.create().build();
		final HttpPost request = new HttpPost(telemetryServiceUrl + "/api/cleaners/" + cleanerId + "/start");

		request.setHeader("Content-Type", MediaType.APPLICATION_JSON);
		final ObjectMapper mapper = new ObjectMapper();

		try {
			request.setEntity(new StringEntity(mapper.writeValueAsString(path)));

			final HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() > 399) {
				logger.error("Error starting cleaner.");
				for (final String errorLine : IOUtils.readLines(response.getEntity().getContent(), "UTF-8")) {
					logger.error(errorLine);
				}

				return false;
			}

			return true;
		} catch (final JsonProcessingException | UnsupportedEncodingException e) {
			logger.error("Unable to stringify path data. ", e);
		} catch (final IOException e) {
			logger.error("Unable to send start message. ", e);
		}

		return false;
	}
}
