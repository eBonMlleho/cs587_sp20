/*
 * Author: Zhanghao Wen
 */

import java.io.IOException;

//import com.sun.corba.se.impl.ior.ByteBuffer;

public class Test {

	public static void main(String[] args) throws IOException {

		// Testing GetLocalTime
//		GetLocalTime getTime = new GetLocalTime();
//
//		getTime.valid.setValue(false);
//
//		System.out.println("Before execution, time valid is : " + getTime.valid.getValue());
//
//		getTime.execute("localhost", 6666);
//
//		int t = getTime.time.getValue();
//		// Time valid becomes true if getLocalTime return successfully
//		System.out.println("After execution, time valid is : " + getTime.valid.getValue());
//
//		System.out.println("Time is : " + new java.util.Date((long) t * 1000));
//

		// Testing GetLocalOS
		GetLocalOS getOS = new GetLocalOS();

		System.out.println("Before execution, OS valid is : " + getOS.valid.getValue());

		getOS.execute("localhost", 6666);

		String s = getOS.getOSText();

		System.out.println("After execution, OS valid is : " + getOS.valid.getValue());

		System.out.println("Operating System is : " + s);

	}

}
