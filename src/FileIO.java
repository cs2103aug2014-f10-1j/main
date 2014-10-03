import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//@author A0096529N
/**
 * @author Steven Khong
 *
 */
public class FileIO {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
	static String SAVE_LOCATION = "stream.json";
	
	public static HashMap<String, Task> load() throws IOException {
		return null;
	}
	public static void save(HashMap<String, Task> allTasks) throws IOException {
		try {
			JSONArray tasksJson = mapToJson(allTasks);
			writeToFile(new File(SAVE_LOCATION), tasksJson);
		} catch (JSONException e) {
			throw new IOException("JSON conversion failed - " + e.getMessage());
		}
	}
	
	static void writeToFile(File destin, JSONArray tasksJson) throws IOException {

		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(tasksJson.toString().getBytes());
		}
	}
	static JSONArray mapToJson(Map<String, Task> map) throws JSONException {
		JSONArray mapJson = new JSONArray();
		for (String key:map.keySet()) {
			JSONObject taskJson = taskToJson(map.get(key));
			mapJson.put(taskJson);
		}
		
		return mapJson;
	}
	static JSONObject taskToJson(Task task) throws JSONException {
		JSONObject taskJson = new JSONObject();
		taskJson.put(TaskKey.NAME, task.getTaskName());
		taskJson.put(TaskKey.DESCRIPTION, task.getDescription());
		taskJson.put(TaskKey.TAGS, task.getTags());
		taskJson.put(TaskKey.DEADLINE, formatDate(task.getDeadline()));
		return taskJson;
	}
	static String formatDate(Calendar calendar) {
		if (calendar == null) {
			return null;
		} else {
			return formatDate(calendar.getTime());
		}
	}
	static String formatDate(Date date) {
		if (date == null) {
			return null;
		} else {
			return dateFormat.format(date);
		}
	}

	private class TaskKey {
		static final String DEADLINE = "deadline";
		static final String NAME = "taskName";
		static final String DESCRIPTION = "taskDescription";
		static final String TAGS = "tags";
	}
}
