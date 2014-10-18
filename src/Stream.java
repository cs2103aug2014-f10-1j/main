import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import model.StreamObject;
import model.StreamTask;
import parser.StreamParser;
import parser.StreamParser.CommandType;
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

	private StreamObject stobj;
	private StreamIO stio;
	private StreamParser parser;

	private String filename;
	private Stack<String> inputStack;
	private Stack<StreamTask> dumpedTasks;
	private Stack<ArrayList<String>> orderingStack;
	private List<String> logMessages;

	private static final Scanner INPUT_SCANNER = new Scanner(System.in);

	public Stream(String file) {
		stio = new StreamIO(String.format(StreamUtil.PARAM_SAVEFILE, file));
		parser = new StreamParser();

		filename = file;
		inputStack = new Stack<String>();
		dumpedTasks = new Stack<StreamTask>();
		orderingStack = new Stack<ArrayList<String>>();
		logMessages = new ArrayList<String>();

		load();
	}

	//@author A0096529N
	private void load() {
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

	//@author A0096529N
	private String save() {
		String result = null;
		try {
			HashMap<String, StreamTask> allTasks = stobj.getAllTasks();
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

	// @author A0093874N

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
	private String addTask(String taskName) throws StreamModificationException {
		assert (taskName != null) : StreamUtil.FAIL_NULL_INPUT;
		stobj.addTask(taskName);
		assert (stobj.hasTask(taskName)) : StreamUtil.FAIL_NOT_ADDED;
		inputStack.push(String.format(StreamUtil.CMD_DISMISS,
				stobj.getNumberOfTasks()));
		return String.format(StreamUtil.LOG_ADD, taskName);
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
		for (int i = 0; i < noOfTasks; i++) {
			deleteTask(stobj.getTaskNames().get(0));
			/*
			 * This is because we don't want the "recover 1". Rather, we'll
			 * replace with "recover noOfTasks" at the end of the process.
			 */
			inputStack.pop();
			orderingStack.pop();
		}
		assert (stobj.getNumberOfTasks() == 0) : StreamUtil.FAIL_NOT_CLEARED;
		inputStack.push(String.format(StreamUtil.CMD_RECOVER, noOfTasks));
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
		currentTask.printTaskDetails();

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

	private String setName(String oldName, String newName, Integer index)
			throws StreamModificationException {
		assert (oldName != null && newName != null && index != null) : StreamUtil.FAIL_NULL_INPUT;
		stobj.updateTaskName(oldName, newName);

		// This section is contributed by A0093874N
		inputStack.push(String.format(StreamUtil.CMD_MODIFY, index, oldName));
		return String.format(StreamUtil.LOG_MODIFY, oldName, newName);
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
	private void setDueDate(String taskName, int taskIndex, Calendar calendar)
			throws StreamModificationException {
		String parsedCalendar = calendar.get(Calendar.DAY_OF_MONTH) + "/"
				+ (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.YEAR);
		StreamTask currentTask = stobj.getTask(taskName);
		Calendar currentDeadline = currentTask.getDeadline();
		stobj.setDueTime(taskName, calendar);
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
		// This section is contributed by A0093874N
		showAndLogResult(String.format(StreamUtil.LOG_DUE, taskName,
				parsedCalendar));
		//
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
		inputStack.push(String.format(StreamUtil.CMD_MARK, index, "ongoing"));
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
		inputStack.push(String.format(StreamUtil.CMD_MARK, index, "done"));
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
	 */

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
	private List<StreamTask> search(String keyphrase) {
		assert (keyphrase != null) : StreamUtil.FAIL_NULL_INPUT;
		return stobj.findTasks(keyphrase);
	}

	// TODO who's the author of printTasks()?

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
	 * 
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
		System.out.println(logMessage);
		log(StreamUtil.showAsTerminalResponse(logMessage));
	}
	
	// @author A0096529N
	private void log(String message) {
		logMessages.add(message);
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
		stio.saveLogFile(logMessages, logFileName);
	}

	// TODO who's the author?

	private void executeInput(CommandType command, String content)
			throws StreamModificationException, IOException {

		String[] contents;
		String[] tags;
		String taskName;
		String logMessage;
		int taskIndex;

		switch (command) {
			case ADD:
				showAndLogCommand("ADD");
				logMessage = addTask(content);
				showAndLogResult(logMessage);
				break;

			case DEL:
				showAndLogCommand("DELETE");
				taskIndex = Integer.parseInt(content);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				logMessage = deleteTask(taskName);
				showAndLogResult(logMessage);
				break;

			case DESC:
				showAndLogCommand("DESCRIBE");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				String description = contents[1];
				logMessage = setDescription(taskName, taskIndex, description);
				showAndLogResult(logMessage);
				break;

			case DUE:
				showAndLogCommand("DUE");
				contents = content.split(" ", 2);

				taskIndex = Integer.parseInt(contents[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);

				if (contents[1].trim().equals("null")) {
					stobj.setNullDeadline(taskName);
					showAndLogResult(String.format(StreamUtil.LOG_DUE_NEVER,
							taskName));
				} else {
					String[] dueDate = contents[1].split("/");
					int year;
					if (dueDate.length == 2) {
						year = Calendar.getInstance().get(Calendar.YEAR);
					} else {
						year = Integer.parseInt(dueDate[2]);
					}
					int day = Integer.parseInt(dueDate[0]);
					int month = Integer.parseInt(dueDate[1]);
					Calendar calendar = new GregorianCalendar(year, month - 1,
							day);
					setDueDate(taskName, taskIndex, calendar);
				}
				break;

			case MODIFY:
				showAndLogCommand("MODIFY");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				String oldTaskName = stobj.getTaskNames().get(taskIndex - 1);
				String newTaskName = contents[1];
				logMessage = setName(oldTaskName, newTaskName, taskIndex);
				showAndLogResult(logMessage);
				break;

			case MARK:
				showAndLogCommand("MARK");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				String markType = contents[1].trim();
				/*
				 * TODO make the markType more flexible. maybe "finished",
				 * "not done", "not finished", ...
				 */
				if (markType.equals("done")) {
					logMessage = markAsDone(taskName, taskIndex);
				} else if (markType.equals("ongoing")) {
					logMessage = markAsOngoing(taskName, taskIndex);
				} else {
					logMessage = "Unknown marking type: " + markType;
				}
				showAndLogResult(logMessage);
				break;

			case TAG:
				showAndLogCommand("TAG");
				tags = content.split(" ");
				ArrayList<String> tagsAdded = new ArrayList<String>();
				ArrayList<String> tagsNotAdded = new ArrayList<String>();
				taskIndex = Integer.parseInt(tags[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				for (int i = 1; i < tags.length; i++) {
					if (stobj.addTag(taskName, tags[i])) {
						tagsAdded.add(tags[i]);
					} else {
						tagsNotAdded.add(tags[i]);
					}
				}
				inputStack.push("untag " + taskIndex + " "
						+ StreamUtil.listDownArrayContent(tagsAdded, " "));
				if (!tagsAdded.isEmpty()) {
					showAndLogResult(String.format(StreamUtil.LOG_TAGS_ADDED,
							taskName,
							StreamUtil.listDownArrayContent(tagsAdded, ", ")));
				}
				if (!tagsNotAdded.isEmpty()) {
					showAndLogResult(String
							.format(StreamUtil.LOG_TAGS_NOT_ADDED, taskName,
									StreamUtil.listDownArrayContent(
											tagsNotAdded, ", ")));
				}
				break;

			case UNTAG:
				showAndLogCommand("UNTAG");
				tags = content.split(" ");
				ArrayList<String> tagsRemoved = new ArrayList<String>();
				ArrayList<String> tagsNotRemoved = new ArrayList<String>();
				taskIndex = Integer.parseInt(tags[0]);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				for (int i = 1; i < tags.length; i++) {
					if (stobj.removeTag(taskName, tags[i])) {
						tagsRemoved.add(tags[i]);
					} else {
						tagsNotRemoved.add(tags[i]);
					}
				}
				inputStack.push("tag " + taskIndex + " "
						+ StreamUtil.listDownArrayContent(tagsRemoved, " "));
				if (!tagsRemoved.isEmpty()) {
					showAndLogResult(String.format(StreamUtil.LOG_TAGS_REMOVED,
							taskName,
							StreamUtil.listDownArrayContent(tagsRemoved, ", ")));
				}
				if (!tagsNotRemoved.isEmpty()) {
					showAndLogResult(String.format(
							StreamUtil.LOG_TAGS_NOT_REMOVED, taskName,
							StreamUtil.listDownArrayContent(tagsNotRemoved,
									", ")));
				}
				break;

			case SEARCH:
				showAndLogCommand("SEARCH");
				List<StreamTask> searchResult = search(content);
				System.out.println("Search result for " + content);
				for (int i = 1; i <= searchResult.size(); i++) {
					System.out.println(i + ". "
							+ searchResult.get(i - 1).getTaskName());
				}
				showAndLogResult(String.format(StreamUtil.LOG_SEARCH, content,
						searchResult.size()));
				break;

			case VIEW:
				showAndLogCommand("VIEW");
				taskIndex = Integer.parseInt(content);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				logMessage = printDetails(taskName);
				showAndLogResult(logMessage);
				break;

			case CLEAR:
				showAndLogCommand("CLEAR");
				clearAllTasks();
				showAndLogResult(StreamUtil.LOG_CLEAR);
				break;

			case UNDO:
				showAndLogCommand("UNDO");
				if (inputStack.isEmpty()) {
					showAndLogResult(StreamUtil.LOG_UNDO_FAIL);
				} else {
					String undoneInput = inputStack.pop();
					showAndLogResult(StreamUtil.LOG_UNDO_SUCCESS);
					processInput(undoneInput);

					/*
					 * VERY IMPORTANT because almost all inputs will add its
					 * counterpart to the inputStack. If not popped, the undo
					 * process will be trapped between just two processes.
					 */
					inputStack.pop();
				}
				break;

			case RECOVER:
				showAndLogCommand("RECOVER");
				int noOfTasksToRecover = Integer.parseInt(content);
				for (int i = 0; i < noOfTasksToRecover; i++) {
					StreamTask task = dumpedTasks.pop();
					stobj.recoverTask(task);
					showAndLogResult(String.format(StreamUtil.LOG_RECOVER,
							task.getTaskName()));
				}
				stobj.setOrdering(orderingStack.pop());
				inputStack.push("some fake input to be popped");
				break;

			case DISMISS:
				showAndLogCommand("DISMISS");
				taskIndex = Integer.parseInt(content);
				taskName = stobj.getTaskNames().get(taskIndex - 1);
				dismissTask(taskName);
				showAndLogResult(String.format(StreamUtil.LOG_DELETE, taskName));
				inputStack.push("some fake input to be popped");
				break;

			case EXIT:
				showAndLogCommand("EXIT");
				System.out.println(StreamUtil.MSG_THANK_YOU);
				saveLogFile();
				System.exit(0);

			default:
				showAndLogResult(StreamUtil.LOG_CMD_UNKNOWN);
		}
	}

	// @author A0093874N

	public void processInput(String input) {
		parser.interpretCommand(input);
		CommandType command = parser.getCommandType();
		String content = parser.getCommandContent();
		try {
			executeInput(command, content);
		} catch (AssertionError e) {
			showAndLogResult(String.format(StreamUtil.LOG_ERRORS,
					"AssertionError", e.getMessage()));
		} catch (Exception e) {
			showAndLogResult(String.format(StreamUtil.LOG_ERRORS, e.getClass()
					.getSimpleName(), e.getMessage()));
		}
	}

	// @author A0093874N

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
		Stream stream = new Stream(StreamUtil.PARAM_FILENAME);
		System.out.println(StreamUtil.MSG_WELCOME);

		while (true) {
			stream.doRoutineProcess();
		}
	}

}