package pl.reticular.br.engine;

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
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import pl.reticular.br.model.Simulation;

public class SimulationView extends GLSurfaceView {

	SimulationRenderer renderer;

	public SimulationView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public SimulationView(Context context, Simulation simulation) {
		super(context);
		renderer = new SimulationRenderer(simulation);

		setEGLContextClientVersion(2);

		// this should provide depth buffer
		setEGLConfigChooser(true);

		setRenderer(renderer);

		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return renderer.onTouchEvent(e);
	}

	public SimulationRenderer getRenderer() {
		return renderer;
	}
}
