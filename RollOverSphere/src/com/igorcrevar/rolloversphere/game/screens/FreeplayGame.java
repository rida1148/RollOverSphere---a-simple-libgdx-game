package com.igorcrevar.rolloversphere.game.screens;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.game.GameTypes;
import com.igorcrevar.rolloversphere.game.SettingsHelper;
import com.igorcrevar.rolloversphere.objects.boxes.Box;
import com.igorcrevar.rolloversphere.objects.boxes.BoxType;
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
		//update and render boxes. return boxes collided with ball
		List<Box> collided = mBoxesManager.doAll(mCamera, timeDiff, mLastBallPos, mMainBall.position, mMainBall.boundingSphereR);
		if (collided != null){
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
		
		mSpriteBatch.begin();
		//draw player points
		String str = String.format("Points: %d", mPointsManager.getScore());
		mFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		mFont.draw(mSpriteBatch, str, getScreenX(5.0f), getScreenY(5.0f));			
		//upgrade part
		upgradeTimeoutUpdateAndRender();
		//end upgrade
		//update and draw points
		mPointsManager.update(timeDiff);	
		//upgrade text
		renderNotificationText(timeDiff, 0.3f, 0.5f, NotifStates.NOT_SHOW);
		mSpriteBatch.end();
		
		mMainBall.render(mCamera);		
	}

	@Override
	protected Thread getBoxThread() {
		return new Thread(new Runnable() {			
			@Override
			public void run(){
				while(mGameStatus != GameStatus.EXIT){
					if (mBoxesManager.getBoxesCount() < 12){
						mBoxesManager.addNew(mMainBall.position, mMainBall.boundingSphereR);
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
	public void hide() {
		super.hide();
		SettingsHelper.addScore(mPointsManager.getScore(), GameTypes.FREEPLAY);
	}
}
