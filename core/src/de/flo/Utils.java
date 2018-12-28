package de.flo;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Utils {

    public static final int OFFSET = 2;
    public static final int BRICK_SIZE = 32;


    public static float getCoord(float x){
        return BRICK_SIZE * x;
    }

    public static int calcWorldXFromScreenX(int x, Viewport viewport){
        return (int)(x * (viewport.getWorldWidth() / Gdx.graphics.getWidth()));
    }

    public static int calcWorldYFromScreenY(int y, Viewport viewport){
        // Calc screen pixels to world width
        y = (int)(y * (viewport.getWorldHeight() / Gdx.graphics.getHeight()));
        // Invert coords (screen coords start top left - world down left)
        y = (int)((y * -1) + viewport.getWorldHeight());
        return y;
    }


}
