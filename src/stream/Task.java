package stream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;

public class Task {
	
	private final String ERROR_NO_DESCRIPTION =  "Error: The task \"%1$s\" does not have a description.";
	private final String ERROR_TAG_DOES_NOT_EXIST = "Error: The tag \"%1$s\" does not exist.";
	
	//Attributes
	private String taskName;
	private String taskDescription;
	private Calendar deadline;
	private ArrayList<String> tags;
	private boolean isDone;


	//Constructor
	public Task(String taskName) {
		this.taskName = taskName;
		this.taskDescription = null;
		this.deadline = null;
		this.tags = new ArrayList<String>();
		this.isDone = false;
	}

	
	//Name Part
	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}
	
	//Description Part
	//@author A0118007R  
	//updated by A0119401U
	
	public void printTaskDetails(){
		System.out.println("Task name = " + taskName);
		System.out.println("Description = " + taskDescription);
		String strDeadline = null;
		if(deadline==null){
			strDeadline = "not specified";
		}
		else{
			strDeadline+=deadline.get(Calendar.YEAR);
			strDeadline+=(deadline.get(Calendar.MONTH)+1);
			strDeadline+=deadline.get(Calendar.DATE);
		}
		System.out.println("Deadline = " + strDeadline);
		System.out.println("Tags = ");
		if(isDone){
			System.out.println("Status: Done.");
		}
		else{
			System.out.println("Status: Not finished.");
		}
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
	
	//@author A0119401U
	//Deadline Part
	public void setDeadline(Calendar deadline){
		this.deadline=deadline;
	}
	
	public Calendar getDeadline(){
		return this.deadline;
	}
	
	//@author A0118007R
	
	public void setNullDeadline(){
		this.deadline = null;
	}
	//@author A0119401U
	
	//This one needs to be checked later
	public boolean isDue(){
		return deadline.before(Calendar.getInstance());
	}
	
	//Tags Part
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
	
	//Mark_As_Done Part
	
	public boolean getProgress(){
		return this.isDone;
	}
	
	public void markAsDone(){
		this.isDone = true;
	}
	
	public void markAsOngoing(){
		this.isDone = false;
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