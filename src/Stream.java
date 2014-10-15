import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import exception.StreamIOException;
import exception.StreamModificationException;
import fileio.StreamIO;
import parser.StreamParser;
import parser.StreamParser.CommandType;
import model.StreamTask;
import model.StreamObject;

public class Stream {

	private StreamObject st;
	private StreamIO stio;

	private Stack<String> inputStack;
	private Stack<StreamTask> dumpedTasks;
	private ArrayList<String> logMessages;
	private String timeStart;

	private static final String MESSAGE_WELCOME = "Welcome to Stream!";
	private static final String ERROR_TASK_ALREADY_EXISTS = "Error: \"%1$s\" already exists in the tasks list.";
	private static final String SAVE_LOCATION = "stream.json";

	void initialize() {
		st = new StreamObject();
		stio = new StreamIO(SAVE_LOCATION);
		inputStack = new Stack<String>();
		dumpedTasks = new Stack<StreamTask>();
		logMessages = new ArrayList<String>();

		Calendar now = Calendar.getInstance();
		String day = addZeroToTime(now.get(Calendar.DAY_OF_MONTH));
		String mth = addZeroToTime(now.get(Calendar.MONTH));
		Integer yr = now.get(Calendar.YEAR);
		String hr = addZeroToTime(now.get(Calendar.HOUR_OF_DAY));
		String min = addZeroToTime(now.get(Calendar.MINUTE));
		String sec = addZeroToTime(now.get(Calendar.SECOND));
		timeStart = day + mth + yr.toString().substring(2) + " " + hr + min
				+ sec;

	}

	public static Stream newStream() {
		Stream stream = new Stream();
		stream.initialize();
		return stream;
	}

	public void load() {
		st.load();
	}

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
	 * @return <strong>String</strong> Logging message
	 */
	public String addTask(String newTaskName)
			throws StreamModificationException {
		assert (newTaskName == null) : "Error: pre-condition not fulfilled";
		if (hasTask(newTaskName)) {
			throw new StreamModificationException(String.format(
					ERROR_TASK_ALREADY_EXISTS, newTaskName));
		} else {
			st.addTask(newTaskName);
			int currentNoOfTasks = st.getTaskNames().size();
			inputStack.push("dismiss " + currentNoOfTasks);
			return "Added " + newTaskName;
		}
	}

	/**
	 * Checks whether a specific task is included in the task list. Mainly for
	 * testing purpose.
	 * 
	 * @author Wilson Kurniawan
	 */
	public Boolean hasTask(String task) {
		return st.hasTask(task);
	}

	// @author A0118007R

	public void printDetails(String task) {
		try {
			StreamTask currentTask = st.getTask(task);
			currentTask.printTaskDetails();
		} catch (Exception e) {

		}
	}

	public void changeName(String oldName, String newName, int index) {
		try {
			st.updateTaskName(oldName, newName);
			//
			inputStack.push("modify " + index + " " + oldName);
			//
		} catch (Exception e) {

		}
	}

	/**
	 * Adds a description to the task
	 * 
	 * @author A0118007R improved by A0093874N
	 */
	public void setDescription(String task, int index, String description) {
		try {
			StreamTask currentTask = st.getTask(task);
			String oldDescription = currentTask.getDescription();
			currentTask.setDescription(description);
			//
			inputStack.push("desc " + index + " " + oldDescription);
			//
		} catch (Exception e) {

		}
	}

	// @author A0119401U
	/**
	 * Mark the selected task as done
	 * 
	 */
	public void markAsDone(String task, int index) {
		try {
			st.markTaskAsDone(task);
			//
			inputStack.push("mark " + index + " " + "ongoing");
			//
		} catch (Exception e) {

		}
	}

	// @author A0118007R
	/**
	 * Mark the selected task as ongoing
	 * 
	 */

	public void markAsOngoing(String task, int index) {
		try {
			st.markTaskAsOngoing(task);
			//
			inputStack.push("mark " + index + " " + "done");
			//
		} catch (Exception e) {

		}
	}

	// @author A0119401U
	/**
	 * Set the due date of the selected task
	 * 
	 * @param taskName
	 *            , taskIndex, calendar
	 * 
	 * @author A0119401U
	 * 
	 */
	public void setDueDate(String taskName, int taskIndex, Calendar calendar) {
		try {
			StreamTask currentTask = st.getTask(taskName);
			Calendar currentDeadline = currentTask.getDeadline();
			st.setDueTime(taskName, calendar);
			if (currentDeadline == null) {
				inputStack.push("due " + taskIndex + " " + "null");
			} else {

				//
				inputStack.push("due " + taskIndex + " "
						+ currentDeadline.get(Calendar.DATE) + "/"
						+ (currentDeadline.get(Calendar.MONTH) + 1) + "/"
						+ currentDeadline.get(Calendar.YEAR));
				//
			}
		} catch (Exception e) {

		}
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

	public void dismissTask(String task) {
		try {
			st.deleteTask(task);
		} catch (Exception e) {

		}
	}

	/**
	 * Deletes a specific task
	 * 
	 * @author A0118007R improved by A0093874N
	 */

	public void deleteTask(String task) {
		try {
			StreamTask deletedTask = st.getTask(task);
			dismissTask(task);
			dumpedTasks.push(deletedTask);
			inputStack.push("recover 1");
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
	public List<StreamTask> search(String keyphrase) {
		return st.findTasks(keyphrase);
	}

	// @author A0093874N

	/**
	 * Clears all tasks upon receiving the command "clear".
	 * 
	 * @author Wilson Kurniawan
	 */
	public void clearAllTasks() {
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

	public static Scanner inputScanner = new Scanner(System.in);

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

	public static void main(String[] args) {
		System.out.println(MESSAGE_WELCOME);
		Stream stream = Stream.newStream();
		stream.load(); // can consider placing this step into
						// Stream.initialize(), so no need call here.
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

	void printTasks() {
		System.out.println(" ");
		System.out.println("Your current tasks: ");
		ArrayList<String> myTasks = st.getTaskNames();
		int numberOfTasks = myTasks.size();

		for (int i = 1; i <= numberOfTasks; i++) {
			System.out.println(i + ". " + myTasks.get(i - 1));
		}
	}

	void processAndExecute(String input) {

		// updated by A0119401U
		/*
		 * ParserContent parsedContent = Parser.interpretCommand(input);
		 * CommandType command = parsedContent.getCommandKey(); String content =
		 * parsedContent.getCommandContent();
		 */

		StreamParser parser = new StreamParser();
		parser.interpretCommand(input);
		CommandType command = parser.getCommandType();
		String content = parser.getCommandContent();
		String[] contents;
		String taskName;
		int taskIndex;

		switch (command) {
			case ADD:
				printReceivedCommand("ADD");
				try {
					addTask(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case DEL:
				printReceivedCommand("DELETE");
				taskIndex = Integer.parseInt(content);
				taskName = st.getTaskNames().get(taskIndex - 1);
				try {
					deleteTask(taskName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case DESC:
				printReceivedCommand("DESCRIBE");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				taskName = st.getTaskNames().get(taskIndex - 1);
				String description = contents[1];
				try {
					setDescription(taskName, taskIndex, description);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case DUE:
				printReceivedCommand("DUE");
				contents = content.split(" ", 2);

				taskIndex = Integer.parseInt(contents[0]);
				taskName = st.getTaskNames().get(taskIndex - 1);

				if (contents[1].trim().equals("null")) {
					try {

						st.setNullDeadline(taskName);
					} catch (StreamModificationException e) {

					}
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

					try {
						setDueDate(taskName, taskIndex, calendar);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case MODIFY:
				printReceivedCommand("MODIFY");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				String oldTaskName = st.getTaskNames().get(taskIndex - 1);
				String newTaskName = contents[1];
				try {
					changeName(oldTaskName, newTaskName, taskIndex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case MARK:
				printReceivedCommand("MARK");
				contents = content.split(" ", 2);
				taskIndex = Integer.parseInt(contents[0]);
				taskName = st.getTaskNames().get(taskIndex - 1);
				try {
					if (contents[1].trim().equals("done")) {
						markAsDone(taskName, taskIndex);
					} else {
						markAsOngoing(taskName, taskIndex);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case VIEW:
				printReceivedCommand("VIEW");
				taskIndex = Integer.parseInt(content);
				taskName = st.getTaskNames().get(taskIndex - 1);
				printDetails(taskName);
				break;

			case CLEAR:
				printReceivedCommand("CLEAR");
				try {
					clearAllTasks();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case UNDO:
				printReceivedCommand("UNDO");
				if (inputStack.isEmpty()) {
					System.out.println("Nothing to undo!");
				} else {
					String undoneInput = inputStack.pop();
					processAndExecute(undoneInput);

					/*
					 * VERY IMPORTANT because almost all inputs will add its
					 * counterpart to the inputStack. If not popped, the undo
					 * process will be trapped between just two processes.
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
				}
				inputStack.push("some fake input to be popped");
				break;

			case DISMISS:
				printReceivedCommand("DISMISS");
				taskIndex = Integer.parseInt(content);
				taskName = st.getTaskNames().get(taskIndex - 1);
				try {
					dismissTask(taskName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				inputStack.push("some fake input to be popped");
				break;

			case EXIT:
				printReceivedCommand("EXIT");
				System.out
						.println("Thank you for using this internal release of Stream[BETA]!");
				System.exit(0);

			default:
				System.out
						.println("Unknown command with contents : " + content);
		}
	}

}
