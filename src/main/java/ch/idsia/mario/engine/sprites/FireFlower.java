package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;


public class FireFlower extends Sprite
{
    @SuppressWarnings("unused")
	private int width = 4;
    int height = 24;

    private LevelScene world;
    public int facing;

    public boolean avoidCliffs = false;
    private int life;

    public FireFlower(LevelScene world, int x, int y)
    {
        kind = KIND_FIRE_FLOWER;
        sheet = Art.items;

        this.x = x;
        this.y = y;
        this.world = world;
        xPicO = 8;
        yPicO = 15;

        xPic = 1;
        yPic = 0;
        height = 12;
        facing = 1;
        wPic  = hPic = 16;
        life = 0;
    }

    public void collideCheck()
    {
        // Allow for extra playful Mario
        Mario[] marios = new Mario[LevelScene.TWO_PLAYERS ? 2 : 1];
        marios[0] = world.mario;
        if(LevelScene.TWO_PLAYERS) {
        	marios[1] = world.mario2;
        }

        for(Mario mario : marios) {
        	float xMarioD = mario.x - x;
        	float yMarioD = mario.y - y;
        	@SuppressWarnings("unused")
        	float w = 16;
        	if (xMarioD > -16 && xMarioD < 16)
        	{
        		if (yMarioD > -height && yMarioD < mario.height)
        		{
        			mario.getFlower();
        			spriteContext.removeSprite(this);
        		}
        	}
        }
    }

    public void move()
    {
        if (life<9)
        {
            layer = 0;
            y--;
            life++;
            return;
        }
    }
}