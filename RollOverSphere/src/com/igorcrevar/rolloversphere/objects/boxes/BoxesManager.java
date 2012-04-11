package com.igorcrevar.rolloversphere.objects.boxes;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.collsion.CollisionSolver;
import com.igorcrevar.rolloversphere.collsion.ICollisionIterationHandler;
import com.igorcrevar.rolloversphere.objects.boxes.factory.IBoxesFactory;

public class BoxesManager implements ICollisionIterationHandler{
	private List<Box> mBoxes = new ArrayList<Box>();
	//doAll method will use these field
	private List<Box> mRemovedBoxes = new ArrayList<Box>();
	private float mRadiusOfBall;
	private IBoxesFactory mBoxesFactory;
	
	private float height;
	private float width;
	private float minX;
	private float minZ;
	
	public BoxesManager(IBoxesFactory bf){
		setBoxesFactory(bf);
	}
	
	public void init(float minX, float maxX, float minZ, float maxZ){
		this.minX = minX;
		this.minZ = minZ;
		this.height = Math.abs(maxZ - minZ);
		this.width = Math.abs(maxX - minX);
	}
	
	public void setBoxesFactory(IBoxesFactory bf){
		mBoxesFactory = bf;
	}
	
	public synchronized void addNew(Vector3 pos, float r){
		Box pb = mBoxesFactory.get();
		
		pb.init();
		boolean isCollided = false;
		pb.position.y = 0.5f;
		
		do{			
			pb.position.x = (float)(Math.random() * width + minX);
			pb.position.z = (float)(Math.random() * height + minZ);
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
		int size = mBoxes.size();
		for (int i = size - 1; i >= 0; --i){
			Box box = mBoxes.get(i);	
			if (box.updateBox(timeDiff)){
				box.render(camera);
			}
			else{
				mBoxes.remove(i);
			}
		}
	}
	
	public List<Box> getCollided(Vector3 pos, float r){
		List<Box> rv = null;
		for (Box t: mBoxes){
			if (CollisionSolver.isCollide(pos, r, t.position, t.boundingSphereR)){
				if (rv == null){
					rv = new Stack<Box>();
					rv.add(t);
				}
			}
		}
		
		return rv;
	}
	
	public void remove(List<Box> pbs){
		for (Box pb : pbs){
			mBoxes.remove(pb);
		}
	}
	
	public synchronized List<Box> doAll(PerspectiveCamera camera, float timeDiff, Vector3 posStart, Vector3 posEnd, float r){
		//TODO: create new class and pass this values to handler via tag object
		mRemovedBoxes.clear();
		mRadiusOfBall = r;
		//call interpolation solved
		CollisionSolver.iterateOver(this, posStart, posEnd, null);
		if (mRemovedBoxes.size() > 0){		
			remove(mRemovedBoxes);			
		}
		updateAndRender(camera, timeDiff);
		return mRemovedBoxes;
	}
	
	public synchronized int getBoxesCount(){
		return mBoxes.size();
	}
	
	public synchronized int getNotUpgradeBoxCount(){
		int size = 0;
		for (Box box:mBoxes){
			if (box.getUpgrade() == BoxType.NOT_UPGRADE){
				++size;
			}
		}
		
		return size;
	}


	@Override
	public boolean iterationHandler(Vector3 position, Object tag) {
		List<Box> rv = getCollided(position, mRadiusOfBall);
		if (rv != null){
			mRemovedBoxes.addAll(rv);
		}
		
		return true;
	}
}