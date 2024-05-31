package org.lwjgl.input;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Mouse extends GLFWCursorPosCallback {

	private static float x;
	private static float y;
	private static float prevX;
	private static float prevY;

	private static boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST];
	private static boolean[] justPressedButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
	
	private static boolean isGrabbed = false;

	public static void create() throws LWJGLException {
	}

	public static void destroy() {
	}

	private static long window;

	public Mouse(long window) {
		Mouse.window = window;
	}

	public static void setGrabbed(boolean g) {
		Mouse.isGrabbed = g;
		if (g) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			return;
		}
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	public static boolean isGrabbed() {
		return Mouse.isGrabbed;
	}

	@Override
	public void invoke(long w, double xpos, double ypos) {
		prevX = x;
		prevY = y;
		x = (float) xpos;
		y = (float) ypos;
	}

	public static float getX() {
		return x;
	}

	public static float getY() {
		return y;
	}

	public static float getDX() {
		float dx = x - prevX;
		prevX = x;
		return dx;
	}

	public static float getDY() {
		float dy = prevY - y;
		prevY = y;
		return dy;
	}

	public static boolean isLeftClicked() {
		return buttons[GLFW_MOUSE_BUTTON_LEFT];
	}

	public static boolean isLeftJustClicked() {
		boolean r = justPressedButtons[GLFW_MOUSE_BUTTON_LEFT];
		justPressedButtons[GLFW_MOUSE_BUTTON_LEFT] = false;
		return r;
	}

	public static boolean isRightClicked() {
		return buttons[GLFW_MOUSE_BUTTON_RIGHT];
	}

	public static boolean isRightJustClicked() {
		boolean r = justPressedButtons[GLFW_MOUSE_BUTTON_RIGHT];
		justPressedButtons[GLFW_MOUSE_BUTTON_RIGHT] = false;
		return r;
	}

	public static boolean isMiddleClicked() {
		return buttons[GLFW_MOUSE_BUTTON_MIDDLE];
	}

	public static boolean isMiddleJustClicked() {
		boolean r = justPressedButtons[GLFW_MOUSE_BUTTON_MIDDLE];
		justPressedButtons[GLFW_MOUSE_BUTTON_MIDDLE] = false;
		return r;
	}

	public static class MouseButtons extends GLFWMouseButtonCallback {

		@Override
		public void invoke(long w, int button, int action, int mods) {
			Mouse.buttons[button] = GLFW_PRESS == action;
			Mouse.justPressedButtons[button] = GLFW_PRESS == action;
		}

	}

}
