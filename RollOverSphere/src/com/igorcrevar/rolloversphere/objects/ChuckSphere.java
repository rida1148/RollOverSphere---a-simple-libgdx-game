package com.igorcrevar.rolloversphere.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.collsion.CollisionSolver;
import com.igorcrevar.rolloversphere.mesh_gl10.AbstractMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.BallMesh;
import com.igorcrevar.rolloversphere.mesh_gl10.MeshManager;
import com.igorcrevar.rolloversphere.objects.boxes.UpgradeType;
import com.igorcrevar.rolloversphere.texture.TextureManager;

public class ChuckSphere extends GameObject{
	public static final float DEFAULT_FRICTION = 0.85f;
	public static final float SMALL_FRICTION = 0.65f;
	private static final float VELOCITY_NORMALIZER_NORMAL = 0.4f;
	private static final float VELOCITY_NORMALIZER_FAST = 0.6f;
	
	//position of sphere in previous frame
	public Vector3 prevPosition;
	
	private Vector3 mRotationReal = new Vector3(0.0f, 0.0f, 0.0f);
	private Vector3 mVelocity = new Vector3(0.0f, 0.0f, 0.0f); //in seconds
	private UpgradeType mUpgradeType;
	private float mFriction;
	private float mMinZ;
	private float mMaxZ;
	private float mMinX;
	private float mMaxX;
	
	public ChuckSphere(){
		mUpgradeType = UpgradeType.NOT_UPGRADE;
		prevPosition = position;
		mFriction = DEFAULT_FRICTION;
	}
	
	/**
	 * Change current upgrade type. Depend of upgrade sphere will act differently(easier change direction, greater velocity increment, etc)
	 * @param ut UpgradeType
	 */
	public void setUpgradeType(UpgradeType ut){
		mUpgradeType = ut;
	}
	
	/**
	 * Get position of sphere in previous frame
	 * @return Vector3 of prev position
	 */
	public Vector3 getPrevPosition(){
		return prevPosition;
	}
	
	/**
	 * Change boundaries for sphere position
	 */
	public void setBounds(float minx, float maxx, float minz, float maxz){
		mMinX = minx;
		mMaxX = maxx;
		mMinZ = minz;
		mMaxZ = maxz;
	}
	
	@Override
	protected Texture getTexture() {
		return TextureManager.getInstance().getTexture("badlogic");
	}

	@Override
	protected AbstractMesh getMesh() {
		BallMesh mesh = (BallMesh)MeshManager.getInstance().getMesh("ball");
		boundingSphereR = mesh.r;
		return mesh;
	}
	
	@Override
	public void update(float timeDiff) {
		prevPosition = position; //save last position
		
		//update position and rotation
		position.x += timeDiff * mVelocity.x;
		position.z += timeDiff * mVelocity.z;
		
		//update rotations
		mRotationReal.x = fixAngle(mRotationReal.x + timeDiff * mVelocity.z * 8);
		mRotationReal.z = fixAngle(mRotationReal.z - timeDiff * mVelocity.x * 8);
		
		//gimbal lock solver - just pick "more important" rotation on every frame
 		if (Math.abs(mVelocity.z) >= Math.abs(mVelocity.x)){
			rotation.x = mRotationReal.x;
			rotation.z = 0.0f;
		}
		else{
			rotation.x = 0.0f;
			rotation.z = mRotationReal.z;
		}
		
		//update velocities
		mVelocity.x -= mVelocity.x * mFriction * timeDiff;
		mVelocity.z -= mVelocity.z * mFriction * timeDiff;		
	
		//handle position boundaries
		if (position.x < mMinX){
			position.x = mMinX;
			mVelocity.x = -mVelocity.x;
		}
		
		if (position.x > mMaxX){
			position.x = mMaxX;
			mVelocity.x = -mVelocity.x;
		}
		
		if (position.z < mMinZ){
			position.z = mMinZ;
			mVelocity.z = -mVelocity.z;
		}
		
		if (position.z > mMaxZ){
			position.z = mMaxZ;
			mVelocity.z = -mVelocity.z;
		}
	}
	
	/**
	 * Change velocity of chuck sphere
	 * @param dx increment(decrement) for velocity x component 
	 * @param dz increment(decrement) for velocity z component
	 */
	public void updatePlayerVelocity(float dx, float dz ){
		switch (mUpgradeType){
		case NOT_UPGRADE:
			dx = dx * VELOCITY_NORMALIZER_NORMAL;
			dz = dz * VELOCITY_NORMALIZER_NORMAL;
			
			mFriction = ChuckSphere.DEFAULT_FRICTION;
			mVelocity.x = fixVelocity(mVelocity.x + dx);
			mVelocity.z = fixVelocity(mVelocity.z + dz);
			break;
		case UPGRADE_EASY_CHANGE_DIRECTION:
			dx = dx * VELOCITY_NORMALIZER_NORMAL;
			dz = dz * VELOCITY_NORMALIZER_NORMAL;
			
			int oldSign = CollisionSolver.getSign(mVelocity.x);
			int incSign = CollisionSolver.getSign(dx);
			if (Math.abs(oldSign - incSign) == 2){
				mVelocity.x = 0.0f;// -movingVelocity.x;
			}
			
			oldSign = CollisionSolver.getSign(mVelocity.y);
			incSign = CollisionSolver.getSign(dz );
			if (Math.abs(oldSign - incSign) == 2){
				mVelocity.y = 0.0f;
			}
			
			mFriction = ChuckSphere.DEFAULT_FRICTION;
			mVelocity.x = fixVelocity(mVelocity.x + dx);
			mVelocity.z = fixVelocity(mVelocity.z + dz);
			break;
		case UPGRADE_SPEED:
			dx = dx * VELOCITY_NORMALIZER_FAST;
			dz = dz * VELOCITY_NORMALIZER_FAST;
			
			mFriction = ChuckSphere.DEFAULT_FRICTION;
			mVelocity.x = fixVelocity(mVelocity.x + dx);
			mVelocity.z = fixVelocity(mVelocity.z + dz);
			break;
		}
	}
	
	/**
	 * Updates moving velocity towards z axis
	 */
	public void updatePlayerVelocityZSpecial() {
		if (mVelocity.z < 0){
			mVelocity.z = fixVelocity(mVelocity.z - 120.0f);
		}
		else{
			mVelocity.z = fixVelocity(mVelocity.z + 120.0f);
		}
	}
	
	/**
	 * Fix velocity so it doesnt exceed boundaries
	 * @param velocity
	 * @return velocity in boundaries
	 */
	private float fixVelocity(float v){
		if (v > 480.0f){
			return 480.0f;
		}
		if (v < -480.0f){
			return -480.0f;
		}
		return v;
	}

	/**
	 * Fix rotation angle so it is inside 0..360
	 * @param value angle
	 * @return fixed angle
	 */
	private float fixAngle(float value){
		if (value > 360.0f){
			return 360.0f - value;
		}
		else if (value < 0){
			return value + 360.0f;
		}
		
		return value;
	}

}
