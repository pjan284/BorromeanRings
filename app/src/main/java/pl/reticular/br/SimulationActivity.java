package pl.reticular.br;

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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import pl.reticular.br.dialogs.FreezeRingsDialog;
import pl.reticular.br.dialogs.LineWidthDialog;
import pl.reticular.br.dialogs.SegmentNumberDialog;
import pl.reticular.br.engine.SimulationView;
import pl.reticular.br.model.ChainColor;
import pl.reticular.br.model.Simulation;
import pl.reticular.br.utils.PrefsHelper;
import pl.reticular.br.utils.Setting;

public class SimulationActivity extends AppCompatActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener,
		FreezeRingsDialog.OnRingClickedListener {

	private SimulationView simulationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.simulation_layout);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_simulation);
		setSupportActionBar(toolbar);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.simulation_container);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				Gravity.CENTER);

		PrefsHelper.getInstance().init(this);

		Simulation simulation;

		Simulation oldSimulation = PrefsHelper.getInstance().loadOldSimulation();
		if (oldSimulation != null) {
			simulation = oldSimulation;
		} else {
			simulation = createNewSimulation();
		}

		simulationView = new SimulationView(this, simulation);

		if (frameLayout != null) {
			frameLayout.addView(simulationView, params);
		}

		setLineWidth(PrefsHelper.getInstance().getLineWidth());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.simulation_menu, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();

		PrefsHelper.getInstance().getPrefs().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		PrefsHelper.getInstance().getPrefs().unregisterOnSharedPreferenceChangeListener(this);

		PrefsHelper.getInstance().saveSimulation(simulationView.getRenderer().getSimulation());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_line_width:
				showLineWidthDialog();
				break;
			case R.id.action_segment_number:
				showSegmentNumberDialog();
				break;
			case R.id.action_freeze_chains:
				showFreezeChainsDialog();
				break;
			case R.id.action_reset:
				resetSimulation();
				break;
			case R.id.action_about:
				showAbout();
				break;
			case android.R.id.home:
				onBackPressed();
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		switch (Setting.valueOf(key)) {
			case LineWidth:
				setLineWidth(PrefsHelper.getInstance().getLineWidth());
				break;
			case SegmentsNumber:
				resetSimulation();
				break;
			default:
				break;
		}
	}

	private void showLineWidthDialog() {
		DialogFragment dialog = new LineWidthDialog();
		dialog.show(getSupportFragmentManager(), "LineWidthDialog");
	}

	private void showSegmentNumberDialog() {
		DialogFragment dialog = new SegmentNumberDialog();
		dialog.show(getSupportFragmentManager(), "SegmentNumberDialog");
	}

	private void showFreezeChainsDialog() {
		FreezeRingsDialog dialog = new FreezeRingsDialog();
		dialog.setListener(this);
		dialog.setFrozen(simulationView.getRenderer().getSimulation().getFrozenChains());
		dialog.show(getSupportFragmentManager(), "FreezeRingsDialog");
	}

	private void showAbout() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}

	private void setLineWidth(int width) {
		simulationView.getRenderer().setLineWidth(width + 1);
	}

	private Simulation createNewSimulation() {
		return new Simulation(PrefsHelper.getInstance().getSegmentNumber());
	}

	private void resetSimulation() {
		Simulation simulation = createNewSimulation();
		simulationView.getRenderer().setSimulation(simulation);
	}

	@Override
	public void onRingClicked(ChainColor color, boolean checked) {
		simulationView.getRenderer().setFrozenChain(color, checked);
	}
}
