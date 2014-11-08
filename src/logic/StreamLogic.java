package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.mdimension.jchronic.Chronic;

import parser.StreamParser;
import parser.StreamParser.FilterType;
import parser.StreamParser.RankType;
import model.StreamObject;
import model.StreamTask;
import util.StreamConstants;
import util.StreamUtil;
import exception.StreamModificationException;

/**
 * Some documentation.
 * 
 * @version V0.4
 * @author John Kevin Tjahjadi
 */
public class StreamLogic extends BaseLogic {

	private StreamObject streamObject;
	private TaskLogic taskLogic = TaskLogic.init();

	private StreamLogic() {
	}

	public static StreamLogic init(StreamObject streamObject) {
		StreamLogic logic = new StreamLogic();
		logic.streamObject = streamObject;
		return logic;
	}

	// @author A0093874N
	public ArrayList<Integer> getIndex() {
		ArrayList<Integer> index = new ArrayList<Integer>();
		for (int i = 0; i < streamObject.size(); i++) {
			index.add(i + 1);
		}
		return index;
	}

	// @author A0093874N
	public void setOrdering(ArrayList<String> anotherTaskList) {
		assert (StreamUtil.listEqual(streamObject.getTaskList(),
				anotherTaskList)) : StreamConstants.Assertion.NOT_EQUAL;
		streamObject.setTaskList(anotherTaskList);
		logDebug(String.format(StreamConstants.LogMessage.REORDER_TASKS,
				Arrays.toString(anotherTaskList.toArray())));
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
	public String sortAlpha(boolean descending) {
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
		return "Sort by alphabetical order, "
				+ (descending ? "descending." : "ascending.");
	}

	// @author A0096529N
	public String sortStartTime(boolean descending) {
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
						return o2.getStartTime().compareTo(o1.getStartTime());
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
						return o1.getStartTime().compareTo(o2.getStartTime());
					}
				}
			});
		}
		return "Sort by start time "
				+ (descending ? "descending." : "ascending.");
	}

	// @author A0096529N
	public String sortDeadline(boolean descending) {
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
						return o2.getDeadline().compareTo(o1.getDeadline());
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
						return o1.getDeadline().compareTo(o2.getDeadline());
					}
				}
			});
		}
		return "Sort by deadline "
				+ (descending ? "descending." : "ascending.");
	}

	// @author A0096529N
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
			logDebug(String.format(
					StreamConstants.LogMessage.ADD_DUPLICATE_TASK, newTaskName));
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_ALREADY_EXISTS,
					newTaskName));
		} else {
			streamObject.put(newTaskName, new StreamTask(newTaskName));
			logDebug(String.format(StreamConstants.LogMessage.ADDED_TASK,
					newTaskName));
		}
	}

	// @author A0093874N

	public void recoverTask(StreamTask task) {
		streamObject.put(task.getTaskName(), task);
		logDebug(String.format(StreamConstants.LogMessage.RECOVERED_TASK,
				task.getTaskName()));
	}

	// @author A0096529N

	public void clear() {
		streamObject.clear();
		logDebug(StreamConstants.LogMessage.CLEARED_TASKS);
	}

	// @author A0093874N
	/**
	 * <p>
	 * Checks whether a specific task is already included in the tasks list.
	 * Only for testing.
	 * </p>
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null
	 * </p>
	 * 
	 * @author Wilson Kurniawan
	 * @param taskName
	 *            - the task name
	 * @return <strong>Boolean</strong> - true if the
	 *         <strong>StreamTask</strong> <i>taskName</i> exists, false
	 *         otherwise.
	 */
	public Boolean hasTask(String taskName) {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		return streamObject.containsKey(taskName);
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
		if (hasTask(taskName)) {
			return streamObject.get(taskName);
		} else {
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_DOES_NOT_EXIST,
					taskName));
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
			streamObject.remove(taskName);
		} else {
			throw new StreamModificationException(String.format(
					StreamConstants.ExceptionMessage.ERR_TASK_DOES_NOT_EXIST,
					taskName));
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
	public String updateTaskName(String taskName, String newTaskName)
			throws StreamModificationException {
		assert (taskName != null && newTaskName != null) : StreamConstants.Assertion.NULL_INPUT;
		StreamTask task = getTask(taskName);
		if (!taskName.equals(newTaskName)) {
			if (streamObject.containsKey(newTaskName)) {
				logDebug(String.format(
						StreamConstants.LogMessage.UPDATE_TASK_NAME_DUPLICATE,
						newTaskName));
				throw new StreamModificationException(
						String.format(
								StreamConstants.ExceptionMessage.ERR_NEW_TASK_NAME_NOT_AVAILABLE,
								newTaskName));
			}
		}
		int index = streamObject.indexOf(taskName);

		streamObject.remove(task.getTaskName());
		task.setTaskName(newTaskName);
		streamObject.put(newTaskName, task, index);
		// This section is contributed by A0093874N
		logDebug(String.format(StreamConstants.LogMessage.UPDATE_TASK_NAME,
				taskName, newTaskName));
		return String.format(StreamConstants.LogMessage.NAME, taskName,
				newTaskName);
	}

	// @author A0118007R
	/**
	 * Modifies the various specified parameters of a task.
	 * 
	 * <p>
	 * Precondition: head of the modifyParams list is a valid parameter
	 * </p>
	 * 
	 * @param taskName
	 *            to be modified
	 * @param modifyParams
	 *            various parameters that are going to be modified
	 * @throws StreamModificationException
	 *             if taskName given does not return a match, i.e. task not
	 *             found. 
	 */
	public void modifyTaskWithParams(String taskName, List<String> modifyParams)
			throws StreamModificationException {
		StreamTask task = getTask(taskName);
		String attribute = modifyParams.get(0);
		String contents = "";
		for (int i = 1; i < modifyParams.size(); i++) {
			String s = modifyParams.get(i);
			if (StreamUtil.isValidAttribute(s)) {
				// first content is guaranteed to be a valid parameter
				modifyTask(task, attribute, contents.trim());
				attribute = s;
				contents = "";

			} else {
				contents = contents + s + " ";
			}
		}
		modifyTask(task, attribute, contents);
	}

	// @author A0096529N
	/**
	 * Modify an attribute of a task
	 * 
	 * @param task
	 *            to modify
	 * @param attribute
	 *            to be modified
	 * @param contents
	 *            that contains the instruction to modify that attribute
	 * @return result of this operation
	 * @throws StreamModificationException
	 */
	public void modifyTask(StreamTask task, String attribute, String contents)
			throws StreamModificationException {
		if (attribute.equalsIgnoreCase("-name")) {
			// modify name need access to streamObject, special case
			updateTaskName(task.getTaskName(), contents);
		} else {
			taskLogic.modifyTask(task, attribute, contents);
		}
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
		for (int i = 0; i < streamObject.size(); i++) {
			StreamTask task = streamObject.get(streamObject.get(i));

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

		logDebug(String.format(StreamConstants.LogMessage.SEARCHED_TASKS,
				keyphrase, Arrays.toString(tasks.toArray())));
		return tasks;
	}

	// @author A0093874N
	/**
	 * Filter tasks by various categories
	 * 
	 * @param criteria
	 *            the filtering criteria
	 *            
	 */
	public ArrayList<Integer> filterTasks(String criteria) {
		ArrayList<Integer> tasks = new ArrayList<Integer>();
		FilterType type = StreamParser.parseFilterType(criteria);
		String[] contents;
		Calendar dueDate;
		for (int i = 1; i <= streamObject.size(); i++) {
			StreamTask task = streamObject.get(streamObject.get(i - 1));
			switch (type) {
				case DONE:
					if (task.isDone()) {
						tasks.add(i);
					}
					break;
				case NOT:
					if (!task.isDone()) {
						tasks.add(i);
					}
					break;
				case HIRANK:
					if (StreamParser.parseRanking(task.getRank()) == RankType.HI) {
						tasks.add(i);
					}
					break;
				case MEDRANK:
					if (StreamParser.parseRanking(task.getRank()) == RankType.MED) {
						tasks.add(i);
					}
					break;
				case LORANK:
					if (StreamParser.parseRanking(task.getRank()) == RankType.LO) {
						tasks.add(i);
					}
					break;
				case STARTBEF:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getStartTime() != null
							&& task.getStartTime().before(dueDate)) {
						tasks.add(i);
					}
					break;					
				case STARTAFT:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getStartTime() != null
							&& task.getStartTime().after(dueDate)) {
						tasks.add(i);
					}
					break;					
				case DUEBEF:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getDeadline() != null
							&& task.getDeadline().before(dueDate)) {
						tasks.add(i);
					}
					break;
				case DUEAFT:
					contents = criteria.split(" ", 3);
					dueDate = Chronic.parse(contents[2]).getBeginCalendar();
					if (task.getDeadline() != null
							&& task.getDeadline().after(dueDate)) {
						tasks.add(i);
					}
					break;
				case NOTIMING:
					if (task.isFloatingTask()) {
						tasks.add(i);
					}
					break;
				case DEADLINED:
					if (task.isDeadlineTask()) {
						tasks.add(i);
					}
					break;
				case EVENT:
					if (task.isTimedTask()) {
						tasks.add(i);
					}
					break;
				case STARTON:
				case DUEON:
					// TODO think on how to implement this
				default:
					// shouldn't happen, but in case it happens, pretend
					// that there is no filter
					tasks.add(i);
					break;
			}
		}
		logDebug(String.format(StreamConstants.LogMessage.FILTERED_TASKS,
				criteria, Arrays.toString(tasks.toArray())));
		return tasks;
	}

	// @author A0093874N
	/**
	 * Gets the number of tasks added.
	 * 
	 * @author Wilson Kurniawan
	 */
	public int getNumberOfTasks() {
		return streamObject.size();
	}

	// @author A0096529N
	/**
	 * Retrieves the task name by index
	 * 
	 * <p>
	 * Precondition: index is a valid index
	 * </p>
	 * 
	 * @param index
	 *            the index of the task
	 */
	public String getTaskNumber(int index) {
		return streamObject.getTaskList().get(index - 1);
	}

	// @author A0096529N
	/**
	 * @return taskMap a copy of the task map.
	 */
	public HashMap<String, StreamTask> getTaskMap() {
		return new HashMap<String, StreamTask>(streamObject.getTaskMap());
	}

	// @author A0096529N
	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<String> getTaskList() {
		return new ArrayList<String>(streamObject.getTaskList());
	}

	// @author A0096529N
	/**
	 * @return taskList a copy of the task list.
	 */
	public ArrayList<StreamTask> getStreamTaskList() {
		ArrayList<StreamTask> taskList = new ArrayList<StreamTask>();
		HashMap<String, StreamTask> taskMap = streamObject.getTaskMap();
		for (String key : taskMap.keySet()) {
			taskList.add(taskMap.get(key));
		}
		return taskList;
	}

	// @author A0093874N

	public ArrayList<StreamTask> getStreamTaskList(ArrayList<Integer> indices) {
		ArrayList<StreamTask> tasks = new ArrayList<StreamTask>();
		HashMap<String, StreamTask> taskMap = streamObject.getTaskMap();
		ArrayList<String> taskList = streamObject.getTaskList();
		for (Integer index : indices) {
			tasks.add(taskMap.get(taskList.get(index - 1).toLowerCase()));
		}
		return tasks;
	}

	@Override
	protected String getLoggerComponentName() {
		return StreamConstants.ComponentTag.STREAMLOGIC;
	}

	// @author A0096529N
	/**
	 * @deprecated not really used...
	 */
	public ArrayList<String> getTimedTaskList() {
		ArrayList<String> timedTaskList = new ArrayList<String>();
		for (String taskName : streamObject.keySet()) {
			if (streamObject.get(taskName).isTimedTask()) {
				timedTaskList.add(taskName);
			}
		}
		return timedTaskList;
	}

	// @author A0096529N
	/**
	 * @deprecated not really used...
	 */
	public ArrayList<String> getDeadlineTaskList() {
		ArrayList<String> timedTaskList = new ArrayList<String>();
		for (String taskName : streamObject.keySet()) {
			if (streamObject.get(taskName).isDeadlineTask()) {
				timedTaskList.add(taskName);
			}
		}
		return timedTaskList;
	}

	// @author A0096529N
	/**
	 * @deprecated not really used...
	 */
	public ArrayList<String> getFloatingTaskList() {
		ArrayList<String> timedTaskList = new ArrayList<String>();
		for (String taskName : streamObject.keySet()) {
			if (streamObject.get(taskName).isFloatingTask()) {
				timedTaskList.add(taskName);
			}
		}
		return timedTaskList;
	}

	// Depreciated methods

	/**
	 * @deprecated
	 */
	public ArrayList<String> getTaskNames() {
		return streamObject.getTaskList();
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
		task.markAsDone();
	}

	// @author A0119401U
	/**
	 * @deprecated
	 * 
	 */
	public void markTaskAsOngoing(String taskName)
			throws StreamModificationException {
		StreamTask task = this.getTask(taskName);
		task.markAsOngoing();
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
		task.setDeadline(calendar);
	}

	// @author A0119401U
	/**
	 * 
	 * @deprecated
	 */
	public void setNullDeadline(String taskName)
			throws StreamModificationException {
		StreamTask currentTask = this.getTask(taskName);
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
		task.setRank(newRank);

	}

	// @author A0119401U
	/**
	 * 
	 * @param taskName
	 * @return the actual index of a given task
	 * @deprecated not really needed, can be un-deprecated if we find a use
	 */
	public int getTaskIndex(String taskName) {
		return (streamObject.indexOf(taskName) + 1);
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

	// @author A0118007R
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

}