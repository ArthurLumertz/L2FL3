package org.lwjgl.util.glu;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class GLU extends Util {
	
	public static final int GLU_INVALID_ENUM        = 100900;
	public static final int GLU_INVALID_VALUE       = 100901;
	public static final int GLU_OUT_OF_MEMORY       = 100902;
	public static final int GLU_INCOMPATIBLE_GL_VERSION     = 100903;
	public static final int GLU_VERSION             = 100800;
	public static final int GLU_EXTENSIONS          = 100801;
	public static final boolean GLU_TRUE                = true;
	public static final boolean GLU_FALSE               = false;
	public static final int GLU_SMOOTH              = 100000;
	public static final int GLU_FLAT                = 100001;
	public static final int GLU_NONE                = 100002;
	public static final int GLU_POINT               = 100010;
	public static final int GLU_LINE                = 100011;
	public static final int GLU_FILL                = 100012;
	public static final int GLU_SILHOUETTE          = 100013;
	public static final int GLU_OUTSIDE             = 100020;
	public static final int GLU_INSIDE              = 100021;

	private static final float[] IDENTITY_MATRIX = new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

	private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer finalMatrix = BufferUtils.createFloatBuffer(16);

	private static final FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
	private static final float[] in = new float[4];
	private static final float[] out = new float[4];

	private static final float[] forward = new float[3];
	private static final float[] side = new float[3];
	private static final float[] up = new float[3];

	/**
	 * Make matrix an identity matrix
	 */
	private static void __gluMakeIdentityf(FloatBuffer m) {
		int oldPos = m.position();
		m.put(IDENTITY_MATRIX);
		m.position(oldPos);
	}

	/**
	 * Method __gluMultMatrixVecf
	 *
	 * @param finalMatrix
	 * @param in
	 * @param out
	 */
	private static void __gluMultMatrixVecf(FloatBuffer m, float[] in, float[] out) {
		for (int i = 0; i < 4; i++) {
			out[i] = in[0] * m.get(m.position() + 0 * 4 + i) + in[1] * m.get(m.position() + 1 * 4 + i)
					+ in[2] * m.get(m.position() + 2 * 4 + i) + in[3] * m.get(m.position() + 3 * 4 + i);

		}
	}

	/**
	 * @param src
	 * @param inverse
	 *
	 * @return true if the matrix was succesfully inverted
	 */
	private static boolean __gluInvertMatrixf(FloatBuffer src, FloatBuffer inverse) {
		int i, j, k, swap;
		float t;
		FloatBuffer temp = GLU.tempMatrix;

		for (i = 0; i < 16; i++) {
			temp.put(i, src.get(i + src.position()));
		}
		__gluMakeIdentityf(inverse);

		for (i = 0; i < 4; i++) {
			/*
			 * * Look for largest element in column
			 */
			swap = i;
			for (j = i + 1; j < 4; j++) {
				/*
				 * if (fabs(temp[j][i]) > fabs(temp[i][i])) { swap = j;
				 */
				if (Math.abs(temp.get(j * 4 + i)) > Math.abs(temp.get(i * 4 + i))) {
					swap = j;
				}
			}

			if (swap != i) {
				/*
				 * * Swap rows.
				 */
				for (k = 0; k < 4; k++) {
					t = temp.get(i * 4 + k);
					temp.put(i * 4 + k, temp.get(swap * 4 + k));
					temp.put(swap * 4 + k, t);

					t = inverse.get(i * 4 + k);
					inverse.put(i * 4 + k, inverse.get(swap * 4 + k));
					// inverse.put((i << 2) + k, inverse.get((swap << 2) + k));
					inverse.put(swap * 4 + k, t);
					// inverse.put((swap << 2) + k, t);
				}
			}

			if (temp.get(i * 4 + i) == 0) {
				/*
				 * * No non-zero pivot. The matrix is singular, which shouldn't * happen. This
				 * means the user gave us a bad matrix.
				 */
				return false;
			}

			t = temp.get(i * 4 + i);
			for (k = 0; k < 4; k++) {
				temp.put(i * 4 + k, temp.get(i * 4 + k) / t);
				inverse.put(i * 4 + k, inverse.get(i * 4 + k) / t);
			}
			for (j = 0; j < 4; j++) {
				if (j != i) {
					t = temp.get(j * 4 + i);
					for (k = 0; k < 4; k++) {
						temp.put(j * 4 + k, temp.get(j * 4 + k) - temp.get(i * 4 + k) * t);
						inverse.put(j * 4 + k, inverse.get(j * 4 + k) - inverse.get(i * 4 + k) * t);
						/*
						 * inverse.put( (j << 2) + k, inverse.get((j << 2) + k) - inverse.get((i << 2) +
						 * k) * t);
						 */
					}
				}
			}
		}
		return true;
	}

	/**
	 * @param a
	 * @param b
	 * @param r
	 */
	private static void __gluMultMatricesf(FloatBuffer a, FloatBuffer b, FloatBuffer r) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				r.put(r.position() + i * 4 + j,
						a.get(a.position() + i * 4 + 0) * b.get(b.position() + 0 * 4 + j)
								+ a.get(a.position() + i * 4 + 1) * b.get(b.position() + 1 * 4 + j)
								+ a.get(a.position() + i * 4 + 2) * b.get(b.position() + 2 * 4 + j)
								+ a.get(a.position() + i * 4 + 3) * b.get(b.position() + 3 * 4 + j));
			}
		}
	}

	public static void gluOrtho2D(float left, float right, float bottom, float top) {

		glOrtho(left, right, bottom, top, -1.0, 1.0);
	}

	/**
	 * Method gluPerspective.
	 *
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
		float sine, cotangent, deltaZ;
		float radians = (float) (fovy / 2 * Math.PI / 180);

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return;
		}

		cotangent = (float) Math.cos(radians) / sine;

		__gluMakeIdentityf(matrix);

		matrix.put(0 * 4 + 0, cotangent / aspect);
		matrix.put(1 * 4 + 1, cotangent);
		matrix.put(2 * 4 + 2, -(zFar + zNear) / deltaZ);
		matrix.put(2 * 4 + 3, -1);
		matrix.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
		matrix.put(3 * 4 + 3, 0);

		GL11.glMultMatrixf(matrix);
	}

	/**
	 * Method gluLookAt
	 *
	 * @param eyex
	 * @param eyey
	 * @param eyez
	 * @param centerx
	 * @param centery
	 * @param centerz
	 * @param upx
	 * @param upy
	 * @param upz
	 */
	public static void gluLookAt(float eyex, float eyey, float eyez, float centerx, float centery, float centerz,
			float upx, float upy, float upz) {
		float[] forward = GLU.forward;
		float[] side = GLU.side;
		float[] up = GLU.up;

		forward[0] = centerx - eyex;
		forward[1] = centery - eyey;
		forward[2] = centerz - eyez;

		up[0] = upx;
		up[1] = upy;
		up[2] = upz;

		normalize(forward);

		/* Side = forward x up */
		cross(forward, up, side);
		normalize(side);

		/* Recompute up as: up = side x forward */
		cross(side, forward, up);

		__gluMakeIdentityf(matrix);
		matrix.put(0 * 4 + 0, side[0]);
		matrix.put(1 * 4 + 0, side[1]);
		matrix.put(2 * 4 + 0, side[2]);

		matrix.put(0 * 4 + 1, up[0]);
		matrix.put(1 * 4 + 1, up[1]);
		matrix.put(2 * 4 + 1, up[2]);

		matrix.put(0 * 4 + 2, -forward[0]);
		matrix.put(1 * 4 + 2, -forward[1]);
		matrix.put(2 * 4 + 2, -forward[2]);

		GL11.glMultMatrixf(matrix);
		glTranslatef(-eyex, -eyey, -eyez);
	}

	/**
	 * Method gluProject
	 *
	 * @param objx
	 * @param objy
	 * @param objz
	 * @param modelMatrix
	 * @param projMatrix
	 * @param viewport
	 * @param win_pos
	 */
	public static boolean gluProject(float objx, float objy, float objz, FloatBuffer modelMatrix,
			FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer win_pos) {

		float[] in = GLU.in;
		float[] out = GLU.out;

		in[0] = objx;
		in[1] = objy;
		in[2] = objz;
		in[3] = 1.0f;

		__gluMultMatrixVecf(modelMatrix, in, out);
		__gluMultMatrixVecf(projMatrix, out, in);

		if (in[3] == 0.0)
			return false;

		in[3] = (1.0f / in[3]) * 0.5f;

		// Map x, y and z to range 0-1
		in[0] = in[0] * in[3] + 0.5f;
		in[1] = in[1] * in[3] + 0.5f;
		in[2] = in[2] * in[3] + 0.5f;

		// Map x,y to viewport
		win_pos.put(0, in[0] * viewport.get(viewport.position() + 2) + viewport.get(viewport.position() + 0));
		win_pos.put(1, in[1] * viewport.get(viewport.position() + 3) + viewport.get(viewport.position() + 1));
		win_pos.put(2, in[2]);

		return true;
	}

	/**
	 * Method gluUnproject
	 *
	 * @param winx
	 * @param winy
	 * @param winz
	 * @param modelMatrix
	 * @param projMatrix
	 * @param viewport
	 * @param obj_pos
	 */
	public static boolean gluUnProject(float winx, float winy, float winz, FloatBuffer modelMatrix,
			FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer obj_pos) {
		float[] in = GLU.in;
		float[] out = GLU.out;

		__gluMultMatricesf(modelMatrix, projMatrix, finalMatrix);

		if (!__gluInvertMatrixf(finalMatrix, finalMatrix))
			return false;

		in[0] = winx;
		in[1] = winy;
		in[2] = winz;
		in[3] = 1.0f;

		// Map x and y from window coordinates
		in[0] = (in[0] - viewport.get(viewport.position() + 0)) / viewport.get(viewport.position() + 2);
		in[1] = (in[1] - viewport.get(viewport.position() + 1)) / viewport.get(viewport.position() + 3);

		// Map to range -1 to 1
		in[0] = in[0] * 2 - 1;
		in[1] = in[1] * 2 - 1;
		in[2] = in[2] * 2 - 1;

		__gluMultMatrixVecf(finalMatrix, in, out);

		if (out[3] == 0.0)
			return false;

		out[3] = 1.0f / out[3];

		obj_pos.put(obj_pos.position() + 0, out[0] * out[3]);
		obj_pos.put(obj_pos.position() + 1, out[1] * out[3]);
		obj_pos.put(obj_pos.position() + 2, out[2] * out[3]);

		return true;
	}

	/**
	 * Method gluPickMatrix
	 *
	 * @param x
	 * @param y
	 * @param deltaX
	 * @param deltaY
	 * @param viewport
	 */
	public static void gluPickMatrix(float x, float y, float deltaX, float deltaY, IntBuffer viewport) {
		if (deltaX <= 0 || deltaY <= 0) {
			return;
		}

		/* Translate and scale the picked region to the entire window */
		glTranslatef((viewport.get(viewport.position() + 2) - 2 * (x - viewport.get(viewport.position() + 0))) / deltaX,
				(viewport.get(viewport.position() + 3) - 2 * (y - viewport.get(viewport.position() + 1))) / deltaY, 0);
		glScalef(viewport.get(viewport.position() + 2) / deltaX, viewport.get(viewport.position() + 3) / deltaY, 1.0f);
	}
	
	public static int gluBuild2DMipmaps(
			int target,
			int components,
			int width,
			int height,
			int format,
			int type,
			ByteBuffer data) {

		return MipMap.gluBuild2DMipmaps(target, components, width, height, format, type, data);
	}
}