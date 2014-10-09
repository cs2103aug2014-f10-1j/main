package stream;

import java.util.List;

import parser.Parser;
import parser.Parser.CommandType;
import parser.ParserContent;

public class Stream {

	private static StreamObject st;
	private static final String MESSAGE_WELCOME = "Welcome to Stream!";

	public void initialize() {
		st = new StreamObject();
	}

	/**
	 * Adds a new task to the task list.
	 * 
	 * @author Wilson Kurniawan
	 * @command must include the new task to be added
	 */
	public void addTask(String newTask) throws Exception {
		st.addTask(newTask);
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
	
	//author A0118007R
	/**
	 * Adds a description to the task
	 * 
	 * @author A0118007R
	 */
	
	public void setDescription(String task, String description){
		try {
			Task currentTask = st.getTask(task);
			currentTask.setDescription(description);
		} catch(Exception e){
			
		} 
	}
	
	/**
	 * Modify a task's description
	 * This method is just to differentiate the set new description and modify description part
	 * @author A0118007R
	 */
	
	public void changeDescription(String task, String newDescription){
		setDescription(task, newDescription);
	}
	
	/**
	 * Deletes a specific task
	 * 
	 * @author A0118007R
	 */
	
	public void deleteTask(String task){
		try{
			st.deleteTask(task);
		} catch(Exception e){
			
		}
	}
	
	//@author A0096529N	
	/**
	 * Search for tasks with specified key phrase, 
	 * in the task name, description and tags.
	 * <p>Key phrase will be broken down into key
	 * words (by splitting with space character).
	 * Key words will be used to search in the tags.</p>
	 * 
	 * <p>Precondition: keyphrase != null</p>
	 * 
	 * @return tasks - a list of tasks containing 
	 * the key phrase.
	 * @author Steven Khong
	 */
	public List<Task> search(String keyphrase) {
		return st.findTasks(keyphrase);
	}
	
	//author A0118007R
	public static void main(String[] args){
		System.out.println(MESSAGE_WELCOME);
<<<<<<< HEAD
		
=======
		String input = "add TaskName";
		ParserContent parsedContent = Parser.interpretCommand(input);
		CommandType command = parsedContent.getCommandKey();
		String content = parsedContent.getCommandContent();
		switch(command) {
		case ADD:
			System.out.println("Command received [ADD] with contents : " + content);
		default:
			System.out.println("Unknown command with contents : " + content);
		}
>>>>>>> f62b7ed062c77458494d3eedb486e240a34dcd64
	}

}
