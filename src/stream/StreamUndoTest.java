package stream;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StreamUndoTest {

	private static Stream st;

	@Before
	public void setUp() {
		new File("streamtestfile.json").delete();
		st = new Stream("streamtestfile");
	}

	@Test
	public void undoAddTest() throws Exception {
		st.filterAndProcessInput("add do CS2103");
		st.filterAndProcessInput("add do CS2105");
		st.filterAndProcessInput("add do CS2106");
		st.filterAndProcessInput("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is not included", false,
				st.hasTask("do CS2106"));		st.filterAndProcessInput("undo");

	}

	@Test
	public void undoRemoveTest() throws Exception {
		st.filterAndProcessInput("add do CS2103");
		st.filterAndProcessInput("add do CS2105");
		st.filterAndProcessInput("add do CS2106");
		st.filterAndProcessInput("delete 2");
		st.filterAndProcessInput("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
	}
	
// de-utilize the test for now as modify is under modification
/*
	@Test
	public void undoModifyTest() throws Exception {
		st.filterAndProcessInput("add do CS2103");
		st.filterAndProcessInput("add do CS2105");
		st.filterAndProcessInput("add do CS2106");
		st.filterAndProcessInput("modify 1 do CS2107");
		st.filterAndProcessInput("modify 1 do CS2104");
		st.filterAndProcessInput("undo");
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
		assertEquals("do CS2107 is included", true, st.hasTask("do CS2107"));
	}
*/
	@Test
	public void undoClearTest() throws Exception {
		st.filterAndProcessInput("add do CS2103");
		st.filterAndProcessInput("add do CS2104");
		st.filterAndProcessInput("add do CS2105");
		st.filterAndProcessInput("add do CS2106");
		st.filterAndProcessInput("add do CS2107");
		st.filterAndProcessInput("clear");
		st.filterAndProcessInput("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		assertEquals("do CS2104 is included", true, st.hasTask("do CS2104"));
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
		assertEquals("do CS2107 is included", true, st.hasTask("do CS2107"));
	}
}
