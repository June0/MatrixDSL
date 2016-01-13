package org.isep.matrixDSL.java.asm;

public class Addition {
	
	public static void main(String[] args) {
		System.out.println(""+getValueAdd(1,2));
	}
	
	public static int getValueAdd(int a, int b){
		return add(a,b);
	}
	
	public static int add(int a, int b){
		return a + b;
	}
	
	public static int tabTest(int[] tab){
		int a = tab[0];
		return a;
	}
}
