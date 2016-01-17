package org.isep.matrixDSL.java.domain;

import clojure.lang.PersistentVector;

public class Matrix {
	private int argument;
	private int height;
	private int width;
	
	public Matrix(PersistentVector matrix){
		PersistentVector width_info = (PersistentVector) matrix.get(1);
		this.argument = Integer.parseInt(width_info.get(1).toString());
		
		PersistentVector height_info = (PersistentVector) matrix.get(2);
		this.argument = Integer.parseInt(height_info.get(1).toString());
		
		PersistentVector argument_info = (PersistentVector) matrix.get(3);
		this.argument = Integer.parseInt(argument_info.get(1).toString());
	}

	public int getArgument() {
		return argument;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
