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

import java.util.ArrayList;
import java.util.EnumMap;

import pl.reticular.br.utils.EqualPartsEllipse;
import pl.reticular.br.utils.MathUtils;
import pl.reticular.br.utils.Ray;
import pl.reticular.br.utils.Savable;
import pl.reticular.br.utils.Vector2d;
import pl.reticular.br.utils.Vector3f;

public class Simulation implements Savable {

	private EnumMap<ChainColor, Chain> chains;

	private enum Keys {
		Chains
	}

	public Simulation(int segments) {
		chains = new EnumMap<>(ChainColor.class);

		ArrayList<Vector2d> points = EqualPartsEllipse.getInstance().calcPoints(0.8, 0.4, segments);

		chains.put(ChainColor.Red, new Chain(
						points,
						new Vector3f(0.0f, 0.0f, 1.0f),
						0.0f
				)
		);

		ArrayList<Vector2d> points2 = new ArrayList<>();
		for (Vector2d point : points) {
			points2.add(new Vector2d(point.Y, point.X));
		}

		chains.put(ChainColor.Green, new Chain(
						points2,
						new Vector3f(0.0f, 1.0f, 0.0f),
						75.0f
				)
		);

		chains.put(ChainColor.Blue, new Chain(
						points2,
						new Vector3f(1.0f, 0.0f, 0.0f),
						-75.0f
				)
		);

		for (ChainColor chainColor : ChainColor.values()) {
			chains.get(chainColor);
		}
	}

	public Simulation(JSONObject json) throws JSONException {
		chains = new EnumMap<>(ChainColor.class);

		JSONObject chainStates = json.getJSONObject(Keys.Chains.toString());

		for (ChainColor chainColor : ChainColor.values()) {
			JSONObject chainState = chainStates.getJSONObject(chainColor.name());
			chains.put(chainColor, new Chain(chainState));
		}
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		JSONObject chainStates = new JSONObject();

		for (ChainColor chainColor : ChainColor.values()) {
			JSONObject chainState = chains.get(chainColor).toJSON();
			chainStates.put(chainColor.name(), chainState);
		}

		state.put(Keys.Chains.toString(), chainStates);

		return state;
	}

	public float getSafeDragDistance() {
		float min = 1000.0f;
		for (ChainColor chainColor : ChainColor.values()) {
			float current = chains.get(chainColor).getSafeDragDistance();
			if (current < min) {
				min = current;
			}
		}
		return min;
	}

	public EnumMap<ChainColor, Chain> getChains() {
		return chains;
	}

	public Particle selectParticle(Ray ray, float r) {
		float minDistance = r;
		Particle chosen = null;
		for (ChainColor chainColor : ChainColor.values()) {
			for (Particle particle : chains.get(chainColor).particles) {
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
		ChainColor colors[] = ChainColor.values();

		for (ChainColor color : colors) {
			chains.get(color).resolve();
		}

		for (ChainColor color : colors) {
			chains.get(color).resolveSelfCollisions();
		}

		for (int i = 0; i < colors.length; i++) {
			for (int j = i + 1; j < colors.length; j++) {
				chains.get(colors[i]).resolveMutualCollisions(chains.get(colors[j]));
			}
		}

		for (ChainColor color : colors) {
			chains.get(color).circularize();
		}

		for (ChainColor color : colors) {
			chains.get(color).update(areaDimensions);
		}

	}

	public void setFrozenChain(ChainColor color, boolean frozen) {
		chains.get(color).setFrozen(frozen);
	}

	public EnumMap<ChainColor, Boolean> getFrozenChains() {
		EnumMap<ChainColor, Boolean> map = new EnumMap<>(ChainColor.class);
		for (ChainColor color : ChainColor.values()) {
			map.put(color, chains.get(color).isFrozen());
		}
		return map;
	}
}
