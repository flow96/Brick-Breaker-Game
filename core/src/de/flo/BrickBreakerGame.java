package de.flo;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.flo.screens.GameScreen;

public class BrickBreakerGame extends Game {
	SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new GameScreen(batch));
	}


	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
