package com.igorcrevar.rolloversphere.objects.boxes;

import com.igorcrevar.rolloversphere.mesh_gl10.AbstractMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.CubeMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.MeshManager;

public class UpgradeDirection extends Box{
	private float angle = 0.0f;
	
	public UpgradeDirection(){
		mColor = new float[] { 0.0f, 0.1f, 1.0f, 1.0f };
		mPoints = 0;
		mFadeoutSpeed = 0.35f;
		mRotationSpeed = 80.0f;
	}
	
	@Override
	protected AbstractMesh getMesh() {
		CubeMesh mesh = (CubeMesh)MeshManager.getInstance().getMesh("cube");
		setScaleAndBoundingSphereR(0.7f, mesh.size);
		return mesh;
	}

	@Override
	public void update(float timeDiff) {		
		rotation.y = angle;
		rotation.x = angle * 1.5f;
	}
	
	@Override
	public UpgradeType getUpgradeType(){
		return UpgradeType.UPGRADE_EASY_CHANGE_DIRECTION;
	}
	
	@Override
	public int getUpgradeTimeout(){
		return 5;
	}
}