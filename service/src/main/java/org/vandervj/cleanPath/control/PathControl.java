/**
 * Copyright 2019, Kion Group All Rights Reserved.
 * Created by vandervj on 3/5/2019.
 */
package org.vandervj.cleanPath.control;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.vandervj.cleanPath.boundary.dto.CleanerDto;
import org.vandervj.cleanPath.boundary.dto.MapDto;
import org.vandervj.cleanPath.entity.CleanerDirection;
import org.vandervj.cleanPath.net.CleanerTelemetry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

@Named
public class PathControl {

	private static final String MOVE_ACTION = "MOVE";
	private static final String TURN_LEFT_ACTION = "TURN_LEFT";
	private static final String TURN_RIGHT_ACTION = "TURN_RIGHT";
	private static final String WALL_CHARACTER = "#";

	@Inject
	private MapControl mapControl;

	@Inject
	private CleanerControl cleanerControl;

	@Inject
	private CleanerTelemetry cleanerTelemetryRequest;

	public void startCleaner(final int cleanerId) {
		final CleanerDto cleaner = cleanerControl.getCleaner(cleanerId);
		final MapDto map = mapControl.getMap(cleaner.getMapId());

		final List<String> path = calculatePath(cleaner.getPositionX(), cleaner.getPositionY(), cleaner.getDirection(), map);

		if (!cleanerTelemetryRequest.startCleaner(cleanerId, path)) {
			throw new ClientErrorException("Unable to start cleaner.", 502);
		}
	}

	private List<String> calculatePath(final int initialX, final int initialY, final CleanerDirection initialDirection, final MapDto map) {
		final List<String> path = new ArrayList<>();
		final Stack<Pair<Integer, Integer>> currentPath = new Stack<>();
		final Set<String> tilesToVisit = new HashSet<>();

		for (int y = 0; y < map.size(); y++) {
			for (int x = 0; x < map.get(y).size(); x++) {
				if (!map.get(y).get(x).equals(WALL_CHARACTER)) {
					tilesToVisit.add(buildVisited(x, y));
				}
			}
		}

		int x = initialX;
		int y = initialY;
		CleanerDirection direction = initialDirection;

		currentPath.push(new ImmutablePair<>(initialX, initialY));
		tilesToVisit.remove(buildVisited(initialX, initialY));

		while (tilesToVisit.size() > 0) {
			final Pair<Triple<Integer, Integer, CleanerDirection>, String[]> nextPosition = getNextMove(x, y, direction, map, tilesToVisit);

			if (nextPosition != null) {
				x = nextPosition.getLeft().getLeft();
				y = nextPosition.getLeft().getMiddle();
				direction = nextPosition.getLeft().getRight();

				tilesToVisit.remove(buildVisited(x, y));
				currentPath.push(new ImmutablePair<>(x, y));
				path.addAll(Arrays.asList(nextPosition.getRight()));
			} else {
				try {
					currentPath.pop();
					
					final Pair<Integer, Integer> backPosition = currentPath.peek();
					final CleanerDirection backDirection;

					if (x > backPosition.getLeft()) {
						// Go West
						backDirection = CleanerDirection.WEST;
					} else if (x < backPosition.getLeft()) {
						// Go East
						backDirection = CleanerDirection.EAST;
					} else if (y > backPosition.getRight()) {
						// Go North
						backDirection = CleanerDirection.NORTH;
					} else if (y < backPosition.getRight()) {
						// Go South
						backDirection = CleanerDirection.SOUTH;
					} else {
						throw new AssertionError("Duplicate positions on stack");
					}

					path.addAll(getMovesToChangeDirection(direction, backDirection));
					path.add(MOVE_ACTION);

					x = backPosition.getLeft();
					y = backPosition.getRight();
					direction = backDirection;
				} catch (final EmptyStackException e) {
					// End of the stack
				}
			}
		}

		return path;
	}

	public List<String> getMovesToChangeDirection(final CleanerDirection startDirection, final CleanerDirection endDirection) {
		int difference = startDirection.ordinal() - endDirection.ordinal();

		if (difference == -1 || difference == 3) {
			return Arrays.asList(new String[] { TURN_RIGHT_ACTION });
		} if (difference == 1 || difference == -3) {
			return Arrays.asList(new String[] { TURN_LEFT_ACTION });
		} else if (Math.abs(difference) == 2) {
			return Arrays.asList(new String[] { TURN_LEFT_ACTION, TURN_LEFT_ACTION });
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Gets the next position and how to get there. Returns null if no move is valid.
	 *
	 * @param x - Current X position
	 * @param y - Current Y position
	 * @param direction - Current facing direction
	 * @param map - Map layout
	 * @param tilesToVisit - Tile locations yet to be visited
	 * @return Pair - Left is Triple of next x, next y, next direction - Right is moves to get to next position.
	 */
	private Pair<Triple<Integer, Integer, CleanerDirection>, String[]> getNextMove(final int x, final int y, final CleanerDirection direction, final MapDto map, final Set<String> tilesToVisit) {
		final Triple<Integer, Integer, CleanerDirection> forwardPosition = calculateForwardPosition(x, y, direction);
		final Triple<Integer, Integer, CleanerDirection> leftPosition = calculateLeftPosition(x, y, direction);
		final Triple<Integer, Integer, CleanerDirection> rightPosition = calculateRightPosition(x, y, direction);
		final Triple<Integer, Integer, CleanerDirection> backPosition = calculateBackPosition(x, y, direction);

		if (isValidMove(forwardPosition, map, tilesToVisit)) {
			return new ImmutablePair<>(forwardPosition, new String[] { MOVE_ACTION });
		} else if (isValidMove(leftPosition, map, tilesToVisit)) {
			return new ImmutablePair<>(leftPosition, new String[] { TURN_LEFT_ACTION, MOVE_ACTION });
		} else if (isValidMove(rightPosition, map, tilesToVisit)) {
			return new ImmutablePair<>(rightPosition, new String[] { TURN_RIGHT_ACTION, MOVE_ACTION });
		} else if (isValidMove(backPosition, map, tilesToVisit)) {
			return new ImmutablePair<>(backPosition, new String[] { TURN_RIGHT_ACTION, TURN_RIGHT_ACTION, MOVE_ACTION });
		}

		return null;
	}

	private boolean isValidMove(final Triple<Integer, Integer, CleanerDirection> nextPosition, final MapDto map, final Set<String> tilesToVisit) {
		final int x = nextPosition.getLeft();
		final int y = nextPosition.getMiddle();

		return x >= 0 && y >= 0 && y < map.size() && x < map.get(y).size() && !map.get(y).get(x).equals(WALL_CHARACTER) && tilesToVisit.contains(buildVisited(x, y));
	}

	private Triple<Integer, Integer, CleanerDirection> calculateForwardPosition(final int x, final int y, final CleanerDirection direction) {
		switch(direction) {
			case NORTH:
				return new ImmutableTriple<>(x, y - 1, direction);
			case EAST:
				return new ImmutableTriple<>(x + 1, y, direction);
			case SOUTH:
				return new ImmutableTriple<>(x, y + 1, direction);
			case WEST:
				return new ImmutableTriple<>(x - 1, y, direction);
			default:
				throw new AssertionError("Null direction in path");
		}
	}

	private Triple<Integer, Integer, CleanerDirection> calculateLeftPosition(final int x, final int y, final CleanerDirection direction) {
		final CleanerDirection nextDirection = CleanerDirection.values()[(direction.ordinal() + 3) % 4];

		switch(direction) {
			case NORTH:
				return new ImmutableTriple<>(x - 1, y, nextDirection);
			case EAST:
				return new ImmutableTriple<>(x, y - 1, nextDirection);
			case SOUTH:
				return new ImmutableTriple<>(x + 1, y, nextDirection);
			case WEST:
				return new ImmutableTriple<>(x, y + 1, nextDirection);
			default:
				throw new AssertionError("Null direction in path");
		}
	}

	private Triple<Integer, Integer, CleanerDirection> calculateRightPosition(final int x, final int y, final CleanerDirection direction) {
		final CleanerDirection nextDirection = CleanerDirection.values()[(direction.ordinal() + 1) % 4];

		switch(direction) {
			case NORTH:
				return new ImmutableTriple<>(x + 1, y, nextDirection);
			case EAST:
				return new ImmutableTriple<>(x, y + 1, nextDirection);
			case SOUTH:
				return new ImmutableTriple<>(x - 1, y, nextDirection);
			case WEST:
				return new ImmutableTriple<>(x, y - 1, nextDirection);
			default:
				throw new AssertionError("Null direction in path");
		}
	}

	private Triple<Integer, Integer, CleanerDirection> calculateBackPosition(final int x, final int y, final CleanerDirection direction) {
		final CleanerDirection nextDirection = CleanerDirection.values()[(direction.ordinal() + 2) % 4];

		switch(direction) {
			case NORTH:
				return new ImmutableTriple<>(x, y + 1, nextDirection);
			case EAST:
				return new ImmutableTriple<>(x - 1, y, nextDirection);
			case SOUTH:
				return new ImmutableTriple<>(x, y - 1, nextDirection);
			case WEST:
				return new ImmutableTriple<>(x + 1, y, nextDirection);
			default:
				throw new AssertionError("Null direction in path");
		}
	}

	private String buildVisited(final int x, final int y) {
		return x + "-" + y;
	}

}
