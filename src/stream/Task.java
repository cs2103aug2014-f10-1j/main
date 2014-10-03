package stream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;

public class Task {
	

	private String taskName;

	private String taskDescription;
	
	private final String ERROR_NO_DESCRIPTION =  "Error: The task \"%1$s\" does not have a description.";

	private Calendar deadline = null;
	private ArrayList<String> tags = new ArrayList<String>();


	// Tasks Methods
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

	//Deadline Methods
	public void setDeadline(Calendar deadline){
		this.deadline=deadline;
	}
	
	public Calendar getDeadline(){
		return this.deadline;
	}
	
	//This one needs to be checked later
	public boolean isDue(){
		return deadline.before(Calendar.getInstance());
	}
	
	//Tags Methods
	public void addTag(String newTag){
		tags.add(newTag.toUpperCase());
		Collections.sort(tags);
	}
	
	public String getTag(int index){
		if(tags.isEmpty()){
			return null;
		}
		else{
			return tags.get(index);
		}
	}
	
	public ArrayList<String> getTags(){
		return tags;
	}
	
	public boolean hasTag(String tag){
		if(tags.contains(tag.toUpperCase())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void deleteTag(String tag){
		if(hasTag(tag)){
			tags.remove(tag.toUpperCase());
		}
		else;
	}
	

}