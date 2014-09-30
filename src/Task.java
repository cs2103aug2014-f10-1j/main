import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;

public class Task {
	

	private String taskName;
	private Calendar deadline = null;
	private ArrayList<String> tags = new ArrayList<String>();

	
	///////////Tasks Methods/////////////
	public Task(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}
	
	///////////Deadline Methods/////////////
	public setDeadline(Calendar deadline){
		this.deadline=deadline;
	}
	
	public Calendar getDeadline(){
		return this.deadline;
	}
	
	//This one needs to be checked later
	public boolean isDue(){
		return deadline.before(Calendar.getInstance());
	}
	
	///////////Tags Methods/////////////
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
		if(tags.hasTag(tag)){
			tags.remove(tag.toUpperCase());
		}
		else;
	}
	
}