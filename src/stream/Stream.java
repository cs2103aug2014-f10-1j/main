package stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import model.StreamObject;
import model.StreamTask;
import parser.StreamParser;
import parser.StreamParser.CommandType;
import ui.StreamUI;
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
 * @version v0.2
 * @author Wilson Kurniawan, John Kevin Tjahjadi, Steven Khong Wai How, Jiang
 *         Shenhao
 */

// TODO all the null assertions shouldn't be there. Rather, StreamParser should
// check for null input before passing the commands to the logic to process.
// Looking forward to the parser being able to do it!

public class Stream {

	StreamObject stobj;
	private StreamUI stui;
	private StreamParser parser;
	private StreamLogger logger;

	private String filename;
	private Stack<String> inputStack;
	private Stack<StreamTask> dumpedTasks;
	private Stack<ArrayList<String>> orderingStack;

	private static final Scanner INPUT_SCANNER = new Scanner(System.in);

	private final String[] validParameters = { "name", "desc", "due", "by",
			"tag", "untag" };

	// author ??? refactored by A0118007R

	public Stream(String file) {
		initStreamLogger();
		initStreamIO(file);
		initializeStream();
		load();
		resetTasks();
	}

	private void resetTasks() {
		stui.resetAvailableTasks(stobj.getCounter(),
				stobj.getStreamTaskList(stobj.getCounter()), true, false);
	}

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
			filename = String.format(StreamUtil.PARAM_SAVEFILE, file);
		} else {
			filename = file;
		}
		StreamIO.setSaveLocation(filename);
	}

	// @author A0096529N
	private void initStreamLogger() {
		logger = StreamLogger.init(StreamUtil.COMPONENT_STREAM);
	}

	// @author A0096529N
	void load() {
		try {
			HashMap<String, StreamTask> allTasks = new HashMap<String, StreamTask>();
			ArrayList<String> taskList = new ArrayList<String>();

			StreamIO.load(allTasks, taskList);
			stobj = new StreamObject(allTasks, taskList);

		} catch (StreamIOException e) {
			log(String.format(StreamUtil.LOG_LOAD_FAILED, e.getMessage()));
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
			result = String.format(StreamUtil.LOG_LOAD_FAILED, e.getMessage());
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
		assert (taskName != null) : StreamUtil.FAIL_NULL_INPUT;
		return stobj.hasTask(taskName);
	}

	// @author A0118007R
	boolean isValidParameter(String param) {
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
		assert (taskName != null) : StreamUtil.FAIL_NULL_INPUT;
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

		return String.format(StreamUtil.LOG_ADD, nameOfTask);

	}

	// @author A0118007R

	private void addTaskAndProcessParameters(String nameOfTask,
			boolean nameFound, ArrayList<String> modifyParams)
			throws StreamModificationException {

		stobj.addTask(nameOfTask);
		assert (stobj.hasTask(nameOfTask)) : StreamUtil.FAIL_NOT_ADDED;

		inputStack.push(String.format(StreamUtil.CMD_DISMISS,
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

	private void processParameterModification(String command, String contents,
			StreamTask task) throws StreamModificationException {
		contents = contents.trim();
		String taskName = task.getTaskName();
		switch (command) {
			case "name":
				setName(taskName, contents);
				break;
			case "desc":
				task.setDescription(contents);
				break;
			case "due":
			case "by":
				if (contents.equals("null")) {
					task.setDeadline(null);
				} else {
					Calendar due = StreamUtil.parseCalendar(contents);
					task.setDeadline(due);
				}
				break;
			case "tag":
				String[] newTags = contents.split(" ");
				ArrayList<String> tagsAdded = new ArrayList<String>();
				ArrayList<String> tagsNotAdded = new ArrayList<String>();
				addTags(newTags, taskName, tagsAdded, tagsNotAdded);
				break;
			case "untag":
				String[] tagsToRemove = contents.split(" ");
				ArrayList<String> tagsRemoved = new ArrayList<String>();
				ArrayList<String> tagsNotRemoved = new ArrayList<String>();
				removeTags(tagsToRemove, taskName, tagsRemoved, tagsNotRemoved);
				break;
		// TODO include marking also, if possible
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
		assert (taskName != null) : StreamUtil.FAIL_NULL_INPUT;
		stobj.deleteTask(taskName);
		assert (!stobj.hasTask(taskName)) : StreamUtil.FAIL_NOT_DELETED;
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
		assert (taskName != null) : StreamUtil.FAIL_NULL_INPUT;
		StreamTask deletedTask = stobj.getTask(taskName);
		ArrayList<String> order = stobj.getOrdering();
		orderingStack.push(order);
		dismissTask(taskName);

		// This section is contributed by A0093874N
		dumpedTasks.push(deletedTask);
		inputStack.push(String.format(StreamUtil.CMD_RECOVER, 1));
		return String.format(StreamUtil.LOG_DELETE, taskName);
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
		orderingStack.push(stobj.getOrdering());
		clear(noOfTasks);
		assert (stobj.getNumberOfTasks() == 0) : StreamUtil.FAIL_NOT_CLEARED;
		inputStack.push(String.format(StreamUtil.CMD_RECOVER, noOfTasks));
	}

	private void clear(int noOfTasks) throws StreamModificationException {
		for (int i = 0; i < noOfTasks; i++) {
			deleteTask(stobj.getTaskNames().get(0));
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
		assert (taskName != null) : StreamUtil.FAIL_NULL_INPUT;
		StreamTask currentTask = stobj.getTask(taskName);
		stui.displayDetails(currentTask);

		// This section is contributed by A0093874N
		return String.format(StreamUtil.LOG_VIEW, taskName);
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
		assert (oldName != null && newName != null) : StreamUtil.FAIL_NULL_INPUT;
		stobj.updateTaskName(oldName, newName);

		// This section is contributed by A0093874N
		return String.format(StreamUtil.LOG_NAME, oldName, newName);
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
	String setDescription(String task, int index, String description)
			throws StreamModificationException {
		StreamTask currentTask = stobj.getTask(task);
		String oldDescription = currentTask.getDescription();
		currentTask.setDescription(description);

		// This section is contributed by A0093874N
		inputStack.push(String.format(StreamUtil.CMD_DESC, index,
				oldDescription));
		return String.format(StreamUtil.LOG_DESC, currentTask.getTaskName(),
				description);
		//
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
		if (calendar == null) {
			stobj.setNullDeadline(taskName);
			return String.format(StreamUtil.LOG_DUE_NEVER, taskName);
		} else {
			String parsedCalendar = StreamUtil.getCalendarWriteUp(calendar);
			stobj.setDueTime(taskName, calendar);
			return String.format(StreamUtil.LOG_DUE, taskName, parsedCalendar);
		}
	}

	private void pushInverseDueCommand(int taskIndex, Calendar currentDeadline) {
		if (currentDeadline == null) {
			inputStack.push(String
					.format(StreamUtil.CMD_DUE, taskIndex, "null"));
		} else {
			inputStack.push(String.format(
					StreamUtil.CMD_DUE,
					taskIndex,
					currentDeadline.get(Calendar.DATE) + "/"
							+ (currentDeadline.get(Calendar.MONTH) + 1) + "/"
							+ currentDeadline.get(Calendar.YEAR)));
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
	private String markAsDone(String task, int index)
			throws StreamModificationException {
		stobj.markTaskAsDone(task);
		// This section is contributed by A0118007R
		inputStack.push(String.format(StreamUtil.CMD_MARK_NOT_DONE, index));
		//
		// This section is contributed by A0093874N
		return String.format(StreamUtil.LOG_MARK, task, "done");
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
	private String markAsOngoing(String task, int index)
			throws StreamModificationException {
		stobj.markTaskAsOngoing(task);
		//
		inputStack.push(String.format(StreamUtil.CMD_MARK_DONE, index));
		//
		// This section is contributed by A0093874N
		return String.format(StreamUtil.LOG_MARK, task, "ongoing");
		//
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
		assert (keyphrase != null) : StreamUtil.FAIL_NULL_INPUT;
		// modified by A0093874N
		return stobj.findTasks(keyphrase);
	}

	// @author A0093874N

	private ArrayList<Integer> filter(String criteria) {
		assert (criteria != null) : StreamUtil.FAIL_NULL_INPUT;
		// modified by A0093874N
		return stobj.filterTasks(criteria);
	}

	// @author A0118007R
	/**
	 * @deprecated
	 */
	private void printTasks() {
		System.out.println(" ");
		System.out.println("Your current tasks: ");
		ArrayList<String> myTasks = stobj.getTaskNames();
		int numberOfTasks = myTasks.size();

		for (int i = 1; i <= numberOfTasks; i++) {
			System.out.println(i + ". " + myTasks.get(i - 1));
		}
	}

	// @author A0118007R

	/**
	 * @deprecated for now
	 */
	private void showAndLogCommand(String command) {
		String commandReceived = String.format(StreamUtil.LOG_CMD_RECEIVED,
				command);
		System.out.println(commandReceived);
		log(StreamUtil.showAsTerminalResponse(commandReceived));
	}

	// @author A0093874N

	/**
	 * 
	 */
	private void showAndLogResult(String logMessage) {
		stui.log(logMessage, false);
		log(StreamUtil.showAsTerminalResponse(logMessage));
	}

	// @author A0118007R

	private void showAndLogError(String errorMessage) {
		stui.log(errorMessage, true);
		log(StreamUtil.showAsTerminalResponse(errorMessage));
	}

	// @author A0096529N
	private void log(String message) {
		logger.log(LogLevel.DEBUG, message);
	}

	// @author A0093874N
	private void logCommand(String cmd) {
		log(StreamUtil.showAsTerminalResponse(String.format(
				StreamUtil.LOG_CMD_RECEIVED, cmd)));
	}

	// @author A0093874N

	private void saveLogFile() throws IOException {
		Calendar now = Calendar.getInstance();
		String day = StreamUtil.addZeroToTime(now.get(Calendar.DAY_OF_MONTH));
		String mth = StreamUtil.addZeroToTime(now.get(Calendar.MONTH));
		Integer yr = now.get(Calendar.YEAR);
		String hr = StreamUtil.addZeroToTime(now.get(Calendar.HOUR_OF_DAY));
		String min = StreamUtil.addZeroToTime(now.get(Calendar.MINUTE));
		String sec = StreamUtil.addZeroToTime(now.get(Calendar.SECOND));
		String logFileName = String.format(StreamUtil.PARAM_LOGFILE, filename,
				day, mth, yr.toString().substring(2), hr, min, sec);
		StreamIO.saveLogFile(StreamLogger.getLogStack(), logFileName);
	}

	// @author A0118007R

	private void executeInput(CommandType command, String content)
			throws StreamModificationException, IOException {

		String[] contents;
		String[] tags;
		String taskName;
		String logMessage;
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
				taskName = stobj.getTaskNames().get(taskIndex - 1);
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
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				String description = contents[1];
				logMessage = setDescription(taskName, taskIndex, description);
				showAndLogResult(logMessage);
				break;

			case DUE:
				logCommand("DUE");
				contents = content.split(" ", 2);

				taskIndex = Integer.parseInt(contents[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);

				logMessage = checkForNullAndProcessInput(contents, taskName,
						taskIndex);
				stui.resetAvailableTasks(stobj.getCounter(),
						stobj.getStreamTaskList(stobj.getCounter()), false,
						false);
				showAndLogResult(logMessage);
				break;

			case MODIFY:
				logCommand("MODIFY");
				contents = content.split(" ");
				taskIndex = Integer.parseInt(contents[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);

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

				showAndLogResult(String.format(StreamUtil.LOG_MODIFY, taskName));
				break;

			case NAME:
				logCommand("NAME");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				String oldTaskName = stobj.getTaskNames().get(taskIndex - 1);
				String newTaskName = contents[1];
				logMessage = setName(oldTaskName, newTaskName);
				inputStack.push(String.format(StreamUtil.CMD_NAME, taskIndex,
						oldTaskName));
				stui.resetAvailableTasks(stobj.getCounter(),
						stobj.getStreamTaskList(stobj.getCounter()), false,
						false);
				showAndLogResult(String.format(StreamUtil.LOG_NAME,
						oldTaskName, newTaskName));
				break;

			case MARK:
				logCommand("MARK");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				String markType = contents[1].trim();
				mark(taskName, taskIndex, markType);
				stui.resetAvailableTasks(stobj.getCounter(),
						stobj.getStreamTaskList(stobj.getCounter()), false,
						false);
				break;

			case TAG:
				logCommand("TAG");
				tags = content.split(" ");
				ArrayList<String> tagsAdded = new ArrayList<String>();
				ArrayList<String> tagsNotAdded = new ArrayList<String>();
				taskIndex = Integer.parseInt(tags[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				addTags(tags, taskName, tagsAdded, tagsNotAdded);
				pushTaggingIntoInputStack(taskIndex, tagsAdded);
				logTagsAdded(taskName, tagsAdded, tagsNotAdded);
				break;

			case UNTAG:
				logCommand("UNTAG");
				tags = content.split(" ");
				ArrayList<String> tagsRemoved = new ArrayList<String>();
				ArrayList<String> tagsNotRemoved = new ArrayList<String>();
				taskIndex = Integer.parseInt(tags[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				removeTags(tags, taskName, tagsRemoved, tagsNotRemoved);
				pushUntaggingIntoInputStack(taskIndex, tagsRemoved);
				logTagsRemoved(taskName, tagsRemoved, tagsNotRemoved);
				break;

			case FILTER:
				logCommand("FILTER");
				ArrayList<Integer> filterResult = filter(content);
				stui.resetAvailableTasks(filterResult,
						stobj.getStreamTaskList(filterResult), true, true);
				showAndLogResult(String.format(StreamUtil.LOG_FILTER, content,
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
				showAndLogResult(String.format(StreamUtil.LOG_SEARCH, content,
						searchResult.size()));
				break;

			case VIEW:
				logCommand("VIEW");
				taskIndex = Integer.parseInt(content);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				logMessage = printDetails(taskName);
				showAndLogResult(logMessage);
				break;

			case CLEAR:
				logCommand("CLEAR");
				clearAllTasks();
				stui.resetAvailableTasks(stobj.getCounter(),
						stobj.getStreamTaskList(stobj.getCounter()), false,
						false);
				showAndLogResult(StreamUtil.LOG_CLEAR);
				break;

			case UNDO:
				logCommand("UNDO");
				processUndo();
				break;

			case RECOVER:
				logCommand("RECOVER");
				int noOfTasksToRecover = Integer.parseInt(content);
				recover(noOfTasksToRecover);
				showAndLogResult(String.format(StreamUtil.LOG_RECOVER,
						noOfTasksToRecover));
				stobj.setOrdering(orderingStack.pop());
				stui.resetAvailableTasks(stobj.getCounter(),
						stobj.getStreamTaskList(stobj.getCounter()), false,
						false);
				inputStack.push("some fake input to be popped");
				break;

			case DISMISS:
				logCommand("DISMISS");
				taskIndex = Integer.parseInt(content);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				dismissTask(taskName);
				showAndLogResult(String.format(StreamUtil.LOG_DELETE, taskName));
				stui.resetAvailableTasks(stobj.getCounter(),
						stobj.getStreamTaskList(stobj.getCounter()), false,
						false);
				inputStack.push("some fake input to be popped");
				break;

			case EXIT:
				logCommand("EXIT");
				System.out.println(StreamUtil.MSG_THANK_YOU);
				saveLogFile();
				System.exit(0);

			default:
				showAndLogError(StreamUtil.LOG_CMD_UNKNOWN);
		}
	}

	private void recover(int noOfTasksToRecover) {
		for (int i = 0; i < noOfTasksToRecover; i++) {
			StreamTask task = dumpedTasks.pop();
			stobj.recoverTask(task);
		}
	}

	private void processUndo() {
		if (inputStack.isEmpty()) {
			showAndLogResult(StreamUtil.LOG_UNDO_FAIL);
		} else {
			String undoneInput = inputStack.pop();
			showAndLogResult(StreamUtil.LOG_UNDO_SUCCESS);
			processInput(undoneInput);

			/*
			 * VERY IMPORTANT because almost all inputs will add its counterpart
			 * to the inputStack. If not popped, the undo process will be
			 * trapped between just two processes.
			 */
			inputStack.pop();
		}
	}

	private void pushUntaggingIntoInputStack(int taskIndex,
			ArrayList<String> tagsRemoved) {
		if (tagsRemoved.size() != 0) {
			inputStack.push(String.format(StreamUtil.CMD_TAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsRemoved, " ")));
		}
	}

	private void pushTaggingIntoInputStack(int taskIndex,
			ArrayList<String> tagsAdded) {
		if (tagsAdded.size() != 0) {
			inputStack.push(String.format(StreamUtil.CMD_UNTAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsAdded, " ")));
		}
	}

	private void mark(String taskName, int taskIndex, String markType)
			throws StreamModificationException {
		String logMessage;
		/*
		 * TODO make the markType more flexible. maybe "finished", "not done",
		 * "not finished", ...
		 */
		if (markType.equals("done")) {
			logMessage = markAsDone(taskName, taskIndex);
			showAndLogResult(logMessage);
		} else if (markType.equals("ongoing")) {
			logMessage = markAsOngoing(taskName, taskIndex);
			showAndLogResult(logMessage);
		} else {
			logMessage = "Unknown marking type: " + markType;
			showAndLogError(logMessage);
		}
	}

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

	private void addModificationParameters(String[] contents,
			ArrayList<String> modifyParams) {
		for (int i = 1; i < contents.length; i++) {
			modifyParams.add(contents[i]);
		}
	}

	private String buildInverseCommand(String taskName, int taskIndex,
			StreamTask currTask) {
		String inverseCommand = "modify " + taskIndex + " name " + taskName
				+ " ";

		inverseCommand = buildModifyDescription(currTask, inverseCommand);
		inverseCommand = buildModifyDeadline(currTask, inverseCommand);
		return inverseCommand;
	}

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

	private String checkForNullAndProcessInput(String[] contents,
			String taskName, int taskIndex) throws StreamModificationException {
		String logMessage;
		if (contents[1].trim().equals("null")) {
			logMessage = setDueDate(taskName, taskIndex, null);
		} else {
			String due = contents[1];
			Calendar calendar = StreamUtil.parseCalendar(due);
			logMessage = setDueDate(taskName, taskIndex, calendar);
		}
		return logMessage;
	}

	// all methods from the previous tag until are refactored by A0118007R

	// @author A0093874N

	private String compareTagged(ArrayList<String> oldTags,
			ArrayList<String> newTags) {
		String inverseTag = "tag ";
		inverseTag = buildInverseTag(oldTags, newTags, inverseTag);
		return inverseTag;
	}

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

	private void logTagsAdded(String taskName, ArrayList<String> tagsAdded,
			ArrayList<String> tagsNotAdded) {
		logAddedTags(taskName, tagsAdded);
		logUnaddedTags(taskName, tagsNotAdded);
	}

	private void logUnaddedTags(String taskName, ArrayList<String> tagsNotAdded) {
		if (!tagsNotAdded.isEmpty()) {
			log(String.format(StreamUtil.LOG_TAGS_NOT_ADDED, taskName,
					StreamUtil.listDownArrayContent(tagsNotAdded, ", ")));
		}
	}

	private void logAddedTags(String taskName, ArrayList<String> tagsAdded) {
		if (!tagsAdded.isEmpty()) {
			showAndLogResult(String.format(StreamUtil.LOG_TAGS_ADDED, taskName,
					StreamUtil.listDownArrayContent(tagsAdded, ", ")));
		} else {
			showAndLogResult(StreamUtil.LOG_NO_TAGS_ADDED);
		}
	}

	// @author A0093874N

	private void logTagsRemoved(String taskName, ArrayList<String> tagsRemoved,
			ArrayList<String> tagsNotRemoved) {
		logRemovedTags(taskName, tagsRemoved);
		logUnremovedTags(taskName, tagsNotRemoved);
	}

	private void logUnremovedTags(String taskName,
			ArrayList<String> tagsNotRemoved) {
		if (!tagsNotRemoved.isEmpty()) {
			log(String.format(StreamUtil.LOG_TAGS_NOT_REMOVED, taskName,
					StreamUtil.listDownArrayContent(tagsNotRemoved, ", ")));
		}
	}

	private void logRemovedTags(String taskName, ArrayList<String> tagsRemoved) {
		if (!tagsRemoved.isEmpty()) {
			showAndLogResult(String.format(StreamUtil.LOG_TAGS_REMOVED,
					taskName,
					StreamUtil.listDownArrayContent(tagsRemoved, ", ")));
		} else {
			showAndLogResult(StreamUtil.LOG_NO_TAGS_REMOVED);
		}
	}

	// @author A0118007R
	// TODO may want to move this to Util <later>

	public boolean isInteger(String x) {
		try {
			Integer.parseInt(x);
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	// @author A0093874N

	private void addTags(String[] tags, String taskName,
			ArrayList<String> tagsAdded, ArrayList<String> tagsNotAdded)
			throws StreamModificationException {
		int start = 0;
		processAddingTags(tags, taskName, tagsAdded, tagsNotAdded, start);
	}

	private void processAddingTags(String[] tags, String taskName,
			ArrayList<String> tagsAdded, ArrayList<String> tagsNotAdded,
			int start) throws StreamModificationException {
		if (isInteger(tags[0])) {
			start = 1;
		}
		for (int i = start; i < tags.length; i++) {
			if (stobj.addTag(taskName, tags[i])) {
				tagsAdded.add(tags[i]);
			} else {
				tagsNotAdded.add(tags[i]);
			}
		}
	}

	// @author A0093874N

	private void removeTags(String[] tags, String taskName,
			ArrayList<String> tagsRemoved, ArrayList<String> tagsNotRemoved)
			throws StreamModificationException {
		int start = 0;
		if (isInteger(tags[0])) {
			start = 1;
		}
		processRemoveTags(tags, taskName, tagsRemoved, tagsNotRemoved, start);
	}

	private void processRemoveTags(String[] tags, String taskName,
			ArrayList<String> tagsRemoved, ArrayList<String> tagsNotRemoved,
			int start) throws StreamModificationException {
		for (int i = start; i < tags.length; i++) {
			if (stobj.removeTag(taskName, tags[i])) {
				tagsRemoved.add(tags[i]);
			} else {
				tagsNotRemoved.add(tags[i]);
			}
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
			showAndLogError(String.format(StreamUtil.LOG_ERRORS,
					"AssertionError", e.getMessage()));
		} catch (Exception e) {
			showAndLogError(String.format(StreamUtil.LOG_ERRORS, e.getClass()
					.getSimpleName(), e.getMessage()));
		}
	}

	// @author A0093874N

	public void filterAndProcessInput(String input) {
		if (input == null) {
			showAndLogError(String.format(StreamUtil.LOG_ERRORS,
					"AssertionError", StreamUtil.FAIL_NULL_INPUT));
		} else {
			log(input);
			if (input.length() >= 7
					&& (input.substring(0, 7).equals("recover") || input
							.substring(0, 7).equals("dismiss"))) {
				showAndLogError(StreamUtil.LOG_CMD_UNKNOWN);
			} else {
				processInput(input);
				save();
			}
		}
	}

	// @author A0093874N

	/**
	 * @deprecated now using the GUI console to receive input
	 */
	private void doRoutineProcess() {
		printTasks();
		System.out
				.println("========================================================");
		System.out.print("Enter Command: ");
		String input = INPUT_SCANNER.nextLine();
		log(input);
		if (input.length() >= 7
				&& (input.substring(0, 7).equals("recover") || input.substring(
						0, 7).equals("dismiss"))) {
			/*
			 * input commands "recover" and "dismiss" is for the undo process
			 * and cannot be accessed directly by user
			 */
			showAndLogResult(StreamUtil.LOG_CMD_UNKNOWN);
		} else {
			processInput(input);
		}
		save();
	}

	public static void main(String[] args) {
		new Stream(StreamUtil.PARAM_FILENAME);
	}

}