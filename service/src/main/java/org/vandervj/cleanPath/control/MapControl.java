/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 2/28/2019.
 */
package org.vandervj.cleanPath.control;

import org.vandervj.cleanPath.boundary.dto.MapDto;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
public class MapControl {

	public static List<MapDto> maps = new ArrayList<>();

	static {
		maps.add(new MapDto(

				"####################".split(""),
				"#             ## # #".split(""),
				"# # ## #####   # # #".split(""),
				"# # ## #####  ## # #".split(""),
				"# #            #   #".split(""),
				"# # ########  #### #".split(""),
				"# #              # #".split(""),
				"# # ########  ## # #".split(""),
				"#                # #".split(""),
				"# # ########  ## # #".split(""),
				"# #              # #".split(""),
				"# # ########  ## # #".split(""),
				"#                  #".split(""),
				"####################".split("")

		));
	}

	public MapDto getMap(final int id) {
		try {
			return maps.get(id);
		} catch (final IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void setMap(final int id, final MapDto map) {
		maps.add(id, map);
	}
}
