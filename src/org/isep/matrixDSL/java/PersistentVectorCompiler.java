package org.isep.matrixDSL.java;

import org.isep.matrixDSL.java.asm.ByteToClass;
import org.isep.matrixDSL.java.domain.Vector;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import clojure.lang.PersistentVector;

public class PersistentVectorCompiler extends ClassLoader implements Opcodes{
	private ClassWriter cw;
	private FieldVisitor fv;
	private MethodVisitor mv;
	private AnnotationVisitor av0;
	
	/**
	 * 
	 * @param a vector like : 
		[:S
		 [:sub
		  [:add
		   [:add
		    [:add
		     [:vector [:vsize "3"] [:argument "2"]]
		     [:vector [:vsize "3"] [:argument "2"]]]
		    [:vector [:vsize "3"] [:argument "3"]]]
		   [:vector [:vsize "3"] [:argument "4"]]]
		  [:vector [:vsize "3"] [:argument "6"]]]]
	 */
	public static Class compileExpression(PersistentVector vector, String className) {
		ClassWriter cw = new ClassWriter(0);
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		//TODO create methode RUN with good argument numbeer
		
		PersistentVector globalExpression = (PersistentVector) vector.get(1);
		
		String operation = (String) globalExpression.get(0).toString();
		PersistentVector vectorOrOperation = (PersistentVector) globalExpression.get(1);
		PersistentVector secondVector = (PersistentVector) globalExpression.get(2);

		
		addSubPersistentVector(operation, vectorOrOperation, secondVector);
		
		cw.visitEnd();
		ByteToClass byteToClass = new ByteToClass();
		return byteToClass.byteToClass(cw.toByteArray(), className);
	}
	
	public static void test(PersistentVector vector) {
		
	}
	
	/**
	 * 
	 * @param operation (add or sub)
	 * @param a vector or operation for recursive call
	 * @param a vector
	 */
	private static void addSubPersistentVector(String addsub, PersistentVector vectorOrOperation, PersistentVector vector) {
		if (isVector(vectorOrOperation)) {
			Vector vector1 = new Vector(vectorOrOperation);
			Vector vector2 = new Vector(vector);
			if(addsub.equals(":add")) {
				addVector(vector1, vector2);
			}
			else if (addsub.equals(":sub")) {
				subVector(vector1, vector2);
			}
		} else {
			PersistentVector childVectorOrOperation = (PersistentVector) vectorOrOperation.get(1);
			PersistentVector childVector = (PersistentVector) vectorOrOperation.get(2);
			
			Vector vector3 = new Vector(vector);
			addSubPersistentVector(vectorOrOperation.get(0).toString(), childVectorOrOperation, childVector);
			
			if(addsub.equals(":add")) {
				System.out.print("+"+ vector3.getArgument());
				//TODO call the java methode who generate the bit code who call the add method between current tab and reference one.
				
			} else if (addsub.equals(":sub")) {
				System.out.print("-"+ vector3.getArgument());
				//TODO call the java methode who generate the bit code who call the sub method between current tab and reference one.
			}
		}
	}

	private static int addVector(Vector vector1, Vector vector2) {
		int somme = vector1.getArgument() + vector2.getArgument();
		System.out.println("somme : ("+ somme+")");
		
		//TODO call the java methode who generate the bit code who call the add method
		//TODO put the result as reference tab
		return somme;
		
	}

	private static int subVector(Vector vector1, Vector vector2) {
		int soustraction = vector1.getArgument() - vector2.getArgument();
		System.out.println("soustraction : ("+ soustraction+")");
		
		//TODO call the java methode who generate the bit code who call the sub method
		//TODO put the result as reference tab
		return soustraction;
	}
	
	/**
	 * 
	 * @return true if vector, false if operation
	 */
	private static boolean isVector(PersistentVector vectorOrOperation) {
		if (vectorOrOperation.get(0).toString().equals(":vector")){
			return true;
		} else {
			return false;
		}
	}

	public static int hello(int n) {
		return 53+n;
	}
	
	public void createAddMethod(){
		//TODO 
	}
	
	public void createSubMethod(){
		//TODO 
	}
}
