package fileio;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import stream.Task;

//@author A0096529N
/**
 * @author Steven Khong
 *
 */
public class FileIO {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
	static String SAVE_LOCATION = "stream.json";

	public static HashMap<String, Task> load() throws StreamIOException {
		JSONArray tasksJson = loadFromFile(new File(SAVE_LOCATION));
		return jsonToMap(tasksJson);
	}

	public static void save(HashMap<String, Task> allTasks) throws StreamIOException {
		JSONArray tasksJson = mapToJson(allTasks);
		writeToFile(new File(SAVE_LOCATION), tasksJson);
	}

	static void writeToFile(File destin, JSONArray tasksJson) throws StreamIOException {
		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(tasksJson.toString().getBytes());
		} catch (IOException e) {
			throw new StreamIOException("Could not write to file - " + e.getMessage());
		}
	}
	static JSONArray loadFromFile(File file) throws StreamIOException {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			String mapJsonString = stringBuilder.toString().trim();
			return new JSONArray(mapJsonString);
		} catch (IOException e) {
			throw new StreamIOException("Could not load file - " + e.getMessage());
		} catch (JSONException e) {
			throw new StreamIOException("File corrupted, could not parse file contents - " + e.getMessage());
		}
	}
	static JSONArray mapToJson(Map<String, Task> map) throws StreamIOException {
		JSONArray mapJson = new JSONArray();
		for (String key:map.keySet()) {
			JSONObject taskJson = taskToJson(map.get(key));
			mapJson.put(taskJson);
		}
		return mapJson;
	}
	static HashMap<String, Task> jsonToMap(JSONArray tasksJson) {
		return null;	//TODO implement method
	}
	static JSONObject taskToJson(Task task) throws StreamIOException {
		try {
			JSONObject taskJson = new JSONObject();
			taskJson.put(TaskKey.NAME, task.getTaskName());
			taskJson.put(TaskKey.DESCRIPTION, task.getDescription());
			taskJson.put(TaskKey.TAGS, task.getTags());
			taskJson.put(TaskKey.DEADLINE, formatDate(task.getDeadline()));
			return taskJson;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - " + e.getMessage());
		}
	}
	static Task jsonToTask(JSONObject taskJson) {
		return null;	//TODO implement method
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
