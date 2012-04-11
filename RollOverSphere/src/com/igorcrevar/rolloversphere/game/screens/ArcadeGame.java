package com.igorcrevar.rolloversphere.game.screens;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.game.GameTypes;
import com.igorcrevar.rolloversphere.game.SettingsHelper;
import com.igorcrevar.rolloversphere.objects.boxes.Box;
import com.igorcrevar.rolloversphere.objects.boxes.BoxType;
import com.igorcrevar.rolloversphere.objects.boxes.factory.ArcadeBoxesFactory;
import com.igorcrevar.rolloversphere.objects.boxes.factory.IBoxesFactory;

public class ArcadeGame extends TheGame {
	private long mTimeOfGameOver;
	private long mTimeOfGameStart;
	
	public ArcadeGame(Game game) {
		super(game);
		mTimeOfGameStart = -1;
	}

	@Override
	protected IBoxesFactory getBoxesFactory() {
		return new ArcadeBoxesFactory();
	}
	
	@Override
	public void resume() {
		super.resume();
		//if we are in play status then mark mTimeOfGameStart for reset on next render frame
		if (mGameStatus == GameStatus.PLAY){
			mTimeOfGameStart = -1;
		}
	}

	@Override
	protected void renderGameSpecific(float timeDiff) {
		//init game start time if needed
		if (mTimeOfGameStart == -1){
			mTimeOfGameStart = mLastTime;
		}
				
		List<Box> collided = mBoxesManager.doAll(mCamera, timeDiff, mLastBallPos, mMainBall.position, mMainBall.boundingSphereR);
		if (GameStatus.PLAY == mGameStatus){
			//update and render boxes. return boxes collided with ball	
			if (collided != null){
				Vector3 pos = new Vector3();
				for (Box pb: collided){
					pos.set(pb.position);
					mCamera.project(pos);
					//if (pb.getType() == ArcadeBoxesFactory.TYPE_DO_NOT_PICK_UP){
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
		}
		else{
			mMainBall.render(mCamera);
		}
		
		mSpriteBatch.begin();
		//draw player points
		mFont.setColor(Color.WHITE);
		mFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		String str = String.format("Points: %d", mPointsManager.getScore());
		mFont.draw(mSpriteBatch, str, getScreenX(5.0f), getScreenY(5.0f));							
		if (GameStatus.PLAY == mGameStatus){
			long timeout = (mLastTime - mTimeOfGameStart) / 1000;
			if (timeout >= SettingsHelper.arcadeGameTimeout){
				mGameStatus = GameStatus.GAMEOVER;
				mNotifTextAnimationState = NotifStates.FADE_IN;
				mNotifTextColor = Color.RED;
				mNotifTextColor.a = 0.0f;
				mTimeOfGameOver = mLastTime;
				SettingsHelper.addScore(mPointsManager.getScore(), GameTypes.ARCADE);
			}
			
			str = String.format("Time: %d", SettingsHelper.arcadeGameTimeout - timeout);
			mFont.draw(mSpriteBatch, str, getScreenX(5.0f), getScreenY(40.0f));	
			upgradeTimeoutUpdateAndRender();
			renderNotificationText(timeDiff, 0.9f, 0.5f, NotifStates.NOT_SHOW);
		}
		else{			 
			mNotifText = "Time's up!";
			renderNotificationText(timeDiff, 2.2f, 2.0f, NotifStates.FADE_IN);
		}
		
		mPointsManager.update(timeDiff);	
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
				while(mGameStatus == GameStatus.PLAY){
					mBoxesManager.addNew(mMainBall.position, mMainBall.boundingSphereR);
					try {						
						Thread.sleep(400);
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
