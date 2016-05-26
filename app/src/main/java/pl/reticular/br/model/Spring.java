package pl.reticular.br.model;

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

import pl.reticular.br.utils.Vector3f;

public class Spring {
	public static void resolveNormal(Vector3f p1, Vector3f p2, float segmentLength) {
		float lx = p1.X - p2.X;
		float ly = p1.Y - p2.Y;
		float lz = p1.Z - p2.Z;

		float currentLength = Vector3f.length(lx, ly, lz);

		if (currentLength == 0.0f) {
			return;
		}

		float diff = segmentLength - currentLength;

		lx /= currentLength;
		ly /= currentLength;
		lz /= currentLength;

		float diffX = lx * diff * 0.5f;
		float diffY = ly * diff * 0.5f;
		float diffZ = lz * diff * 0.5f;

		//try to restore original length
		p1.add(diffX, diffY, diffZ);
		p2.add(-diffX, -diffY, -diffZ);
	}

	public static void resolveRepelling(Vector3f p1, Vector3f p2, float minDistance, float a) {
		float lx = p1.X - p2.X;
		float ly = p1.Y - p2.Y;
		float lz = p1.Z - p2.Z;

		float currentLengthSquared = lx * lx + ly * ly + lz * lz;

		if (currentLengthSquared < minDistance * minDistance && currentLengthSquared != 0.0f) {
			float currentLength = (float) Math.sqrt(currentLengthSquared);
			float diff = minDistance - currentLength;

			lx /= currentLength;
			ly /= currentLength;
			lz /= currentLength;

			float diffX = lx * diff * a;
			float diffY = ly * diff * a;
			float diffZ = lz * diff * a;

			p1.add(diffX, diffY, diffZ);
			p2.add(-diffX, -diffY, -diffZ);
		}
	}
}
