package stream;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StreamAddTaskTest {

	private static Stream stream;

	@Before
	public void setup() throws Exception {
		stream = Stream.newStream();
		stream.addTask("Submit CE2");
		stream.addTask("Study for midterms");
	}
	
	@Test
	public void addTaskTest1() throws Exception {
		assertEquals("Submit CE2 is added to the list.", true,
				stream.hasTask("Submit CE2"));
	}

	@Test
	public void addTaskTest2() {
		assertEquals("Study for midterms is added to the list.", true,
				stream.hasTask("Study for midterms"));
	}

	@Test
	public void addTaskTest3() {
		assertEquals("Watch Rurouni Kenshin is not added to the list.", false,
				stream.hasTask("Watch Rurouni Kenshin"));
	}

	@Test
	public void addTaskTest4() {
		try {
			stream.addTask("Submit CE2");
		} catch (Exception e) {
			assertEquals("Exception should be generated.",
					"Error: \"Submit CE2\" already exists in the tasks list.",
					e.getMessage());
		}
	}
	
	@Test
	public void deleteTest() throws Exception{
		stream.initialize();
		stream.addTask("Submit CE2");
		stream.deleteTask("Submit CE2");
		assertEquals(stream.hasTask("Submit CE2"), false);
	}
}
