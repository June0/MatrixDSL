package org.isep.matrixDSL.java.asm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ByteToClass extends ClassLoader {
	
	public void byteToClass(byte[] bytes) {
		Class addClass = defineClass("Addition", bytes, 0, bytes.length);
		
		try {
			String[] tab = {" "};
			Method method = addClass.getMethod("add", int.class, int.class);
			System.out.print(method.invoke(null,1, 2));
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resolveClass(addClass);
	}
	
	public Class byteToClass(byte[] bytes, String className) {
		
		return defineClass(className, bytes, 0, bytes.length);
	}
}
