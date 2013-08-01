package javax.el.test;

public class TestClass {
	private static class TestInterfaceImpl implements TestInterface {
		public int getFourtyTwo() {
			return 42;
		}
	}
	
	private static class NestedClass {
		public static class TestInterfaceImpl2 implements TestInterface {
			public int getFourtyTwo() {
				return 42;
			}
		}
	}
	
	private TestInterface anonymousTestInterface = new TestInterface() {
		public int getFourtyTwo() {
			return 42;
		}
	};

	public TestInterface getNestedTestInterface() {
		return new TestInterfaceImpl();
	}

	public TestInterface getNestedTestInterface2() {
		return new NestedClass.TestInterfaceImpl2();
	}

	public TestInterface getAnonymousTestInterface() {
		return anonymousTestInterface;
	}
}
