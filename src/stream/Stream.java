package stream;
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
	
	public static void main(String[] args){
		System.out.println(MESSAGE_WELCOME);
	}

}
