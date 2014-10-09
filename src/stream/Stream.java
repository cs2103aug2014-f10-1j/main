package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import parser.Parser;
import parser.Parser.CommandType;
import parser.ParserContent;

public class Stream {

	private static StreamObject st;
	private static final String MESSAGE_WELCOME = "Welcome to Stream!";

	public static void initialize() {
		st = new StreamObject();
	}

	/**
	 * Adds a new task to the task list.
	 * 
	 * @author Wilson Kurniawan
	 * @command must include the new task to be added
	 */
	public static void addTask(String newTask) throws Exception {
		st.addTask(newTask);
	}

	/**
	 * Checks whether a specific task is included in the task list. Mainly for
	 * testing purpose.
	 * 
	 * @author Wilson Kurniawan
	 */
	public static Boolean hasTask(String task) {
		return st.hasTask(task);
	}

	// author A0118007R
	
	public static void printDetails(String task){
		try {
			Task currentTask = st.getTask(task);
			currentTask.printTaskDetails();
		} catch (Exception e){
			
		}
	}
	
	public static void changeName(String oldName, String newName){
		try {
			st.updateTaskName(oldName, newName);
		} catch (Exception e) {

		}
	}

	/**
	 * Adds a description to the task
	 * 
	 * @author A0118007R
	 */
	public static void setDescription(String task, String description) {
		try {
			Task currentTask = st.getTask(task);
			currentTask.setDescription(description);
		} catch (Exception e) {

		}
	}

	/**
	 * Modify a task's description This method is just to differentiate the set
	 * new description and modify description part
	 * 
	 * @author A0118007R
	 */

	public static void changeDescription(String task, String newDescription) {
		setDescription(task, newDescription);
	}

	/**
	 * Deletes a specific task
	 * 
	 * @author A0118007R
	 */

	public static void deleteTask(String task) {
		try {
			st.deleteTask(task);
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
	public static List<Task> search(String keyphrase) {
		return st.findTasks(keyphrase);
	}

	// author A0118007R
	
	
	public static void printReceivedCommand(String command){
		System.out.println("Command received [" + command + "]");
	}
	public static Scanner inputScanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.println(MESSAGE_WELCOME);
		initialize();
		st.load();
		while (true){
			System.out.println("========================================================");
			System.out.print("Enter Command: ");
			String input = inputScanner.nextLine();
			processAndExecute(input);
			System.out.println(" ");
			System.out.println("Your current tasks: ");
			ArrayList<String> myTasks = st.getTaskNames();
			int numberOfTasks = myTasks.size();
			
			for (int i = 1; i <= numberOfTasks; i++){
				System.out.println(i + ". " + myTasks.get(i-1));
			}
			
			st.save();
		}
		
		
		
	}

	private static void processAndExecute(String input) {
		ParserContent parsedContent = Parser.interpretCommand(input);
		CommandType command = parsedContent.getCommandKey();
		String content = parsedContent.getCommandContent();
		String[] contents;
		
		switch (command) {
			case ADD:
				printReceivedCommand("ADD");
				
				try {
					addTask(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			case DEL:
				printReceivedCommand("DELETE");
				
				try {
					deleteTask(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			case DESC:
				printReceivedCommand("DESCRIBE");
				contents = content.split(" ", 2);
				String taskName = contents[0];
				String description = contents [1];
				
				try {
					setDescription(taskName, description);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			case MODIFY:
				printReceivedCommand("MODIFY");
				contents = content.split(" ", 2);
				String oldTaskName = contents[0];
				String newTaskName = contents[1];
				
				try {
					changeName(oldTaskName, newTaskName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			case VIEW:
				printReceivedCommand("VIEW");
				String nameOfTask = content;
				printDetails(nameOfTask);
				
			default:
				System.out
						.println("Unknown command with contents : " + content);
		}
	}

}
