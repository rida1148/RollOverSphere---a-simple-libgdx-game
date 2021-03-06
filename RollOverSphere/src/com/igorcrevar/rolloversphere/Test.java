package com.igorcrevar.rolloversphere;

import com.badlogic.gdx.math.Vector3;
import com.igorcrevar.rolloversphere.collsion.CollisionSolver;
import com.igorcrevar.rolloversphere.collsion.ICollisionIterationHandler;

public class Test implements ICollisionIterationHandler{
	protected static class SimpleTag
	{
		public SimpleTag(String t){
			text = t;
		}
		public String text;
	}
	
	public static void main(String[] argv) {
		ICollisionIterationHandler handler = new Test();
		Vector3 posStart = new Vector3();
		Vector3 posEnd = new Vector3();
		Object tag = new Test.SimpleTag("Federer");
		posStart.set(10.0f, 0.0f, 1.0f);
		posEnd.set(-33.0f, 0.0f, -30.5f);
		CollisionSolver.iterateOver(handler, posStart, posEnd, tag);
	}
	
	@Override
	public boolean iterationHandler(Vector3 position, Object tag) {
		System.out.println(String.format("%f %f %f", position.x, position.y, position.z));
		return true;
	}

}
