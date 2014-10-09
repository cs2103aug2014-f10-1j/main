package stream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;

public class Task {
	

	private String taskName;

	private String taskDescription;
	
	private final String ERROR_NO_DESCRIPTION =  "Error: The task \"%1$s\" does not have a description.";
	private final String ERROR_TAG_DOES_NOT_EXIST = "Error: The tag \"%1$s\" does not exist.";

	private Calendar deadline = null;
	private ArrayList<String> tags;


	// Tasks Methods
	public Task(String taskName) {
		this.taskName = taskName;
		this.taskDescription = null;
		this.tags = new ArrayList<String>();
	}

	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}
	
	//author A0118007R
	
	public void printTaskDetails(){
		System.out.println("Task name = " + taskName);
		System.out.println("Description = " + taskDescription);
		System.out.println("Deadline = ");
		System.out.println("Tags = ");
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
	
	//author Shenhao
	
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
		} else {
			System.out.println(String.format(ERROR_TAG_DOES_NOT_EXIST, tag));
		}
	}
	

	//@author A0096529N
	/**
	 * Checks if there is a match between a list
	 * of given keywords and the tags on this task.
	 * 
	 * <p>Precondition: tags != null</p>
	 * 
	 * @param tags list of keywords
	 * @return true if there is a match, false otherwise.
	 */
	public boolean hasTag(String[] tags) {
		for (String tag : tags) {
			if (this.hasTag(tag)) {
				return true;
			}
		}
		return false;
	}
}