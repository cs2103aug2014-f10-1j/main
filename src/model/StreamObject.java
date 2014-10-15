package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import exception.StreamIOException;
import exception.StreamModificationException;
import fileio.StreamIO;

public class StreamObject {

	private static final String ERROR_TASK_DOES_NOT_EXIST = "Error: The task \"%1$s\" does not exist.";
	private static final String ERROR_NEW_TASK_NAME_NOT_AVAILABLE = "Error: The name \"%1$s\" is not available.";

	private HashMap<String, StreamTask> allTasks;
	private ArrayList<String> taskList;

	public StreamObject() {
		this.allTasks = new HashMap<String, StreamTask>();
		this.taskList = new ArrayList<String>();
	}

	public ArrayList<String> getTaskNames() {
		return taskList;
	}

	/**
	 * Adds a new task to StreamObject
	 * 
	 * <p>
	 * Precondition: newTaskName != null
	 * </p>
	 * 
	 * @param newTaskName
	 *            name of the new task
	 */
	public void addTask(String newTaskName) throws StreamModificationException {
		this.allTasks.put(newTaskName.toLowerCase(), new StreamTask(newTaskName));
		this.taskList.add(newTaskName);
	}

	public void recoverTask(StreamTask task) {
		this.allTasks.put(task.getTaskName(), task);
		this.taskList.add(task.getTaskName());
	}

	/**
	 * Checks if a specific task is present
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to check for
	 * @return true if task with the given taskName is found.
	 */
	public Boolean hasTask(String taskName) {
		return this.allTasks.containsKey(taskName.toLowerCase());
	}

	// @author A0118007R

	/**
	 * Save
	 */

	public void save() {
		try {
			StreamIO.save(allTasks);
		} catch (StreamIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * load
	 */

	public void load() {
		this.allTasks = new HashMap<String, StreamTask>();
		this.taskList = new ArrayList<String>();

		try {
			allTasks = StreamIO.load();
		} catch (StreamIOException e) {
			// No previous state - first use.
		}

		for (String key : allTasks.keySet()) {
			taskList.add(allTasks.get(key).getTaskName());
		}
		
	}

	/**
	 * Gets a specific task
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to be returned
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public StreamTask getTask(String taskName) throws StreamModificationException {
		if (hasTask(taskName.toLowerCase())) {
			return allTasks.get(taskName.toLowerCase());
		} else {
			throw new StreamModificationException(String.format(
					ERROR_TASK_DOES_NOT_EXIST, taskName));
		}
	}

	// @author A0118007R
	/**
	 * Deletes a specific task
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to be deleted
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void deleteTask(String taskName) throws StreamModificationException {
		if (hasTask(taskName)) {
			allTasks.remove(taskName.toLowerCase());
			taskList.remove(taskName);
		} else {
			throw new StreamModificationException(String.format(
					ERROR_TASK_DOES_NOT_EXIST, taskName));
		}
	}

	// @author A0096529N
	/**
	 * Modify a task's deadline
	 * 
	 * <p>
	 * Precondition: taskName, deadline != null
	 * </p>
	 * 
	 * @param taskName
	 *            to be modified
	 * @param deadline
	 *            to be set in the task
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void changeDeadline(String taskName, Calendar deadline)
			throws StreamModificationException {
		StreamTask task = allTasks.get(taskName.toLowerCase());
		if (task == null) {
			throw new StreamModificationException(String.format(
					ERROR_TASK_DOES_NOT_EXIST, taskName));
		}
		task.setDeadline(deadline);
	}

	// @author A0096529N
	/**
	 * Remove the given tag from a specified task
	 * 
	 * <p>
	 * Precondition: taskName, tag != null
	 * </p>
	 * 
	 * @param taskName
	 *            to be modified
	 * @param tag
	 *            to be removed from the task
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void removeTag(String taskName, String tag)
			throws StreamModificationException {
		StreamTask task = getTask(taskName);
		task.deleteTag(tag);
	}

	// @author A0096529N
	/**
	 * Add the given tag from a specified task. Does nothing if tag already
	 * present.
	 * 
	 * <p>
	 * Precondition: taskName, tag != null
	 * </p>
	 * 
	 * @param taskName
	 *            to be modified
	 * @param tag
	 *            to be added to the task
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void addTag(String taskName, String tag)
			throws StreamModificationException {
		StreamTask task = getTask(taskName);
		if (!task.hasTag(tag)) {
			task.addTag(tag);
			// TODO update UI job done
		}
	}

	// @author A0096529N
	/**
	 * Change task name of the task
	 * 
	 * <p>
	 * Precondition: taskName, newName != null
	 * </p>
	 * 
	 * @param taskName
	 *            to be modified
	 * @param newTaskName
	 *            name to be set to the task
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found. Or when task with newTaskName is already present.
	 */
	public void updateTaskName(String taskName, String newTaskName)
			throws StreamModificationException {
		StreamTask task = getTask(taskName.toLowerCase());
		if (allTasks.containsKey(newTaskName.toLowerCase())) {
			throw new StreamModificationException(String.format(
					ERROR_NEW_TASK_NAME_NOT_AVAILABLE, newTaskName));
		}

		allTasks.remove(task.getTaskName().toLowerCase());
		task.setTaskName(newTaskName);
		allTasks.put(newTaskName.toLowerCase(), task);

		int index = taskList.indexOf(taskName);
		taskList.add(index, newTaskName);
		taskList.remove(index + 1);
	}
	
	//@author A0119401U
	/**
	 * 
	 * Mark the selected task as done
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 * 
	 * @throws StreamModificationException
	 */
	public void markTaskAsDone(String taskName)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		allTasks.remove(taskName.toLowerCase());
		task.markAsDone();
		allTasks.put(taskName.toLowerCase(), task);
	}
	
	public void markTaskAsOngoing(String taskName)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		allTasks.remove(taskName.toLowerCase());
		task.markAsOngoing();
		allTasks.put(taskName.toLowerCase(), task);
	}
	
	//@author A0119401U
	/**
	 * 
	 * Set the due time of the selected task
	 * 
	 * @param taskName, calendar
	 * 
	 * @throws StreamModificationException
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * The case "calendar is null" will be dealt with by Task.java
	 */
	public void setDueTime(String taskName, Calendar calendar)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		allTasks.remove(taskName.toLowerCase());
		task.setDeadline(calendar);
		allTasks.put(taskName.toLowerCase(), task);
	}
	
	public void setNullDeadline(String taskName)
			throws StreamModificationException {
		StreamTask currentTask = this.getTask(taskName.toLowerCase());
		currentTask.setNullDeadline();
	}
	
	

	// @author A0096529N
	
	/**
	 * Search for tasks with specified key phrase, in the task name, description
	 * and tags.
	 * <p>
	 * Key phrase will be broken down into key words (by splitting with space
	 * character). Key words will be used to search in the tags.
	 * </p>
	 * 
	 * <p>
	 * Precondition: keyphrase != null
	 * </p>
	 * 
	 * @return tasks - a list of tasks containing the key phrase, empty list if
	 *         nothing matches
	 * @author Steven Khong
	 */
	public List<StreamTask> findTasks(String keyphrase) {
		// Split key phrase into keywords
		String[] keywords = null;
		if (keyphrase.contains(" ")) {
			keywords = keyphrase.split(" ");
		} else {
			keywords = new String[] { keyphrase };
		}

		List<StreamTask> tasks = new ArrayList<StreamTask>();
		for (String key : allTasks.keySet()) {
			StreamTask task = allTasks.get(key);

			// check for matches between keywords and tags
			if (task.hasTag(keywords)) {
				tasks.add(task);
				continue;
			}
			// check if task description contains key phrase
			if (task.getDescription().contains(keyphrase)) {
				tasks.add(task);
				continue;
			}
			// check if task name contains key phrase
			if (task.getTaskName().contains(keyphrase)) {
				tasks.add(task);
				continue;
			}
		}

		return tasks;
	}
}