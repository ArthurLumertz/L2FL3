package org.lwjgl.opengl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Display {

	private static long window;

	private static int prevW, prevH;
	private static DisplayMode displayMode;
	private static String title = "Game";
	private static boolean vsync = false;

	public static void setDisplayMode(DisplayMode displayMode) {
		Display.prevW = displayMode.getWidth();
		Display.prevH = displayMode.getHeight();
		Display.displayMode = displayMode;
		if (window != NULL) {
			GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowSize(window, displayMode.getWidth(), displayMode.getHeight());
			int wx = (videoMode.width() - displayMode.getWidth()) / 2;
			int wy = (videoMode.height() - displayMode.getHeight()) / 2;
			glfwSetWindowPos(window, wx, wy);
		}
	}

	public static void setTitle(String title) {
		if (title == null)
			title = "";
		Display.title = title;
		if (Display.window != NULL)
			glfwSetWindowTitle(Display.window, title);
	}

	public static void setFullscreen(boolean fullscreen) {
		Display.displayMode.setFullscreen(fullscreen);
		if (window != NULL) {
			GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			if (fullscreen) {
				Display.displayMode.setWidth(videoMode.width());
				Display.displayMode.setHeight(videoMode.height());
				glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, videoMode.width(), videoMode.height(),
						videoMode.refreshRate());
			} else {
				Display.displayMode.setWidth(Display.prevW);
				Display.displayMode.setHeight(Display.prevH);
				int wx = (videoMode.width() - Display.displayMode.getWidth()) / 2;
				int wy = (videoMode.height() - Display.displayMode.getHeight()) / 2;
				glfwSetWindowPos(window, wx, wy);
				glfwSetWindowSize(window, Display.displayMode.getWidth(), Display.displayMode.getHeight());
			}
		}
	}

	public static void setVSyncEnabled(boolean sync) {
		Display.vsync = sync;
	}

	public static void create() throws LWJGLException {
		if (!glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW!");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		Display.window = glfwCreateWindow(Display.displayMode.getWidth(), Display.displayMode.getHeight(),
				Display.title, Display.displayMode.isFullscreen() ? glfwGetPrimaryMonitor() : NULL, NULL);
		if (Display.window == NULL) {
			throw new RuntimeException("Failed to create GLFW window!");
		}

		if (!displayMode.isFullscreen()) {
			GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			int wx = (videoMode.width() - Display.displayMode.getWidth()) / 2;
			int wy = (videoMode.height() - Display.displayMode.getHeight()) / 2;
			glfwSetWindowPos(Display.window, wx, wy);
		}

		glfwSetKeyCallback(Display.window, new Keyboard());
		glfwSetCursorPosCallback(Display.window, new Mouse(Display.window));
		glfwSetMouseButtonCallback(Display.window, new Mouse.MouseButtons());

		glfwMakeContextCurrent(Display.window);
		GL.createCapabilities();

		glfwSwapInterval(Display.vsync ? 1 : 0);

		glfwShowWindow(Display.window);
	}

	public static void update() {
		glfwSwapBuffers(Display.window);
		glfwPollEvents();
	}

	public static void destroy() {
		glfwTerminate();
	}

	public static boolean isCloseRequested() {
		return glfwWindowShouldClose(Display.window);
	}

	public static DisplayMode getDisplayMode() {
		return Display.displayMode;
	}

	public static int getWidth() {
		return Display.displayMode.getWidth();
	}

	public static int getHeight() {
		return Display.displayMode.getHeight();
	}

}
