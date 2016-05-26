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

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.reticular.br.utils.EqualPartsEllipse;
import pl.reticular.br.utils.MathUtils;
import pl.reticular.br.utils.Ray;
import pl.reticular.br.utils.Savable;
import pl.reticular.br.utils.Vector2d;
import pl.reticular.br.utils.Vector3f;

public class Simulation implements Savable {

	private ArrayList<Chain> chains;

	private enum Keys {
		Chains
	}

	public Simulation(int segments) {
		chains = new ArrayList<>();

		ArrayList<Vector2d> points = EqualPartsEllipse.getInstance().calcPoints(0.8, 0.4, segments);

		Chain chain1 = new Chain(
				points,
				new Vector3f(0.0f, 0.0f, 1.0f),
				0.0f,
				Color.RED
		);
		chains.add(chain1);

		ArrayList<Vector2d> points2 = new ArrayList<>();
		for (Vector2d point : points) {
			points2.add(new Vector2d(point.Y, point.X));
		}

		Chain chain2 = new Chain(
				points2,
				new Vector3f(0.0f, 1.0f, 0.0f),
				75.0f,
				Color.GREEN
		);
		chains.add(chain2);

		Chain chain3 = new Chain(
				points2,
				new Vector3f(1.0f, 0.0f, 0.0f),
				-75.0f,
				Color.BLUE
		);
		chains.add(chain3);
	}

	public Simulation(JSONObject json) throws JSONException {
		chains = new ArrayList<>();

		JSONArray chainStates = json.getJSONArray(Keys.Chains.toString());

		for (int i = 0; i < chainStates.length(); i++) {
			JSONObject chainState = chainStates.getJSONObject(i);
			Chain chain = new Chain(chainState);
			chains.add(chain);
		}
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		JSONArray chainStates = new JSONArray();

		for (Chain chain : chains) {
			JSONObject chainState = chain.toJSON();
			chainStates.put(chainState);
		}

		state.put(Keys.Chains.toString(), chainStates);

		return state;
	}

	public float getSafeDragDistance() {
		float min = 1000.0f;
		for (Chain chain : chains) {
			float current = chain.getSafeDragDistance();
			if (current < min) {
				min = current;
			}
		}
		return min;
	}

	public ArrayList<Chain> getChains() {
		return chains;
	}

	public Particle selectParticle(Ray ray, float r) {
		float minDistance = r;
		Particle chosen = null;
		for (Chain chain : chains) {
			for (Particle particle : chain.particles) {
				float d = MathUtils.distance(particle.pos, ray);
				if (d <= minDistance) {
					minDistance = d;
					chosen = particle;
				}
			}
		}
		return chosen;
	}

	public void update(Vector3f areaDimensions) {
		for (Chain chain : chains) {
			chain.resolve();
		}

		for (Chain chain : chains) {
			chain.resolveSelfCollisions();
		}

		for (int i = 0; i < chains.size(); i++) {
			for (int j = i + 1; j < chains.size(); j++) {
				chains.get(i).resolveOtherCollisions(chains.get(j));
			}
		}

		for (Chain chain : chains) {
			chain.circularize();
		}

		for (Chain chain : chains) {
			chain.update(areaDimensions);
		}
	}
}
