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
import android.util.Log;

abstract class Shader {
	protected int program;

	public Shader() {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShaderCode());
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShaderCode());

		program = GLES20.glCreateProgram();
		checkGlError("glCreateProgram");

		GLES20.glAttachShader(program, vertexShader);
		checkGlError("glAttachShader");

		GLES20.glAttachShader(program, fragmentShader);
		checkGlError("glAttachShader");

		GLES20.glLinkProgram(program);
		checkGlError("glLinkProgram");

		if (!GLES20.glIsProgram(program)) {
			Log.e(getClass().getName(), "Cannot initialize shader");
		}
	}

	protected abstract String getVertexShaderCode();

	protected abstract String getFragmentShaderCode();

	private void checkGlError(String glOperation) {
		int error = GLES20.glGetError();
		while (error != GLES20.GL_NO_ERROR) {
			Log.e(getClass().getName(), String.format("%s: Error %d", glOperation, error));
			error = GLES20.glGetError();
		}
	}

	private int loadShader(int shaderType, String shaderCode) {
		int shader = GLES20.glCreateShader(shaderType);
		checkGlError("glCreateShader");

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		checkGlError("glShaderSource");

		GLES20.glCompileShader(shader);
		checkGlError("glCompileShader");

		return shader;
	}
}
