
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StreamUndoTest {

	private static Stream st;

	@Before
	public void setUp() {
		st = new Stream("streamtestfile");
	}

	@Test
	public void undoAddTest() throws Exception {
		st.processAndExecute("add do CS2103");
		st.processAndExecute("add do CS2105");
		st.processAndExecute("add do CS2106");
		st.processAndExecute("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is not included", false,
				st.hasTask("do CS2106"));		st.processAndExecute("undo");

	}

	@Test
	public void undoRemoveTest() throws Exception {
		st.processAndExecute("add do CS2103");
		st.processAndExecute("add do CS2105");
		st.processAndExecute("add do CS2106");
		st.processAndExecute("delete 2");
		st.processAndExecute("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
	}

	@Test
	public void undoModifyTest() throws Exception {
		st.processAndExecute("add do CS2103");
		st.processAndExecute("add do CS2105");
		st.processAndExecute("add do CS2106");
		st.processAndExecute("modify 1 do CS2107");
		st.processAndExecute("modify 1 do CS2104");
		st.processAndExecute("undo");
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
		assertEquals("do CS2107 is included", true, st.hasTask("do CS2107"));
	}

	@Test
	public void undoClearTest() throws Exception {
		st.processAndExecute("add do CS2103");
		st.processAndExecute("add do CS2104");
		st.processAndExecute("add do CS2105");
		st.processAndExecute("add do CS2106");
		st.processAndExecute("add do CS2107");
		st.processAndExecute("clear");
		st.processAndExecute("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		assertEquals("do CS2104 is included", true, st.hasTask("do CS2104"));
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
		assertEquals("do CS2107 is included", true, st.hasTask("do CS2107"));
	}
}
