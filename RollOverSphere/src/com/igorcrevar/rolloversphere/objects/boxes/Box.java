package com.igorcrevar.rolloversphere.objects.boxes;

import com.badlogic.gdx.graphics.Texture;
import com.igorcrevar.rolloversphere.mesh_gl10.AbstractMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.CubeMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.MeshManager;
import com.igorcrevar.rolloversphere.objects.GameObject;

public class Box extends GameObject {
	protected float mFadeoutSpeed;
	protected float mAngle = 0.0f;
	protected int mPoints;
	protected float mRotationSpeed;
	
	//TODO: grid random positioning
	//public int gridX;
	//public int gridY;
	
	public Box(){
		mFadeoutSpeed = 0.0f;
		mPoints = 5;
		mRotationSpeed = 20.0f;
		setColor(1.0f, 0.0f, 0.0f, 1.0f);
	}
	
	public Box(float[] color, int points, float fadeoutSpeed, float rotationSpeed){
		mColor = color;
		mPoints = points;
		mFadeoutSpeed = fadeoutSpeed;
		mRotationSpeed = rotationSpeed;
	}
	
	public final void setPointsWorth(int v){
		mPoints = v;
	}
	
	public final void setColor(float r, float g, float b, float a){
		if (mColor == null){
			mColor = new float[4];
		}
		
		mColor[0] = r;
		mColor[1] = g;
		mColor[2] = b;
		mColor[3] = a;
	}
	
	public final void setRotationSpeed(float v){
		mRotationSpeed = v;
	}
	
	public final void setFadeoutSpeed(float v){
		mFadeoutSpeed = v;
	}
	
	@Override
	protected AbstractMesh getMesh() {
		CubeMesh mesh = (CubeMesh)MeshManager.getInstance().getMesh("cube");		
		boundingSphereR = mesh.size;
		return mesh;
	}

	@Override
	protected Texture getTexture() {
		return null;
	}

	
	/* This method should be overrided because it is called from updateBox
	 * (non-Javadoc)
	 * @see com.igorcrevar.punchthecircle.objects.GameObject#update(float)
	 */
	@Override
	public void update(float timeDiff) {
		rotation.y = mAngle;
	}
	
	/**
	 * This method is called from boxmanager.
	 * @param timeDiff
	 * @return true if box is still alive or false if it is dead
	 */
	public final boolean updateBox(float timeDiff){
		update(timeDiff);
		
		mAngle = mAngle + mRotationSpeed * timeDiff;
		if (mAngle > 360.0f){
			mAngle = 360.0f - mAngle;
		}
		
		if (mFadeoutSpeed > 0.0f){
			mColor[3] = mColor[3] - mFadeoutSpeed * timeDiff;
			return mColor[3] > 0.0f;
		}
		
		return true;
	}

	public final int getPointsWorth(){
		return mPoints;
	}

	/**
	 * Override in childs to specify type of upgrade(if upgrade point box)
	 * @return
	 */
	public UpgradeType getUpgradeType(){
		return UpgradeType.NOT_UPGRADE;
	}
	
	/**
	 * Override in childs to specify how much upgrade lasts in seconds
	 * @return
	 */
	public int getUpgradeTimeout(){
		return 10;
	}
	
	public boolean isNormalBox(){
		return UpgradeType.NOT_UPGRADE == getUpgradeType();
	}
}
