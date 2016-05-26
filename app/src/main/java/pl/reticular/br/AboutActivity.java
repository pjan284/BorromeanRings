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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_about);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		int versionCode = BuildConfig.VERSION_CODE;
		String versionName = BuildConfig.VERSION_NAME;
		String version = String.format(Locale.US, "v. %s (%d)", versionName, versionCode);

		TextView appVersion = (TextView) findViewById(R.id.app_version);
		if (appVersion != null) {
			appVersion.setText(version);
		}

		Button wikipediaButton = (Button) findViewById(R.id.button_wikipedia);
		if (wikipediaButton != null) {
			wikipediaButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					goToWikipedia();
				}
			});
		}

		Button licenseButton = (Button) findViewById(R.id.button_license);
		if (licenseButton != null) {
			licenseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showLicense();
				}
			});
		}
	}

	private void goToWikipedia() {
		//TODO: Go to Wikipedia app
		String url = "https://en.wikipedia.org/wiki/Borromean_rings";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(getClass().getName(), "cannot open browser");
		}
	}

	private void showLicense() {
		String url = "https://www.gnu.org/licenses/gpl.html";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(getClass().getName(), "cannot open browser");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}
