package org.isep.matrixDSL.java;

import org.isep.matrixDSL.java.domain.Matrix;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import clojure.lang.PersistentVector;

public class MatrixCompiler implements Opcodes {
	private ClassWriter cw;
	private MethodVisitor mv;
	
	private String className;
	/**
	 * The number of parameters which attempt the run method generate.
	 * Used also as the created matrix that is the result of the computation.
	 */
	private int paramNumber;
	
	/**
	 * 
	 * @param a persistentVector like : 
		[:S [:add [:matrix [:width 2] [:height 3] [:argument 2]] [:matrix [:width 2] [:height 3] [:argument 1]]]]
	 */
	public byte[] compileExpression(PersistentVector expression, String className) {
		this.className = className;
		PersistentVector globalExpression = (PersistentVector) expression.get(1);
		
		paramNumber = getParamsNumber(globalExpression, 0);
		
		String operation = (String) globalExpression.get(0).toString();
		PersistentVector matrixOrOperation = (PersistentVector) globalExpression.get(1);
		PersistentVector secondMatrix = (PersistentVector) globalExpression.get(2);
		
		cw = new ClassWriter(0);
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		
		int matrixWidth = Integer.parseInt((String) ((PersistentVector) secondMatrix.get(1)).get(1));
		int matrixHeight = Integer.parseInt((String) ((PersistentVector) secondMatrix.get(2)).get(1));
		createAddByteCodeMethod(matrixWidth, matrixHeight);
		createSubByteCodeMethod(matrixWidth, matrixHeight);
		
		String runArguments = constructStringDefiningArguments(paramNumber);
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", runArguments, null, null);
		mv.visitCode();
		// TODO
		mv.visitIntInsn(BIPUSH, matrixWidth);
		mv.visitIntInsn(NEWARRAY, T_INT);
		mv.visitVarInsn(ASTORE, paramNumber);

		addSubPersistentVector(operation, matrixOrOperation, secondMatrix);
		
		// TODO
		mv.visitVarInsn(ALOAD, paramNumber);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, paramNumber+1);
		mv.visitEnd();
		
		cw.visitEnd();
		return cw.toByteArray();
	}

	/**
	 * Create an add bytecode method from cw 
	 * which compute two matrix of width and height defined in parameters, in the first one
	 */
	private void createAddByteCodeMethod(int width, int height) {
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "add", "([[I[[I)V", null, null);
		mv.visitCode();
		
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				// Put i-j value of first matrix in the stack for visitInsn(IASTORE)
				mv.visitVarInsn(ALOAD, 0);
				mv.visitIntInsn(BIPUSH, i);
				mv.visitInsn(AALOAD);
				mv.visitIntInsn(BIPUSH, j);
				// Load i-j value of first matrix for the add
				mv.visitVarInsn(ALOAD, 0);
				mv.visitIntInsn(BIPUSH, i);
				mv.visitInsn(AALOAD);
				mv.visitIntInsn(BIPUSH, j);
				mv.visitInsn(IALOAD);
				// Load i-j value of second matrix for the add
				mv.visitVarInsn(ALOAD, 1);
				mv.visitIntInsn(BIPUSH, i);
				mv.visitInsn(AALOAD);
				mv.visitIntInsn(BIPUSH, j);
				mv.visitInsn(IALOAD);
				// Compute
				mv.visitInsn(IADD);
				mv.visitInsn(IASTORE);
			}
		}
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(5, 2);
		mv.visitEnd();
		mv = null;
	}

	private void createSubByteCodeMethod(int width, int height) {
		
	}
	
	/**
	 * 
	 * @param operation (add or sub)
	 * @param a vector or operation for recursive call
	 * @param a vector
	 */
	private void addSubPersistentVector(String addsub, PersistentVector matrixOrOperation, PersistentVector matrix) {
		Matrix currentMatrix = new Matrix(matrix);
		if (isMatrix(matrixOrOperation)) {
			Matrix deepestMatrix = new Matrix(matrixOrOperation);
			
			if(addsub.equals(":add")) {
				addMatrix(deepestMatrix);
				addMatrix(currentMatrix);
			}
			else if (addsub.equals(":sub")) {
				addMatrix(deepestMatrix);
				subMatrix(currentMatrix);
			}
		} else {
			PersistentVector childMatrixOrOperation = (PersistentVector) matrixOrOperation.get(1);
			PersistentVector childMatrix = (PersistentVector) matrixOrOperation.get(2);
			
			addSubPersistentVector(matrixOrOperation.get(0).toString(), childMatrixOrOperation, childMatrix);
			
			if(addsub.equals(":add")) {
				addMatrix(currentMatrix);
			} else if (addsub.equals(":sub")) {
				subMatrix(currentMatrix);
			}
		}
	}

	/**
	 * @param matrix to add to the reference matrix
	 */
	private void addMatrix(Matrix matrix) {
		mv.visitVarInsn(ALOAD, paramNumber);
		mv.visitVarInsn(ALOAD, matrix.getArgument()-1);
		mv.visitMethodInsn(INVOKESTATIC, className, "add", "([I[I)V", false);
	}

	private void subMatrix(Matrix matrix) {
		//TODO
	}
	
	
	/**
	 * Read recursively an expression and retrieve the param number for run method
	 */
	private static int getParamsNumber(PersistentVector expression, int knownParamsNumber) {
		if (isMatrix(expression)) {
			Matrix matrix = new Matrix(expression);
			if(matrix.getArgument() > knownParamsNumber) {
				knownParamsNumber = matrix.getArgument();
			}
		} else {
			Matrix matrix = new Matrix((PersistentVector) expression.get(2));
			if(matrix.getArgument() > knownParamsNumber) {
				knownParamsNumber = matrix.getArgument();
			}
			knownParamsNumber = getParamsNumber((PersistentVector) expression.get(1), knownParamsNumber);
		}
		return knownParamsNumber;
	}
	
	/**
	 * Return a string defining array arguments for ASM, like ([[I[[I)[[I
	 */
	private static String constructStringDefiningArguments(int paramNumber) {
		//Build arguments string
		String arguments = "(";
		for (int i=0; i<paramNumber; i++) {
			arguments += "[[I";
		}
		
		// Close arguments string
		arguments += ")[[I";
		return arguments;
	}
	
	/**
	 * @return true if matrix, false if operation
	 */
	private static boolean isMatrix(PersistentVector matrixOrOperation) {
		if (matrixOrOperation.get(0).toString().equals(":matrix")){
			return true;
		} else {
			return false;
		}
	}
}
