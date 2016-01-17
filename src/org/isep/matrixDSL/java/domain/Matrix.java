package org.isep.matrixDSL.java.domain;

import clojure.lang.PersistentVector;

public class Matrix {
	private int size;
	private int height;
	private int width;
	
	public Matrix(PersistentVector vector){
		PersistentVector size_info = (PersistentVector) vector.get(1);
		this.size = Integer.parseInt(size_info.get(1).toString());
		
		//TODO height and width
	}

	public int getSize() {
		return size;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
