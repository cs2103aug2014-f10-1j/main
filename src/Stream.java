import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import exception.StreamModificationException;
import fileio.StreamIO;
import parser.StreamParser;
import parser.StreamParser.CommandType;
import model.StreamTask;
import model.StreamObject;

public class Stream {

	private StreamObject st;
	private StreamIO stio;
	private StreamParser parser;

	private String filename;
	private Stack<String> inputStack;
	private Stack<StreamTask> dumpedTasks;
	private ArrayList<String> logMessages;

	private static Scanner inputScanner = new Scanner(System.in);

	private static final int POS_YEAR_SUBSTRING = 2;

	private static final String FILENAME_TO_USE = "stream";
	
	private static final String VERSION = "v0.2";
	private static final String MESSAGE_WELCOME = "Welcome to Stream "
			+ VERSION + "!";
	private static final String MESSAGE_THANK_YOU = "Thank you for using this internal release of Stream[BETA]!";
	private static final String SAVE_LOCATION = "%1$s.json";
	private static final String LOGFILE_LOCATION = "%1$s %2$s%3$s%4$s %5$s%6$s%7$s.txt";

	private static final String ERROR_NULL_INPUT = "Null input value detected";

	private static final String LOG_MESSAGE_ADD = "Added %1$s";
	private static final String LOG_MESSAGE_DELETE = "Deleted %1$s";
	private static final String LOG_MESSAGE_VIEW = "Viewed the details for %1$s";
	private static final String LOG_MESSAGE_DESC = "Changed description for %1$s to %2$s";
	private static final String LOG_MESSAGE_CLEAR = "Cleared all tasks";
	private static final String LOG_MESSAGE_RECOVER = "Recovered %1$s";
	private static final String LOG_MESSAGE_MODIFY = "Changed name for %1$s to %2$s";
	private static final String LOG_MESSAGE_MARK = "%1$s marked as %2$s";
	private static final String LOG_MESSAGE_ERRORS = "%1$s: %2$s";
	private static final String LOG_MESSAGE_UNDO_FAIL = "Nothing to undo";
	private static final String LOG_MESSAGE_UNDO_SUCCESS = "Undoing the previous action";
	private static final String LOG_MESSAGE_DUE_NEVER = "Due date for %1$s is removed";
	private static final String LOG_MESSAGE_DUE = "Due date for %1$s set to %2$s";
	private static final String LOG_MESSAGE_CMD_UNKNOWN = "Unknown command entered";

	public Stream(String file) {
		st = new StreamObject();
		stio = new StreamIO(String.format(SAVE_LOCATION, file));
		parser = new StreamParser();

		filename = file;
		inputStack = new Stack<String>();
		dumpedTasks = new Stack<StreamTask>();
		logMessages = new ArrayList<String>();

		st.load();
	}

	// deprecated by A0093874N; unless we want to implement commands to allow
	// user to re-load last saved file explicitly, this is unnecessary
	/*
	 * public void load() { st.load(); }
	 */

	public void save() {
		st.save();
	}

	// @author A0093874N

	/**
	 * <p>
	 * Adds a new task named <i>newTaskName</i> to the task list.
	 * </p>
	 * <p>
	 * Precondition: <i>newTaskName</i> is not null
	 * </p>
	 * 
	 * @author Wilson Kurniawan
	 * @throws StreamModificationException
	 *             if task named <i>newTaskName</i> is already present.
	 * @return <strong>String</strong> - the message to be logged
	 */
	public String addTask(String newTaskName)
			throws StreamModificationException {
		assert (newTaskName == null) : ERROR_NULL_INPUT;
		st.addTask(newTaskName);
		int currentNoOfTasks = st.getTaskNames().size();
		inputStack.push("dismiss " + currentNoOfTasks);
		return String.format(LOG_MESSAGE_ADD, newTaskName);
	}

	// @author A0093874N

	/**
	 * Checks whether a specific task is included in the task list. Mainly for
	 * testing purpose.
	 * 
	 * @author Wilson Kurniawan
	 * @return <strong>Boolean</strong> - true if the <strong>Task</strong>
	 *         <i>taskName</i> exists, false otherwise.
	 */
	public Boolean hasTask(String taskName) {
		return st.hasTask(taskName);
	}

	// @author A0118007R

	/**
	 * Prints the task details
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the message to be logged
	 */

	public String printDetails(String task) throws StreamModificationException {
		StreamTask currentTask = st.getTask(task);
		currentTask.printTaskDetails();

		// This section is contributed by A0093874N
		return String.format(LOG_MESSAGE_VIEW, currentTask.getTaskName());
		//
	}

	// @author A0118007R

	/**
	 * Changes the task name
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the message to be logged
	 */

	public String changeName(String oldName, String newName, int index)
			throws StreamModificationException {
		st.updateTaskName(oldName, newName);

		// This section is contributed by A0093874N
		inputStack.push("modify " + index + " " + oldName);
		return String.format(LOG_MESSAGE_MODIFY, oldName, newName);
		//
	}

	// @author A0118007R

	/**
	 * Adds a description to the task
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the message to be logged
	 */
	public String setDescription(String task, int index, String description)
			throws StreamModificationException {
		StreamTask currentTask = st.getTask(task);
		String oldDescription = currentTask.getDescription();
		currentTask.setDescription(description);

		// This section is contributed by A0093874N
		inputStack.push("desc " + index + " " + oldDescription);
		return String.format(LOG_MESSAGE_DESC, currentTask.getTaskName(),
				description);
		//

	}

	// @author A0119401U

	/**
	 * Mark the selected task as done
	 * 
	 * @author Jiang Shenhao
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the message to be logged
	 */
	public String markAsDone(String task, int index)
			throws StreamModificationException {
		st.markTaskAsDone(task);
		// This section is contributed by A0118007R
		inputStack.push("mark " + index + " " + "ongoing");
		//
		// This section is contributed by A0093874N
		return String.format(LOG_MESSAGE_MARK, task, "done");
		//
	}

	// @author A0118007R

	/**
	 * Mark the selected task as ongoing
	 * 
	 * @author John Kevin Tjahjadi
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the message to be logged
	 */
	public String markAsOngoing(String task, int index)
			throws StreamModificationException {
		st.markTaskAsOngoing(task);
		//
		inputStack.push("mark " + index + " " + "done");
		//
		// This section is contributed by A0093874N
		return String.format(LOG_MESSAGE_MARK, task, "ongoing");
		//
	}

	// @author A0119401U
	/**
	 * Set the due date of the selected task
	 * 
	 * @author Jiang Shenhao
	 * @throws StreamModificationException
	 */
	public void setDueDate(String taskName, int taskIndex, Calendar calendar)
			throws StreamModificationException {
		String parsedCalendar = calendar.get(Calendar.DAY_OF_MONTH) + "/"
				+ (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.YEAR);
		StreamTask currentTask = st.getTask(taskName);
		Calendar currentDeadline = currentTask.getDeadline();
		st.setDueTime(taskName, calendar);
		if (currentDeadline == null) {
			inputStack.push("due " + taskIndex + " " + "null");
		} else {
			inputStack.push("due " + taskIndex + " "
					+ currentDeadline.get(Calendar.DATE) + "/"
					+ (currentDeadline.get(Calendar.MONTH) + 1) + "/"
					+ currentDeadline.get(Calendar.YEAR));
		}
		// This section is contributed by A0093874N
		showAndLog(String.format(LOG_MESSAGE_DUE, taskName, parsedCalendar));
		//
	}

	/**
	 * Modify a task's description This method is just to differentiate the set
	 * new description and modify description part
	 * 
	 * @author A0118007R
	 */

	public void changeDescription(String task, int index, String newDescription) {
		try {
			setDescription(task, index, newDescription);
		} catch (Exception e) {

		}
	}

	// @author A0093874N

	public void dismissTask(String task) {
		try {
			st.deleteTask(task);
		} catch (Exception e) {

		}
	}

	// @author A0118007R

	/**
	 * Deletes a specific task
	 * 
	 * @author A0118007R improved by A0093874N
	 * @throws StreamModificationException
	 * @return <strong>String</strong> - the message to be logged
	 */

	public String deleteTask(String task) throws StreamModificationException {
		StreamTask deletedTask = st.getTask(task);
		dismissTask(task);
		dumpedTasks.push(deletedTask);
		inputStack.push("recover 1");
		return String.format(LOG_MESSAGE_DELETE, deletedTask.getTaskName());
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
	public List<StreamTask> search(String keyphrase) {
		return st.findTasks(keyphrase);
	}

	// @author A0093874N

	/**
	 * Clears all tasks upon receiving the command "clear".
	 * 
	 * @author Wilson Kurniawan
	 */
	public void clearAllTasks() throws StreamModificationException {
		int noOfTasks = st.getTaskNames().size();
		for (int i = noOfTasks - 1; i >= 0; i--) {
			deleteTask(st.getTaskNames().get(i));
			/*
			 * This is because we don't want the "recover 1". Rather, we'll
			 * replace with "recover noOfTasks" at the end of the process.
			 */
			inputStack.pop();
		}
		inputStack.push("recover " + noOfTasks);
	}

	// @author A0118007R

	public static void printReceivedCommand(String command) {
		System.out.println("Command received [" + command + "]");
	}

	// @author A0093874N
	
	public static String addZeroToTime(Integer time) {
		String convertedTime = time.toString();
		if (time < 10) {
			convertedTime = "0" + convertedTime;
		}
		if (time < 1) {
			convertedTime = "0" + convertedTime;
		}
		return convertedTime;
	}

	void printTasks() {
		System.out.println(" ");
		System.out.println("Your current tasks: ");
		ArrayList<String> myTasks = st.getTaskNames();
		int numberOfTasks = myTasks.size();

		for (int i = 1; i <= numberOfTasks; i++) {
			System.out.println(i + ". " + myTasks.get(i - 1));
		}
	}

	// @author A0093874N
	
	void showAndLog(String logMessage) {
		System.out.println(logMessage);
		logMessages.add(logMessage);
	}

	void processAndExecute(String input) {

		// updated by A0119401U
		/*
		 * ParserContent parsedContent = Parser.interpretCommand(input);
		 * CommandType command = parsedContent.getCommandKey(); String content =
		 * parsedContent.getCommandContent();
		 */

		parser.interpretCommand(input);
		CommandType command = parser.getCommandType();
		String content = parser.getCommandContent();
		String[] contents;
		String taskName;
		String logMessage;
		int taskIndex;

		try {
			assert (command == null) : ERROR_NULL_INPUT;
			assert (content == null) : ERROR_NULL_INPUT;
			switch (command) {
				case ADD:
					printReceivedCommand("ADD");
					logMessage = addTask(content);
					showAndLog(logMessage);
					break;

				case DEL:
					printReceivedCommand("DELETE");
					taskIndex = Integer.parseInt(content);
					taskName = st.getTaskNames().get(taskIndex - 1);
					logMessage = deleteTask(taskName);
					showAndLog(logMessage);
					break;

				case DESC:
					printReceivedCommand("DESCRIBE");
					contents = content.split(" ", 2);
					taskIndex = Integer.parseInt(contents[0]);
					taskName = st.getTaskNames().get(taskIndex - 1);
					String description = contents[1];
					logMessage = setDescription(taskName, taskIndex,
							description);
					showAndLog(logMessage);
					break;

				case DUE:
					printReceivedCommand("DUE");
					contents = content.split(" ", 2);

					taskIndex = Integer.parseInt(contents[0]);
					taskName = st.getTaskNames().get(taskIndex - 1);

					if (contents[1].trim().equals("null")) {
						st.setNullDeadline(taskName);
						showAndLog(String.format(LOG_MESSAGE_DUE_NEVER,
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
						Calendar calendar = new GregorianCalendar(year,
								month - 1, day);
						setDueDate(taskName, taskIndex, calendar);
					}
					break;

				case MODIFY:
					printReceivedCommand("MODIFY");
					contents = content.split(" ", 2);
					taskIndex = Integer.parseInt(contents[0]);
					String oldTaskName = st.getTaskNames().get(taskIndex - 1);
					String newTaskName = contents[1];
					logMessage = changeName(oldTaskName, newTaskName, taskIndex);
					showAndLog(logMessage);
					break;

				case MARK:
					printReceivedCommand("MARK");
					contents = content.split(" ", 2);
					taskIndex = Integer.parseInt(contents[0]);
					taskName = st.getTaskNames().get(taskIndex - 1);
					if (contents[1].trim().equals("done")) {
						logMessage = markAsDone(taskName, taskIndex);
					} else {
						logMessage = markAsOngoing(taskName, taskIndex);
					}
					showAndLog(logMessage);
					break;

				case VIEW:
					printReceivedCommand("VIEW");
					taskIndex = Integer.parseInt(content);
					taskName = st.getTaskNames().get(taskIndex - 1);
					logMessage = printDetails(taskName);
					showAndLog(logMessage);
					break;

				case CLEAR:
					printReceivedCommand("CLEAR");
					clearAllTasks();
					showAndLog(LOG_MESSAGE_CLEAR);
					break;

				case UNDO:
					printReceivedCommand("UNDO");
					if (inputStack.isEmpty()) {
						showAndLog(LOG_MESSAGE_UNDO_FAIL);
					} else {
						String undoneInput = inputStack.pop();
						showAndLog(LOG_MESSAGE_UNDO_SUCCESS);
						processAndExecute(undoneInput);

						/*
						 * VERY IMPORTANT because almost all inputs will add its
						 * counterpart to the inputStack. If not popped, the
						 * undo process will be trapped between just two
						 * processes.
						 */
						inputStack.pop();
					}
					break;

				case RECOVER:
					printReceivedCommand("RECOVER");
					int noOfTasksToRecover = Integer.parseInt(content);
					for (int i = 0; i < noOfTasksToRecover; i++) {
						StreamTask task = dumpedTasks.pop();
						st.recoverTask(task);
						showAndLog(String.format(LOG_MESSAGE_RECOVER,
								task.getTaskName()));
					}
					inputStack.push("some fake input to be popped");
					break;

				case DISMISS:
					printReceivedCommand("DISMISS");
					taskIndex = Integer.parseInt(content);
					taskName = st.getTaskNames().get(taskIndex - 1);
					dismissTask(taskName);
					showAndLog(String.format(LOG_MESSAGE_DELETE, taskName));
					inputStack.push("some fake input to be popped");
					break;

				case EXIT:
					printReceivedCommand("EXIT");
					System.out.println(MESSAGE_THANK_YOU);

					Calendar now = Calendar.getInstance();
					String day = addZeroToTime(now.get(Calendar.DAY_OF_MONTH));
					String mth = addZeroToTime(now.get(Calendar.MONTH));
					Integer yr = now.get(Calendar.YEAR);
					String hr = addZeroToTime(now.get(Calendar.HOUR_OF_DAY));
					String min = addZeroToTime(now.get(Calendar.MINUTE));
					String sec = addZeroToTime(now.get(Calendar.SECOND));
					String logFileName = String.format(LOGFILE_LOCATION,
							filename, day, mth,
							yr.toString().substring(POS_YEAR_SUBSTRING), hr,
							min, sec);
					stio.saveLogFile(logMessages, logFileName);

					System.exit(0);

				default:
					showAndLog(LOG_MESSAGE_CMD_UNKNOWN);
			}
		} catch (AssertionError e) {
			showAndLog(String.format(LOG_MESSAGE_ERRORS, "AssertionError",
					e.getMessage()));
		} catch (Exception e) {
			showAndLog(String.format(LOG_MESSAGE_ERRORS, e.getClass()
					.getSimpleName(), e.getMessage()));
		}
	}

	public static void main(String[] args) {
		System.out.println(MESSAGE_WELCOME);
		Stream stream = new Stream(FILENAME_TO_USE);

		while (true) {
			stream.printTasks();
			System.out
					.println("========================================================");
			System.out.print("Enter Command: ");
			String input = inputScanner.nextLine();
			// TODO restrict "recover" and "dismiss" from user somehow
			/*
			 * workaround for now: if we spot "recover" or "dismiss", halt
			 * immediately. any better idea?
			 */
			if (input.length() >= 7
					&& (input.substring(0, 7).equals("recover") || input
							.substring(0, 7).equals("dismiss"))) {
				System.out.println("Unknown command with contents : ");
			} else {
				stream.processAndExecute(input);
			}

			stream.save();
		}
	}
	
}
