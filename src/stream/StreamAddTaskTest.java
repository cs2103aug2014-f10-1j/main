package stream;
import static org.junit.Assert.*;
import org.junit.Test;
//@author A0096529N
public class StreamAddTaskTest {

	private static Stream str = new Stream();

	@Test
	public void addTaskTest1() throws Exception {
		str.initialize();
		Stream.addTask("Submit CE2");
		Stream.addTask("Study for midterms");
		assertEquals("Submit CE2 is added to the list.", true,
				str.hasTask("Submit CE2"));
	}

	@Test
	public void addTaskTest2() {
		assertEquals("Study for midterms is added to the list.", true,
				str.hasTask("Study for midterms"));
	}

	@Test
	public void addTaskTest3() {
		assertEquals("Watch Rurouni Kenshin is not added to the list.", false,
				str.hasTask("Watch Rurouni Kenshin"));
	}

	@Test
	public void addTaskTest4() {
		try {
			Stream.addTask("Submit CE2");
		} catch (Exception e) {
			assertEquals("Exception should be generated.",
					"Error: \"Submit CE2\" already exists in the tasks list.",
					e.getMessage());
		}
	}
	
	@Test
	public void deleteTest() throws Exception{
		str.initialize();
		Stream.addTask("Submit CE2");
		str.deleteTask("Submit CE2");
		assertEquals(str.hasTask("Submit CE2"), false);
	}
}
