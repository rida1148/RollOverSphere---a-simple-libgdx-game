package com.igorcrevar.rolloversphere.texture;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureManager {
	private Map<String, Texture> mTextures = new HashMap<String, Texture>();
	private boolean mIsLoaded = false;
	private static TextureManager instance;
	public static String textureBasePath = "assets/images/%s.jpg";
	static
	{
		instance = new TextureManager();
	}
	
	public static TextureManager getInstance(){
		return instance;
	}
	
	public void load(String[] keys, String[] files){
		if (!mIsLoaded){
			for (int i = 0; i < keys.length; ++i){
				Texture txt = new Texture(Gdx.files.internal(String.format(textureBasePath, files[i])));
				mTextures.put(keys[i], txt);
			}
			mIsLoaded = true;
		}
	}
	
	/**
	 * Binds texture form textures map with key "key"
	 * @param key String key of the texture
	 */
	public void bind(String key){
		mTextures.get(key).bind();
	}
	
	public Texture getTexture(String key){
		return mTextures.get(key);
	}
	
	public void dispose(){
		if (mIsLoaded){
			for (String key: mTextures.keySet()){
				mTextures.get(key).dispose();
			}
			mIsLoaded = false;
		}
	}
}
