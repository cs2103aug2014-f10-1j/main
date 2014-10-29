package stream;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import util.StreamUtil;
import static org.junit.Assert.*;

// @author A0093874N

/**
 * Most tags here are based on the "combining multiple inputs" heuristic.
 * 
 * "Equivalence partitioning" can also be seen for null and non-null field
 * modification (see nullModificationTest).
 */
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
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		st.filterAndProcessInput("add do CS2105");
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		st.filterAndProcessInput("add do CS2106");
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
		st.filterAndProcessInput("undo");
		assertEquals("do CS2106 is not included", false,
				st.hasTask("do CS2106"));
		st.filterAndProcessInput("undo");
		assertEquals("do CS2105 is not included", false,
				st.hasTask("do CS2105"));
		st.filterAndProcessInput("undo");
		assertEquals("do CS2103 is not included", false,
				st.hasTask("do CS2103"));
	}

	@Test
	public void undoRemoveTest() throws Exception {
		st.filterAndProcessInput("add do CS2103");
		st.filterAndProcessInput("delete 1");
		assertEquals("do CS2103 is not included", false,
				st.hasTask("do CS2103"));
		st.filterAndProcessInput("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
	}

	@Test
	public void undoRenameTest() throws Exception {
		st.filterAndProcessInput("add do CS2103");
		st.filterAndProcessInput("name 1 do CS2107");
		st.filterAndProcessInput("name 1 do CS2104");
		assertEquals("do CS2104 is included", true, st.hasTask("do CS2104"));
		st.filterAndProcessInput("undo");
		assertEquals("do CS2107 is included", true, st.hasTask("do CS2107"));
		st.filterAndProcessInput("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
	}

	@Test
	public void undoClearTest() throws Exception {
		st.filterAndProcessInput("add do CS2103");
		st.filterAndProcessInput("add do CS2104");
		st.filterAndProcessInput("add do CS2105");
		st.filterAndProcessInput("add do CS2106");
		st.filterAndProcessInput("add do CS2107");
		st.filterAndProcessInput("clear");
		assertEquals("no tasks added", 0, st.stobj.getNumberOfTasks());
		st.filterAndProcessInput("undo");
		assertEquals("5 tasks added", 5, st.stobj.getNumberOfTasks());
	}

	@Test
	public void undoModifyTest() throws Exception {
		String taskNameForTest = "a task";
		String newTaskName = "another task";
		st.filterAndProcessInput("add " + taskNameForTest);
		st.filterAndProcessInput("modify 1 name " + newTaskName
				+ " tag fordemo v0.2 desc multiple inputs");
		assertEquals("new name is \"" + newTaskName + "\"", false,
				st.hasTask(taskNameForTest));
		assertEquals("has description", "multiple inputs",
				st.stobj.getTask(newTaskName).getDescription());
		assertFalse("has tags", st.stobj.getTask(newTaskName).getTags()
				.isEmpty());
		st.filterAndProcessInput("undo");
		assertEquals("old name is \"" + taskNameForTest + "\"", true,
				st.hasTask(taskNameForTest));
		assertEquals("no description", null, st.stobj.getTask(taskNameForTest)
				.getDescription());
		assertTrue("no tags", st.stobj.getTask(taskNameForTest).getTags()
				.isEmpty());
		st.filterAndProcessInput("undo");
		assertFalse("no task added", st.hasTask(taskNameForTest));
	}

	@Test
	public void nullModificationTest() throws Exception {
		String taskNameForTest = "a task";
		st.filterAndProcessInput("add " + taskNameForTest);
		st.filterAndProcessInput("modify 1 due 11/11 desc multiple inputs");
		assertEquals("has date 11/11", "11/11/2014",
				StreamUtil.getCalendarWriteUp(st.stobj.getTask(taskNameForTest)
						.getDeadline()));
		assertEquals("has description", "multiple inputs",
				st.stobj.getTask(taskNameForTest).getDescription());
		st.filterAndProcessInput("modify 1 due null desc null");
		assertEquals("has date 11/11", null, st.stobj.getTask(taskNameForTest)
				.getDeadline());
		assertEquals("has description", null, st.stobj.getTask(taskNameForTest)
				.getDescription());
	}
	
	@Test
	public void undoSearchTest() throws Exception {
		// undoing search is done by invoking clrsrc - short form of clear search
		st.filterAndProcessInput("add a task");
		st.filterAndProcessInput("add some task");
		st.filterAndProcessInput("add another task");
		st.filterAndProcessInput("add new task");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
		st.filterAndProcessInput("search new");
		assertEquals("1 task viewable", 1, st.stui.getNumberOfTasksStored());		
		st.filterAndProcessInput("clrsrc");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
	}
	
	@Test
	public void undoFilterTest() throws Exception {
		st.filterAndProcessInput("add a task");
		st.filterAndProcessInput("add some task");
		st.filterAndProcessInput("add another task");
		st.filterAndProcessInput("add new task");
		st.filterAndProcessInput("mark 1 done");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
		st.filterAndProcessInput("filter done");
		assertEquals("1 task viewable", 1, st.stui.getNumberOfTasksStored());		
		st.filterAndProcessInput("filter ongoing");
		assertEquals("3 tasks viewable", 3, st.stui.getNumberOfTasksStored());
		st.filterAndProcessInput("clrsrc");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());		
	}

}