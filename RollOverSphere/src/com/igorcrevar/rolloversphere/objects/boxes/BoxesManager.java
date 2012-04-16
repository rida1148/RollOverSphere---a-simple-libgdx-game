package com.igorcrevar.rolloversphere.objects.boxes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.collsion.CollisionSolver;
import com.igorcrevar.rolloversphere.collsion.ICollisionIterationHandler;
import com.igorcrevar.rolloversphere.objects.boxes.factory.IBoxesFactory;

public class BoxesManager implements ICollisionIterationHandler{
	private List<Box> mBoxes = new ArrayList<Box>();
	//list of all boxes intersected with passed sphere
	private List<Box> mIntersectedBoxes = new ArrayList<Box>();
	//list of all boxes expired(timeouted) after update
	private List<Box> mExpiredBoxes = new ArrayList<Box>();
	
	private float mRadiusOfBall;
	private IBoxesFactory mBoxesFactory;	
	private float mHeight;
	private float mWidth;
	private float mMinX;
	private float mMinZ;
	
	public BoxesManager(IBoxesFactory bf){
		setBoxesFactory(bf);
	}
	
	public void init(float minX, float maxX, float minZ, float maxZ){
		this.mMinX = minX;
		this.mMinZ = minZ;
		this.mHeight = Math.abs(maxZ - minZ);
		this.mWidth = Math.abs(maxX - minX);
	}
	
	public void setBoxesFactory(IBoxesFactory bf){
		mBoxesFactory = bf;
	}
	
	public synchronized void addNew(Vector3 pos, float r){
		Box pb = mBoxesFactory.get();		
		pb.init();
		pb.position.y = 0.5f;
		
		boolean isCollided = false;		
		do{			
			pb.position.x = (float)(Math.random() * mWidth + mMinX);
			pb.position.z = (float)(Math.random() * mHeight + mMinZ);
			if (!CollisionSolver.isCollide(pos, r, pb.position, pb.boundingSphereR)){
				isCollided = false;
				for (Box t: mBoxes){
					if (CollisionSolver.isCollide(pb.position, pb.boundingSphereR, t.position, t.boundingSphereR)){
						isCollided = true;
						break;
					}
				}
			}
			else{
				isCollided = true;
			}
		}
		while (isCollided);
				
		mBoxes.add(pb);
	}
	
	/**
	 * Update boxes, remove not alive and render
	 * @param camera
	 * @param timeDiff
	 */
	public void updateAndRender(PerspectiveCamera camera, float timeDiff){
		mExpiredBoxes.clear(); //clear list of expired
		int size = mBoxes.size();
		for (int i = size - 1; i >= 0; --i){
			Box box = mBoxes.get(i);	
			if (box.updateBox(timeDiff)){
				box.render(camera);
			}
			else{
				mBoxes.remove(i);
				mExpiredBoxes.add(box); //add to expired list
			}
		}
	}
	
	private void doCollideCheck(Vector3 pos, float r){
		int size = mBoxes.size();
		for (int i = size - 1; i >= 0; --i){
			Box box = mBoxes.get(i);
			if (CollisionSolver.isCollide(pos, r, box.position, box.boundingSphereR)){
				mIntersectedBoxes.add(box);
				mBoxes.remove(box);
			}
		}
	}
	
	/* Do not call this method!!!
	 * (non-Javadoc)
	 * @see com.igorcrevar.rolloversphere.collsion.ICollisionIterationHandler#iterationHandler(com.badlogic.gdx.math.Vector3, java.lang.Object)
	 */
	@Override
	public boolean iterationHandler(Vector3 position, Object tag) {
		doCollideCheck(position, mRadiusOfBall);		
		return true;
	}
	
	public void doCollideCheck(Vector3 posStart, Vector3 posEnd, float r){
		mIntersectedBoxes.clear();
		//TODO: mRadiusOfBall should be in tag
		mRadiusOfBall = r;		
		CollisionSolver.iterateOver(this, posStart, posEnd, null);		
	}
	
	public void remove(List<Box> pbs){
		for (Box pb : pbs){
			mBoxes.remove(pb);
		}
	}
	
	/**
	 * Check collision with sphere(center from posStart to posEnd and radius s) for every box
	 * remove collided boxes. update and render boxes 
	 * @return list of removed boxes
	 */
	public synchronized void doAll(PerspectiveCamera camera, float timeDiff, 
								   Vector3 posStart, Vector3 posEnd, float r){
		doCollideCheck(posStart, posEnd, r);
		updateAndRender(camera, timeDiff);
	}
	
	public synchronized int getBoxesCount(){
		return mBoxes.size();
	}
	
	public synchronized int getNotUpgradeBoxCount(){
		int size = 0;
		for (Box box:mBoxes){
			if (box.getUpgradeType() == UpgradeType.NOT_UPGRADE){
				++size;
			}
		}
		
		return size;
	}

	public List<Box> getExpired(){
		return mExpiredBoxes;
	}
	
	public List<Box> getIntersectedBoxes(){
		return mIntersectedBoxes;
	}
	
}
