package org.isep.matrixDSL.java;

import java.util.ArrayList;

import clojure.lang.PersistentVector;

public class PersistentVectorCompiler {
	public static String test() {
		
		ArrayList<Integer> array = new ArrayList<Integer>();
		array.add(1);
		array.add(3);
		array.add(5);
		PersistentVector test = PersistentVector.create(array);
		System.out.print("Array ? : "+test.toArray()+"..");
		int cnt = test.count();
		System.out.print(cnt);
		
		System.out.println("object ? : "+test.tail.toString());
		return "TODO";
	}
	
	public static int hello(int n) {
		return 53+n;
	}
}
