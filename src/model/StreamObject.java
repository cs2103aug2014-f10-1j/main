package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import util.StreamConstants;
import util.StreamUtil;
import exception.StreamModificationException;

/**
 * Some documentation.
 * 
 * @version V0.4
 * @author John Kevin Tjahjadi
 */
public class StreamObject {

	private static final String ERROR_TASK_ALREADY_EXISTS = "\"%1$s\" already exists in the tasks list.";
	private static final String ERROR_TASK_DOES_NOT_EXIST = "The task \"%1$s\" does not exist.";
	private static final String ERROR_NEW_TASK_NAME_NOT_AVAILABLE = "The name \"%1$s\" is not available.";

	protected HashMap<String, StreamTask> taskMap;
	protected ArrayList<String> taskList;

	public StreamObject() {
		this.taskMap = new HashMap<String, StreamTask>();
		this.taskList = new ArrayList<String>();
	}

	public StreamObject(HashMap<String, StreamTask> taskMap,
			ArrayList<String> taskList) {
		this.taskMap = taskMap;
		this.taskList = taskList;
	}

	public ArrayList<String> getTaskNames() {
		return taskList;
	}

	// @author A0093874N

	public ArrayList<Integer> getCounter() {
		ArrayList<Integer> counter = new ArrayList<Integer>();
		for (int i = 0; i < taskList.size(); i++) {
			counter.add(i + 1);
		}
		return counter;
	}

	// @author A0093874N

	public ArrayList<String> getOrdering() {
		ArrayList<String> order = new ArrayList<String>();
		for (String task : taskList) {
			order.add(task);
		}
		return order;
	}

	// @author A0093874N

	public void setOrdering(ArrayList<String> anotherTaskList) {
		assert (StreamUtil.listEqual(taskList, anotherTaskList)) : StreamConstants.Assertion.NOT_EQUAL;
		taskList = anotherTaskList;
	}

	// @author A0096529N

	public void setOrderingWithTasks(List<StreamTask> anotherTaskList) {

		ArrayList<String> orderList = new ArrayList<String>();
		for (StreamTask task : anotherTaskList) {
			orderList.add(task.getTaskName());
		}
		setOrdering(orderList);
	}

	// @author A0096529N

	public void sortAlpha(boolean descending) {
		if (descending) {
			sort(new Comparator<StreamTask>() {
				@Override
				public int compare(StreamTask o1, StreamTask o2) {
					return o2.getTaskName().compareTo(o1.getTaskName());
				}
			});
		} else {
			sort(new Comparator<StreamTask>() {
				@Override
				public int compare(StreamTask o1, StreamTask o2) {
					return o1.getTaskName().compareTo(o2.getTaskName());
				}
			});
		}
	}

	// @author A0096529N

	public void sortStartTime(boolean descending) {
		if (descending) {
			sort(new Comparator<StreamTask>() {
				@Override
				public int compare(StreamTask o1, StreamTask o2) {
					if (o1.getStartTime() == null && o2.getStartTime() == null) {
						return 0;
					} else if (o1.getStartTime() == null) {
						return -1;
					} else if (o2.getStartTime() == null) {
						return 1;
					} else {
						return o1.getStartTime().compareTo(o2.getStartTime());
					}
				}
			});
		} else {
			sort(new Comparator<StreamTask>() {
				@Override
				public int compare(StreamTask o1, StreamTask o2) {
					if (o1.getStartTime() == null && o2.getStartTime() == null) {
						return 0;
					} else if (o1.getStartTime() == null) {
						return 1;
					} else if (o2.getStartTime() == null) {
						return -1;
					} else {
						return o2.getStartTime().compareTo(o1.getStartTime());
					}
				}
			});
		}
	}

	// @author A0096529N

	public void sortDeadline(boolean descending) {
		if (descending) {
			sort(new Comparator<StreamTask>() {
				@Override
				public int compare(StreamTask o1, StreamTask o2) {
					if (o1.getDeadline() == null && o2.getDeadline() == null) {
						return 0;
					} else if (o1.getDeadline() == null) {
						return 1;
					} else if (o2.getDeadline() == null) {
						return -1;
					} else {
						return o1.getDeadline().compareTo(o2.getDeadline());
					}
				}
			});
		} else {
			sort(new Comparator<StreamTask>() {
				@Override
				public int compare(StreamTask o1, StreamTask o2) {
					if (o1.getDeadline() == null && o2.getDeadline() == null) {
						return 0;
					} else if (o1.getDeadline() == null) {
						return 1;
					} else if (o2.getDeadline() == null) {
						return -1;
					} else {
						return o2.getDeadline().compareTo(o1.getDeadline());
					}
				}
			});
		}
	}

	private void sort(Comparator<StreamTask> comparator) {
		List<StreamTask> tempList = getStreamTaskList();
		Collections.sort(tempList, comparator);
		setOrderingWithTasks(tempList);
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
		if (hasTask(newTaskName)) {
			throw new StreamModificationException(String.format(
					ERROR_TASK_ALREADY_EXISTS, newTaskName));
		} else {
			this.taskMap.put(newTaskName.toLowerCase(), new StreamTask(
					newTaskName));
			this.taskList.add(newTaskName);
		}
	}

	// @author A0093874N

	public void recoverTask(StreamTask task) {
		this.taskMap.put(task.getTaskName().toLowerCase(), task);
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
		return this.taskMap.containsKey(taskName.toLowerCase());
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
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found
	 */
	public StreamTask getTask(String taskName)
			throws StreamModificationException {
		if (hasTask(taskName.toLowerCase())) {
			return taskMap.get(taskName.toLowerCase());
		} else {
			throw new StreamModificationException(String.format(
					ERROR_TASK_DOES_NOT_EXIST, taskName));
		}
	}

	// @author A0118007R
	/**
	 * get task description
	 * 
	 * @throws StreamModificationException
	 * @deprecated
	 */

	public String getTaskDescription(String taskName)
			throws StreamModificationException {
		StreamTask myTask = getTask(taskName);
		return myTask.getDescription();
	}

	/**
	 * get task deadline
	 * 
	 * @deprecated
	 */

	public Calendar getTaskDeadline(String taskName)
			throws StreamModificationException {
		StreamTask myTask = getTask(taskName);
		return myTask.getDeadline();
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
			taskMap.remove(taskName.toLowerCase());
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
	 * @deprecated
	 */
	public void changeDeadline(String taskName, Calendar deadline)
			throws StreamModificationException {
		StreamTask task = taskMap.get(taskName.toLowerCase());
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
	 * @deprecated
	 */
	public Boolean removeTag(String taskName, String tag)
			throws StreamModificationException {
		StreamTask task = getTask(taskName);
		if (task.hasTag(tag)) {
			task.deleteTag(tag);
			// Slight improvement by A0093874N
			return true;
		} else {
			return false;
		}
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
	 * @deprecated
	 */
	public Boolean addTag(String taskName, String tag)
			throws StreamModificationException {
		StreamTask task = getTask(taskName);
		if (!task.hasTag(tag)) {
			task.addTag(tag);
			// Slight improvement by A0093874N
			return true;
		} else {
			return false;
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
		if (!taskName.equals(newTaskName)) {
			if (taskMap.containsKey(newTaskName.toLowerCase())) {
				throw new StreamModificationException(String.format(
						ERROR_NEW_TASK_NAME_NOT_AVAILABLE, newTaskName));
			}
		}

		taskMap.remove(task.getTaskName().toLowerCase());
		task.setTaskName(newTaskName);
		taskMap.put(newTaskName.toLowerCase(), task);

		int index = taskList.indexOf(taskName);
		taskList.add(index, newTaskName);
		taskList.remove(index + 1);
	}

	// @author A0119401U
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
	 * @deprecated
	 */
	public void markTaskAsDone(String taskName)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		taskMap.remove(taskName.toLowerCase());
		task.markAsDone();
		taskMap.put(taskName.toLowerCase(), task);
	}

	/**
	 * @deprecated
	 * 
	 */
	public void markTaskAsOngoing(String taskName)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		taskMap.remove(taskName.toLowerCase());
		task.markAsOngoing();
		taskMap.put(taskName.toLowerCase(), task);
	}

	// @author A0119401U
	/**
	 * 
	 * Set the due time of the selected task
	 * 
	 * @param taskName
	 *            , calendar
	 * 
	 * @throws StreamModificationException
	 * 
	 *             <p>
	 *             Precondition: taskName != null
	 *             </p>
	 * 
	 *             The case "calendar is null" will be dealt with by Task.java
	 * @deprecated
	 */
	public void setDueTime(String taskName, Calendar calendar)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		taskMap.remove(taskName.toLowerCase());
		task.setDeadline(calendar);
		taskMap.put(taskName.toLowerCase(), task);
	}

	/**
	 * 
	 * @deprecated
	 */
	public void setNullDeadline(String taskName)
			throws StreamModificationException {
		StreamTask currentTask = this.getTask(taskName.toLowerCase());
		currentTask.setNullDeadline();
	}

	// @author A0119401U
	/**
	 * Set the rank of the selected task
	 * <p>
	 * Key info is the newly entered rank of the selected task
	 * </p>
	 * 
	 * <p>
	 * Preconditions: new rank is one of the followings: high, medium, low
	 * </p>
	 * 
	 * @param taskName
	 *            , newRank
	 * 
	 * @throws StreamModificationException
	 * @deprecated
	 */
	public void setNewRank(String taskName, String newRank)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		taskMap.remove(taskName.toLowerCase());
		task.setRank(newRank);
		taskMap.put(taskName.toLowerCase(), task);

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
	// modified by A0093874N
	public ArrayList<Integer> findTasks(String keyphrase) {
		// Split key phrase into keywords
		String[] keywords = null;
		if (keyphrase.contains(" ")) {
			keywords = keyphrase.split(" ");
		} else {
			keywords = new String[] { keyphrase };
		}

		ArrayList<Integer> tasks = new ArrayList<Integer>();
		for (int i = 0; i < taskList.size(); i++) {
			StreamTask task = taskMap.get(taskList.get(i).toLowerCase());

			// check for matches between keywords and tags
			if (task.hasTag(keywords)) {
				tasks.add(i + 1);
				continue;
			}
			// improved by A0093874N: case-insensitive search
			// check if task description contains key phrase
			if (task.getDescription() != null
					&& task.getDescription().toLowerCase()
							.contains(keyphrase.toLowerCase())) {
				tasks.add(i + 1);
				continue;
			}
			// check if task name contains key phrase
			if (task.getTaskName().toLowerCase()
					.contains(keyphrase.toLowerCase())) {
				tasks.add(i + 1);
				continue;
			}
		}

		return tasks;
	}

	// @author A0093874N

	public ArrayList<Integer> filterTasks(String criteria) {
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		String[] type = criteria.split(" ");
		Calendar dueDate;
		for (int i = 1; i <= taskList.size(); i++) {
			StreamTask task = taskMap.get(taskList.get(i - 1).toLowerCase());
			switch (type[0]) {
				case "done":
					if (task.isDone()) {
						tasks.add(i);
					}
					break;
				case "ongoing":
					if (!task.isDone()) {
						tasks.add(i);
					}
					break;
				case "notime":
					if (task.isFloatingTask()) {
						tasks.add(i);
					}
					break;
				case "before":
					dueDate = StreamUtil.parseCalendar(type[1]);
					if (task.getDeadline() != null
							&& task.getDeadline().before(dueDate)) {
						tasks.add(i);
					}
					break;
				case "after":
					dueDate = StreamUtil.parseCalendar(type[1]);
					if (task.getDeadline() != null
							&& task.getDeadline().after(dueDate)) {
						tasks.add(i);
					}
					break;
				default:
					// shouldn't happen as input is filtered in parser
					break;
			}
		}
		return tasks;
	}

	// @author A0093874N
	/**
	 * Gets the number of tasks added.
	 * 
	 * @author Wilson Kurniawan
	 */
	public int getNumberOfTasks() {
		return taskList.size();
	}

	// @author A0096529N
	/**
	 * @return taskMap a copy of the task map.
	 */
	public HashMap<String, StreamTask> getTaskMap() {
		return new HashMap<String, StreamTask>(this.taskMap);
	}

	// @author A0096529N
	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<String> getTaskList() {
		return new ArrayList<String>(this.taskList);
	}

	// @author A0096529N
	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<StreamTask> getStreamTaskList() {
		ArrayList<StreamTask> taskList = new ArrayList<StreamTask>();
		for (String key : taskMap.keySet()) {
			taskList.add(taskMap.get(key));
		}
		return taskList;
	}

	// @author A0093874N

	public ArrayList<StreamTask> getStreamTaskList(ArrayList<Integer> indices) {
		ArrayList<StreamTask> tasks = new ArrayList<StreamTask>();
		for (Integer index : indices) {
			tasks.add(this.taskMap.get(this.taskList.get(index - 1)
					.toLowerCase()));
		}
		return tasks;
	}

	// @author A0096529N
	public ArrayList<String> getTimedTaskList() {
		ArrayList<String> timedTaskList = new ArrayList<String>();
		for (String taskName : this.taskMap.keySet()) {
			if (this.taskMap.get(taskName).isTimedTask()) {
				timedTaskList.add(taskName);
			}
		}
		return timedTaskList;
	}

	// @author A0096529N
	public ArrayList<String> getDeadlineTaskList() {
		ArrayList<String> timedTaskList = new ArrayList<String>();
		for (String taskName : this.taskMap.keySet()) {
			if (this.taskMap.get(taskName).isDeadlineTask()) {
				timedTaskList.add(taskName);
			}
		}
		return timedTaskList;
	}

	// @author A0096529N
	public ArrayList<String> getFloatingTaskList() {
		ArrayList<String> timedTaskList = new ArrayList<String>();
		for (String taskName : this.taskMap.keySet()) {
			if (this.taskMap.get(taskName).isFloatingTask()) {
				timedTaskList.add(taskName);
			}
		}
		return timedTaskList;
	}
}