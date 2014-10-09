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

	public void addTask(String newTask) throws ModificationException {
		if (this.allTasks.containsKey(newTask)) {
			throw new ModificationException(String.format(ERROR_TASK_ALREADY_EXISTS, 
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
	public Task getTask(String task) throws ModificationException{
		if (hasTask(task)){
			return allTasks.get(task);
		} else {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					task));
		}
	}

	/**
	 * Deletes a specific task
	 * 
	 * @author A0118007R
	 */
	public void deleteTask(String task) throws ModificationException {
		if(hasTask(task)){
			allTasks.remove(task);
		} else {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					task));
		}
	}

	//@author A0096529N
	/**
	 * Modify a task's deadline
	 * 
	 * <p>Precondition: deadline != null</p>
	 * 
	 * @param taskName to be modified
	 * @param deadline to be set in the task
	 * @throws ModificationException 
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
	 * <p>Precondition: tag != null</p>
	 * 
	 * @param taskName to be modified
	 * @param tag to be removed from the task
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found
	 */
	public void removeTag(String taskName, String tag) 
			throws ModificationException {
		Task task = allTasks.get(taskName);
		if (task == null) {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					taskName));
		}
		task.deleteTag(tag);
	}
	
	//@author A0096529N
	/**
	 * Add the given tag from a specified task. Does
	 * nothing if tag already present.
	 * 
	 * <p>Precondition: tag != null</p>
	 * 
	 * @param taskName to be modified
	 * @param tag to be added to the task
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found
	 */
	public void addTag(String taskName, String tag) 
			throws ModificationException {
		Task task = allTasks.get(taskName);
		if (task == null) {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					taskName));
		}
		if (!task.hasTag(tag)) {
			task.addTag(tag);
			// TODO update UI job done
		}
	}
	
	//@author A0096529N
	/**
	 * Change task name of the task
	 * 
	 * <p>Precondition: newName != null</p>
	 * 
	 * @param taskName to be modified
	 * @param newTaskName name to be set to the task
	 * @throws ModificationException if taskName given
	 * does not return a match, i.e. task not found. Or
	 * when task with newTaskName is already present.
	 */
	public void updateTaskName(String taskName, String newTaskName) 
			throws ModificationException {
		Task task = allTasks.get(taskName);
		if (task == null) {
			throw new ModificationException(String.format(ERROR_TASK_DOES_NOT_EXIST, 
					taskName));
		} else if (allTasks.containsKey(newTaskName)) {
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