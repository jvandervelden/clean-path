/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.boundary.dto;

import org.springframework.boot.json.GsonJsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapDto extends ArrayList<List<String>> implements List<List<String>> {
	public MapDto() {}

	public MapDto(String[]... map) {
		for (final String[] row : map) {
			this.add(Arrays.asList(row));
		}
	}
}
