package org.isep.matrixDSL.java;

import org.isep.matrixDSL.java.domain.Vector;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import clojure.lang.PersistentVector;

public class PersistentVectorCompiler implements Opcodes {
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
	public byte[] compileExpression(PersistentVector vector, String className) {
		PersistentVector globalExpression = (PersistentVector) vector.get(1);
		
		int paramNumber = getParamsNumber(globalExpression, 0);
//		System.out.println("Param number : "+paramNumber);
		
		String operation = (String) globalExpression.get(0).toString();
		PersistentVector vectorOrOperation = (PersistentVector) globalExpression.get(1);
		PersistentVector secondVector = (PersistentVector) globalExpression.get(2);
		
		cw = new ClassWriter(0);
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		
		// TODO create add and sub bytecode methods
		int vectorSize = Integer.parseInt((String) ((PersistentVector) secondVector.get(1)).get(1));
		createAddByteCodeMethod(vectorSize);
		
		String runArguments = constructStringDefiningArguments(paramNumber);
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", runArguments, null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, className, "add", "([I[I)V", false);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		
//		addSubPersistentVector(operation, vectorOrOperation, secondVector);
		
		cw.visitEnd();
		return cw.toByteArray();
	}

	/**
	 * Create an add bytecode method from cw 
	 * which compute two array of size paramNumber in the first one
	 */
	private void createAddByteCodeMethod(int paramNumber) {
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "add", "([I[I)V", null, null);
		mv.visitCode();
		
		for (int i=0; i<paramNumber; i++) {
			// Put i value of first array in the stack for visitInsn(IASTORE)
			mv.visitVarInsn(ALOAD, 0);
			mv.visitIntInsn(BIPUSH, i);
			// Load i value of first array for the add
			mv.visitVarInsn(ALOAD, 0);
			mv.visitIntInsn(BIPUSH, i);
			mv.visitInsn(IALOAD);
			// Load i value of first array for the add
			mv.visitVarInsn(ALOAD, 1);
			mv.visitIntInsn(BIPUSH, i);
			mv.visitInsn(IALOAD);
			mv.visitInsn(IADD);
			mv.visitInsn(IASTORE);
		}
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(5, paramNumber);
		mv.visitEnd();
		mv = null;
	}
	
	/**
	 * 
	 * @param operation (add or sub)
	 * @param a vector or operation for recursive call
	 * @param a vector
	 */
	private void addSubPersistentVector(String addsub, PersistentVector vectorOrOperation, PersistentVector vector) {
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

	private int addVector(Vector vector1, Vector vector2) {
		int somme = vector1.getArgument() + vector2.getArgument();
		System.out.println("somme : ("+ somme+")");
		
		//TODO call the java methode who generate the bit code who call the add method
		//TODO put the result as reference tab
		return somme;
	}

	private int subVector(Vector vector1, Vector vector2) {
		int soustraction = vector1.getArgument() - vector2.getArgument();
		System.out.println("soustraction : ("+ soustraction+")");
		
		//TODO call the java methode who generate the bit code who call the sub method
		//TODO put the result as reference tab
		return soustraction;
	}
	
	
	/**
	 * Read recursively an expression and retrieve the param number for run method
	 */
	private static int getParamsNumber(PersistentVector expression, int knownParamsNumber) {
		if (isVector(expression)) {
			Vector vector = new Vector(expression);
			if(vector.getArgument() > knownParamsNumber) {
				knownParamsNumber = vector.getArgument();
			}
		} else {
			Vector vector = new Vector((PersistentVector) expression.get(2));
			if(vector.getArgument() > knownParamsNumber) {
				knownParamsNumber = vector.getArgument();
			}
			knownParamsNumber = getParamsNumber((PersistentVector) expression.get(1), knownParamsNumber);
		}
		return knownParamsNumber;
	}
	
	/**
	 * Return a string defining array arguments for ASM, like ([i[i)V
	 */
	private static String constructStringDefiningArguments(int paramNumber) {
		//Build arguments string
		String arguments = "(";
		for (int i=0; i<paramNumber; i++) {
			arguments += "[I";
		}
		
		// Close arguments sting, add V for void, nothing returned
		arguments += ")[I";
		return arguments;
	}
	
	/**
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
