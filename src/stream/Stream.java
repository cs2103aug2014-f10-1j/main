package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import parser.Parser;
import parser.Parser.CommandType;
import parser.ParserContent;

public class Stream {

	private StreamObject st;
	private Stack<String> inputStack;
	private Stack<Task> dumpedTasks;
	private static final String MESSAGE_WELCOME = "Welcome to Stream!";
	private static final String ERROR_TASK_ALREADY_EXISTS = "Error: \"%1$s\" already exists in the tasks list.";

	void initialize() {
		st = new StreamObject();
		inputStack = new Stack<String>();
		dumpedTasks = new Stack<Task>();
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
	 * Adds a new task to the task list.
	 * 
	 * <p>
	 * Precondition: newTaskName != null
	 * </p>
	 * 
	 * @author Wilson Kurniawan
	 * @throws ModificationException
	 *             if task named newTask is already present.
	 */
	public void addTask(String newTask) throws ModificationException {
		if (hasTask(newTask)) {
			throw new ModificationException(String.format(
					ERROR_TASK_ALREADY_EXISTS, newTask));
		} else {
			st.addTask(newTask);
			int currentNoOfTasks = st.getTaskNames().size();
			inputStack.push("delete " + currentNoOfTasks);
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
			Task currentTask = st.getTask(task);
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
			Task currentTask = st.getTask(task);
			String oldDescription = currentTask.getDescription();
			currentTask.setDescription(description);
			//
			inputStack.push("desc " + index + " " + oldDescription);
			//
		} catch (Exception e) {

		}
	}
	
	//@author A0119401U
	/**
	 * Mark the selected task as done
	 * 
	 */
	public void markAsDone(String task, int index) {
		try {
			Task currentTask = st.getTask(task);
			currentTask.markAsDone();
			//
			inputStack.push("mark " + index + " " + "done");
			//
		} catch (Exception e) {
			
		}
	}

	/**
	 * Modify a task's description This method is just to differentiate the set
	 * new description and modify description part
	 * 
	 * @author A0118007R
	 */

	public void changeDescription(String task, int index,
			String newDescription) {
		try {
			setDescription(task, index, newDescription);
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
			Task deletedTask = st.getTask(task);
			st.deleteTask(task);
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
	public List<Task> search(String keyphrase) {
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

	public static void main(String[] args) {
		System.out.println(MESSAGE_WELCOME);
		Stream stream = Stream.newStream();
		stream.load(); // can consider placing this step into Stream.initialize(), so no need call here.
		while (true) {
			stream.printTasks();
			System.out
					.println("========================================================");
			System.out.print("Enter Command: ");
			String input = inputScanner.nextLine();
			// TODO restrict "recover" from user somehow
			/*
			 * workaround for now: if we spot "recover", halt immediately. any
			 * better idea?
			 */
			if (input.length() >= 7 && input.substring(0, 7) == "recover") {
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
		ParserContent parsedContent = Parser.interpretCommand(input);
		CommandType command = parsedContent.getCommandKey();
		String content = parsedContent.getCommandContent();
		String[] contents;
		String taskName;

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
				int taskIndex = Integer.parseInt(content);
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
				contents = content.split(" ",2);
				taskIndex = Integer.parseInt(contents[0]);
				taskName = st.getTaskNames().get(taskIndex-1);
				try {
					markAsDone(taskName, taskIndex);
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
					Task task = dumpedTasks.pop();
					st.recoverTask(task);
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
