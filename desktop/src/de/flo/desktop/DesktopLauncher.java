package de.flo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.flo.BrickBreakerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 11 * 32;
		config.height = 20 * 32;
		new LwjglApplication(new BrickBreakerGame(), config);
	}
}
