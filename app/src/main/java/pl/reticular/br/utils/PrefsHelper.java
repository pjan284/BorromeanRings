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

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.br.model.Simulation;

public class PrefsHelper {
	private static PrefsHelper instance = new PrefsHelper();

	public static PrefsHelper getInstance() {
		return instance;
	}

	private PrefsHelper() {
	}

	private SharedPreferences prefs;

	private static final String PREFS_NAME = "Preferences";

	private static final int DefaultLineWidth = 5;

	private static final int DefaultSegmentNumber = 48;

	public void init(Context context) {
		prefs = context.getSharedPreferences(PREFS_NAME, 0);
	}

	public SharedPreferences getPrefs() {
		return prefs;
	}

	public void remove(String key) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(key);
		editor.apply();
	}

	public void putString(String key, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.apply();
	}

	public int getLineWidth() {
		return prefs.getInt(Setting.LineWidth.toString(), DefaultLineWidth);
	}

	public void setLineWidth(int width) {
		putInt(Setting.LineWidth.toString(), width);
	}

	public int getSegmentNumber() {
		return prefs.getInt(Setting.SegmentsNumber.toString(), DefaultSegmentNumber);
	}

	public void setSegmentNumber(int number) {
		putInt(Setting.SegmentsNumber.toString(), number);
	}

	public void saveSimulation(Simulation simulation) {
		try {
			String data = simulation.toJSON().toString();
			putString(Setting.SimulationData.toString(), data);
		} catch (JSONException e) {
			clearSimulationData();
		}
	}

	private void clearSimulationData() {
		remove(Setting.SimulationData.toString());
	}

	public Simulation loadOldSimulation() {
		if (!prefs.contains(Setting.SimulationData.toString())) {
			return null;
		}

		try {
			String data = prefs.getString(Setting.SimulationData.toString(), "{}");
			JSONObject lastGameData = new JSONObject(data);
			return new Simulation(lastGameData);
		} catch (JSONException e) {
			clearSimulationData();
			return null;
		}
	}
}
