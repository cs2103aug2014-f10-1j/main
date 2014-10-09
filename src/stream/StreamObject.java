package stream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class StreamObject {

	private static String ERROR_TASK_ALREADY_EXISTS = "Error: \"%1$s\" already exists in the tasks list.";

	/**
	 * String for error when adding a description to a non-existent task
	 * 
	 * @author A0118007R
	 */
	private static final String ERROR_TASK_DOES_NOT_EXIST = "Error: The task \"%1$s\" does not exist!";

	private HashMap<String, Task> allTasks;

	public StreamObject() {
		this.allTasks = new HashMap<String, Task>();
	}

	public void addTask(String newTask) throws Exception {
		if (this.allTasks.containsKey(newTask)) {
			throw new Exception(String.format(ERROR_TASK_ALREADY_EXISTS,
					newTask));
		} else {
			this.allTasks.put(newTask, new Task(newTask));
		}
	}

	public Boolean hasTask(String task) {
		return this.allTasks.containsKey(task);
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
			allTasks.remove(task);
		} else {
			throw new Exception(String.format(ERROR_TASK_DOES_NOT_EXIST, task));
		}
	}

	//@author A0096529N
	/**
	 * Search for tasks with specified key phrase, 
	 * in the task name, description and tags.
	 * 
	 * @return tasks - a list of tasks containing 
	 * the key phrase.
	 * @author Steven Khong
	 */
	public List<Task> findTasks(String keyphrase) {
		// Split key phrase into keywords
		String[] keywords  = null;
		if (keyphrase.contains(" ")) {
			keywords = keyphrase.split(" ");
		} else {
			keywords = new String[] {keyphrase};
		}
		
		List<Task> tasks = new ArrayList<Task>();
		for (String key : allTasks.keySet()) {
			Task task = allTasks.get(key);
			
			// check for matches between keywords and tags
			if (task.hasTag(keywords)) {
				tasks.add(task);
				continue;
			}
			// check if task description contains key phrase
			if (task.getDescription().contains(keyphrase)) {
				tasks.add(task);
				continue;
			}
			// check if task name contains key phrase
			if (task.getTaskName().contains(keyphrase)) {
				tasks.add(task);
				continue;
			}
		}
		
		return tasks;
	}
}