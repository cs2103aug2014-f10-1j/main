package model;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StreamAddTaskTest {

	private StreamObject streamObject;

	@Before
	public void setup() throws Exception {
		streamObject = new StreamObject();
		streamObject.addTask("Submit CE2");
		streamObject.addTask("Study for midterms");
	}
	
	@Test
	public void addTaskTest1() {
		assertEquals("Submit CE2 is added to the list.", true,
				streamObject.hasTask("Submit CE2"));
	}
	@Test
	public void addTaskTest2() {
		assertEquals("Study for midterms is added to the list.", true,
				streamObject.hasTask("Study for midterms"));
	}
	@Test
	public void addTaskTest3() {
		assertEquals("Watch Rurouni Kenshin is not added to the list.", false,
				streamObject.hasTask("Watch Rurouni Kenshin"));
	}
	@Test
	public void addTaskTest4() {
		try {
			streamObject.addTask("Submit CE2");
		} catch (Exception e) {
			assertEquals("Exception should be generated.",
					"\"Submit CE2\" already exists in the tasks list.",
					e.getMessage());
		}
	}
	
	@Test
	public void deleteTest() throws Exception {
		streamObject.deleteTask("Submit CE2");
		assertEquals(streamObject.hasTask("Submit CE2"), false);
	}
	
	
}
