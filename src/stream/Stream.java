package stream;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.ImageIcon;

import logic.StackLogic;
import logic.StreamLogic;
import logic.TaskLogic;
import model.StreamObject;
import model.StreamTask;
import parser.StreamParser;
import parser.StreamParser.CommandType;
import parser.StreamParser.MarkType;
import parser.StreamParser.SortType;
import ui.StreamUI;
import util.StreamConstants;
import util.StreamExternals;
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
 * @version V0.5
 */
public class Stream {

	StreamObject streamObject = StreamObject.getInstance();
	StreamUI stui;
	TaskLogic taskLogic = TaskLogic.init();
	StackLogic stackLogic = StackLogic.init();
	StreamLogic streamLogic = StreamLogic.init(streamObject);

	private StreamParser parser;
	private StreamLogger logger = StreamLogger
			.init(StreamConstants.ComponentTag.STREAM);

	private String filename;

	//@author A0118007R
	/**
	 * Stream Constructor to initialize the program.
	 */
	public Stream(String file) {
		initializeExtFiles();
		initStreamIO(file);
		initializeStream();
		load();
		refreshUI();
	}

	private void initializeStream() {
		stui = new StreamUI(this);
		parser = new StreamParser();
	}

	//@author A0093874N
	private void initializeExtFiles() {
		ImageIcon headerText = new ImageIcon(getClass().getResource(
				"/img/header.png"));
		ImageIcon doneIcon = new ImageIcon(getClass().getResource(
				"/img/taskDoneIcon.png"));
		ImageIcon notDoneIcon = new ImageIcon(getClass().getResource(
				"/img/taskOngoingIcon.png"));
		ImageIcon overdueIcon = new ImageIcon(getClass().getResource(
				"/img/taskOverdueIcon.png"));
		ImageIcon inactiveIcon = new ImageIcon(getClass().getResource(
				"/img/taskInactiveIcon.png"));
		ImageIcon hiRankIcon = new ImageIcon(getClass().getResource(
				"/img/taskHighPriority.png"));
		ImageIcon medRankIcon = new ImageIcon(getClass().getResource(
				"/img/taskNormalPriority.png"));
		ImageIcon lowRankIcon = new ImageIcon(getClass().getResource(
				"/img/taskLowPriority.png"));
		ImageIcon startCalIcon = new ImageIcon(getClass().getResource(
				"/img/startdate.png"));
		ImageIcon nullStartCalIcon = new ImageIcon(getClass().getResource(
				"/img/nostartdate.png"));
		ImageIcon endCalIcon = new ImageIcon(getClass().getResource(
				"/img/enddate.png"));
		ImageIcon nullEndCalIcon = new ImageIcon(getClass().getResource(
				"/img/noenddate.png"));
		Font titleFont = null;
		Font consoleFont = null;
		try {
			titleFont = Font.createFont(Font.TRUETYPE_FONT, getClass()
					.getResourceAsStream("/fonts/Awesome Java.ttf"));
			consoleFont = Font.createFont(Font.TRUETYPE_FONT, getClass()
					.getResourceAsStream("/fonts/Ubuntu.ttf"));
		} catch (Exception e) {

		}
		StreamExternals.init(headerText, doneIcon, notDoneIcon, overdueIcon,
				inactiveIcon, hiRankIcon, medRankIcon, lowRankIcon,
				startCalIcon, nullStartCalIcon, endCalIcon, nullEndCalIcon,
				titleFont, consoleFont);
	}

	private void saveLogFile() throws StreamIOException {
		Calendar now = Calendar.getInstance();
		String logFileName = String.format(StreamConstants.LOGFILE_FORMAT,
				StreamUtil.getDateString(now));
		StreamIO.saveLogFile(StreamLogger.getLogStack(), logFileName);
	}

	//@author A0096529N
	private void initStreamIO(String file) {
		if (!file.endsWith(StreamConstants.SAVEFILE_EXTENSION)) {
			filename = String.format(StreamConstants.SAVEFILE_FORMAT, file);
		} else {
			filename = file;
		}
		StreamIO.setFilename(filename);
	}

	/**
	 * Loads the StreamObject state from a saved file, into the current
	 * streamObject instance. No new instance of StreamObject is created.
	 */
	void load() {
		try {
			HashMap<String, StreamTask> taskMap = new HashMap<String, StreamTask>();
			ArrayList<String> taskList = new ArrayList<String>();

			StreamIO.load(taskMap, taskList);
			streamObject.setTaskList(taskList);
			streamObject.setTaskMap(taskMap);
		} catch (StreamIOException e) {
			e.printStackTrace();
			log(String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage()));
		}
	}

	/**
	 * Saves the current StreamObject state using StreamIO
	 * 
	 * @return result the result of this operation
	 */
	String save() {
		String result = null;
		try {
			HashMap<String, StreamTask> allTasks = streamObject.getTaskMap();
			ArrayList<String> taskList = streamObject.getTaskList();
			StreamIO.save(allTasks, taskList);
			result = "File saved to " + StreamIO.getSaveLocation();
		} catch (StreamIOException e) {
			e.printStackTrace();
			result = String.format(StreamConstants.LogMessage.LOAD_FAILED,
					e.getMessage());
			log(result);
		}

		return result;
	}

	//@author A0118007R
	private void executeInput(CommandType command, Integer index, String content)
			throws StreamModificationException, StreamIOException {
		switch (command) {
			case ADD:
				executeAdd(content);
				refreshUI();
				break;

			case DEL:
				executeDelete(index);
				refreshUI();
				break;

			case DESC:
				executeDescribe(index, content);
				refreshUI();
				break;

			case DUE:
				executeDue(index, content);
				refreshUI();
				break;

			case START:
				executeStartTime(index, content);
				refreshUI();
				break;

			case MODIFY:
				executeModify(index, content);
				refreshUI();
				break;

			case NAME:
				executeName(index, content);
				refreshUI();
				break;

			case RANK:
				executeRank(index, content);
				refreshUI();
				break;

			case MARK:
				executeMark(index, content.trim());
				refreshUI();
				break;

			case TAG:
				executeTag(index, content);
				refreshUI();
				break;

			case UNTAG:
				executeUntag(index, content);
				refreshUI();
				break;

			case FILTER:
				ArrayList<Integer> filterResult = executeFilter(content);
				refreshUI(filterResult, true, true);
				break;

			case CLRSRC:
				refreshUI();
				break;

			case SEARCH:
				ArrayList<Integer> searchResult = executeSearch(content);
				refreshUI(searchResult, true, true);
				break;

			case SORT:
				executeSort(content);
				refreshUI();
				break;

			case VIEW:
				executeView(index);
				break;

			case CLEAR:
				executeClear();
				refreshUI();
				break;

			case UNDO:
				executeUndo();
				break;

			case RECOVER:
				executeRecover(index);
				refreshUI();
				break;

			case DISMISS:
				executeDismiss(index);
				refreshUI();
				break;

			case UNSORT:
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

			case PAGE:
				stui.repopulateTaskView(index);
				break;

			case HELP:
				stui.openHelpBox();
				break;

			case EXIT:
				executeExit();

			default:
				showAndLogError(StreamConstants.LogMessage.CMD_UNKNOWN);
		}
	}

	/**
	 * Adds a new task to the tasks list.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null
	 * </p>
	 * 
	 * @param taskNameWithParams
	 *            - the task name
	 * @throws StreamModificationException
	 *             - if a <b>StreamTask<b> named <i>taskName</i> is already
	 *             present.
	 * @return <strong>String</strong> - the log message
	 */
	private void executeAdd(String taskNameWithParams)
			throws StreamModificationException {

		assertNotNull(taskNameWithParams);
		String[] contents = taskNameWithParams.split(" ");
		String taskName = "";
		ArrayList<String> modifyParams = new ArrayList<String>();

		for (int i = 0; i < contents.length; i++) {
			String word = contents[i];
			if (StreamUtil.isValidAttribute(word)) {
				appendEverything(contents, modifyParams, i);
				break;
			} else {
				taskName = taskName + word + " ";
			}
		}

		taskName = taskName.trim();
		addTaskWithParams(taskName, modifyParams);

		StreamTask task = streamLogic.getTask(taskName);
		stui.setActiveTask(task);

		String result = String.format(StreamConstants.LogMessage.ADD, taskName);
		showAndLogResult(result);
	}

	/**
	 * Appends all trailing strings that are not part of a task's name to be
	 * modified as parameters.
	 * 
	 * @param contents
	 *            - the trailing strings
	 * @param modifyParams
	 *            - the storage for parameter modification
	 * @param i
	 *            - the starting index
	 */
	private void appendEverything(String[] contents,
			ArrayList<String> modifyParams, int i) {
		for (int k = i - 1; k < contents.length; k++) {
			modifyParams.add(contents[k]);
		}
	}

	/**
	 * Deletes a task from the tasks list and then archives it so it can be
	 * recovered by undo process.
	 * <p>
	 * Pre-condition: <i>taskName</i> is not null.
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeDelete(Integer taskIndex)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		assertNotNull(taskName);
		StreamTask deletedTask = streamLogic.getTask(taskName);
		ArrayList<String> order = streamLogic.getTaskList();

		assertNotNull(taskName);
		
		streamLogic.deleteTask(taskName);
		assertNoTask(taskName);

		stackLogic.pushInverseDeleteCommand(deletedTask, order);
		String result = String.format(StreamConstants.LogMessage.DELETE,
				taskName);
		showAndLogResult(result);
	}

	/**
	 * Asserts if the given task name does not exists.
	 * 
	 * @param taskName
	 *            - the task name to be checked whether it exists or not
	 */
	private void assertNoTask(String taskName) {
		assert (!streamLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_DELETED;
	}

	/**
	 * Asserts if the task name is null.
	 * 
	 * @param taskName
	 *            - the task name to be chekced whether it is null
	 */
	private void assertNotNull(String taskName) {
		assert (taskName != null) : StreamConstants.Assertion.NULL_INPUT;
	}

	//@author A0093874N
	/**
	 * Deletes a task from the tasks list permanently.
	 * 
	 * @throws StreamModificationException
	 */
	private void executeDismiss(Integer taskIndex)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);

		assertNotNull(taskName);
		streamLogic.deleteTask(taskName);
		assertNoTask(taskName);

		stackLogic.pushPlaceholderInput();
		String result = String.format(StreamConstants.LogMessage.DELETE,
				taskName);
		showAndLogResult(result);
	}

	/**
	 * Clears all tasks upon receiving the command "clear".
	 * 
	 * @throws StreamModificationException
	 */
	private void executeClear() throws StreamModificationException {
		stackLogic.pushInverseClearCommand(streamLogic.getTaskList(),
				streamLogic.getStreamTaskList());
		streamLogic.clear();
		assert (streamLogic.getNumberOfTasks() == 0) : StreamConstants.Assertion.NOT_CLEARED;
		showAndLogResult(StreamConstants.LogMessage.CLEAR);
	}

	//@author A0118007R
	/**
	 * Prints the task details.
	 * 
	 * @throws StreamModificationException
	 */
	private void executeView(Integer taskIndex)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);

		assertNotNull(taskName);
		StreamTask currentTask = streamLogic.getTask(taskName);
		stui.displayDetails(currentTask);
		stui.setActiveTask(currentTask);

		// This section is contributed by A0093874N
		String result = String
				.format(StreamConstants.LogMessage.VIEW, taskName);
		showAndLogResult(result);
	}

	/**
	 * Adds a description to a task.
	 * <p>
	 * Pre-condition: <i>task, index, description</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeDescribe(Integer taskIndex, String description)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask currentTask = streamLogic.getTask(taskName);
		String oldDescription = currentTask.getDescription();
		currentTask.setDescription(description.equals("null") ? null
				: description);
		stui.setActiveTask(currentTask);

		stackLogic.pushInverseSetDescriptionCommand(taskIndex, oldDescription);
		String result = String.format(StreamConstants.LogMessage.DESC,
				currentTask.getTaskName(), description);
		showAndLogResult(result);
	}

	//@author A0119401U
	/**
	 * Adds a rank to a task.
	 * <p>
	 * Pre-condition: <i>task, index, description</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeRank(Integer taskIndex, String taskRank)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		taskRank = StreamParser.translateRanking(StreamParser
				.parseRanking(taskRank));
		StreamTask currentTask = streamLogic.getTask(taskName);
		String oldRank = currentTask.getRank();
		currentTask.setRank(taskRank);
		stui.setActiveTask(currentTask);

		stackLogic.pushInverseSetRankingCommand(taskIndex, oldRank);
		String result = String.format(StreamConstants.LogMessage.RANK,
				currentTask.getTaskName(), taskRank);
		showAndLogResult(result);
	}

	//@author A0118007R
	/**
	 * Changes the name of a task.
	 * <p>
	 * Pre-condition: <i>task, index, name</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeName(Integer taskIndex, String newTaskName)
			throws StreamModificationException {
		String oldTaskName = streamLogic.getTaskNumber(taskIndex);
		streamLogic.updateTaskName(oldTaskName, newTaskName);
		StreamTask task = streamLogic.getTask(newTaskName);
		stui.setActiveTask(task);

		stackLogic.pushInverseSetNameCommand(taskIndex, oldTaskName);

		String result = String.format(StreamConstants.LogMessage.NAME,
				oldTaskName, newTaskName);
		showAndLogResult(result);
	}

	/**
	 * Modifies various parameters of a task.
	 * <p>
	 * Pre-condition: <i>task, index, specified params</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeModify(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] contents = content.split(" ");
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask currTask = streamLogic.getTask(taskName);

		String inverseCommand = stackLogic.prepareInverseModifyCommand(
				taskName, taskIndex, currTask);

		streamLogic.modifyTaskWithParams(taskName, Arrays.asList(contents));
		stackLogic.pushInverseModifyCommand(inverseCommand);
		stui.setActiveTask(currTask);

		String result = String.format(StreamConstants.LogMessage.MODIFY,
				taskName);
		showAndLogResult(result);
	}

	//@author A0093874N
	/**
	 * Untags some tags that are specified in the input.
	 * <p>
	 * Pre-condition: <i>task, index, tags</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeUntag(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] tags = content.split(" ");
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask task = streamLogic.getTask(taskName);
		ArrayList<String> processedTags = taskLogic.removeTags(task, tags);
		stackLogic.pushInverseUntagCommand(taskIndex, processedTags);
		stui.setActiveTask(task);
		logRemovedTags(taskName, processedTags);
	}

	/**
	 * Tag some tags that are specified in the input.
	 * <p>
	 * Pre-condition: <i>task, index, tags</i> not null
	 * </p>
	 * 
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the log message
	 */
	private void executeTag(Integer taskIndex, String content)
			throws StreamModificationException {
		String[] tags = content.split(" ");
		String taskName = streamLogic.getTaskNumber(taskIndex);
		StreamTask task = streamLogic.getTask(taskName);
		ArrayList<String> processedTags = taskLogic.addTags(task, tags);
		stackLogic.pushInverseAddTagCommand(taskIndex, processedTags);
		stui.setActiveTask(task);
		logAddedTags(taskName, processedTags);
	}

	/**
	 * Reverts ordering after being sorted.
	 * 
	 * @return <strong>String</strong> - the log message
	 */
	private void executeUnsort() {
		streamLogic.setOrdering(stackLogic.popOrder());
		stackLogic.pushPlaceholderInput();
		showAndLogResult(StreamConstants.LogMessage.UNSORT);
	}

	/**
	 * Recovers deleted task from the archive.
	 */
	private void executeRecover(Integer noOfTasksToRecover) {
		stackLogic.pushPlaceholderInput();

		for (int i = 0; i < noOfTasksToRecover; i++) {
			StreamTask task = stackLogic.recoverTask();
			streamLogic.recoverTask(task);
		}
		streamLogic.setOrdering(stackLogic.popOrder());

		String result = String.format(StreamConstants.LogMessage.RECOVER,
				noOfTasksToRecover);
		showAndLogResult(result);
	}

	/**
	 * Execute the undo operation for the last user action
	 */
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

	//@author A0118007R
	/**
	 * Marks the category as the specified category
	 * 
	 * @param taskIndex
	 * @param markType
	 *            - the category to be used for marking
	 * @throws StreamModificationException
	 */
	private void executeMark(Integer taskIndex, String markType)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		MarkType parsedMarkType = StreamParser.parseMarking(markType);
		StreamTask task = streamLogic.getTask(taskName);
		String result = null;
		result = processMarking(taskIndex, markType, parsedMarkType, task);
		stui.setActiveTask(task);
		showAndLogResult(result);
	}

	private String processMarking(Integer taskIndex, String markType,
			MarkType parsedMarkType, StreamTask task) {
		String result;
		switch (parsedMarkType) {
			case DONE:
				result = markAsDone(task, taskIndex);
				break;
			case NOT:
				result = markAsOngoing(task, taskIndex);
				break;
			case INACTIVE:
			case OVERDUE:
				result = "Disallowed marking type: " + markType;
				break;
			default:
				// should not happen, but let's play safe
				result = "Unknown marking type: " + markType;
		}
		return result;
	}

	//@author A0118007R
	private void executeDue(Integer taskIndex, String content)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String result = null;
		result = processDue(content, taskIndex, taskName);
		StreamTask task = streamLogic.getTask(taskName);
		stui.setActiveTask(task);
		showAndLogResult(result);
	}

	private String processDue(String content, int taskIndex, String taskName)
			throws StreamModificationException {
		String result;
		if (content.trim().equals("null")) {
			result = setDueDate(taskName, taskIndex, null);
		} else {
			String due = content;
			due = StreamUtil.parseWithChronic(due);
			Calendar calendar = StreamUtil.parseCalendar(due);
			result = setDueDate(taskName, taskIndex, calendar);
		}
		return result;
	}

	private void executeStartTime(Integer taskIndex, String content)
			throws StreamModificationException {
		String taskName = streamLogic.getTaskNumber(taskIndex);
		String result = processStartTime(content, taskIndex, taskName);
		StreamTask task = streamLogic.getTask(taskName);
		stui.setActiveTask(task);
		showAndLogResult(result);
	}

	private String processStartTime(String content, int taskIndex,
			String taskName) throws StreamModificationException {
		String result;
		if (content.trim().equals("null")) {
			result = setStartDate(taskName, taskIndex, null);
		} else {
			String start = content;
			start = StreamUtil.parseWithChronic(start);
			Calendar calendar = StreamUtil.parseCalendar(start);
			result = setStartDate(taskName, taskIndex, calendar);
		}
		return result;
	}

	//@author A0096529N
	// updated by A0119401U
	private void executeSort(String content) {
		ArrayList<String> oldOrdering = streamLogic.getTaskList();
		stackLogic.pushInverseSortCommand(oldOrdering);

		String result = null;
		String sortBy = null;
		String order = null;
		boolean descending = true;
		if (content != null && content.contains(" ")) {
			sortBy = content.split(" ")[0];
			order = content.split(" ")[1];
		} else {
			sortBy = content == null ? "" : content;
			order = "";
		}
		SortType type = StreamParser.parseSorting(sortBy);
		try {
			descending = StreamParser.getSortingOrder(order);
		} catch (StreamParserException e) {
			// ignore exception
		}

		result = processSorting(sortBy, descending, type);
		showAndLogResult(result);
	}

	private String processSorting(String sortBy, boolean descending,
			SortType type) {
		String result;
		switch (type) {
			case ALPHA:
				result = streamLogic.sortAlpha(descending);
				break;
			case END:
				result = streamLogic.sortDeadline(descending);
				break;
			case START:
				result = streamLogic.sortStartTime(descending);
				break;
			case TIME:
				result = streamLogic.sortTime(descending);
				break;
			case IMPORTANCE:
				result = streamLogic.sortImportance(descending);
				break;
			default:
				result = "Unknown sort category \"" + sortBy + "\"";
				break;
		}
		return result;
	}

	private void executeExit() {
		showAndLogResult(StreamConstants.Message.THANK_YOU);
		save();
		try {
			saveLogFile();
		} catch (StreamIOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		System.exit(0);
	}

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
	 */
	private ArrayList<Integer> executeSearch(String content) {
		assertNotNull(content);
		ArrayList<Integer> searchResult = streamLogic.findTasks(content);

		String result = String.format(StreamConstants.LogMessage.SEARCH,
				content, searchResult.size());
		showAndLogResult(result);

		return searchResult;
	}

	//@author A0093874N
	private ArrayList<Integer> executeFilter(String content) {
		assertNotNull(content);
		ArrayList<Integer> filterResult = streamLogic.filterTasks(content);

		String result = String.format(StreamConstants.LogMessage.FILTER,
				content, filterResult.size());
		showAndLogResult(result);

		return filterResult;
	}

	//@author A0118007R
	private void addTaskWithParams(String taskName,
			ArrayList<String> modifyParams) throws StreamModificationException {

		streamLogic.addTask(taskName);
		assert (streamLogic.hasTask(taskName)) : StreamConstants.Assertion.NOT_ADDED;
		stackLogic.pushInverseAddCommand(streamLogic.getNumberOfTasks());
		processParameterAddition(taskName, modifyParams);
	}

	private void processParameterAddition(String taskName,
			ArrayList<String> modifyParams) throws StreamModificationException {
		if (modifyParams.size() > 0) {
			streamLogic.modifyTaskWithParams(taskName, modifyParams);
		}
	}

	//@author A0119401U
	/**
	 * Set the due date of the selected task
	 * 
	 * @throws StreamModificationException
	 */
	private String setDueDate(String taskName, int taskIndex,
			Calendar newDeadline) throws StreamModificationException {
		StreamTask task = streamLogic.getTask(taskName);
		Calendar deadline = task.getDeadline();
		Calendar startTime = task.getStartTime();
		return processDueDate(taskIndex, newDeadline, task, deadline, startTime);

	}

	private String processDueDate(int taskIndex, Calendar newDeadline,
			StreamTask task, Calendar deadline, Calendar startTime) {
		if (StreamUtil.isValidDeadline(newDeadline, startTime)) {
			stackLogic.pushInverseDueCommand(taskIndex, deadline);
			// This section is contributed by A0093874N
			return taskLogic.setDeadline(task, newDeadline);
			//
		} else {
			return StreamConstants.ExceptionMessage.ERR_DEADLINE_BEFORE_STARTTIME;
		}
	}

	/**
	 * Set the start date of the selected task
	 * 
	 * @throws StreamModificationException
	 */
	private String setStartDate(String taskName, int taskIndex,
			Calendar newStartTime) throws StreamModificationException {
		StreamTask currentTask = streamLogic.getTask(taskName);
		Calendar currentStartTime = currentTask.getStartTime();
		Calendar deadline = currentTask.getDeadline();
		return processStartTime(taskIndex, newStartTime, currentTask,
				currentStartTime, deadline);
	}

	private String processStartTime(int taskIndex, Calendar newStartTime,
			StreamTask currentTask, Calendar currentStartTime, Calendar deadline) {
		if (StreamUtil.isValidStartTime(deadline, newStartTime)) {
			stackLogic.pushInverseStartCommand(taskIndex, currentStartTime);
			return taskLogic.setStartTime(currentTask, newStartTime);
		} else {
			return StreamConstants.ExceptionMessage.ERR_STARTTIME_AFTER_DEADLINE;
		}
	}

	/**
	 * Mark the selected task as done
	 * 
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

	//@author A0118007R
	/**
	 * Mark the selected task as ongoing
	 * 
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

	//@author A0093874N
	private void refreshUI(boolean isReset, boolean isSearching) {
		stui.resetAvailableTasks(streamLogic.getIndices(),
				streamLogic.getStreamTaskList(streamLogic.getIndices()), isReset,
				isSearching);
	}

	private void refreshUI(ArrayList<Integer> index, boolean isReset,
			boolean isSearching) {
		stui.resetAvailableTasks(index, streamLogic.getStreamTaskList(index),
				isReset, isSearching);
	}

	private void logAddedTags(String taskName, ArrayList<String> tagsAdded) {
		if (!tagsAdded.isEmpty()) {
			showAndLogResult(String.format(
					StreamConstants.LogMessage.TAGS_ADDED, taskName,
					StreamUtil.listDownArrayContent(tagsAdded, ", ")));
		} else {
			showAndLogResult(StreamConstants.LogMessage.NO_TAGS_ADDED);
		}
	}

	private void logRemovedTags(String taskName, ArrayList<String> tagsRemoved) {
		if (!tagsRemoved.isEmpty()) {
			showAndLogResult(String.format(
					StreamConstants.LogMessage.TAGS_REMOVED, taskName,
					StreamUtil.listDownArrayContent(tagsRemoved, ", ")));
		} else {
			showAndLogResult(StreamConstants.LogMessage.NO_TAGS_REMOVED);
		}
	}

	private void showAndLogResult(String logMessage) {
		showAndLogResult(logMessage, logMessage);
	}

	//@author A0118007R
	private void showAndLogError(String errorMessage) {
		showAndLogError(errorMessage, errorMessage);
	}

	/*
	 * These two can be used if the log message to be documented and to be
	 * displayed are different (especially for exception/error messages)
	 */

	//@author A0093874N
	private void showAndLogResult(String logMessageForDoc,
			String logMessageForUser) {
		stui.log(logMessageForUser, false);
		log(StreamUtil.showAsTerminalResponse(logMessageForDoc));
	}

	private void showAndLogError(String errorMessageForDoc,
			String errorMessageForUser) {
		stui.log(errorMessageForUser, true);
		log(StreamUtil.showAsTerminalResponse(errorMessageForDoc));
	}

	//@author A0096529N
	private void log(String message) {
		logger.log(LogLevel.DEBUG, message);
	}

	//@author A0093874N
	private void processInput(String input) {
		try {
			executeUserInput(input);
		} catch (AssertionError e) {
			processAssertionError(e);
		} catch (StreamParserException e) {
			log(String.format(StreamConstants.LogMessage.ERRORS, e.getClass()
					.getSimpleName(), e.getMessage()));
			processAndShowParserExceptionMessage(e);
		} catch (Exception e) {
			log(String.format(StreamConstants.LogMessage.ERRORS, e.getClass()
					.getSimpleName(), e.getMessage()));
			processAndShowExceptionMessage(e);
		}
	}

	private void processAndShowExceptionMessage(Exception e) {
		showAndLogError(String.format(
				StreamConstants.LogMessage.UNEXPECTED_ERROR, e.getClass()
						.getSimpleName() + " " + e.getMessage()));
	}

	private void processAndShowParserExceptionMessage(StreamParserException e) {
		if (e.getMessage().equals("Empty Input")) {
			showAndLogError(String.format(
					StreamConstants.LogMessage.EMPTY_INPUT_ERROR, e.getClass()
							.getSimpleName() + " " + e.getMessage()));
		} else {
			showAndLogError(String.format(
					StreamConstants.LogMessage.PARSER_ERROR, e.getClass()
							.getSimpleName() + " " + e.getMessage()));
		}
	}

	private void processAssertionError(AssertionError e) {
		log(String.format(StreamConstants.LogMessage.ERRORS, "AssertionError",
				e.getMessage()));
		showAndLogError(String.format(
				StreamConstants.LogMessage.UNEXPECTED_ERROR, e.getMessage()));
	}

	private void executeUserInput(String input) throws StreamParserException,
			StreamModificationException, StreamIOException {
		parser.interpretCommand(input, streamLogic.getNumberOfTasks());
		CommandType command = parser.getCommandType();
		Integer index = parser.getCommandIndex();
		String content = parser.getCommandContent();
		executeInput(command, index, content);
	}

	/*
	 * Inputs like unsort, recover, and dismiss cannot be triggered by user;
	 * only can be triggered by the machine as part of undo.
	 */
	private Boolean isRestrictedInput(String input) {
		if (input.length() >= 6) {
			return checkRestrictedInput(input);
		} else {
			return false;
		}
	}

	private Boolean checkRestrictedInput(String input) {
		if (input.substring(0, 6).equals("unsort")) {
			return true;
		} else {
			return input.length() >= 7
					&& (input.substring(0, 7).equals("recover") || input
							.substring(0, 7).equals("dismiss"));
		}
	}

	public void filterAndProcessInput(String input) {
		assert (input != null) : String.format(
				StreamConstants.LogMessage.ERRORS, "AssertionError",
				StreamConstants.Assertion.NULL_INPUT);

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

	//@author A0118007R-unused
	/**
	 * @deprecated - use resetUI instead
	 */
	@SuppressWarnings("unused")
	private void resetTasks() {
		stui.resetAvailableTasks(streamLogic.getIndices(),
				streamLogic.getStreamTaskList(streamLogic.getIndices()), true,
				false);
	}

	/**
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void printTasks() {
		System.out.println(" ");
		System.out.println("Your current tasks: ");
		int numberOfTasks = streamLogic.getNumberOfTasks();

		for (int i = 1; i <= numberOfTasks; i++) {
			System.out.println(i + ". " + streamLogic.getTaskNumber(i));
		}
	}

	/**
	 * @deprecated for now
	 */
	@SuppressWarnings("unused")
	private void showAndLogCommand(String command) {
		String commandReceived = String.format(
				StreamConstants.LogMessage.CMD_RECEIVED, command);
		System.out.println(commandReceived);
		log(StreamUtil.showAsTerminalResponse(commandReceived));
	}

	/**
	 * Modify a task's description This method is just to differentiate the set
	 * new description and modify description part
	 * 
	 * @deprecated by A0093874N. Can be un-deprecated if we find a use for it.
	 */
	@SuppressWarnings("unused")
	private void changeDescription(String task, int index, String newDescription) {
		try {
			// executeDescribe(newDescription);
		} catch (Exception e) {

		}
	}

	/**
	 * <p>
	 * Changes a task's name.
	 * </p>
	 * <p>
	 * Pre-condition: <i>oldName, newName, index</i> not null
	 * </p>
	 * 
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

	/**
	 * @deprecated the powered-up parser now handles this already
	 */
	@SuppressWarnings("unused")
	private void removeIndex(String[] tags, String[] tagsToBeAdded) {
		for (int i = 1; i < tags.length; i++) {
			tagsToBeAdded[i - 1] = tags[i];
		}
	}
}