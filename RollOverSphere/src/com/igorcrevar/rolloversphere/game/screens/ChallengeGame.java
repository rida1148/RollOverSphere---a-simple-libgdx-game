package com.igorcrevar.rolloversphere.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.igorcrevar.rolloversphere.game.GameTypes;
import com.igorcrevar.rolloversphere.objects.boxes.Box;
import com.igorcrevar.rolloversphere.objects.boxes.factory.ChallengeBoxesFactory;
import com.igorcrevar.rolloversphere.objects.boxes.factory.IBoxesFactory;

public class ChallengeGame extends TheGame{

	public ChallengeGame(Game game) {
		super(game);		
	}

	@Override
	protected IBoxesFactory getBoxesFactory() {		
		return new ChallengeBoxesFactory();
	}

	@Override
	protected void renderGameSpecific(float timeDiff) {
		//if at least one expired and we didnt pick it - game over
		if (GameStatus.PLAY == mGameStatus && mBoxesManager.getExpired().size() > 0){
			for (Box box:mBoxesManager.getExpired()){
				if (box.isNormalBox()){
					mGameStatus = GameStatus.GAMEOVER;
					mNotifTextAnimationState = NotifStates.FADE_IN;
					mNotifTextColor = Color.RED;
					mNotifTextColor.a = 0.0f;
					break;
				}
			}
		}
		
		mSpriteBatch.begin();
		//draw player points
		mFont.setColor(Color.WHITE);
		mFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		String str = String.format("Points: %d", mPointsManager.getScore());
		mFont.draw(mSpriteBatch, str, getScreenX(5.0f), getScreenY(5.0f));	
		
		if (GameStatus.PLAY == mGameStatus){
			upgradeTimeoutUpdateAndRender(timeDiff);
			renderNotificationText(timeDiff, 0.9f, 0.5f, NotifStates.NOT_SHOW);
		}
		else{			 
			mGameOverTimer += timeDiff;
			//prevent overflow
			if (mGameOverTimer > 100.0f){
				mGameOverTimer = mGameOverTimer - 100.0f;
			}
			mNotifText = "Game over!";
			renderNotificationText(timeDiff, 2.2f, 2.0f, NotifStates.FADE_IN);
		}
		
		mPointsManager.update(timeDiff);	
		mSpriteBatch.end();
	}
	
	@Override
	protected Thread getBoxThread() {
		return new Thread(new Runnable() {			
			@Override
			public void run(){
				long sleepTime = 2000;
				long dec = 4;
				while(mGameStatus == GameStatus.PLAY){
					mBoxesManager.addNew(mChuckSphere.position, mChuckSphere.boundingSphereR);
					try {						
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					sleepTime -= dec;
					if (sleepTime < 300){
						sleepTime = 300;
					}
				}
			}
		});
	}

	@Override
	protected GameTypes getGameType() {
		return GameTypes.CHALLENGE;
	}
}

