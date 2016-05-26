package pl.reticular.br.utils;

/*
 * Copyright (C) 2016 Piotr Jankowski
 *
 * This file is part of Borromean Rings.
 *
 * Borromean Rings is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Borromean Rings is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Borromean Rings. If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;

public class EqualPartsEllipse {
	private static EqualPartsEllipse instance = new EqualPartsEllipse();

	public static EqualPartsEllipse getInstance() {
		return instance;
	}

	private EqualPartsEllipse() {
	}

	private Vector2d[] calcPositions(double r1, double r2, int parts) {
		Vector2d positions[] = new Vector2d[parts];
		for (int i = 0; i < parts; i++) {
			double angle = (double) i * 2 * Math.PI / (double) parts;
			positions[i] = new Vector2d(r1 * Math.sin(angle), r2 * Math.cos(angle));
		}
		return positions;
	}

	private double[] calcLengths(Vector2d positions[]) {
		double lengths[] = new double[positions.length];
		for (int i = 0; i < positions.length; i++) {
			lengths[i] = Vector2d.distance(positions[i], positions[(i + 1) % positions.length]);
		}
		return lengths;
	}

	private double calcSum(double lengths[]) {
		double sum = 0.0;
		for (double length : lengths) {
			sum += length;
		}
		return sum;
	}

	private ArrayList<Integer> divide(double lengths[], int segments) {
		double optimal = calcSum(lengths) / segments;

		ArrayList<Integer> divPoints = new ArrayList<>();
		double currentSum = 0.0;

		divPoints.add(0);

		for (int i = 1; i < lengths.length && divPoints.size() < segments; i++) {
			currentSum += lengths[i];
			if (currentSum >= optimal) {
				divPoints.add(i);
				currentSum = 0.0f;
			}
		}
		return divPoints;
	}

	public ArrayList<Vector2d> calcPoints(double r1, double r2, int segments) {
		int accuracy = 4096;

		Vector2d positions[] = calcPositions(r1, r2, accuracy);
		double lengths[] = calcLengths(positions);
		ArrayList<Integer> division = divide(lengths, segments);

		ArrayList<Vector2d> result = new ArrayList<>();
		for (Integer div : division) {
			result.add(positions[div]);
		}

		return result;
	}
}
