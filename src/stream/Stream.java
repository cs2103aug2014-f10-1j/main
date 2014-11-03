package stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;

import logic.TaskLogic;
import model.StreamObject;
import model.StreamTask;
import parser.StreamParser;
import parser.StreamParser.CommandType;
import ui.StreamUI;
import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import util.StreamUtil;
import exception.StreamIOException;
import exception.StreamModificationException;
import fileio.StreamIO;

/**
 * <b>Stream</b> is the main product of the project. It is the amalgamation of
 * all different components from different packages, namely <b>StreamUI</b>,
 * <b>StreamObject</b>, <b>StreamIO</b>, and <b>StreamParser</b>.
 * 
 * @version V0.4
 * @author Wilson Kurniawan, John Kevin Tjahjadi, Steven Khong Wai How, Jiang
 *         Shenhao
 */

public class Stream {

	StreamObject stobj;
	StreamUI stui;
	private StreamParser parser;
	private StreamLogger logger = StreamLogger.init(StreamConstants.ComponentTag.STREAM);
	
	private TaskLogic taskLogic = TaskLogic.init();

	private String filename;
	private Stack<String> inputStack;
	private Stack<StreamTask> dumpedTasks;
	private Stack<ArrayList<String>> orderingStack;

	private final String[] validParameters = { "name", "desc", "start", "from", "due", "by",
			"tag", "untag", "rank", "mark" };

	// author ??? refactored by A0118007R

	public Stream(String file) {
		initStreamIO(file);
		initializeStream();
		load();
		resetTasks();
	}

	// @author A0118007R

	private void resetTasks() {
		stui.resetAvailableTasks(stobj.getCounter(),
				stobj.getStreamTaskList(stobj.getCounter()), true, false);
	}

	// @author A0118007R

	private void initializeStream() {
		stui = new StreamUI(this);
		parser = new StreamParser();
		inputStack = new Stack<String>();
		dumpedTasks = new Stack<StreamTask>();
		orderingStack = new Stack<ArrayList<String>>();
	}

	// @author A0096529N
	private void initStreamIO(String file) {
		if (!file.endsWith(".json")) {
			filename = String.format(StreamConstants.SAVEFILE, file);
		} else {
			filename = file;
		}
		StreamIO.setSaveLocation(filename);
	}

	// @author A0096529N
	void load() {
		try {
			HashMap<String, StreamTask> allTasks = new HashMap<String, StreamTask>();
			ArrayList<String> taskList = new ArrayList<String>();

			StreamIO.load(allTasks, taskList);
			stobj = new StreamObject(allTasks, taskList);

		} catch (StreamIOException e) {
			log(String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage()));
			stobj = new StreamObject();
		}

	}

	// @author A0096529N
	String save() {
		String result = null;
		try {
			HashMap<String, StreamTask> allTasks = stobj.getTaskMap();
			ArrayList<String> taskList = stobj.getTaskList();
			StreamIO.save(allTasks, taskList);
			result = "File saved to " + StreamIO.getSaveLocation();
		} catch (StreamIOException e) {
			result = String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage());
			log(result);
		}

		return result;
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
	Boolean hasTask(String taskName) {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		return stobj.hasTask(taskName);
	}

	// @author A0118007R
	private boolean isValidParameter(String param) {
		for (String s : validParameters) {
			if (s.equals(param)) {
				return true;
			}
		}

		return false;
	}

	// @author A0093874N modified by A0118007R

	/**
	 * <p>
	 * Adds a new task to the tasks list.
	 * </p>
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null
	 * </p>
	 * 
	 * @param taskName
	 *            - the task name
	 * @author Wilson Kurniawan
	 * @throws StreamModificationException
	 *             - if a <b>StreamTask<b> named <i>taskName</i> is already
	 *             present.
	 * @return <strong>String</strong> - the log message
	 */
	String addTask(String taskName) throws StreamModificationException {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		// from here, section is modified by A0118007R
		String content = taskName;
		String[] splittedContent = content.split(" ");

		String nameOfTask = "";
		// scans until next valid parameter and officially add the task
		// TODO: refactor

		// this section onwards is contributed by A0118007R
		boolean nameFound = false;
		ArrayList<String> modifyParams = new ArrayList<String>();

		for (int i = 0; i < splittedContent.length; i++) {
			String s = splittedContent[i];
			if (!nameFound) {
				if (isValidParameter(s)) {
					nameFound = true;
				} else {
					nameOfTask = nameOfTask + s + " ";
				}
			} else {
				for (int k = i - 1; k < splittedContent.length; k++) {
					modifyParams.add(splittedContent[k]);
				}
				break;
			}
		}

		nameOfTask = nameOfTask.trim(); // this is the solution to the bug
		addTaskAndProcessParameters(nameOfTask, nameFound, modifyParams);
		//

		return String.format(StreamConstants.LogMessage.ADD, nameOfTask);

	}

	// @author A0118007R

	private void addTaskAndProcessParameters(String nameOfTask,
			boolean nameFound, ArrayList<String> modifyParams)
					throws StreamModificationException {

		stobj.addTask(nameOfTask);
		assert (stobj.hasTask(nameOfTask)) : StreamConstants.Assertion.NOT_ADDED;

		inputStack.push(String.format(StreamConstants.Commands.DISMISS,
				stobj.getNumberOfTasks()));

		if (nameFound) {
			executeModifyParameters(nameOfTask, modifyParams);
		}
	}

	// @author A0118007R

	private void executeModifyParameters(String nameOfTask,
			ArrayList<String> modifyParams) throws StreamModificationException {
		StreamTask currentTask = stobj.getTask(nameOfTask);

		// method for splitting the input to add to the specified param
		// TODO: refactor
		String command = modifyParams.get(0);
		String contents = "";
		for (int i = 1; i < modifyParams.size(); i++) {
			String s = modifyParams.get(i);
			if (isValidParameter(s)) {
				// first content is guaranteed to be a valid parameter
				processParameterModification(command, contents.trim(),
						currentTask);
				command = s;
				contents = "";

			} else {
				contents = contents + s + " ";
			}
		}
		processParameterModification(command, contents, currentTask);

	}

	// @author A0118007R
	// updated by A0119401U

	private void processParameterModification(String command, String contents,
			StreamTask task) throws StreamModificationException {
		contents = contents.trim();
		String taskName = task.getTaskName();
		switch (command) {
		case "name":
			setName(taskName, contents);
			break;
		case "desc":
			if (contents.equals("null")) {
				task.setDescription(null);
			} else {
				task.setDescription(contents);
			}
			break;
		case "due":
		case "by":
		case "to":
			if (contents.equals("null")) {
				task.setDeadline(null);
			} else {
				// TODO implement JChronic here ASAP!
				Calendar due = StreamUtil.parseCalendar(contents);
				task.setDeadline(due);
			}
			break;
		case "start":
		case "from":
			if (contents.equals("null")) {
				task.setStartTime(null);
			} else {

				Calendar start = StreamUtil.parseCalendar(contents);
				task.setStartTime(start);
			}
			break;
		case "tag":
			String[] newTags = contents.split(" ");
			taskLogic.addTags(task, newTags);
			break;
		case "untag":
			String[] tagsToRemove = contents.split(" ");
			taskLogic.removeTags(task, tagsToRemove);
			break;
		case "rank":
			String inputRank = contents.trim();
			if (inputRank.equals("high") || inputRank.equals("h")
					|| inputRank.equals("medium") || inputRank.equals("m")
					|| inputRank.equals("low") || inputRank.equals("l")) {
				task.setRank(inputRank);
			}
			break;
		case "mark":
			String status = contents.trim();
			if (status.equals("done") || status.equals("finished")) {
				task.markAsDone();
			} else if (status.equals("ongoing") || status.equals("going")
					|| status.equals("doing")) {
				task.markAsOngoing();
			}
			break;
		}
	}

	// @author A0093874N

	/**
	 * <p>
	 * Deletes a task from the tasks list permanently.
	 * </p>
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null.
	 * </p>
	 * 
	 * @author Wilson Kurniawan
	 * @param taskName
	 *            - the task name
	 * @throws StreamModificationException
	 */

	private void dismissTask(String taskName)
			throws StreamModificationException {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		stobj.deleteTask(taskName);
		assert (!stobj.hasTask(taskName)) : StreamConstants.Assertion.NOT_DELETED;
	}

	// @author A0118007R

	/**
	 * <p>
	 * Deletes a task from the tasks list and then archives it so it can be
	 * recovered by undo process.
	 * </p>
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null.
	 * </p>
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */

	private String deleteTask(String taskName)
			throws StreamModificationException {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		StreamTask deletedTask = stobj.getTask(taskName);
		ArrayList<String> order = stobj.getTaskList();
		orderingStack.push(order);
		dismissTask(taskName);

		// This section is contributed by A0093874N
		dumpedTasks.push(deletedTask);
		inputStack.push(String.format(StreamConstants.Commands.RECOVER, 1));
		return String.format(StreamConstants.LogMessage.DELETE, taskName);
		//
	}

	// @author A0093874N

	/**
	 * Clears all tasks upon receiving the command "clear".
	 * 
	 * @author Wilson Kurniawan
	 * @throws StreamModificationException
	 */
	private void clearAllTasks() throws StreamModificationException {
		int noOfTasks = stobj.getNumberOfTasks();
		orderingStack.push(stobj.getTaskList());
		clear(noOfTasks);
		assert (stobj.getNumberOfTasks() == 0) : StreamConstants.Assertion.NOT_CLEARED;
		inputStack.push(String.format(StreamConstants.Commands.RECOVER,
				noOfTasks));
	}

	// @author A0093874N

	private void clear(int noOfTasks) throws StreamModificationException {
		for (int i = 0; i < noOfTasks; i++) {
			deleteTask(stobj.getTaskNumber(1));
			/*
			 * This is because we don't want the "recover 1". Rather, we'll
			 * replace with "recover noOfTasks" at the end of the process.
			 */
			inputStack.pop();
			orderingStack.pop();
		}
	}

	// @author A0118007R

	/**
	 * <p>
	 * Prints the task details.
	 * </p>
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null
	 * </p>
	 * 
	 * @author John Kevin Tjahjadi
	 * @param taskName
	 *            - the task name
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */

	private String printDetails(String taskName)
			throws StreamModificationException {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		StreamTask currentTask = stobj.getTask(taskName);
		stui.displayDetails(currentTask);

		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.VIEW, taskName);
		//
	}

	// @author A0118007R

	/**
	 * <p>
	 * Changes a task's name.
	 * </p>
	 * <p>
	 * Pre-condition: <i>oldName, newName, index</i> not null
	 * </p>
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */

	private String setName(String oldName, String newName)
			throws StreamModificationException {
		assert (oldName != null && newName != null) : StreamConstants.Assertion.NULL_INPUT;
		stobj.updateTaskName(oldName, newName);

		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.NAME, oldName, newName);
		//
	}

	// @author A0118007R

	/**
	 * <p>
	 * Adds a description to a task.
	 * </p>
	 * <p>
	 * Pre-condition: <i>task, index, description</i> not null
	 * </p>
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private String setDescription(String task, int index, String description)
			throws StreamModificationException {
		StreamTask currentTask = stobj.getTask(task);
		String oldDescription = currentTask.getDescription();
		currentTask.setDescription(description.equals("null") ? null : description);

		// This section is contributed by A0093874N
		inputStack.push(String.format(StreamConstants.Commands.DESC, index,
				oldDescription));
		return String.format(StreamConstants.LogMessage.DESC,
				currentTask.getTaskName(), description);
		//
	}

	// @author A0119401U

	private String setRanking(String task, int index, String taskRank)
			throws StreamModificationException {
		StreamTask currentTask = stobj.getTask(task);
		String oldRank = currentTask.getRank();
		currentTask.setRank(taskRank);

		inputStack.push(String.format(StreamConstants.Commands.RANK, index,
				oldRank));
		return String.format(StreamConstants.LogMessage.RANK,
				currentTask.getTaskName(), taskRank);
	}

	// @author A0119401U

	/**
	 * Set the due date of the selected task
	 * 
	 * @author Jiang Shenhao
	 * @throws StreamModificationException
	 */
	private String setDueDate(String taskName, int taskIndex, Calendar calendar)
			throws StreamModificationException {
		StreamTask currentTask = stobj.getTask(taskName);
		Calendar currentDeadline = currentTask.getDeadline();
		pushInverseDueCommand(taskIndex, currentDeadline);
		// This section is contributed by A0093874N
		return setDeadline(taskName, calendar);
		//
	}

	// @author A0093874N
	private String setDeadline(String taskName, Calendar calendar)
			throws StreamModificationException {
		StreamTask task = stobj.getTask(taskName);
		if (calendar == null) {
			task.setNullDeadline();
			return String
					.format(StreamConstants.LogMessage.DUE_NEVER, taskName);
		} else {
			task.setDeadline(calendar);
			String parsedCalendar = StreamUtil.getCalendarWriteUp(calendar);
			return String.format(StreamConstants.LogMessage.DUE, taskName,
					parsedCalendar);
		}
	}

	// @author A0118007R

	private void pushInverseDueCommand(int taskIndex, Calendar currentDeadline) {
		if (currentDeadline == null) {
			inputStack.push(String.format(StreamConstants.Commands.DUE,
					taskIndex, "null"));
		} else {
			// TODO incorporate some JChronic here?
			inputStack.push(String.format(StreamConstants.Commands.DUE,
					taskIndex, currentDeadline.get(Calendar.DATE) + "/"
							+ (currentDeadline.get(Calendar.MONTH) + 1) + "/"
							+ currentDeadline.get(Calendar.YEAR)));
		}
	}

	// @author A0119401U

	/**
	 * Set the start date of the selected task
	 * 
	 * @author Jiang Shenhao
	 * @throws StreamModificationException
	 */
	private String setStartDate(String taskName, int taskIndex, Calendar calendar)
			throws StreamModificationException {
		StreamTask currentTask = stobj.getTask(taskName);
		Calendar currentStartTime = currentTask.getStartTime();
		pushInverseStartCommand(taskIndex, currentStartTime);
		return setStartTime(taskName, calendar);

	}

	private String setStartTime(String taskName, Calendar calendar)
			throws StreamModificationException {
		StreamTask task = stobj.getTask(taskName);
		if (calendar == null) {
			task.setNullStartTime();
			return String
					.format(StreamConstants.LogMessage.START_NOT_SPECIFIED, taskName);
		} else {
			task.setStartTime(calendar);
			String parsedCalendar = StreamUtil.getCalendarWriteUp(calendar);
			return String.format(StreamConstants.LogMessage.START, taskName,
					parsedCalendar);
		}
	}


	private void pushInverseStartCommand(int taskIndex, Calendar currentStartTime) {
		if (currentStartTime == null) {
			inputStack.push(String.format(StreamConstants.Commands.START,
					taskIndex, "null"));
		} else {
			inputStack.push(String.format(StreamConstants.Commands.START,
					taskIndex, currentStartTime.get(Calendar.DATE) + "/"
							+ (currentStartTime.get(Calendar.MONTH) + 1) + "/"
							+ currentStartTime.get(Calendar.YEAR)));
		}
	}

	// @author A0119401U

	/**
	 * Mark the selected task as done
	 * 
	 * @author Jiang Shenhao
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private String markAsDone(StreamTask task, int index)
			throws StreamModificationException {
		task.markAsDone();
		// This section is contributed by A0118007R
		inputStack.push(String.format(StreamConstants.Commands.MARK_NOT_DONE,
				index));
		//
		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.MARK,
				task.getTaskName(), "done");
		//
	}

	// @author A0118007R

	/**
	 * Mark the selected task as ongoing
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private String markAsOngoing(StreamTask task, int index)
			throws StreamModificationException {
		task.markAsOngoing();
		//
		inputStack.push(String
				.format(StreamConstants.Commands.MARK_DONE, index));
		//
		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.MARK,
				task.getTaskName(), "ongoing");
		//
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
	 * @return tasks - a list of tasks containing the key phrase.
	 * @author Steven Khong
	 */
	private ArrayList<Integer> search(String keyphrase) {
		assert (keyphrase != null) : StreamConstants.Assertion.NULL_INPUT;
		// modified by A0093874N
		return stobj.findTasks(keyphrase);
	}

	// @author A0093874N

	private ArrayList<Integer> filter(String criteria) {
		assert (criteria != null) : StreamConstants.Assertion.NULL_INPUT;
		// modified by A0093874N
		return stobj.filterTasks(criteria);
	}

	// @author A0093874N

	/**
	 * 
	 */
	private void showAndLogResult(String logMessage) {
		showAndLogResult(logMessage, logMessage);
	}

	// @author A0118007R

	private void showAndLogError(String errorMessage) {
		showAndLogError(errorMessage, errorMessage);
	}

	/*
	 * These two can be used if the log message to be documented and to be
	 * displayed are different (especially for exception/error messages)
	 */

	// @author A0093874N

	private void showAndLogResult(String logMessageForDoc,
			String logMessageForUser) {
		stui.log(logMessageForUser, false);
		log(StreamUtil.showAsTerminalResponse(logMessageForDoc));
	}

	// @author A0093874N

	private void showAndLogError(String errorMessageForDoc,
			String errorMessageForUser) {
		stui.log(errorMessageForUser, true);
		log(StreamUtil.showAsTerminalResponse(errorMessageForDoc));
	}

	// @author A0096529N
	private void log(String message) {
		logger.log(LogLevel.DEBUG, message);
	}

	// @author A0093874N
	private void logCommand(String cmd) {
		log(StreamUtil.showAsTerminalResponse(String.format(
				StreamConstants.LogMessage.CMD_RECEIVED, cmd)));
	}

	// @author A0093874N

	private void saveLogFile() throws IOException {
		Calendar now = Calendar.getInstance();
		String logFileName = String.format(StreamConstants.LOGFILE, StreamUtil.getDateString(now));
		StreamIO.saveLogFile(StreamLogger.getLogStack(), logFileName);
	}

	// @author A0118007R

	private void executeInput(CommandType command, String content)
			throws StreamModificationException, IOException {

		String[] contents;
		String[] tags;
		ArrayList<String> processedTags;
		String taskName;
		String logMessage;
		StreamTask task;
		int taskIndex;

		switch (command) {
		case ADD:
			logCommand("ADD");
			logMessage = addTask(content);
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			showAndLogResult(logMessage);
			break;

		case DEL:
			logCommand("DELETE");
			taskIndex = Integer.parseInt(content);
			taskName = stobj.getTaskNumber(taskIndex);
			logMessage = deleteTask(taskName);
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			showAndLogResult(logMessage);
			break;

		case DESC:
			logCommand("DESCRIBE");
			contents = content.split(" ", 2);
			taskIndex = Integer.parseInt(contents[0]);
			taskName = stobj.getTaskNumber(taskIndex);
			String description = contents[1];
			logMessage = setDescription(taskName, taskIndex, description);
			showAndLogResult(logMessage);
			break;

		case DUE:
			logCommand("DUE");
			contents = content.split(" ", 2);

			taskIndex = Integer.parseInt(contents[0]);
			taskName = stobj.getTaskNumber(taskIndex);

			logMessage = checkForNullAndProcessInput(contents, taskName,
					taskIndex, "due");
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			showAndLogResult(logMessage);
			break;

		case START:
			logCommand("START");
			contents = content.split(" ", 2);

			taskIndex = Integer.parseInt(contents[0]);
			taskName = stobj.getTaskNumber(taskIndex);

			logMessage = checkForNullAndProcessInput(contents, taskName,
					taskIndex, "start");

			// Sorry I don't know how to integrate the UI part to the start time
			// so missing code here
			showAndLogResult(logMessage);
			break;

		case MODIFY:
			logCommand("MODIFY");
			contents = content.split(" ");
			taskIndex = Integer.parseInt(contents[0]);
			taskName = stobj.getTaskNumber(taskIndex);

			ArrayList<String> modifyParams = new ArrayList<String>();
			StreamTask currTask = stobj.getTask(taskName);

			// get old state and push to undo

			String inverseCommand = buildInverseCommand(taskName,
					taskIndex, currTask);

			ArrayList<String> oldTags = new ArrayList<String>(
					currTask.getTags());

			addModificationParameters(contents, modifyParams);

			executeModifyParameters(taskName, modifyParams);
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			ArrayList<String> newTags = currTask.getTags();
			inverseCommand = processTagModification(inverseCommand,
					oldTags, newTags);

			inputStack.push(inverseCommand.trim());

			showAndLogResult(String.format(
					StreamConstants.LogMessage.MODIFY, taskName));
			break;

		case NAME:
			logCommand("NAME");
			contents = content.split(" ", 2);
			taskIndex = Integer.parseInt(contents[0]);
			String oldTaskName = stobj.getTaskNumber(taskIndex);
			String newTaskName = contents[1];
			logMessage = setName(oldTaskName, newTaskName);
			inputStack.push(String.format(StreamConstants.Commands.NAME,
					taskIndex, oldTaskName));
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			showAndLogResult(String.format(StreamConstants.LogMessage.NAME,
					oldTaskName, newTaskName));
			break;

		case RANK:
			logCommand("RANK");
			contents = content.split(" ", 2);
			taskIndex = Integer.parseInt(contents[0]);
			taskName = stobj.getTaskNumber(taskIndex);
			String taskRank = contents[1];
			logMessage = setRanking(taskName, taskIndex, taskRank);
			showAndLogResult(logMessage);
			break;

		case MARK:
			logCommand("MARK");
			contents = content.split(" ", 2);
			taskIndex = Integer.parseInt(contents[0]);
			taskName = stobj.getTaskNumber(taskIndex);
			String markType = contents[1].trim();
			mark(stobj.getTask(taskName), taskIndex, markType);
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			break;

		case TAG:
			logCommand("TAG");
			tags = content.split(" ");
			taskIndex = Integer.parseInt(tags[0]);
			taskName = stobj.getTaskNumber(taskIndex);
			task = stobj.getTask(taskName);
			
			processedTags = taskLogic.addTags(task, tags);
			pushTaggingIntoInputStack(taskIndex, processedTags);
			logAddedTags(taskName, processedTags);
			break;

		case UNTAG:
			logCommand("UNTAG");
			tags = content.split(" ");
			taskIndex = Integer.parseInt(tags[0]);
			taskName = stobj.getTaskNumber(taskIndex);
			task = stobj.getTask(taskName);
			
			processedTags = taskLogic.removeTags(task, tags);
			pushUntaggingIntoInputStack(taskIndex, processedTags);
			logRemovedTags(taskName, processedTags);
			break;

		case FILTER:
			logCommand("FILTER");
			ArrayList<Integer> filterResult = filter(content);
			stui.resetAvailableTasks(filterResult,
					stobj.getStreamTaskList(filterResult), true, true);
			showAndLogResult(String.format(
					StreamConstants.LogMessage.FILTER, content,
					filterResult.size()));
			break;

		case CLRSRC:

			resetTasks();
			break;

		case SEARCH:
			logCommand("SEARCH");
			ArrayList<Integer> searchResult = search(content);
			stui.resetAvailableTasks(searchResult,
					stobj.getStreamTaskList(searchResult), true, true);
			showAndLogResult(String.format(
					StreamConstants.LogMessage.SEARCH, content,
					searchResult.size()));
			break;

		case SORT:
			logCommand("SORT");
			ArrayList<String> oldOrdering = stobj.getTaskList();
			orderingStack.push(oldOrdering);
			logMessage = sort(content);
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			inputStack.push("unsort");
			showAndLogResult(logMessage);
			break;

		case VIEW:
			logCommand("VIEW");
			taskIndex = Integer.parseInt(content);
			taskName = stobj.getTaskNumber(taskIndex);
			logMessage = printDetails(taskName);
			showAndLogResult(logMessage);
			break;

		case CLEAR:
			logCommand("CLEAR");
			clearAllTasks();
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			showAndLogResult(StreamConstants.LogMessage.CLEAR);
			break;

		case UNDO:
			logCommand("UNDO");
			processUndo();
			break;

		case RECOVER:
			logCommand("RECOVER");
			int noOfTasksToRecover = Integer.parseInt(content);
			recover(noOfTasksToRecover);
			showAndLogResult(String.format(
					StreamConstants.LogMessage.RECOVER, noOfTasksToRecover));
			stobj.setOrdering(orderingStack.pop());
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			inputStack.push("some fake input to be popped");
			break;

		case DISMISS:
			logCommand("DISMISS");
			taskIndex = Integer.parseInt(content);
			taskName = stobj.getTaskNumber(taskIndex);
			dismissTask(taskName);
			showAndLogResult(String.format(
					StreamConstants.LogMessage.DELETE, taskName));
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			inputStack.push("some fake input to be popped");
			break;

		case UNSORT:
			logCommand("UNSORT");
			stobj.setOrdering(orderingStack.pop());
			stui.resetAvailableTasks(stobj.getCounter(),
					stobj.getStreamTaskList(stobj.getCounter()), false,
					false);
			showAndLogResult(StreamConstants.LogMessage.UNSORT);
			inputStack.push("some fake input to be popped");
			break;

		case FIRST:
			stui.goToFirstPage();
			break;

		case PREV:
			stui.goToPrevPage();
			break;

		case NEXT:
			stui.goToNextPage();
			break;

		case LAST:
			stui.goToLastPage();
			break;

		case EXIT:
			logCommand("EXIT");
			System.out.println(StreamConstants.Message.THANK_YOU);
			saveLogFile();
			System.exit(0);

		default:
			showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
		}
	}

	// @author A0093874N

	private void recover(int noOfTasksToRecover) {
		for (int i = 0; i < noOfTasksToRecover; i++) {
			StreamTask task = dumpedTasks.pop();
			stobj.recoverTask(task);
		}
	}

	// @author A0093874N

	private void processUndo() {
		if (inputStack.isEmpty()) {
			showAndLogResult(StreamConstants.LogMessage.UNDO_FAIL);
		} else {
			String undoneInput = inputStack.pop();
			showAndLogResult(StreamConstants.LogMessage.UNDO_SUCCESS);
			log(StreamUtil.showAsTerminalInput(undoneInput));
			processInput(undoneInput);

			/*
			 * VERY IMPORTANT because almost all inputs will add its counterpart
			 * to the inputStack. If not popped, the undo process will be
			 * trapped between just two processes.
			 */
			inputStack.pop();
		}
	}

	// @author A0118007R

	private void pushUntaggingIntoInputStack(int taskIndex,
			ArrayList<String> tagsRemoved) {
		if (tagsRemoved.size() != 0) {
			inputStack.push(String.format(StreamConstants.Commands.TAG,
					taskIndex,
					StreamUtil.listDownArrayContent(tagsRemoved, " ")));
		}
	}

	// @author A0118007R

	private void pushTaggingIntoInputStack(int taskIndex,
			ArrayList<String> tagsAdded) {
		if (tagsAdded.size() != 0) {
			inputStack
			.push(String.format(StreamConstants.Commands.UNTAG,
					taskIndex,
					StreamUtil.listDownArrayContent(tagsAdded, " ")));
		}
	}

	// @author A0118007R

	private void mark(StreamTask task, int taskIndex, String markType)
			throws StreamModificationException {
		String logMessage;
		/*
		 * TODO make the markType more flexible. maybe "finished", "not done",
		 * "not finished", ...
		 */
		if (markType.equals("done")) {
			logMessage = markAsDone(task, taskIndex);
			showAndLogResult(logMessage);
		} else if (markType.equals("ongoing")) {
			logMessage = markAsOngoing(task, taskIndex);
			showAndLogResult(logMessage);
		} else {
			logMessage = "Unknown marking type: " + markType;
			showAndLogError(logMessage);
		}
	}

	// @author A0118007R

	private String processTagModification(String inverseCommand,
			ArrayList<String> oldTags, ArrayList<String> newTags) {
		String inverseTag = compareTagged(oldTags, newTags);
		String inverseUntag = compareUntagged(oldTags, newTags);
		if (inverseTag != "tag ") {
			inverseCommand += inverseTag;
		}
		if (inverseUntag != "untag ") {
			inverseCommand += inverseUntag;
		}
		return inverseCommand;
	}

	// @author A0118007R

	private void addModificationParameters(String[] contents,
			ArrayList<String> modifyParams) {
		for (int i = 1; i < contents.length; i++) {
			modifyParams.add(contents[i]);
		}
	}

	// @author A0118007R

	private String buildInverseCommand(String taskName, int taskIndex,
			StreamTask currTask) {
		String inverseCommand = "modify " + taskIndex + " name " + taskName
				+ " ";
		// TODO help to refactor these
		// added by A0093874N
		Boolean isDone = currTask.isDone();
		if (isDone) {
			inverseCommand += "mark done ";
		} else {
			inverseCommand += "mark ongoing ";
		}
		String oldRank = currTask.getRank();
		inverseCommand += "rank " + oldRank + " ";
		//
		inverseCommand = buildModifyDescription(currTask, inverseCommand);
		inverseCommand = buildModifyDeadline(currTask, inverseCommand);
		return inverseCommand;
	}

	// @author A0118007R

	private String buildModifyDeadline(StreamTask currTask,
			String inverseCommand) {
		Calendar oldDue = currTask.getDeadline();
		if (oldDue != null) {
			String dueString = StreamUtil.getCalendarWriteUp(oldDue);
			inverseCommand = inverseCommand + "due " + dueString + " ";
		} else {
			inverseCommand = inverseCommand + "due null ";
		}
		return inverseCommand;
	}

	// @author A0118007R

	private String buildModifyDescription(StreamTask currTask,
			String inverseCommand) {
		String oldDesc = currTask.getDescription();
		if (oldDesc != null) {
			inverseCommand = inverseCommand + "desc " + oldDesc + " ";
		} else {
			inverseCommand = inverseCommand + "desc null ";
		}
		return inverseCommand;
	}

	// @author A0118007R
	// updated by A0119401U

	private String checkForNullAndProcessInput(String[] contents,
			String taskName, int taskIndex, String action) throws StreamModificationException {
		String logMessage;
		if(action.equals("due")){

			if (contents[1].trim().equals("null")) {
				logMessage = setDueDate(taskName, taskIndex, null);
			} else {
				String due = contents[1];
				Calendar calendar = StreamUtil.parseCalendar(due);
				logMessage = setDueDate(taskName, taskIndex, calendar);
			}
		}
		else{
			if (contents[1].trim().equals("null")) {
				logMessage = setStartDate(taskName, taskIndex, null);
			} else {
				String start = contents[1];
				Calendar calendar = StreamUtil.parseCalendar(start);
				logMessage = setStartDate(taskName, taskIndex, calendar);
			}
		}
		return logMessage;
	}

	// all methods from the previous tag until are refactored by A0118007R

	// @author A0096529N
	private String sort(String content) {
		String sortBy = null;
		String order = null;
		boolean descending = false;
		if (content.contains(" ")) {
			sortBy = content.split(" ")[0];
			order = content.split(" ")[1];
		} else {
			sortBy = content;
			order = "asc";
		}

		if (order.equalsIgnoreCase("desc")
				|| order.equalsIgnoreCase("descending")
				|| order.equalsIgnoreCase("d")) {
			descending = true;
		}

		switch (sortBy.toLowerCase()) {
		case "alpha":
		case "a":
		case "alphabetical":
		case "alphabetically":
			stobj.sortAlpha(descending);
			return "Sort by alphabetical order, "
			+ (descending ? "descending." : "ascending.");
		case "t":
		case "deadline":
		case "due":
			stobj.sortDeadline(descending);
			return "Sort by deadline "
			+ (descending ? "descending." : "ascending.");
		default:
			return "Unknown sort category \"" + sortBy + "\"";
		}
	}

	// @author A0093874N

	private String compareTagged(ArrayList<String> oldTags,
			ArrayList<String> newTags) {
		String inverseTag = "tag ";
		inverseTag = buildInverseTag(oldTags, newTags, inverseTag);
		return inverseTag;
	}

	// @author A0118007R

	private String buildInverseTag(ArrayList<String> oldTags,
			ArrayList<String> newTags, String inverseTag) {
		for (String old : oldTags) {
			if (!newTags.contains(old)) {
				inverseTag = inverseTag + old + " ";
			}
		}
		return inverseTag;
	}

	// @author A0093874N

	private String compareUntagged(ArrayList<String> oldTags,
			ArrayList<String> newTags) {
		String inverseUntag = "untag ";
		inverseUntag = buildInverseUntag(oldTags, newTags, inverseUntag);
		return inverseUntag;
	}

	// @author A0118007R

	private String buildInverseUntag(ArrayList<String> oldTags,
			ArrayList<String> newTags, String inverseUntag) {
		for (String newer : newTags) {
			if (!oldTags.contains(newer)) {
				inverseUntag += inverseUntag + newer + " ";
			}
		}
		return inverseUntag;
	}

	// @author A0093874N

	private void logAddedTags(String taskName, ArrayList<String> tagsAdded) {
		if (!tagsAdded.isEmpty()) {
			showAndLogResult(String.format(
					StreamConstants.LogMessage.TAGS_ADDED, taskName,
					StreamUtil.listDownArrayContent(tagsAdded, ", ")));
		} else {
			showAndLogResult(StreamConstants.LogMessage.NO_TAGS_ADDED);
		}
	}

	// @author A0093874N

	private void logRemovedTags(String taskName, ArrayList<String> tagsRemoved) {
		if (!tagsRemoved.isEmpty()) {
			showAndLogResult(String.format(
					StreamConstants.LogMessage.TAGS_REMOVED, taskName,
					StreamUtil.listDownArrayContent(tagsRemoved, ", ")));
		} else {
			showAndLogResult(StreamConstants.LogMessage.NO_TAGS_REMOVED);
		}
	}

	// @author A0093874N

	private void processInput(String input) {
		try {
			parser.interpretCommand(input, stobj);
			CommandType command = parser.getCommandType();
			String content = parser.getCommandContent();
			executeInput(command, content);
		} catch (AssertionError e) {
			showAndLogError(String.format(StreamConstants.LogMessage.ERRORS,
					"AssertionError", e.getMessage()));
		} catch (Exception e) {
			showAndLogError(String.format(StreamConstants.LogMessage.ERRORS, e
					.getClass().getSimpleName(), e.getMessage()));
		}
	}

	// @author A0093874N

	private Boolean isBlockedInput(String input) {
		if (input.length() >= 6) {
			if (input.substring(0, 6).equals("unsort")) {
				return true;
			} else {
				return input.length() >= 7
						&& (input.substring(0, 7).equals("recover") || input
								.substring(0, 7).equals("dismiss"));
			}
		} else {
			return false;
		}
	}

	// @author A0093874N

	public void filterAndProcessInput(String input) {
		if (input == null) {
			showAndLogError(String.format(StreamConstants.LogMessage.ERRORS,
					"AssertionError", StreamConstants.Assertion.NULL_INPUT));
		} else {
			log(StreamUtil.showAsTerminalInput(input));
			if (isBlockedInput(input)) {
				showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
			} else {
				processInput(input);
				save();
			}
		}
	}


	public static void main(String[] args) {
		new Stream(StreamConstants.FILENAME);
	}

	// @author A0118007R
	/**
	 * @deprecated
	 */
	void printTasks() {
		System.out.println(" ");
		System.out.println("Your current tasks: ");
		int numberOfTasks = stobj.getNumberOfTasks();

		for (int i = 1; i <= numberOfTasks; i++) {
			System.out.println(i + ". " + stobj.getTaskNumber(i));
		}
	}

	// @author A0118007R
	/**
	 * @deprecated for now
	 */
	void showAndLogCommand(String command) {
		String commandReceived = String.format(
				StreamConstants.LogMessage.CMD_RECEIVED, command);
		System.out.println(commandReceived);
		log(StreamUtil.showAsTerminalResponse(commandReceived));
	}

	// @author A0118007R
	/**
	 * Modify a task's description This method is just to differentiate the set
	 * new description and modify description part
	 * 
	 * @author John Kevin Tjahjadi
	 * @deprecated by A0093874N. Can be un-deprecated if we find a use for it.
	 * 
	 *             TODO unused.
	 */
	@SuppressWarnings("unused")
	private void changeDescription(String task, int index, String newDescription) {
		try {
			setDescription(task, index, newDescription);
		} catch (Exception e) {

		}
	}		
}