import java.util.ArrayList;
import java.util.HashMap;



public class StreamObject {

	private static String ERROR_TASK_ALREADY_EXISTS = "Error: \"%1$s\" already exists in the tasks list.";
	
	/**
	 * String for error when adding a description to a non-existent task
	 * 
	 * @author A0118007R
	 */
	private static final String ERROR_TASK_DOES_NOT_EXIST = "Error: The task \"%1$s\" does not exist!";

	private ArrayList<String> tasksList;
	private HashMap<String, Task> allTasks;

	public StreamObject() {
		this.tasksList = new ArrayList<String>();
		this.allTasks = new HashMap<String, Task>();
	}

	public void addTask(String newTask) throws Exception {
		if (this.tasksList.contains(newTask)) {
			throw new Exception(String.format(ERROR_TASK_ALREADY_EXISTS,
					newTask));
		} else {
			this.tasksList.add(newTask);
			this.allTasks.put(newTask, new Task(newTask));
		}
	}

	public Boolean hasTask(String task) {
		return this.tasksList.contains(task);
	}
	
	/**
	 * Gets a specific task
	 * 
	 * @author A0118007R
	 */
	
	
	
	public Task getTask(String task) throws Exception{
		if (hasTask(task)){
			return allTasks.get(task);
		} else {
			throw new Exception(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					task));
		}
	}
	
	/**
	 * Deletes a specific task
	 * 
	 * @author A0118007R
	 */
	
	public void deleteTask(String task) throws Exception{
		if(hasTask(task)){
			tasksList.remove(task);
			allTasks.remove(task);
		} else {
			throw new Exception(String.format(ERROR_TASK_DOES_NOT_EXIST, task));
		}
	}
	
	
}