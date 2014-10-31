package stream;

import java.io.File;
import java.util.ArrayList;

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

	// for extreme convenience

	public void in(String input) {
		st.filterAndProcessInput(input);
	}

	public Boolean compare(ArrayList<String> actual, String[] expected) {
		if (actual.size() != expected.length) {
			return false;
		} else {
			for (int i = 0; i < actual.size(); i++) {
				if (!actual.get(i).equals(expected[i])) {
					return false;
				}
			}
			return true;
		}
	}

	@Test
	public void undoAddTest() throws Exception {
		in("add do CS2103");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
		in("add do CS2105");
		assertEquals("do CS2105 is included", true, st.hasTask("do CS2105"));
		in("add do CS2106");
		assertEquals("do CS2106 is included", true, st.hasTask("do CS2106"));
		in("undo");
		assertEquals("do CS2106 is not included", false,
				st.hasTask("do CS2106"));
		in("undo");
		assertEquals("do CS2105 is not included", false,
				st.hasTask("do CS2105"));
		in("undo");
		assertEquals("do CS2103 is not included", false,
				st.hasTask("do CS2103"));
	}

	@Test
	public void undoRemoveTest() throws Exception {
		in("add do CS2103");
		in("delete 1");
		assertEquals("do CS2103 is not included", false,
				st.hasTask("do CS2103"));
		in("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
	}

	@Test
	public void undoRenameTest() throws Exception {
		in("add do CS2103");
		in("name 1 do CS2107");
		in("name 1 do CS2104");
		assertEquals("do CS2104 is included", true, st.hasTask("do CS2104"));
		in("undo");
		assertEquals("do CS2107 is included", true, st.hasTask("do CS2107"));
		in("undo");
		assertEquals("do CS2103 is included", true, st.hasTask("do CS2103"));
	}

	@Test
	public void undoClearTest() throws Exception {
		in("add do CS2103");
		in("add do CS2104");
		in("add do CS2105");
		in("add do CS2106");
		in("add do CS2107");
		in("clear");
		assertEquals("no tasks added", 0, st.stobj.getNumberOfTasks());
		in("undo");
		assertEquals("5 tasks added", 5, st.stobj.getNumberOfTasks());
	}

	@Test
	public void undoModifyTest() throws Exception {
		String taskNameForTest = "a task";
		String newTaskName = "another task";
		in("add " + taskNameForTest);
		in("modify 1 name " + newTaskName
				+ " tag fordemo v0.2 desc multiple inputs");
		assertEquals("new name is \"" + newTaskName + "\"", false,
				st.hasTask(taskNameForTest));
		assertEquals("has description", "multiple inputs",
				st.stobj.getTask(newTaskName).getDescription());
		assertFalse("has tags", st.stobj.getTask(newTaskName).getTags()
				.isEmpty());
		in("undo");
		assertEquals("old name is \"" + taskNameForTest + "\"", true,
				st.hasTask(taskNameForTest));
		assertNull("no description", st.stobj.getTask(taskNameForTest)
				.getDescription());
		assertTrue("no tags", st.stobj.getTask(taskNameForTest).getTags()
				.isEmpty());
		in("undo");
		assertFalse("no task added", st.hasTask(taskNameForTest));
	}

	@Test
	public void nullModificationTest() throws Exception {
		String taskNameForTest = "a task";
		in("add " + taskNameForTest);
		in("modify 1 due 11/11 desc multiple inputs");
		assertEquals("has date 11/11", "11/11/2014",
				StreamUtil.getCalendarWriteUp(st.stobj.getTask(taskNameForTest)
						.getDeadline()));
		assertEquals("has description", "multiple inputs",
				st.stobj.getTask(taskNameForTest).getDescription());
		in("modify 1 due null desc null");
		assertNull("has date 11/11", st.stobj.getTask(taskNameForTest)
				.getDeadline());
		assertNull("has description", st.stobj.getTask(taskNameForTest)
				.getDescription());
	}

	@Test
	public void undoSearchTest() throws Exception {
		// undoing search is done by invoking clrsrc - short form of clear
		// search
		in("add a task");
		in("add some task");
		in("add another task");
		in("add new task");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
		in("search new");
		assertEquals("1 task viewable", 1, st.stui.getNumberOfTasksStored());
		in("clrsrc");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
	}

	@Test
	public void undoFilterTest() throws Exception {
		in("add a task");
		in("add some task");
		in("add another task");
		in("add new task");
		in("mark 1 done");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
		in("filter done");
		assertEquals("1 task viewable", 1, st.stui.getNumberOfTasksStored());
		in("filter ongoing");
		assertEquals("3 tasks viewable", 3, st.stui.getNumberOfTasksStored());
		in("clrsrc");
		assertEquals("4 tasks viewable", 4, st.stui.getNumberOfTasksStored());
	}

	@Test
	public void undoMarkTest() throws Exception {
		in("add a task");
		assertFalse("Task 1 is not done", st.stobj.getTask("a task").isDone());
		in("mark 1 done");
		assertTrue("Task 1 is done", st.stobj.getTask("a task").isDone());
		in("undo");
		assertFalse("Task 1 is not done", st.stobj.getTask("a task").isDone());
	}

	@Test
	public void undoSortTest() throws Exception {
		in("add Task D");
		in("add Task A");
		in("add Task C");
		in("add Task B");
		in("due 1 4/4/2015");
		in("due 2 3/3/2015");
		in("due 3 2/2/2015");
		in("due 4 1/1/2015");
		String[] alphaSorted = { "Task A", "Task B", "Task C", "Task D" };
		String[] chronoSorted = { "Task B", "Task C", "Task A", "Task D" };
		String[] unsorted = { "Task D", "Task A", "Task C", "Task B" };
		assertTrue("Unsorted", compare(st.stobj.getTaskList(), unsorted));
		in("sort a ascending");
		assertTrue("Sorted alphabetically",
				compare(st.stobj.getTaskList(), alphaSorted));
		in("sort t descending");
		assertTrue("Sorted chronologically",
				compare(st.stobj.getTaskList(), chronoSorted));
		in("undo");
		in("undo");
		assertTrue("Now unsorted again",
				compare(st.stobj.getTaskList(), unsorted));
	}

}