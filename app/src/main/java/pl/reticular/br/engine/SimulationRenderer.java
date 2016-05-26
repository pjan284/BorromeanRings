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

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.reticular.br.model.Chain;
import pl.reticular.br.model.Particle;
import pl.reticular.br.model.Simulation;
import pl.reticular.br.utils.MathUtils;
import pl.reticular.br.utils.Ray;
import pl.reticular.br.utils.Vector3f;

public class SimulationRenderer implements GLSurfaceView.Renderer {
	private int viewportWidth;
	private int viewportHeight;
	private Vector3f areaDimensions;
	private float scale;

	private float mvMatrix[];
	private float pMatrix[];

	private Simulation simulation;
	private ArrayList<ChainGeometry> chainGeometries;
	private ArrayList<float[]> chainColors;

	private SimpleShader simpleShader;

	private float mouseX, mouseY;
	private Particle dragged;
	private float safeDragDistance;

	private float lineWidth;

	public SimulationRenderer(Simulation simulation) {
		viewportWidth = 1;
		viewportHeight = 1;
		areaDimensions = new Vector3f();
		scale = 1.0f;

		mvMatrix = new float[16];
		pMatrix = new float[16];

		this.simulation = simulation;

		createDisplayBuffers();

		mouseX = mouseY = 0.0f;
		dragged = null;
		safeDragDistance = simulation.getSafeDragDistance();

		lineWidth = 1.0f;
	}

	private void createDisplayBuffers() {
		chainGeometries = new ArrayList<>();
		chainColors = new ArrayList<>();
		for (Chain chain : simulation.getChains()) {
			ChainGeometry chainGeometry = new ChainGeometry(chain);
			chainGeometries.add(chainGeometry);

			float color[] = {
					Color.red(chain.color) / 255.0f,
					Color.green(chain.color) / 255.0f,
					Color.blue(chain.color) / 255.0f,
					1.0f};
			chainColors.add(color);
		}
	}

	void setCamera(float[] matrix) {
		Matrix.setIdentityM(matrix, 0);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		GLES20.glLineWidth(lineWidth);

		setCamera(mvMatrix);

		synchronized (this) {
			simulation.update(areaDimensions);

			for (int i = 0; i < chainGeometries.size(); i++) {
				chainGeometries.get(i).update(simulation.getChains().get(i));
			}

			for (int i = 0; i < chainGeometries.size(); i++) {
				simpleShader.draw(mvMatrix, pMatrix, chainGeometries.get(i), chainColors.get(i));
			}
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClearDepthf(1.0f);

		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Called after the surface is created
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		viewportWidth = width;
		viewportHeight = height;

		Matrix.setIdentityM(pMatrix, 0);
		float ratio;
		if (height > width) {
			ratio = (float) height / (float) width;
			Matrix.orthoM(pMatrix, 0, -1.0f, 1.0f, -ratio, ratio, -1.0f, 1.0f);
			areaDimensions.set(1.0f, ratio, 1.0f);
			scale = 1.0f / width;
		} else {
			ratio = (float) width / (float) height;
			Matrix.orthoM(pMatrix, 0, -ratio, ratio, -1.0f, 1.0f, -1.0f, 1.0f);
			areaDimensions.set(ratio, 1.0f, 1.0f);
			scale = 1.0f / height;
		}
		Matrix.setIdentityM(mvMatrix, 0);

		simpleShader = new SimpleShader();
	}

	public boolean onTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mouseX = e.getX();
				mouseY = e.getY();

				int viewport[] = {0, 0, viewportWidth, viewportHeight};
				Ray ray = MathUtils.unProjectMatrices(mvMatrix, pMatrix, mouseX, viewportHeight - mouseY, viewport);
				dragged = simulation.selectParticle(ray, 0.1f);

				break;
			case MotionEvent.ACTION_MOVE:
				if (dragged != null) {
					float dx = (e.getX() - mouseX) * scale;
					float dy = (e.getY() - mouseY) * scale;

					Vector3f distance = new Vector3f(dx, -dy, dx * dy);
					if (distance.length() > safeDragDistance) {
						distance.scale(safeDragDistance / distance.length());
					}
					dragged.pos.add(distance);
				}

				mouseX = e.getX();
				mouseY = e.getY();
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				//game.getFinger().cancelTracking();
				break;
		}

		return true;
	}

	public void setLineWidth(float width) {
		lineWidth = width;
	}

	public void setSimulation(Simulation sim) {
		synchronized (this) {
			simulation = sim;
			createDisplayBuffers();
		}
	}

	public Simulation getSimulation() {
		return simulation;
	}
}