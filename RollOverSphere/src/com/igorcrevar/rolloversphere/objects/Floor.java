package com.igorcrevar.rolloversphere.objects;

import com.badlogic.gdx.graphics.Texture;
import com.igorcrevar.rolloversphere.mesh_gl10.AbstractMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.MeshManager;

public class Floor extends GameObject{

	@Override
	protected AbstractMesh getMesh() {		
		return MeshManager.getInstance().getMesh("planexz");
	}

	@Override
	protected Texture getTexture() {
		mColor = new float[] { 0.6f, 0.6f, 0.0f, 0.8f };
		return null;
	}

	@Override
	public void update(float timeDiff) {
	}

}
