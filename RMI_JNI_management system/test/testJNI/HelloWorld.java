class HelloWorld 
{ 
	public native void displayHelloWorld(); 
	static 
     { 
			System.loadLibrary("hello"); 
			//System.load("D:\\graduate school\\cs587\\hw3\\hello.dll");
	} 
	public static void main(String[] args) 
	{ 
 			new HelloWorld().displayHelloWorld(); 
	} 
}
