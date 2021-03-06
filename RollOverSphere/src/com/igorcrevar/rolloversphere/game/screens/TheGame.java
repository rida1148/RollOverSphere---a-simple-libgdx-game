package com.igorcrevar.rolloversphere.game.screens;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.game.GameTypes;
import com.igorcrevar.rolloversphere.game.SettingsHelper;
import com.igorcrevar.rolloversphere.input.IMyInputAdapter;
import com.igorcrevar.rolloversphere.input.MyInputAdapter;
import com.igorcrevar.rolloversphere.mesh_gl10.CubeMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.MeshManager;
import com.igorcrevar.rolloversphere.objects.ChuckSphere;
import com.igorcrevar.rolloversphere.objects.DynamicPlane;
import com.igorcrevar.rolloversphere.objects.Floor;
import com.igorcrevar.rolloversphere.objects.PointsManager;
import com.igorcrevar.rolloversphere.objects.boxes.Box;
import com.igorcrevar.rolloversphere.objects.boxes.BoxesManager;
import com.igorcrevar.rolloversphere.objects.boxes.UpgradeType;
import com.igorcrevar.rolloversphere.objects.boxes.factory.IBoxesFactory;

public abstract class TheGame implements Screen, IMyInputAdapter{
	protected final ChuckSphere mChuckSphere = new ChuckSphere();
	protected final DynamicPlane mWaterBackground = new DynamicPlane();
	protected final Floor mFloor = new Floor();
	protected BoxesManager mBoxesManager;
	protected PointsManager mPointsManager;
	protected BitmapFont mFont;
	protected MyInputAdapter mInputAdapter;
	
	protected SpriteBatch mSpriteBatch;	
	protected PerspectiveCamera mCamera;
	
	protected Game mGame;
	protected enum GameStatus{
		PLAY, GAMEOVER
	}
	protected GameStatus mGameStatus;
	protected GameTypes mGameType;
	//
	protected float mGameOverTimer;

	//upgrade
	protected UpgradeType mUpgradeType = UpgradeType.NOT_UPGRADE;
	protected float mUpgradeTimer;
	protected int mUpgradeTimeout;
	//TODO: budz!!!!
	protected enum NotifStates{
		FADE_IN, FADE_OUT, NOT_SHOW
	}
	protected String mNotifText;
	protected Color mNotifTextColor;
	protected NotifStates mNotifTextAnimationState; //0 - fade in 1 - fade out, 2 - not show
	
	public TheGame(Game game) {
		mGame = game;
		mInputAdapter = new MyInputAdapter(this);
		mGameStatus = GameStatus.PLAY;
		mGameType = getGameType();
		mGameOverTimer = 0;
		init();
	}
	
	private void init(){
		mNotifTextColor = Color.WHITE;
		mNotifTextAnimationState = NotifStates.NOT_SHOW;
		mBoxesManager = new BoxesManager(getBoxesFactory());
		
		mSpriteBatch = new SpriteBatch();
		mFont = new BitmapFont();		
		mPointsManager = new PointsManager(mFont, mSpriteBatch);
		
		CubeMesh mesh = (CubeMesh)MeshManager.getInstance().getMesh("cube");
		mBoxesManager.init(-30.0f, 30.0f, -80.0f, 0.0f, mesh.size);
		
		mWaterBackground.init();
		mChuckSphere.init();
		mFloor.init();
		
		mChuckSphere.setBounds(-30.0f, 30.0f, -80.0f, 0.0f);
		
		mWaterBackground.rotation.x = 90;
		mWaterBackground.position.x = 4;
		mWaterBackground.position.y = 15;
		mWaterBackground.position.z = -80.0f;
		
		mFloor.position.y = -mChuckSphere.boundingSphereR;
		mFloor.position.z = -40.0f;
		
		Gdx.input.setInputProcessor(new MyInputAdapter(this));
	}
	
	protected void createNotification(UpgradeType upgradeType, int timeout) {			
		mUpgradeTimer = 0;
		mUpgradeType = upgradeType;
		mUpgradeTimeout = timeout;
		mNotifText = mUpgradeType.getText();
		mNotifTextColor.a = 0.0f;
		mNotifTextAnimationState = NotifStates.FADE_IN;
	}
	
	protected void renderNotificationText(float timeDiff, float inSpeed, float outSpeed, NotifStates afterFinish){
		if (mNotifTextAnimationState != NotifStates.NOT_SHOW){
			drawInMiddle(mNotifText, mNotifTextColor);
			if (mNotifTextAnimationState == NotifStates.FADE_IN){
				mNotifTextColor.a += timeDiff * inSpeed;
				if (mNotifTextColor.a >= 1.0f){
					mNotifTextAnimationState = NotifStates.FADE_OUT;
					mNotifTextColor.a = 1.0f;
				}
			}
			else{
				mNotifTextColor.a -= timeDiff * outSpeed;
				if (mNotifTextColor.a <= 0.0f){
					mNotifTextAnimationState = afterFinish;
					mNotifTextColor.a = 0.0f;
				}
			}			
		}
	}
	
	protected void drawInMiddle(String str, Color color){
		TextBounds bounds = mFont.getBounds(str);
		float x = (Gdx.graphics.getWidth() - bounds.width) / 2;
		float y = Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() - bounds.height) / 2;
		mFont.setColor(color);
		mFont.draw(mSpriteBatch, str, x, y);  
	}
	
	protected void upgradeTimeoutUpdateAndRender(float timeDelta){
		//upgrade part
		if (mUpgradeType != UpgradeType.NOT_UPGRADE){
			mUpgradeTimer += timeDelta;
			if (mUpgradeTimer >= (float)mUpgradeTimeout){
				//if timeout expires than we are not in upgrade state anymore
				mUpgradeType = UpgradeType.NOT_UPGRADE;
			}
			else{
				int time = (int)(mUpgradeTimeout - mUpgradeTimer);
				String str = "Upgrade: " + time;
				mFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				mFont.draw(mSpriteBatch, str, getScreenXEnd(200.0f), getScreenY(5.0f));
			}
		}
	}
	
	@Override
	public void render(float deltaTime){
		//Enables depth testing.
		Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL10.GL_LEQUAL);
		//blend
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);
		
		mWaterBackground.update(deltaTime);
		mChuckSphere.update(deltaTime);
				
		mWaterBackground.render(mCamera);	    
		mFloor.render(mCamera);
		
		//update and render boxes. return boxes collided with ball
		mBoxesManager.doAll(mCamera, deltaTime, mChuckSphere.getPrevPosition(), 
											   mChuckSphere.position, mChuckSphere.boundingSphereR);
		//get all boxes our sphere collided with
		List<Box> collided = mBoxesManager.getIntersectedBoxes(); 
		if (collided != null){
			Vector3 pos = new Vector3();
			for (Box box: collided){
				pos.set(box.position);
				if (box.isNormalBox()){
					//create point sprite
					mCamera.project(pos);
					mPointsManager.add(box.getPointsWorth(), pos.x, pos.y);						
				}
				else{
					//if picked more than one upgrade - last one will be active!!!!					
					//sphere need to know upgrade also
					mChuckSphere.setUpgradeType(box.getUpgradeType());
					//create upgrade notification and additional data for upgrade
					createNotification(box.getUpgradeType(), box.getUpgradeTimeout());				
				}
			}
		}			
		
		addNewBox(deltaTime);
		mChuckSphere.render(mCamera);
		renderGameSpecific(deltaTime);
		
		if (Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Android  
			&& SettingsHelper.isAcceleatorEnabled) {
			//TODO: handle
		}
	}

	protected abstract void addNewBox(float timeDiff);
	protected abstract void renderGameSpecific(float timeDiff);
	protected abstract IBoxesFactory getBoxesFactory();
	protected abstract GameTypes getGameType();

	@Override
	public void resize(int width, int height) {
		// Sets the current view port to the new size.
		Gdx.gl.glViewport(0, 0, width, height);
		mCamera = new PerspectiveCamera(60.0f, width, height);
		mCamera.rotate(-30.0f, 1.0f, 0.0f, 0.0f);		
		mCamera.far += 50.0f;
		
		mCamera.position.set(0, 40.0f, 30.0f);
		mCamera.update();
		
		if (width >= 800){
			mFont.setScale(2.0f);	
		}
		else if (width >= 480){
			mFont.setScale(1.2f);
		}
		else{
			mFont.setScale(1.0f);
		}
		
		setOpenGlDefault();
	}
	
	private void setOpenGlDefault(){
		Gdx.gl.glClearColor(0f, 0, 0, 1f);		
		Gdx.gl.glClearDepthf(1.0f);		
	}
	
	private void setInputAndStartBoxThread(){
		Gdx.input.setInputProcessor(mInputAdapter);
		Gdx.input.setCatchBackKey(true);
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void hide() {
		//save score on hide!
		SettingsHelper.addScore(mPointsManager.getScore(), mGameType);
		mGameStatus = GameStatus.GAMEOVER; //because of outside app kill	
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
		setInputAndStartBoxThread();
		setOpenGlDefault();		
	}
	
	@Override
	public void onMove(int dx, int dy, int x, int y) {
		if (mGameStatus == GameStatus.PLAY){
			mChuckSphere.updatePlayerVelocity(dx, dy);
		}
	}

	@Override
	public void onClick(int x, int y) {
		if (mGameStatus == GameStatus.GAMEOVER){
			//can not finish this screen until some time pass
			if (mGameOverTimer > 0.8f){
				mGame.setScreen(new MainMenuScreen(mGame));
			}
		}
	}

	@Override
	public void onDoubleClick(int x, int y) {
		if (mGameStatus == GameStatus.PLAY){
			mChuckSphere.updatePlayerVelocityZSpecial();
		}
	}

	@Override
	public void onBack(){
		mGame.setScreen(new MainMenuScreen(mGame));
	}
	
	//// HELPER METHODS FOR realtive X, Y pixel position depending on screen width/height
	protected float getScreenX(double x){
		double rv = x * Gdx.graphics.getWidth() / 800.0;
		return (float)rv;
	}
	
	protected float getScreenY(double y){
		double rv = Gdx.graphics.getHeight() - y * Gdx.graphics.getHeight() / 480.0;
		return (float)rv;
	}
	
	protected float getScreenXEnd(double x){
		double rv = Gdx.graphics.getWidth() - x * Gdx.graphics.getWidth() / 800.0;
		return (float)rv;
	}
	
	protected float getScreenYEnd(double y){
		double rv = y * Gdx.graphics.getHeight() / 480.0;
		return (float)rv;
	}
	
	public String getPointsString() {
		String str = "Points: " + mPointsManager.getScore();
		//String.format("Points: %d", mPointsManager.getScore());
		return str;
	}
}
