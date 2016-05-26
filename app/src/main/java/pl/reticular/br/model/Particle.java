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

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.br.utils.Savable;
import pl.reticular.br.utils.Vector3f;

public class Particle implements Savable {
	public Vector3f pos;
	private Vector3f prevPos;

	private static final float dampingFactor = 0.98f;

	private enum Keys {
		Pos,
		PrevPos
	}

	public Particle(Vector3f position) {
		pos = new Vector3f(position);
		prevPos = new Vector3f(position);
	}

	public Particle(JSONObject json) throws JSONException {
		pos = new Vector3f(json.getJSONObject(Keys.Pos.toString()));
		prevPos = new Vector3f(json.getJSONObject(Keys.PrevPos.toString()));
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(Keys.Pos.toString(), pos.toJSON());
		json.put(Keys.PrevPos.toString(), prevPos.toJSON());

		return json;
	}

	public void update(Vector3f areaDimensions) {
		// Position Verlet integration method
		float nx, ny, nz;
		nx = pos.X + (pos.X - prevPos.X) * dampingFactor;
		ny = pos.Y + (pos.Y - prevPos.Y) * dampingFactor;
		nz = pos.Z + (pos.Z - prevPos.Z) * dampingFactor;

		prevPos.set(pos);

		pos.set(nx, ny, nz);

		if (pos.X > areaDimensions.X) pos.X = areaDimensions.X;
		if (pos.X < -areaDimensions.X) pos.X = -areaDimensions.X;

		if (pos.Y > areaDimensions.Y) pos.Y = areaDimensions.Y;
		if (pos.Y < -areaDimensions.Y) pos.Y = -areaDimensions.Y;

		if (pos.Z > areaDimensions.Z) pos.Z = areaDimensions.Z;
		if (pos.Z < -areaDimensions.Z) pos.Z = -areaDimensions.Z;
	}
}
