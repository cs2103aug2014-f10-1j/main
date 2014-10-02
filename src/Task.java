public class Task {

	private String taskName;
	private String taskDescription;
	
	private final String ERROR_NO_DESCRIPTION =  "Error: The task \"%1$s\" does not have a description.";

	public Task(String taskName) {
		this.taskName = taskName;
		this.taskDescription = null;
	}

	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}
	
	/**
	 * The methods below are related to set descriptions to a specified task
	 * 
	 * @author A0118007R
	 */
	
	public void setDescription(String description){
		this.taskDescription = description;
	}
	
	public String getDescription(){
		String currentDescription = this.taskDescription;
		if(currentDescription == null){
			return String.format(ERROR_NO_DESCRIPTION, this.getTaskName());
		} else {
			return currentDescription;
		}
	}
}