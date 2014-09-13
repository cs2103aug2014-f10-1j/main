import java.util.ArrayList;
import java.util.HashMap;

public class StreamObject {

	private static String ERROR_TASK_ALREADY_EXISTS = "Error: \"%1$s\" already exists in the tasks list.";

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

}