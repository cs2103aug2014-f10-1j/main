import java.util.ArrayList;
import java.util.Collections;

public class Task {
	
	private static final String MESSAGE_NOT_FOUND = "Not found! Please try again!";

	private String taskName;
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