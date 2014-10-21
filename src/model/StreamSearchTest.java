package model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

//@author A0096529N
public class StreamSearchTest {

	private StreamObject streamObject;
	private StreamTask task1, task2;
	private static final Comparator<StreamTask> taskComparator = new Comparator<StreamTask>() {
		@Override public int compare(StreamTask o1, StreamTask o2) {
			return o1.getTaskName().compareTo(o2.getTaskName());
		}
	};; 

	@Before 
	public void setUp() throws Exception {
		streamObject = new StreamObject();
		streamObject.addTask("Find X");
		task1 = streamObject.getTask("Find X");
		task1.setDescription("If a = b and b = c, find x.");
		task1.addTag("x");
		task1.addTag("find");
		task1.addTag("math");
		task1.addTag("simple");
		streamObject.addTask("Find Pandora's Box");
		task2 = streamObject.getTask("Find Pandora's Box");
		task2.setDescription("Try and search around the bamboo forest...");
		task2.addTag("impossible");
		task2.addTag("panda");
		task2.addTag("nolink");
	}

	@Test 
	public void testSearch1() {
		testOneSearch("Search for nothing", streamObject.getStreamTaskList(streamObject.findTasks("nothing")));
	}
	@Test 
	public void testSearch2() {
		testOneSearch("Search for x", streamObject.getStreamTaskList(streamObject.findTasks("x")), task1, task2);
	}
	@Test 
	public void testSearch3() {
		testOneSearch("Search for panda", streamObject.getStreamTaskList(streamObject.findTasks("im looking for a panda")), task2);
	}

	private void testOneSearch(String testMessage, List<StreamTask> actualTasks, StreamTask...tasks) {
		List<StreamTask> expectedTasks = Arrays.asList(tasks);
		Collections.sort(expectedTasks, taskComparator);
		Collections.sort(actualTasks, taskComparator);
		assertEquals(testMessage, expectedTasks, actualTasks);
	}
}
