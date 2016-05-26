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

import org.json.JSONException;
import org.json.JSONObject;

public class Vector3f implements Savable {
	public float X;
	public float Y;
	public float Z;

	public Vector3f() {
		X = 0.0f;
		Y = 0.0f;
		Z = 0.0f;
	}

	public Vector3f(Vector3f v) {
		X = v.X;
		Y = v.Y;
		Z = v.Z;
	}

	public Vector3f(float x, float y, float z) {
		X = x;
		Y = y;
		Z = z;
	}

	public Vector3f(JSONObject json) throws JSONException {
		X = (float) json.getDouble("X");
		Y = (float) json.getDouble("Y");
		Z = (float) json.getDouble("Z");
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		state.put("X", (double) X);
		state.put("Y", (double) Y);
		state.put("Z", (double) Z);

		return state;
	}

	public void set(float x, float y, float z) {
		X = x;
		Y = y;
		Z = z;
	}

	public void set(Vector3f v) {
		X = v.X;
		Y = v.Y;
		Z = v.Z;
	}

	public void add(float x, float y, float z) {
		X += x;
		Y += y;
		Z += z;
	}

	public void add(Vector3f v) {
		X += v.X;
		Y += v.Y;
		Z += v.Z;
	}

	public static Vector3f add(Vector3f v1, Vector3f v2) {
		Vector3f ret = new Vector3f(v1);
		ret.add(v2);
		return ret;
	}

	public void sub(Vector3f v) {
		X -= v.X;
		Y -= v.Y;
		Z -= v.Z;
	}

	public static Vector3f sub(Vector3f v1, Vector3f v2) {
		Vector3f ret = new Vector3f(v1);
		ret.sub(v2);
		return ret;
	}

	public void scale(float a) {
		X *= a;
		Y *= a;
		Z *= a;
	}

	public static Vector3f scale(Vector3f v, float a) {
		return new Vector3f(v.X * a, v.Y * a, v.Z * a);
	}

	public static float dotProduct(Vector3f v1, Vector3f v2) {
		return v1.X * v2.X + v1.Y * v2.Y + v1.Z * v2.Z;
	}

	public Vector3f crossProcuct(Vector3f v2) {
		return new Vector3f(
				(Y * v2.Z) - (Z * v2.Y),
				(Z * v2.X) - (X * v2.Z),
				(X * v2.Y) - (Y * v2.X)
		);
	}

	public static Vector3f crossProcuct(Vector3f v1, Vector3f v2) {
		return new Vector3f(
				(v1.Y * v2.Z) - (v1.Z * v2.Y),
				(v1.Z * v2.X) - (v1.X * v2.Z),
				(v1.X * v2.Y) - (v1.Y * v2.X)
		);
	}

	public float length() {
		return (float) Math.sqrt((double) (X * X + Y * Y + Z * Z));
	}

	public static float length(float x, float y, float z) {
		return (float) Math.sqrt((double) (x * x + y * y + z * z));
	}

	public static float distance(Vector3f v1, Vector3f v2) {
		float x = v1.X - v2.X;
		float y = v1.Y - v2.Y;
		float z = v1.Z - v2.Z;
		return (float) Math.sqrt((double) (x * x + y * y + z * z));
	}

	public void normalize() {
		float l = length();
		if (l != 0.0f) {
			X /= l;
			Y /= l;
			Z /= l;
		}
	}
}