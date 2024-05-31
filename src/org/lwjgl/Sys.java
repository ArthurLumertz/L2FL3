package org.lwjgl;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import javax.swing.JOptionPane;

import org.lwjgl.input.Mouse;

public class Sys {

	public static void alert(String title, String message) {
		if (Mouse.isGrabbed()) {
			Mouse.setGrabbed(false);
		}
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public static String getClipboard() {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			Transferable transferable = clipboard.getContents(null);
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return (String) transferable.getTransferData(DataFlavor.stringFlavor);
			}
		} catch (UnsupportedFlavorException | IOException e) {
			System.out.println("Failed to get clipboard content: " + e.getMessage());
		}
		return null;
	}

	public static String getVersion() {
		return Version.getVersion();
	}

	public static boolean is64Bit() {
		return Objects.equals(System.getProperty("os.arch"), "amd64");
	}

	public static boolean openURL(String url) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(new URI(url));
				return true;
			} catch (IOException | java.net.URISyntaxException e) {
				System.out.println("Failed to open URL: " + e.getMessage());
			}
		}
		return false;
	}
	
	public static long getTimerResolution() {
        return 1000;
    }

    public static long getTime() {
        return System.nanoTime() & 0x7FFFFFFFFFFFFFFFL;
    }
    
    
}
