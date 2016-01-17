package org.isep.matrixDSL.java;

import org.isep.matrixDSL.java.domain.Vector;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import clojure.lang.PersistentVector;

public class MatrixCompiler implements Opcodes {
	private ClassWriter cw;
	private MethodVisitor mv;
	
	private String className;
	private int paramNumber;
	
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
		this.className = className;
		PersistentVector globalExpression = (PersistentVector) vector.get(1);
		
		paramNumber = getParamsNumber(globalExpression, 0);
		
		String operation = (String) globalExpression.get(0).toString();
		PersistentVector vectorOrOperation = (PersistentVector) globalExpression.get(1);
		PersistentVector secondVector = (PersistentVector) globalExpression.get(2);
		
		cw = new ClassWriter(0);
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		
		int vectorSize = Integer.parseInt((String) ((PersistentVector) secondVector.get(1)).get(1));
		createAddByteCodeMethod(vectorSize);
		createSubByteCodeMethod(vectorSize);
		// TODO create sub bytecode methods
		
		String runArguments = constructStringDefiningArguments(paramNumber);
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", runArguments, null, null);
		mv.visitCode();
		mv.visitIntInsn(BIPUSH, vectorSize);
		mv.visitIntInsn(NEWARRAY, T_INT);
		mv.visitVarInsn(ASTORE, paramNumber);

		addSubPersistentVector(operation, vectorOrOperation, secondVector);
		
		mv.visitVarInsn(ALOAD, paramNumber);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, paramNumber+1);
		mv.visitEnd();
		
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

	private void createSubByteCodeMethod(int vectorSize) {
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "sub", "([I[I)V", null, null);
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
			mv.visitInsn(ISUB);
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
		Vector currentVector = new Vector(vector);
		if (isVector(vectorOrOperation)) {
			Vector deepestVector = new Vector(vectorOrOperation);
			
			if(addsub.equals(":add")) {
				addVector(deepestVector);
				addVector(currentVector);
			}
			else if (addsub.equals(":sub")) {
				addVector(deepestVector);
				subVector(currentVector);
			}
		} else {
			PersistentVector childVectorOrOperation = (PersistentVector) vectorOrOperation.get(1);
			PersistentVector childVector = (PersistentVector) vectorOrOperation.get(2);
			
			addSubPersistentVector(vectorOrOperation.get(0).toString(), childVectorOrOperation, childVector);
			
			if(addsub.equals(":add")) {
				addVector(currentVector);
			} else if (addsub.equals(":sub")) {
				subVector(currentVector);
			}
		}
	}

	/**
	 * @param vector to add to the reference vector
	 */
	private void addVector(Vector vector) {
		mv.visitVarInsn(ALOAD, paramNumber);
		mv.visitVarInsn(ALOAD, vector.getArgument()-1);
		mv.visitMethodInsn(INVOKESTATIC, className, "add", "([I[I)V", false);
	}

	private void subVector(Vector vector) {
		mv.visitVarInsn(ALOAD, paramNumber);
		mv.visitVarInsn(ALOAD, vector.getArgument()-1);
		mv.visitMethodInsn(INVOKESTATIC, className, "sub", "([I[I)V", false);
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
}
