package org.isep.matrixDSL.java.domain;

import clojure.lang.PersistentVector;

public class Vector {
	private int size;
	private int argument;
	
	public Vector(PersistentVector vector){
		PersistentVector size_info = (PersistentVector) vector.get(1);
		this.size = Integer.parseInt(size_info.get(1).toString());
		
		PersistentVector argument_info = (PersistentVector) vector.get(2);
		this.argument = Integer.parseInt(argument_info.get(1).toString());
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getArgument() {
		return this.argument;
	}
}
