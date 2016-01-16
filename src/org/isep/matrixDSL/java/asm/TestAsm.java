package org.isep.matrixDSL.java.asm;
import java.io.File;
import java.io.FileWriter;

import org.objectweb.asm.util.ASMifier;

public class TestAsm {

	public static void main(String[] args) {
		 String[] tab =
		 {"bin/org/isep/matrixDSL/java/asm/Addition.class"};
		 try {
		 ASMifier.main(tab);
		 } catch (Exception e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }
		 
//		 ByteToClass byteToClass = new ByteToClass();
//		 try {
//			byteToClass.byteToClass(AdditionDump.dump());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	}
}
