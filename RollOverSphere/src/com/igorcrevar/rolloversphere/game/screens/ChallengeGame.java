package com.igorcrevar.rolloversphere.game.screens;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.game.GameTypes;
import com.igorcrevar.rolloversphere.game.SettingsHelper;
import com.igorcrevar.rolloversphere.objects.boxes.Box;
import com.igorcrevar.rolloversphere.objects.boxes.BoxType;
import com.igorcrevar.rolloversphere.objects.boxes.factory.ChallengeBoxesFactory;
import com.igorcrevar.rolloversphere.objects.boxes.factory.IBoxesFactory;

public class ChallengeGame extends TheGame{
	//TODO: budzzz
	private long mTimeOfGameOver;
	
	public ChallengeGame(Game game) {
		super(game);		
	}

	@Override
	protected IBoxesFactory getBoxesFactory() {		
		return new ChallengeBoxesFactory();
	}

	@Override
	protected void renderGameSpecific(float timeDiff) {
		
		if (GameStatus.PLAY == mGameStatus){
			int oldBoxesCount = mBoxesManager.getNotUpgradeBoxCount();
			//update and render boxes. return boxes collided with ball
			List<Box> collided = mBoxesManager.doAll(mCamera, timeDiff, mLastBallPos, mMainBall.position, mMainBall.boundingSphereR);
			int newBoxesCount = mBoxesManager.getNotUpgradeBoxCount();
			
			if (collided != null){
				oldBoxesCount -= collided.size(); //substract from old count boxes we just picked up
				Vector3 pos = new Vector3();
				for (Box pb: collided){
					pos.set(pb.position);
					mCamera.project(pos);
					if (pb.getUpgrade() == BoxType.NOT_UPGRADE){
						mPointsManager.add(pb.getPointsWorth(), pos.x, pos.y);						
					}
					else{
						//if picked more than one upgrade - last one will be active!!!!
						mUpgradeType = pb.getUpgrade();
						mUpgradeTimeStarted = mLastTime;
						mUpgradeTimeout = pb.getUpgradeTimeout();
						createNotification();				
					}
				}
			}
			
			//if ball didnt pick some normal boxes which fade out than it is game over
			if (oldBoxesCount > newBoxesCount ){
				mGameStatus = GameStatus.GAMEOVER;
				mNotifTextAnimationState = NotifStates.FADE_IN;
				mNotifTextColor = Color.RED;
				mNotifTextColor.a = 0.0f;
				mTimeOfGameOver = mLastTime;
				SettingsHelper.addScore(mPointsManager.getScore(), GameTypes.CHALLENGE);
			}
		}
		else{
			mMainBall.render(mCamera);
		}
		
		mSpriteBatch.begin();
		//draw player points
		String str = String.format("Points: %d", mPointsManager.getScore());
		mFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		mFont.draw(mSpriteBatch, str, getScreenX(5.0f), getScreenY(5.0f));							
		upgradeTimeoutUpdateAndRender();
		//update and draw points
		mPointsManager.update(timeDiff);	
		//upgrade text
		if (GameStatus.PLAY == mGameStatus){
			renderNotificationText(timeDiff, 0.3f, 0.5f, NotifStates.NOT_SHOW);			
		}
		else{			 
			mNotifText = "Game over!";
			renderNotificationText(timeDiff, 2.2f, 2.0f, NotifStates.FADE_IN);
		}
		
		mSpriteBatch.end();
		
		if (GameStatus.PLAY == mGameStatus){
			mMainBall.render(mCamera);			
		}
	}
	
	@Override
	protected Thread getBoxThread() {
		return new Thread(new Runnable() {			
			@Override
			public void run(){
				float deltaCurrentPass = 2.0f;
				long lastTime = System.currentTimeMillis() - 10000;
				while(mGameStatus == GameStatus.PLAY){
					long time = System.currentTimeMillis();
					float delta = (float)((time - lastTime) / 1000.0);
					if (delta > deltaCurrentPass){
						mBoxesManager.addNew(mMainBall.position, mMainBall.boundingSphereR);
						deltaCurrentPass -= 0.005f;
						if (deltaCurrentPass < 0.1f){
							deltaCurrentPass = 0.1f;
						}
						lastTime = time;
					}					
					try {						
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		});
	}
	
	@Override
	public void onClick(int x, int y) {
		if (mGameStatus == GameStatus.GAMEOVER  &&  (mLastTime - mTimeOfGameOver) / 1000 > 0.8){
			mGame.setScreen(new MainMenuScreen(mGame));
		}
		//two seconds must pass
		else {
			super.onClick(x, y);			
		}
	}
}
