package logic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.StreamObject;
import model.StreamTask;

import org.junit.Before;
import org.junit.Test;

//@author A0096529N
public class StreamSearchTest {

	private StreamLogic streamLogic = StreamLogic.init(new StreamObject());
	private StreamTask task1, task2;
	private static final Comparator<StreamTask> taskComparator = new Comparator<StreamTask>() {
		@Override public int compare(StreamTask o1, StreamTask o2) {
			return o1.getTaskName().compareTo(o2.getTaskName());
		}
	};; 

	@Before 
	public void setUp() throws Exception {
		streamLogic.addTask("Find X");
		task1 = streamLogic.getTask("Find X");
		task1.setDescription("If a = b and b = c, find x.");
		task1.getTags().add("X");
		task1.getTags().add("FIND");
		task1.getTags().add("MATH");
		task1.getTags().add("SIMPLE");
		streamLogic.addTask("Find Pandora's Box");
		task2 = streamLogic.getTask("Find Pandora's Box");
		task2.setDescription("Try and search around the bamboo forest...");
		task2.getTags().add("IMPOSSIBLE");
		task2.getTags().add("PANDA");
		task2.getTags().add("NOLINE");
	}

	@Test 
	public void testSearch1() {
		testOneSearch("Search for nothing", streamLogic.getStreamTaskList(streamLogic.findTasks("nothing")));
	}
	@Test 
	public void testSearch2() {
		testOneSearch("Search for x", streamLogic.getStreamTaskList(streamLogic.findTasks("x")), task1, task2);
	}
	@Test 
	public void testSearch3() {
		testOneSearch("Search for panda", streamLogic.getStreamTaskList(streamLogic.findTasks("im looking for a panda")), task2);
	}

	private void testOneSearch(String testMessage, List<StreamTask> actualTasks, StreamTask...tasks) {
		List<StreamTask> expectedTasks = Arrays.asList(tasks);
		Collections.sort(expectedTasks, taskComparator);
		Collections.sort(actualTasks, taskComparator);
		assertEquals(testMessage, expectedTasks, actualTasks);
	}
}