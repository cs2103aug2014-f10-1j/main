package stream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fileio.StreamIO;
import fileio.StreamIOException;

public class StreamObject {

	private static final String ERROR_TASK_DOES_NOT_EXIST = "Error: The task \"%1$s\" does not exist.";
	private static final String ERROR_NEW_TASK_NAME_NOT_AVAILABLE = "Error: The name \"%1$s\" is not available.";

	private List<Task> allTasks;

	public StreamObject() {
		this.allTasks = new ArrayList<Task>();
	}

	// @author A0118007R
	/**
	 * Adds a new task to StreamObject
	 * 
	 * <p>
	 * Precondition: newTaskName != null
	 * </p>
	 * 
	 * @param newTaskName
	 *            name of the new task
	 * @throws ModificationException
	 *             if task with newTaskName is already present.
	 * 
	 */
	public void addTask(String newTaskName) throws ModificationException {
		if (getTask1(newTaskName) == null) {
			allTasks.add(new Task(newTaskName));
		} else {
			throw new ModificationException(String.format(
					ERROR_NEW_TASK_NAME_NOT_AVAILABLE, newTaskName));
		}
	}

	// @author A0093874N
	/**
	 * TODO
	 * @param task
	 */
	public void recoverTask(Task task) {
		this.allTasks.add(task);
	}

	// @author A0118007R
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
		return getTask1(taskName) != null;
	}

	// @author A0118007R
	/**
	 * Gets a specific task
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to be returned
	 * @return task with matching taskName
	 * @throws ModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public Task getTask(String taskName) throws ModificationException {
		Task result = getTask1(taskName);
		if (result == null) {
			throw new ModificationException(String.format(
					ERROR_TASK_DOES_NOT_EXIST, taskName));
		} else {
			return result;
		}
	}

	/**
	 * 
	 * @return list of task names
	 */
	public List<String> getTaskNames() {
		List<String> taskList = new ArrayList<String>();
		for (Task task:allTasks) {
			taskList.add(task.getTaskName());
		}
		return taskList;
	}
	
	// @author A0096529N
	/**
	 * Private helper method used to get a task, 
	 * returns null if not found
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to be returned
	 * @return task with matching taskName, or null if not found.
	 */
	private Task getTask1(String taskName) {
		for (Task task:allTasks) {
			if (task.getTaskName().equals(taskName)) {
				return task;
			}
		}
		return null;
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
	 */
	public List<Task> findTasks(String keyphrase) {
		// Split key phrase into keywords
		String[] keywords = null;
		if (keyphrase.contains(" ")) {
			keywords = keyphrase.split(" ");
		} else {
			keywords = new String[] { keyphrase };
		}

		List<Task> tasks = new ArrayList<Task>();
		for (Task task : allTasks) {

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

	// @author A0096529N
	/**
	 * Gets the index of a task
	 * 
	 * <p>
	 * Precondition: taskName != null
	 * </p>
	 * 
	 * @param taskName
	 *            name of task to be located
	 * @return index of task with matching taskName
	 * @throws ModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public int indexOf(String taskName) throws ModificationException {
		Task task = getTask(taskName);
		return allTasks.indexOf(task);
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
	 * @throws ModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void changeDeadline(String taskName, Calendar deadline)
			throws ModificationException {
		Task task = getTask(taskName);
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
	 * @throws ModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void removeTag(String taskName, String tag)
			throws ModificationException {
		Task task = getTask(taskName);
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
	 * @throws ModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void addTag(String taskName, String tag)
			throws ModificationException {
		Task task = getTask(taskName);
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
	 * @throws ModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found. Or when task with newTaskName is already present.
	 */
	public void updateTaskName(String taskName, String newTaskName)
			throws ModificationException {
		Task task = getTask(taskName);
		if (getTask1(newTaskName) != null) {
			throw new ModificationException(String.format(
					ERROR_NEW_TASK_NAME_NOT_AVAILABLE, newTaskName));
		} else {
			task.setTaskName(newTaskName);
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
	 * @throws ModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public void deleteTask(String taskName) throws ModificationException {
		Task task = getTask1(taskName);
		allTasks.remove(task);
	}

	// @author A0096529N
	/**
	 * Saves task list into storage file
	 * 
	 * @throws StreamIOException when JSON conversion fail due
	 * file corruption or IO failures when loading/accessing
	 * storage file.
	 */
	public void save() throws StreamIOException {
		StreamIO.save(allTasks);
	}

	// @author A0096529N
	/**
	 * Loads task list from storage file
	 * 
	 */
	public void load() {
		try {
			allTasks = StreamIO.load();
		} catch (StreamIOException e) {
			// No previous state - first use.
			allTasks = new ArrayList<Task>();
		}

	}

}