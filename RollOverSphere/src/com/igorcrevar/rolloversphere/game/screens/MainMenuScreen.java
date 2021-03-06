package com.igorcrevar.rolloversphere.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.collsion.OverlapTester;
import com.igorcrevar.rolloversphere.game.AssetsHelper;
import com.igorcrevar.rolloversphere.game.GameTypes;
import com.igorcrevar.rolloversphere.game.SettingsHelper;
import com.igorcrevar.rolloversphere.input.IMyInputAdapter;
import com.igorcrevar.rolloversphere.input.MyInputAdapter;
import com.igorcrevar.rolloversphere.objects.DynamicPlane;

public class MainMenuScreen implements Screen, IMyInputAdapter{
	class OptionItem{
		public OptionItem(String name, GameTypes type){
			this.name = name;
			this.type = type;
			boundingBox = new Rectangle();
		}
		public String name;
		public Rectangle boundingBox;
		public GameTypes type;
	}
	private OptionItem[] mOptions;
	private Game mGame;
	private BitmapFont mFont;
	private MyInputAdapter mInputAdapter;
	
	private Camera mOtherCam;
	private SpriteBatch mSpriteBatch;
	private Vector3 mClickPos = new Vector3();
	private boolean mIsClick = false;
	protected final DynamicPlane mWaterBackground = new DynamicPlane();
	
	public MainMenuScreen (Game game) {
		mFont = AssetsHelper.font;		
		mGame = game;		
		mOptions = new OptionItem[] { 
				new OptionItem("Arcade", GameTypes.ARCADE), new OptionItem("Challenge", GameTypes.CHALLENGE), 
				new OptionItem("Freeplay!", GameTypes.FREEPLAY) };
		
		mSpriteBatch = new SpriteBatch();
		mInputAdapter = new MyInputAdapter(this);
		
		mWaterBackground.init();
		mWaterBackground.rotation.x = 90;
		mWaterBackground.position.x = 0.0f;
		mWaterBackground.position.y = 0.0f;
		mWaterBackground.position.z = 0.0f;
	}
	
	private void updateOptionsBounds(int w, int h){
		float maxHeight = 0;
		//first find max height and update width and height 
		mFont.setScale(2.4f * Gdx.graphics.getWidth() / 800.0f);
		for (OptionItem option: mOptions){
			TextBounds bounds = new TextBounds(mFont.getBounds(option.name));
			option.boundingBox.width = bounds.width;
			option.boundingBox.height = bounds.height;
			if (bounds.height > maxHeight){
				maxHeight = bounds.height;
			}
		}
		
		maxHeight += 30.0f * Gdx.graphics.getWidth() / 800.0f;
		int y = (int)((h - maxHeight * (mOptions.length - 1) - mOptions[mOptions.length - 1].boundingBox.height) / 2.0f);
		//then calculate x, y positions
		for (OptionItem option: mOptions){
			option.boundingBox.x = (w - option.boundingBox.width) / 2.0f;
			option.boundingBox.y = y;
			y += maxHeight;
		}
	}
	
	private void update (float deltaTime) {
		if (mIsClick) {
			mIsClick = false;
			//mGuiCam.unproject(mClickPos); //convert to 2d
			mClickPos.y = Gdx.graphics.getHeight() - mClickPos.y;
			
			for (OptionItem option:mOptions){
				if (OverlapTester.pointInRectangle(option.boundingBox, mClickPos.x, mClickPos.y)) {
					switch(option.type){
					case ARCADE:
						mGame.setScreen(new ArcadeGame(mGame));
						return;
					case CHALLENGE:
						mGame.setScreen(new ChallengeGame(mGame));				
						return;
					case FREEPLAY:
						mGame.setScreen(new FreeplayGame(mGame));
						return;
					}
				}
			}
		}
	}

	private void draw (float deltaTime) {
		Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		mOtherCam.update();
		mWaterBackground.update(deltaTime);
		mWaterBackground.render(mOtherCam);	
		
		mSpriteBatch.begin();
		float baseRation = Gdx.graphics.getWidth() / 800.0f;
		mFont.setScale(baseRation * 2.4f);
		float dec = baseRation * 4.0f;
		for (OptionItem option: mOptions){
			mFont.setColor(0.8f, 0.1f, 0.2f, 1.0f);
			mFont.draw(mSpriteBatch, option.name, option.boundingBox.x - dec, 
					   Gdx.graphics.getHeight() - option.boundingBox.y + dec);
			mFont.setColor(1.0f, 0.7f, 0.3f, 1.0f);
			mFont.draw(mSpriteBatch, option.name, option.boundingBox.x, Gdx.graphics.getHeight() - option.boundingBox.y);
		}
		
		//highscores
		int width = Gdx.graphics.getWidth();
		if (width >= 800){
			mFont.setScale(1.2f);	
		}
		else if (width >= 480){
			mFont.setScale(0.8f);
		}
		else{
			mFont.setScale(0.5f);
		}
		float dy = baseRation * 20.0f;
		float y = dy * 4.0f;
		mFont.setColor(1.0f, 0.7f, 0.3f, 1.0f);
		for (OptionItem option: mOptions){
			//gwt  and string.format :(
			String str = option.name + " " + SettingsHelper.getScore(option.type);			
			mFont.draw(mSpriteBatch, str, 20.0f, y);
			y -= dy;
		}
		mSpriteBatch.end();
	}
	
	@Override
	public void dispose() {	
	}

	@Override
	public void hide() {
		AssetsHelper.music.stop();
	}

	@Override
	public void pause() {
	}

	@Override
	public void render(float deltaTime) {
		update(deltaTime);
		draw(deltaTime);
	}

	@Override
	public void resize(int w, int h) {
		Gdx.gl.glViewport(0, 0, w, h);
		updateOptionsBounds(w, h);
		mOtherCam = new PerspectiveCamera(90.0f, w, h);
		mOtherCam.far += 50.0f;
		
		mOtherCam.position.set(0, 0.0f, 30.0f);
		mOtherCam.update();
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(mInputAdapter);
		
		AssetsHelper.music.play();		
	}

	@Override
	public void onMove(int dx, int dy, int x, int y) {
	}

	@Override
	public void onClick(int x, int y) {
		mIsClick = true;
		mClickPos.set(x, Gdx.graphics.getHeight() - y, 0); 
	}

	@Override
	public void onDoubleClick(int x, int y) {
	}

	@Override
	public void onBack() {
		Gdx.app.exit();
	}

}
