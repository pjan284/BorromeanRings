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

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;

public class MathUtils {

	public static Ray unProjectMatrices(float mvMatrix[],
	                                    float pMatrix[],
	                                    float clickX,
	                                    float clickY,
	                                    int viewport[]) {
		float temp1[] = new float[4];
		float temp2[] = new float[4];

		int result1 = GLU.gluUnProject(clickX, clickY, 1.0f, mvMatrix, 0, pMatrix, 0, viewport, 0, temp1, 0);
		int result2 = GLU.gluUnProject(clickX, clickY, 0.0f, mvMatrix, 0, pMatrix, 0, viewport, 0, temp2, 0);

		if (result1 != GL10.GL_TRUE || result2 != GL10.GL_TRUE) {
			return new Ray(new Vector3f(), new Vector3f());
		}

		Vector3f near = new Vector3f(temp1[0], temp1[1], temp1[2]);
		near.scale(1.0f / temp1[3]);

		Vector3f far = new Vector3f(temp2[0], temp2[1], temp2[2]);
		far.scale(1.0f / temp2[3]);

		return new Ray(near, far);
	}

	/**
	 * http://geomalgorithms.com/a02-_lines.html#Distance-to-Ray-or-Segment
	 */
	public static float distance(Vector3f P, Ray S) {
		Vector3f v = Vector3f.sub(S.P1, S.P0);
		Vector3f w = Vector3f.sub(P, S.P0);

		float c1 = Vector3f.dotProduct(w, v);
		if (c1 <= 0)
			return Vector3f.distance(P, S.P0);

		float c2 = Vector3f.dotProduct(v, v);
		if (c2 <= c1)
			return Vector3f.distance(P, S.P1);

		float b = c1 / c2;
		Vector3f Pb = Vector3f.add(S.P0, Vector3f.scale(v, b));
		return Vector3f.distance(P, Pb);
	}
}
