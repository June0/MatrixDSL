package org.isep.matrixDSL.asm;

public class ByteToClass extends ClassLoader {
	
	public void byteToClass(byte[] bytes) {
		Class addClass = defineClass("Addition", bytes, 0, bytes.length);
		
		resolveClass(addClass);
	}
}
