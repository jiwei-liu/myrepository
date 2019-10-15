package test;

public class TestClasserLoader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassLoader cl = TestClasserLoader.class.getClassLoader();
		System.out.println(cl.toString());
	}

}
