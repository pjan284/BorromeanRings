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

import android.opengl.Matrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.reticular.br.utils.Savable;
import pl.reticular.br.utils.Vector2d;
import pl.reticular.br.utils.Vector3f;

public class Chain implements Savable {
	public Particle particles[];
	private float lengths[];
	private float maxLength;
	private boolean frozen;

	private enum Keys {
		Particles,
		Lengths,
		Frozen
	}

	public Chain(ArrayList<Vector2d> points, Vector3f rotAxis, float rotAngle) {
		rotAxis.normalize();

		int sectors = points.size();

		particles = new Particle[sectors];
		lengths = new float[sectors];

		float rotationMatrix[] = new float[16];
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.rotateM(rotationMatrix, 0, rotAngle, rotAxis.X, rotAxis.Y, rotAxis.Z);

		for (int s = 0; s < sectors; s++) {
			Vector2d point = points.get(s);
			float sv[] = {(float) point.X, (float) point.Y, 0.0f, 1.0f};
			float rv[] = new float[4];
			Matrix.multiplyMV(rv, 0, rotationMatrix, 0, sv, 0);

			Vector3f p = new Vector3f(rv[0], rv[1], rv[2]);
			particles[s] = new Particle(p);

			if (s > 0) {
				lengths[s - 1] = Vector3f.sub(particles[s].pos, particles[s - 1].pos).length();
			}
		}
		lengths[sectors - 1] = Vector3f.sub(particles[sectors - 1].pos, particles[0].pos).length();

		maxLength = 0.0f;
		for (float length : lengths) {
			if (length > maxLength) {
				maxLength = length;
			}
		}

		frozen = false;
	}

	public Chain(JSONObject json) throws JSONException {
		JSONArray particleArray = json.getJSONArray(Keys.Particles.toString());
		particles = new Particle[particleArray.length()];
		for (int i = 0; i < particleArray.length(); i++) {
			JSONObject particleState = particleArray.getJSONObject(i);
			particles[i] = new Particle(particleState);
		}

		JSONArray lengthArray = json.getJSONArray(Keys.Lengths.toString());
		lengths = new float[lengthArray.length()];
		for (int i = 0; i < lengthArray.length(); i++) {
			lengths[i] = (float) lengthArray.getDouble(i);
		}

		maxLength = 0.0f;
		for (float length : lengths) {
			if (length > maxLength) {
				maxLength = length;
			}
		}

		frozen = json.getBoolean(Keys.Frozen.toString());
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		JSONArray particleArray = new JSONArray();
		for (Particle node : particles) {
			JSONObject nodeState = node.toJSON();
			particleArray.put(nodeState);
		}
		state.put(Keys.Particles.toString(), particleArray);

		JSONArray lengthArray = new JSONArray();
		for (float length : lengths) {
			lengthArray.put(length);
		}
		state.put(Keys.Lengths.toString(), lengthArray);

		state.put(Keys.Frozen.toString(), frozen);

		return state;
	}

	public void resolve() {
		Spring.resolveSpring(particles[0].pos, particles[particles.length - 1].pos, lengths[lengths.length - 1]);
		for (int i = 0; i < particles.length - 1; i++) {
			Spring.resolveSpring(particles[i].pos, particles[i + 1].pos, lengths[i]);
		}
	}

	public void resolveSelfCollisions() {
		for (int i = 0; i < particles.length; i++) {

			int last = particles.length;
			if (i == 0) {
				last--;
			}

			Vector3f p1 = particles[i].pos;
			for (int j = i + 2; j < last; j++) {
				Spring.resolveMutualRepelling(p1, particles[j].pos, maxLength, 0.5f);
			}
		}
	}

	public void resolveMutualCollisions(Chain other) {
		for (Particle part1 : particles) {
			Vector3f p1 = part1.pos;
			for (Particle part2 : other.particles) {
				Spring.resolveMutualRepelling(p1, part2.pos, maxLength * 1.5f, 0.5f);
			}
		}
	}

	public void circularize() {
		float a = 0.2f;

		for (int i = 0; i < particles.length - 2; i++) {
			float d = lengths[i] + lengths[i + 1];
			Spring.resolveMutualRepelling(particles[i].pos, particles[i + 2].pos, d, a);
		}

		float d1 = lengths[particles.length - 2] + lengths[particles.length - 1];
		Spring.resolveMutualRepelling(particles[particles.length - 2].pos, particles[0].pos, d1, a);

		float d2 = lengths[particles.length - 1] + lengths[0];
		Spring.resolveMutualRepelling(particles[particles.length - 1].pos, particles[1].pos, d2, a);
	}

	public void update(Vector3f areaDimensions) {
		if (frozen) {
			for (Particle particle : particles) {
				particle.reset();
			}
		} else {
			for (Particle particle : particles) {
				particle.update(areaDimensions);
			}
		}
	}

	public float getSafeDragDistance() {
		return maxLength * 0.2f;    //Arbitrary
	}

	public void setFrozen(boolean f) {
		frozen = f;
	}

	public boolean isFrozen() {
		return frozen;
	}
}
