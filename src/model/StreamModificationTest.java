package model;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import exception.StreamModificationException;

//@author A0096529N
//TODO add tests for negative cases, where exceptions should be thrown where relevant.
public class StreamModificationTest {

	private StreamObject streamObject;
	private StreamTask task1, task2, task3;
	private Calendar taskDeadline;
	private String TASK_NAME_1 = "Find X";
	private String TASK_NAME_2 = "Find Pandora's Box";
	private String TASK_NAME_3 = "Code unit tests";
	
	@Before 
	public void setUp() throws Exception {
		taskDeadline = Calendar.getInstance();
		taskDeadline.set(2014, 9, 17, 18, 48, 45);
		
		Calendar task1Deadline = Calendar.getInstance();
		task1Deadline.setTime(taskDeadline.getTime());
		streamObject = new StreamObject();
		task1 = new StreamTask(TASK_NAME_1);
		task1.setDescription("If a = b and b = c, find x.");
		task1.setDeadline(task1Deadline);
		task1.addTag("x");
		task1.addTag("find");
		task1.addTag("math");
		task1.addTag("simple");
		streamObject.allTasks.put(task1.getTaskName().toLowerCase(), task1);
		streamObject.taskList.add(task1.getTaskName());

		Calendar task2Deadline = Calendar.getInstance();
		task2Deadline.setTime(taskDeadline.getTime());
		task2 = new StreamTask(TASK_NAME_2);
		task2.setDescription("Try and search around the bamboo forest...");
		task2.addTag("impossible");
		task2.setDeadline(task2Deadline);
		task2.addTag("panda");
		task2.addTag("nolink");
		streamObject.allTasks.put(task2.getTaskName().toLowerCase(), task2);
		streamObject.taskList.add(task2.getTaskName());
		
		task3 = new StreamTask(TASK_NAME_3);
		task3.setDescription("Code the unit tests for StreamObject");
		task3.addTag("boringtask");
		task3.addTag("procrastinate");
		streamObject.allTasks.put(task3.getTaskName().toLowerCase(), task3);
		streamObject.taskList.add(task3.getTaskName());
	}

	@Test 
	public void testChangeDeadline() throws StreamModificationException {
		assertEquals("Deadline before modification", 
				toDateString(taskDeadline), toDateString(task1.getDeadline()));
		
		Calendar task1Deadline = Calendar.getInstance();
		task1Deadline.set(2014, 9, 17, 18, 48, 45);
		streamObject.changeDeadline(TASK_NAME_1, task1Deadline);

		assertEquals("Deadline after modification", 
				toDateString(task1Deadline), toDateString(task1.getDeadline()));
	}

	@Test 
	public void testRemoveTag() throws StreamModificationException {
		assertEquals("Tags before modification", true, task3.hasTag("procrastinate"));

		streamObject.removeTag(task3.getTaskName(), "procrastinate");

		assertEquals("Tags after modification", false, task3.hasTag("procrastinate"));
	}

	@Test 
	public void testAddTag() throws StreamModificationException {
		assertEquals("Tags before modification", false, task3.hasTag("tagtobeadded"));

		streamObject.addTag(task3.getTaskName(), "tagtobeadded");

		assertEquals("Tags after modification", true, task3.hasTag("tagtobeadded"));
	}

	@Test 
	public void testUpdateTaskName() throws StreamModificationException {
		String newTaskName = "New task name";
		
		assertEquals("Task name before modification", TASK_NAME_3, task3.getTaskName());

		streamObject.updateTaskName(task3.getTaskName(), newTaskName);

		assertEquals("Task name after modification", newTaskName, task3.getTaskName());
	}

	@Test 
	public void testMarkTaskAsDone() throws StreamModificationException {
		task1.markAsOngoing();
		assertEquals("Done before modification", false, task2.isDone());

		streamObject.markTaskAsDone(task2.getTaskName());

		assertEquals("Done after modification", true, task2.isDone());
	}

	@Test 
	public void testMarkTaskAsOngoing() throws StreamModificationException {
		task1.markAsDone();
		assertEquals("Done before modification", true, task1.isDone());

		streamObject.markTaskAsOngoing(task1.getTaskName());

		assertEquals("Done after modification", false, task1.isDone());
	}

	@Test 
	public void testSetDueTime() throws StreamModificationException {
		assertEquals("Deadline before modification", null, 
				task3.getDeadline());

		Calendar task3Deadline = Calendar.getInstance();
		task3Deadline.setTime(taskDeadline.getTime());
		streamObject.setDueTime(task3.getTaskName(), task3Deadline);

		assertEquals("Deadline after modification", 
				toDateString(taskDeadline), toDateString(task2.getDeadline()));
	}

	@Test 
	public void testSetNullDeadline() throws StreamModificationException {
		assertEquals("Deadline before modification", 
				toDateString(taskDeadline), toDateString(task2.getDeadline()));
		
		streamObject.setNullDeadline(task2.getTaskName());

		assertEquals("Deadline after modification", 
				null, task2.getDeadline());
	}


	private String toDateString(Calendar taskDeadline) {
		return taskDeadline.getTime().toString();
	}
}
