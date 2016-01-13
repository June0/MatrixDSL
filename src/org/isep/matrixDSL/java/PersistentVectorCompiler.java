package org.isep.matrixDSL.java;

import java.util.ArrayList;

import org.isep.matrixDSL.java.domain.Vector;

import clojure.lang.PersistentVector;

public class PersistentVectorCompiler {
	public static String test(PersistentVector vector) {
		String operation;
		
		/*ArrayList<Integer> array = new ArrayList<Integer>();
		array.add(1);
		array.add(3);
		array.add(5);
		PersistentVector test = PersistentVector.create(array);*/
		//System.out.print("Array ? : "+vector.toArray()+"..");
		int cnt = vector.count();
		//System.out.println(vector);
		PersistentVector addsub = (PersistentVector) vector.get(1);
		//System.out.println(addsub);
		//System.out.println(addsub.get(0).toString());
		addSubPersistentVector((String) addsub.get(0).toString(), (PersistentVector) addsub.get(1), (PersistentVector) addsub.get(2));

		
		//System.out.print(cnt);
		
		//System.out.println("object ? : "+vector.tail.toString());
		return "TODO";
	}
	
	/**
	 * 
	 * @param expression first persistent vector expression
	 * @param persistentVector expression 1 if operation
	 * @param persistentVector2 expression 2 if operation
	 */
	private static void addSubPersistentVector(String addsub,
			PersistentVector persistentVector,
			PersistentVector persistentVector2) {


		
		if (persistentVector.get(0).toString().equals(":vector")) {
			Vector vector1 = new Vector(persistentVector);
			Vector vector2 = new Vector(persistentVector2);
			if(addsub.equals(":add")) {
				addVector(vector1, vector2);
			}
			else if (addsub.equals(":sub")) {
				subVector(vector1, vector2);
			}
		}
		else if(persistentVector.get(0).toString().equals(":add") || persistentVector.get(0).toString().equals(":sub")) {
			PersistentVector newPersistentVector1 = (PersistentVector) persistentVector.get(1);
			PersistentVector newPersistentVector2 = (PersistentVector) persistentVector.get(2);
			
			Vector vector3 = new Vector(persistentVector2);
			addSubPersistentVector(persistentVector.get(0).toString(), 
					newPersistentVector1, 
					newPersistentVector2);
			if(persistentVector.get(0).toString().equals(":add")) {
				System.out.print("+"+ vector3.getArgument());
			}
			else if (persistentVector.get(0).toString().equals(":sub")) {
				System.out.print("-"+ vector3.getArgument());
			}
			
		}
	}

	private static int addVector(Vector vector1, Vector vector2) {
		int somme = vector1.getArgument() + vector2.getArgument();
		System.out.println("somme : ("+ somme+")");
		return somme;
		
	}

	private static int subVector(Vector vector1, Vector vector2) {
		int soustraction = vector1.getArgument() - vector2.getArgument();
		System.out.println("soustraction : ("+ soustraction+")");
		return soustraction;
	}

	public static int hello(int n) {
		return 53+n;
	}
}
