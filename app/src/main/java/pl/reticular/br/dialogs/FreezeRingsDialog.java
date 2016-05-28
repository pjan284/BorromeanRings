package pl.reticular.br.dialogs;

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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.EnumMap;

import pl.reticular.br.R;
import pl.reticular.br.model.ChainColor;

public class FreezeRingsDialog extends DialogFragment {

	public interface OnRingClickedListener {
		void onRingClicked(ChainColor color, boolean checked);
	}

	private OnRingClickedListener listener;

	private EnumMap<ChainColor, Boolean> frozen;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(R.string.freeze_rings)
				.setMultiChoiceItems(makeNameArray(getContext()), makeCheckedArray(),
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which,
							                    boolean isChecked) {
								ChainColor color = ChainColor.values()[which];
								listener.onRingClicked(color, isChecked);
							}
						})
				.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});

		return builder.create();
	}

	public void setListener(OnRingClickedListener l) {
		listener = l;
	}

	public void setFrozen(EnumMap<ChainColor, Boolean> f) {
		frozen = f;
	}

	private CharSequence[] makeNameArray(Context context) {
		CharSequence arr[] = new CharSequence[ChainColor.values().length];
		Resources resources = context.getResources();
		for (int i = 0; i < ChainColor.values().length; i++) {
			switch (ChainColor.values()[i]) {
				case Red:
					arr[i] = resources.getString(R.string.red);
					break;
				case Green:
					arr[i] = resources.getString(R.string.green);
					break;
				case Blue:
					arr[i] = resources.getString(R.string.blue);
					break;
				default:
					arr[i] = "";
					break;
			}
		}

		return arr;
	}

	private boolean[] makeCheckedArray() {
		boolean arr[] = new boolean[frozen.size()];
		for (int i = 0; i < ChainColor.values().length; i++) {
			arr[i] = frozen.get(ChainColor.values()[i]);
		}

		return arr;
	}
}
