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
import android.opengl.Matrix;

import java.nio.FloatBuffer;

public class SimpleShader extends Shader {

	// Handlers to attributes
	private int positionHandle;

	// Handlers to uniforms
	private int MVPMatrixHandle;
	private int colorHandle;

	// No need to alloc this every frame
	protected float mvpMatrix[];

	public SimpleShader() {
		// Get handlers to attributes
		positionHandle = GLES20.glGetAttribLocation(program, "a_Position");

		// Get handlers to uniforms
		MVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
		colorHandle = GLES20.glGetUniformLocation(program, "u_Color");

		mvpMatrix = new float[16];
	}

	@Override
	protected String getVertexShaderCode() {
		return "uniform mat4 u_MVPMatrix;\n" +
				"attribute vec4 a_Position;\n" +
				"uniform vec4 u_Color;\n" +

				"varying vec4 v_Color;\n" +

				"void main() {\n" +
				"	gl_Position = u_MVPMatrix * a_Position;\n" +
				"   float a = 0.25 + 0.375 * (-gl_Position.z + 1.0);\n" +
				"	v_Color.r = u_Color.r * a;\n" +
				"	v_Color.g = u_Color.g * a;\n" +
				"	v_Color.b = u_Color.b * a;\n" +
				"	v_Color.a = 1.0;\n" +
				"}\n";
	}

	@Override
	protected String getFragmentShaderCode() {
		return "#ifdef GL_ES\n" +
				"precision highp float;\n" +
				"#endif\n" +

				"varying vec4 v_Color;\n" +

				"void main(void) {\n" +
				"	gl_FragColor = v_Color;\n" +
				"}\n";
	}

	public void draw(float mvMatrix[], float pMatrix[], ChainGeometry model, float color[]) {
		Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);

		FloatBuffer positionsBuffer = model.positionBuffer;

		GLES20.glUseProgram(program);

		// Apply positions
		GLES20.glVertexAttribPointer(positionHandle, ChainGeometry.PositionSize, GLES20.GL_FLOAT, false, 0, positionsBuffer);

		GLES20.glEnableVertexAttribArray(positionHandle);

		// Apply the MVP matrix
		GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);

		//Apply Colors
		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		// Draw
		model.draw();

		// Disable attributes
		GLES20.glDisableVertexAttribArray(positionHandle);
	}
}
