package com.igorcrevar.rolloversphere.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.igorcrevar.rolloversphere.mesh_gl10.AbstractMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.BallMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.CubeMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.MeshManager;
import com.igorcrevar.rolloversphere.mesh_gl10.PlaneXZMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.WaterMesh;
import com.igorcrevar.rolloversphere.texture.TextureManager;

public class AssetsHelper {
	public static BitmapFont font;
	public static Music music;
	
	private static void loadTextures(){
		TextureManager.getInstance().load(new String[] { "badlogic", "flag" }, new String[] { "badlogic", "flag" }, "data/images/%s.jpg");
	}

	private static void loadMeshes(){
		AbstractMesh mesh;
		mesh = new BallMesh();
		mesh.init(4.0f, 20);
		mesh.createMesh();
		MeshManager.getInstance().put("ball", mesh);
		
		mesh = new WaterMesh();
		mesh.init(14, 10, 40.40f, 25.40f);
		mesh.createMesh();
		MeshManager.getInstance().put("water", mesh);
		
		mesh = new CubeMesh();
		mesh.init(1.8f);
		mesh.createMesh();
		MeshManager.getInstance().put("cube", mesh);
		
		mesh = new PlaneXZMesh();
		mesh.init(32.0f, 42.0f, Color.toFloatBits(0xff, 0, 0xff, 0xff));
		mesh.createMesh();
		MeshManager.getInstance().put("planexz", mesh);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("data/sounds/rolloversphere.ogg"));
		music.setLooping(false);
		music.setVolume(0.8f);
	}
	
	public static void load () {
		font = new BitmapFont(Gdx.files.internal("data/font/font.fnt"), Gdx.files.internal("data/font/font.png"), false);
		loadTextures();
		loadMeshes();
	}
	
	public static void free(){
		font.dispose();
		TextureManager.getInstance().dispose();
		MeshManager.getInstance().dispose();
	}

	public static void playSound (Sound sound) {
		if (SettingsHelper.soundEnabled) sound.play(1);
	}
}
