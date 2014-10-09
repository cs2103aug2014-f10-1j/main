package stream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;



public class StreamObject {

	private static final String ERROR_TASK_ALREADY_EXISTS = "Error: \"%1$s\" already exists in the tasks list.";
	private static final String ERROR_TASK_DOES_NOT_EXIST = "Error: The task \"%1$s\" does not exist.";
	private static final String ERROR_NEW_TASK_NAME_NOT_AVAILABLE = "Error: The name \"%1$s\" is not available.";

	private HashMap<String, Task> allTasks;

	public StreamObject() {
		this.allTasks = new HashMap<String, Task>();
	}

	/**
	 * Adds a new task to StreamObject
	 * 
	 * <p>Precondition: newTaskName != null</p>
	 * 
	 * @param newTaskName name of the new task
	 * @throws ModificationException if task with 
	 * newTaskName is already present.
	 */
	public void addTask(String newTaskName) throws ModificationException {
		if (this.allTasks.containsKey(newTaskName)) {
			throw new ModificationException(String.format(ERROR_TASK_ALREADY_EXISTS, 
					newTaskName));
		} else {
			this.allTasks.put(newTaskName, new Task(newTaskName));
		}
	}

	/**
	 * Checks if a specific task is present
	 * 
	 * <p>Precondition: taskName != null</p>
	 * 
	 * @param taskName name of task to check for
	 * @return true if task with the given
	 * taskName is found.
	 */
	public Boolean hasTask(String taskName) {
		return this.allTasks.containsKey(taskName);
	}

	//@author A0118007R
	/**
	 * Gets a specific task
	 * 
	 * <p>Precondition: taskName != null</p>
	 * 
	 * @param taskName name of task to be returned
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found
	 */
	public Task getTask(String taskName) throws ModificationException{
		if (hasTask(taskName)){
			return allTasks.get(taskName);
		} else {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					taskName));
		}
	}

	//@author A0118007R
	/**
	 * Deletes a specific task
	 * 
	 * <p>Precondition: taskName != null</p>
	 * 
	 * @param taskName name of task to be deleted
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found
	 */
	public void deleteTask(String taskName) throws ModificationException {
		if(hasTask(taskName)){
			allTasks.remove(taskName);
		} else {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					taskName));
		}
	}

	//@author A0096529N
	/**
	 * Modify a task's deadline
	 * 
	 * <p>Precondition: taskName, deadline != null</p>
	 * 
	 * @param taskName to be modified
	 * @param deadline to be set in the task
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found
	 */
	public void changeDeadline(String taskName, Calendar deadline) 
			throws ModificationException {
		Task task = allTasks.get(taskName);
		if (task == null) {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					taskName));
		}
		task.setDeadline(deadline);
	}

	//@author A0096529N
	/**
	 * Remove the given tag from a specified task
	 * 
	 * <p>Precondition: taskName, tag != null</p>
	 * 
	 * @param taskName to be modified
	 * @param tag to be removed from the task
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found
	 */
	public void removeTag(String taskName, String tag) 
			throws ModificationException {
		Task task = getTask(taskName);
		task.deleteTag(tag);
	}
	
	//@author A0096529N
	/**
	 * Add the given tag from a specified task. Does
	 * nothing if tag already present.
	 * 
	 * <p>Precondition: taskName, tag != null</p>
	 * 
	 * @param taskName to be modified
	 * @param tag to be added to the task
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found
	 */
	public void addTag(String taskName, String tag) 
			throws ModificationException {
		Task task = getTask(taskName);
		if (!task.hasTag(tag)) {
			task.addTag(tag);
			// TODO update UI job done
		}
	}
	
	//@author A0096529N
	/**
	 * Change task name of the task
	 * 
	 * <p>Precondition: taskName, newName != null</p>
	 * 
	 * @param taskName to be modified
	 * @param newTaskName name to be set to the task
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found. Or
	 * when task with newTaskName is already present.
	 */
	public void updateTaskName(String taskName, String newTaskName) 
			throws ModificationException {
		Task task = getTask(taskName);
		if (allTasks.containsKey(newTaskName)) {
			throw new ModificationException(String.format(ERROR_NEW_TASK_NAME_NOT_AVAILABLE, 
					newTaskName));
		}

		allTasks.remove(task.getTaskName());
		task.setTaskName(newTaskName);
		allTasks.put(newTaskName, task);
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
	 * the key phrase, empty list if nothing matches
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