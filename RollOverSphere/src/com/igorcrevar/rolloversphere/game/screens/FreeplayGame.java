package com.igorcrevar.rolloversphere.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.igorcrevar.rolloversphere.game.GameTypes;
import com.igorcrevar.rolloversphere.objects.boxes.factory.FreeplayBoxesFactory;
import com.igorcrevar.rolloversphere.objects.boxes.factory.IBoxesFactory;

public class FreeplayGame extends TheGame{

	public FreeplayGame(Game game) {
		super(game);		
	}

	@Override
	protected IBoxesFactory getBoxesFactory() {		
		return new FreeplayBoxesFactory();
	}

	@Override
	protected void renderGameSpecific(float timeDiff) {
		mSpriteBatch.begin();
		//draw player points
		mFont.setColor(Color.WHITE);
		mFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		String str = String.format("Points: %d", mPointsManager.getScore());
		mFont.draw(mSpriteBatch, str, getScreenX(5.0f), getScreenY(5.0f));	
		
		upgradeTimeoutUpdateAndRender(timeDiff);
		renderNotificationText(timeDiff, 0.9f, 0.5f, NotifStates.NOT_SHOW);
		
		mPointsManager.update(timeDiff);	
		mSpriteBatch.end();	
	}

	@Override
	protected Thread getBoxThread() {
		return new Thread(new Runnable() {			
			@Override
			public void run(){
				while(mGameStatus == GameStatus.PLAY){
					if (mBoxesManager.getBoxesCount() < 12){
						mBoxesManager.addNew(mChuckSphere.position, mChuckSphere.boundingSphereR);
					}
					try {
						long sleepTime = 1200 - mPointsManager.getScore() * 5;
						if (sleepTime < 100){
							sleepTime = 100;
						}
						Thread.sleep(sleepTime );
					} catch (InterruptedException e) {
					}
				}
			}
		});
	}

	@Override
	protected GameTypes getGameType() {
		return GameTypes.FREEPLAY;
	}
}
