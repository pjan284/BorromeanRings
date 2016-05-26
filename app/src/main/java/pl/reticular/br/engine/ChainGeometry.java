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

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.reticular.br.model.Chain;
import pl.reticular.br.model.Particle;
import pl.reticular.br.utils.Vector3f;

public class ChainGeometry {

	public static final int DrawMode = GLES20.GL_LINE_LOOP;

	public static final int PositionSize = 3;

	private int numVertices;

	public FloatBuffer positionBuffer;

	public ChainGeometry(Chain chain) {
		numVertices = chain.particles.length;
		positionBuffer = createFloatBuffer(numVertices * 3);
		update(chain);
	}

	public void draw() {
		GLES20.glDrawArrays(ChainGeometry.DrawMode, 0, numVertices);
	}

	private FloatBuffer createFloatBuffer(int size) {
		FloatBuffer floatBuffer = ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatBuffer.position(0);
		return floatBuffer;
	}

	public void update(Chain chain) {
		Particle particles[] = chain.particles;
		positionBuffer.position(0);
		for (Particle particle : particles) {
			addSegment(particle.pos);
		}
		positionBuffer.position(0);
	}

	private void addSegment(Vector3f p1) {
		positionBuffer.put(p1.X);
		positionBuffer.put(p1.Y);
		positionBuffer.put(p1.Z);
	}
}
