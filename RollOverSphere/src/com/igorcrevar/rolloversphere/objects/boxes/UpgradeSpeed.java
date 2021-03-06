package com.igorcrevar.rolloversphere.objects.boxes;

import com.igorcrevar.rolloversphere.mesh_gl10.AbstractMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.CubeMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.MeshManager;

public class UpgradeSpeed extends Box{
	
	public UpgradeSpeed(){
		mColor = new float[] { 0.9f, 0.9f, 0.9f, 1.0f };
		mPoints = 0;
		mFadeoutSpeed = 0.35f;
		mRotationSpeed = 95.0f;
	}

	@Override
	protected AbstractMesh getMesh() {
		CubeMesh mesh = (CubeMesh)MeshManager.getInstance().getMesh("cube");
		setScaleAndBoundingSphereR(0.6f, mesh.size);
		return mesh;
	}

	@Override
	public void update(float timeDiff) {		
		rotation.y = mAngle;
		rotation.x = mAngle * 2.0f;
	}

	@Override
	public UpgradeType getUpgradeType(){
		return UpgradeType.UPGRADE_SPEED;
	}
	
	@Override
	public int getUpgradeTimeout(){
		return 5;
	}
}
