package stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import logic.StackLogic;
import logic.StreamLogic;
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
import exception.StreamParserException;
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

	StreamObject streamObject = new StreamObject();
	StreamUI stui;
	TaskLogic taskLogic = TaskLogic.init();
	StackLogic stackLogic = StackLogic.init();
	StreamLogic streamLogic = StreamLogic.init(streamObject);

	private StreamParser parser;
	private StreamLogger logger = StreamLogger.init(StreamConstants.ComponentTag.STREAM);

	private String filename;

	// author ??? refactored by A0118007R
	public Stream(String file) {
		initStreamIO(file);
		initializeStream();
		load();
		resetTasks();
	}

	// @author A0118007R
	private void initializeStream() {
		stui = new StreamUI(this);
		parser = new StreamParser();
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
			HashMap<String, StreamTask> taskMap = new HashMap<String, StreamTask>();
			ArrayList<String> taskList = new ArrayList<String>();

			StreamIO.load(taskMap, taskList);
			streamObject.setTaskList(taskList);
			streamObject.setTaskMap(taskMap);
		} catch (StreamIOException e) {
			log(String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage()));
		}
	}

	// @author A0096529N
	String save() {
		String result = null;
		try {
			HashMap<String, StreamTask> allTasks = streamObject.getTaskMap();
			ArrayList<String> taskList = streamObject.getTaskList();
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
	private void saveLogFile() throws IOException {
		Calendar now = Calendar.getInstance();
		String logFileName = String.format(StreamConstants.LOGFILE, StreamUtil.getDateString(now));
		StreamIO.saveLogFile(StreamLogger.getLogStack(), logFileName);
	}

	// @author A0118007R
	private void executeInput(CommandType command, String content)
			throws StreamModificationException, IOException {
		switch (command) {
		case ADD:
			logCommand("ADD");
			executeAdd(content);
			refreshUI();
			break;

		case DEL:
			logCommand("DELETE");
			executeDelete(content);
			refreshUI();
			break;

		case DESC:
			logCommand("DESCRIBE");
			executeDescription(content);
			break;

		case DUE:
			logCommand("DUE");
			executeDue(content);
			refreshUI();
			break;

		case START:
			logCommand("START");
			executeStartTime(content);
			refreshUI();
			break;

		case MODIFY:
			logCommand("MODIFY");
			executeModify(content);
			refreshUI();
			break;

		case NAME:
			logCommand("NAME");
			executeName(content);
			refreshUI();
			break;

		case RANK:
			logCommand("RANK");
			executeRank(content);
			break;

		case MARK:
			logCommand("MARK");
			executeMark(content);
			refreshUI();
			break;

		case TAG:
			logCommand("TAG");
			executeTag(content);
			break;

		case UNTAG:
			logCommand("UNTAG");
			executeUntag(content);
			break;

		case FILTER:
			logCommand("FILTER");
			ArrayList<Integer> filterResult = executeFilter(content);
			refreshUI(filterResult, true, true);
			break;

		case CLRSRC:
			logCommand("CLRSRC");
			refreshUI();
			break;

		case SEARCH:
			logCommand("SEARCH");
			ArrayList<Integer> searchResult = executeSearch(content);
			refreshUI(searchResult, true, true);
			break;

		case SORT:
			logCommand("SORT");
			executeSort(content);
			refreshUI();
			break;

		case VIEW:
			logCommand("VIEW");
			executeView(content);
			break;

		case CLEAR:
			logCommand("CLEAR");
			executeClear();
			refreshUI();
			break;

		case UNDO:
			logCommand("UNDO");
			executeUndo();
			break;

		case RECOVER:
			logCommand("RECOVER");
			executeRecover(content);
			refreshUI();
			break;

		case DISMISS:
			logCommand("DISMISS");
			executeDismiss(content);
			refreshUI();
			break;

		case UNSORT:
			logCommand("UNSORT");
			executeUnsort();
			refreshUI();
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
			showAndLogResult(StreamConstants.Message.THANK_YOU);
			saveLogFile();
			System.exit(0);

		default:
			showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
		}
	}

	// @author A0093874N modified by A0118007R
	/**
	 * Adds a new task to the tasks list.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null
	 * </p>
	 * 
	 * @param taskNameWithParams
	 *            - the task name
	 * @author Wilson Kurniawan
	 * @throws StreamModificationException
	 *             - if a <b>StreamTask<b> named <i>taskName</i> is already
	 *             present.
	 * @return <strong>String</strong> - the log message
	 */
	private void executeAdd(String taskNameWithParams) 
			throws StreamModificationException {
		assert (taskNameWithParams != null) : StreamConstants.Assertion.NULL_INPUT;
		// from here, section is modified by A0118007R
		String[] contents = taskNameWithParams.split(" ");

		String taskName = "";
		// scans until next valid parameter and officially add the task
		// TODO: refactor

		// this section onwards is contributed by A0118007R
		ArrayList<String> modifyParams = new ArrayList<String>();

		for (int i = 0; i < contents.length; i++) {
			String word = contents[i];
			if (StreamUtil.isValidAttribute(word)) {
				for (int k = i - 1; k < contents.length; k++) {
					modifyParams.add(contents[k]);
				}
				break;
			} else {
				taskName = taskName + word + " ";
			}
		}

		taskName = taskName.trim(); // this is the solution to the bug
		addTaskWithParams(taskName, modifyParams);

		String result = String.format(StreamConstants.LogMessage.ADD, taskName);
		showAndLogResult(result);
	}

	// @author A0093874N
	/**
	 * Deletes a task from the tasks list permanently.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null.
	 * </p>
	 * 
	 * @author Wilson Kurniawan
	 * @param taskName
	 *            - the task name
	 * @throws StreamModificationException
	 */
	private void executeDismiss(String content)
			throws StreamModificationException {
		int taskIndex = Integer.parseInt(content);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		stackLogic.pushPlaceholderInput();

		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		streamLogic.deleteTask(taskName);
		assert (!streamLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_DELETED;

		String result = String.format(StreamConstants.LogMessage.DELETE, taskName);
		showAndLogResult(result);
	}

	// @author A0118007R
	/**
	 * Deletes a task from the tasks list and then archives it so it can be
	 * recovered by undo process.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null.
	 * </p>
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeDelete(String content)
			throws StreamModificationException {
		int taskIndex = Integer.parseInt(content);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		StreamTask deletedTask = streamLogic.getTask(taskName);
		ArrayList<String> order = streamLogic.getTaskList();

		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		streamLogic.deleteTask(taskName);
		assert (!streamLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_DELETED;

		stackLogic.pushInverseDeleteCommand(deletedTask, order);
		String result = String.format(StreamConstants.LogMessage.DELETE, taskName);
		showAndLogResult(result);
	}

	// @author A0093874N
	/**
	 * Clears all tasks upon receiving the command "clear".
	 * 
	 * @author Wilson Kurniawan
	 * @throws StreamModificationException
	 */
	private void executeClear() 
			throws StreamModificationException {
		stackLogic.pushInverseClearCommand(streamLogic.getTaskList(), streamLogic.getStreamTaskList());
		streamLogic.clear();
		assert (streamLogic.getNumberOfTasks() == 0) : StreamConstants.Assertion.NOT_CLEARED;
		showAndLogResult(StreamConstants.LogMessage.CLEAR);
	}

	// @author A0118007R
	/**
	 * Prints the task details.
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
	private void executeView(String content)
			throws StreamModificationException {
		int taskIndex = Integer.parseInt(content);
		String taskName = streamLogic.getTaskNumber(taskIndex);

		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
		StreamTask currentTask = streamLogic.getTask(taskName);
		stui.displayDetails(currentTask);

		// This section is contributed by A0093874N
		String result = String.format(StreamConstants.LogMessage.VIEW, taskName);
		showAndLogResult(result);
	}

	// @author A0118007R
	/**
	 * Adds a description to a task.
	 * <p>
	 * Pre-condition: <i>task, index, description</i> not null
	 * </p>
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeDescription(String content) 
			throws StreamModificationException {
		String[] contents = content.split(" ", 2);
		int taskIndex = Integer.parseInt(contents[0]);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String description = contents[1];
		StreamTask currentTask = streamLogic.getTask(taskName);
		String oldDescription = currentTask.getDescription();
		currentTask.setDescription(description.equals("null") ? null : description);

		stackLogic.pushInverseSetDescriptionCommand(taskIndex, oldDescription);
		String result = String.format(StreamConstants.LogMessage.DESC,
				currentTask.getTaskName(), description);
		showAndLogResult(result);
	}

	// @author A0119401U
	private void executeRank(String content) 
			throws StreamModificationException {
		String[] contents = content.split(" ", 2);
		int taskIndex = Integer.parseInt(contents[0]);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String taskRank = contents[1];
		StreamTask currentTask = streamLogic.getTask(taskName);
		String oldRank = currentTask.getRank();
		currentTask.setRank(taskRank);

		stackLogic.pushInverseSetRankingCommand(taskIndex, oldRank);
		String result = String.format(StreamConstants.LogMessage.RANK,
				currentTask.getTaskName(), taskRank);
		showAndLogResult(result);
	}

	// @author A0118007R
	private void executeName(String content) 
			throws StreamModificationException {
		String[] contents = content.split(" ", 2);
		int taskIndex = Integer.parseInt(contents[0]);
		String oldTaskName = streamLogic.getTaskNumber(taskIndex);
		String newTaskName = contents[1];
		streamLogic.updateTaskName(oldTaskName, newTaskName);

		stackLogic.pushInverseSetNameCommand(taskIndex, oldTaskName);

		String result = String.format(StreamConstants.LogMessage.NAME,
				oldTaskName, newTaskName);
		showAndLogResult(result);
	}

	// @author A0118007R
	private void executeModify(String content) 
			throws StreamModificationException {
		String[] contents = content.split(" ");
		int taskIndex = Integer.parseInt(contents[0]);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask currTask = streamLogic.getTask(taskName);

		// get old state and push to undo
		String inverseCommand = stackLogic.prepareInverseModifyCommand(
				taskName, taskIndex, currTask);

		String[] params = Arrays.copyOfRange(contents, 1, contents.length);
		streamLogic.modifyTaskWithParams(taskName, Arrays.asList(params));

		stackLogic.pushInverseModifyCommand(inverseCommand);

		String result = String.format(StreamConstants.LogMessage.MODIFY, taskName);
		showAndLogResult(result);
	}

	// @author A0093874N
	private void executeUntag(String content) 
			throws StreamModificationException {
		String[] tags;
		ArrayList<String> processedTags;
		String taskName;
		StreamTask task;
		int taskIndex;
		tags = content.split(" ");
		taskIndex = Integer.parseInt(tags[0]);
		taskName = streamLogic.getTaskNumber(taskIndex);
		task = streamLogic.getTask(taskName);

		processedTags = taskLogic.removeTags(task, tags);
		stackLogic.pushInverseUntagCommand(taskIndex, processedTags);
		logRemovedTags(taskName, processedTags);
	}

	// @author A0093874N
	private void executeTag(String content) 
			throws StreamModificationException {
		String[] tags;
		ArrayList<String> processedTags;
		String taskName;
		StreamTask task;
		int taskIndex;
		tags = content.split(" ");
		taskIndex = Integer.parseInt(tags[0]);
		taskName = streamLogic.getTaskNumber(taskIndex);
		task = streamLogic.getTask(taskName);

		processedTags = taskLogic.addTags(task, tags);
		stackLogic.pushInverseAddTagCommand(taskIndex, processedTags);
		logAddedTags(taskName, processedTags);
	}

	// @author A0093874N
	private void executeUnsort() {
		streamLogic.setOrdering(stackLogic.popOrder());
		stackLogic.pushPlaceholderInput();
		showAndLogResult(StreamConstants.LogMessage.UNSORT);
	}

	// @author A0093874N
	private void executeRecover(String content) {
		int noOfTasksToRecover = Integer.parseInt(content);
		stackLogic.pushPlaceholderInput();

		for (int i = 0; i < noOfTasksToRecover; i++) {
			StreamTask task = stackLogic.recoverTask();
			streamLogic.recoverTask(task);
		}
		streamLogic.setOrdering(stackLogic.popOrder());

		String result = String.format(StreamConstants.LogMessage.RECOVER, noOfTasksToRecover);
		showAndLogResult(result);
	}

	// @author A0093874N
	private void executeUndo() {
		if (!stackLogic.hasInverseInput()) {
			showAndLogResult(StreamConstants.LogMessage.UNDO_FAIL);
		} else {
			String undoneInput = stackLogic.popInverseCommand();
			showAndLogResult(StreamConstants.LogMessage.UNDO_SUCCESS);
			log(StreamUtil.showAsTerminalInput(undoneInput));
			processInput(undoneInput);

			/*
			 * VERY IMPORTANT because almost all inputs will add its counterpart
			 * to the inputStack. If not popped, the undo process will be
			 * trapped between just two processes.
			 */
			stackLogic.popInverseCommand();
		}
	}

	// @author A0118007R
	private void executeMark(String content) 
			throws StreamModificationException {
		String[] contents = content.split(" ", 2);
		int taskIndex = Integer.parseInt(contents[0]);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String markType = contents[1].trim();
		StreamTask task = streamLogic.getTask(taskName);
		String result = null;
		/*
		 * TODO make the markType more flexible. maybe "finished", "not done",
		 * "not finished", ...
		 */
		if (markType.equals("done")) {
			result = markAsDone(task, taskIndex);
		} else if (markType.equals("ongoing")) {
			result = markAsOngoing(task, taskIndex);
		} else {
			result = "Unknown marking type: " + markType;
		}
		showAndLogResult(result);
	}

	// @author A0118007R
	private void executeDue(String content) 
			throws StreamModificationException {
		String[] contents = content.split(" ", 2);

		int taskIndex = Integer.parseInt(contents[0]);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String result = null;
		if (contents[1].trim().equals("null")) {
			result = setDueDate(taskName, taskIndex, null);
		} else {
			String due = contents[1];
			due = StreamUtil.parseWithChronic(due);
			Calendar calendar = StreamUtil.parseCalendar(due);
			result = setDueDate(taskName, taskIndex, calendar);
		}
		showAndLogResult(result);
	}



	// @author A0118007R
	private void executeStartTime(String content) 
			throws StreamModificationException {
		String[] contents = content.split(" ", 2);

		int taskIndex = Integer.parseInt(contents[0]);
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String result = null;
		if (contents[1].trim().equals("null")) {
			result = setStartDate(taskName, taskIndex, null);
		} else {
			String start = contents[1];
			start = StreamUtil.parseWithChronic(start);
			Calendar calendar = StreamUtil.parseCalendar(start);
			result = setStartDate(taskName, taskIndex, calendar);
		}
		showAndLogResult(result);
	}

	// @author A0096529N
	private void executeSort(String content) {
		ArrayList<String> oldOrdering = streamLogic.getTaskList();
		stackLogic.pushInverseSortCommand(oldOrdering);

		String result = null;
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
			result = streamLogic.sortAlpha(descending);
			break;
		case "t":
		case "deadline":
		case "due":
			result = streamLogic.sortDeadline(descending);
			break;
		default:
			result = "Unknown sort category \"" + sortBy + "\"";
			break;
		}
		showAndLogResult(result);
	}

	// @author A0096529N
	// modified by A0093874N
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
	private ArrayList<Integer> executeSearch(String content) {
		assert (content != null) : StreamConstants.Assertion.NULL_INPUT;
		ArrayList<Integer> searchResult = streamLogic.findTasks(content);

		String result = String.format(StreamConstants.LogMessage.SEARCH, 
				content, searchResult.size());
		showAndLogResult(result);

		return searchResult;
	}

	// @author A0093874N
	private ArrayList<Integer> executeFilter(String content) {
		assert (content != null) : StreamConstants.Assertion.NULL_INPUT;
		ArrayList<Integer> filterResult = streamLogic.filterTasks(content);

		String result = String.format( StreamConstants.LogMessage.FILTER, 
				content, filterResult.size());
		showAndLogResult(result);

		return filterResult;
	}

	// @author A0118007R
	private void addTaskWithParams(String taskName, ArrayList<String> modifyParams)
			throws StreamModificationException {

		streamLogic.addTask(taskName);
		assert (streamLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_ADDED;

		stackLogic.pushInverseAddCommand(streamLogic.getNumberOfTasks());

		if (modifyParams.size() > 0) {
			streamLogic.modifyTaskWithParams(taskName, modifyParams);
		}
	}

	// @author A0119401U
	/**
	 * Set the due date of the selected task
	 * 
	 * @author Jiang Shenhao
	 * @throws StreamModificationException
	 */
	private String setDueDate(String taskName, int taskIndex, Calendar newDeadline)
			throws StreamModificationException {
		StreamTask task = streamLogic.getTask(taskName);
		Calendar deadline = task.getDeadline();
		stackLogic.pushInverseDueCommand(taskIndex, deadline);
		// This section is contributed by A0093874N
		return taskLogic.setDeadline(task, newDeadline);
		//
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
		StreamTask currentTask = streamLogic.getTask(taskName);
		Calendar currentStartTime = currentTask.getStartTime();
		stackLogic.pushInverseStartCommand(taskIndex, currentStartTime);
		return taskLogic.setStartTime(currentTask, calendar);

	}

	// @author A0119401U
	/**
	 * Mark the selected task as done
	 * 
	 * @author Jiang Shenhao
	 * @return <strong>String</strong> - the log message
	 */
	private String markAsDone(StreamTask task, int index) {
		boolean wasDone = task.isDone();
		task.markAsDone();

		stackLogic.pushInverseSetDoneCommand(wasDone, index);
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
	 * @return <strong>String</strong> - the log message
	 */
	private String markAsOngoing(StreamTask task, int index) {
		boolean wasDone = task.isDone();
		task.markAsOngoing();

		stackLogic.pushInverseSetDoneCommand(wasDone, index);
		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.MARK,
				task.getTaskName(), "ongoing");
		//
	}

	//@author A0096529N
	private void refreshUI() {
		refreshUI(false, false);
	}

	// @author A0093874N
	private void refreshUI(boolean isReset, boolean isSearching) {
		stui.resetAvailableTasks(streamLogic.getIndex(),
				streamLogic.getStreamTaskList(streamLogic.getIndex()), isReset,
				isSearching);
	}

	// @author A0093874N
	private void refreshUI(ArrayList<Integer> index, boolean isReset, boolean isSearching) {
		stui.resetAvailableTasks(index, streamLogic.getStreamTaskList(index), 
				isReset, isSearching);
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
	private void processInput(String input) {
		try {
			parser.interpretCommand(input, streamLogic.getNumberOfTasks());
			CommandType command = parser.getCommandType();
			String content = parser.getCommandContent();
			executeInput(command, content);
		} catch (AssertionError e) {
			log(String.format(StreamConstants.LogMessage.ERRORS, "AssertionError", e.getMessage()));
			showAndLogError(String.format(StreamConstants.LogMessage.UNEXPECTED_ERROR, e.getMessage()));
		} catch (StreamParserException e) {
			log(String.format(StreamConstants.LogMessage.ERRORS, 
					e.getClass().getSimpleName(), e.getMessage()));
			if (e.getMessage().equals("Empty Input")) {
				showAndLogError(String.format(StreamConstants.LogMessage.EMPTY_INPUT_ERROR, 
						e.getClass().getSimpleName() + " " + e.getMessage()));
			} else {
				showAndLogError(String.format(StreamConstants.LogMessage.PARSER_ERROR, 
						e.getClass().getSimpleName() + " " + e.getMessage()));
			}
		} catch (Exception e) {
			log(String.format(StreamConstants.LogMessage.ERRORS, 
					e.getClass().getSimpleName(), e.getMessage()));
			showAndLogError(String.format(StreamConstants.LogMessage.UNEXPECTED_ERROR, 
					e.getClass().getSimpleName() + " " + e.getMessage()));
		}
	}

	// @author A0093874N
	private Boolean isRestrictedInput(String input) {
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
		assert(input != null) : String.format(StreamConstants.LogMessage.ERRORS,
				"AssertionError", StreamConstants.Assertion.NULL_INPUT);

		log(StreamUtil.showAsTerminalInput(input));
		if (isRestrictedInput(input)) {
			showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
		} else {
			processInput(input);
			save();
		}
	}


	public static void main(String[] args) {
		new Stream(StreamConstants.FILENAME);
	}

	// @author A0118007R
	/**
	 * @deprecated - use resetUI instead
	 */
	private void resetTasks() {
		stui.resetAvailableTasks(streamLogic.getIndex(),
				streamLogic.getStreamTaskList(streamLogic.getIndex()), true, false);
	}

	// @author A0093874N
	/**
	 * 
	 * @param noOfTasks
	 * @throws StreamModificationException
	 * @deprecated by A0096529N - undo handling moved to StackLogic
	 */
	@SuppressWarnings("unused")
	private void clear(int noOfTasks) throws StreamModificationException {
		for (int i = 0; i < noOfTasks; i++) {
			executeDelete(streamLogic.getTaskNumber(1));
			/*
			 * This is because we don't want the "recover 1". Rather, we'll
			 * replace with "recover noOfTasks" at the end of the process.
			 */
			//inputStack.pop();
			//orderingStack.pop();
		}
	}

	// @author A0118007R
	/**
	 * @deprecated
	 */
	void printTasks() {
		System.out.println(" ");
		System.out.println("Your current tasks: ");
		int numberOfTasks = streamLogic.getNumberOfTasks();

		for (int i = 1; i <= numberOfTasks; i++) {
			System.out.println(i + ". " + streamLogic.getTaskNumber(i));
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
			executeDescription(newDescription);
		} catch (Exception e) {

		}
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
	 * @deprecated by A0096529N merged into StreamLogic.updateTaskName
	 */
	@SuppressWarnings("unused")
	private String setName(String oldName, String newName)
			throws StreamModificationException {
		assert (oldName != null && newName != null) : StreamConstants.Assertion.NULL_INPUT;
		streamLogic.updateTaskName(oldName, newName);

		// This section is contributed by A0093874N
		return String.format(StreamConstants.LogMessage.NAME, oldName, newName);
	}

}