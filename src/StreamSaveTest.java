import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import model.StreamTask;

import org.junit.Before;
import org.junit.Test;

public class StreamSaveTest {
	private StreamTask task1, task2;
	private HashMap<String, StreamTask> map;
	private ArrayList<String> taskList;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.ENGLISH);
	private File testFile = new File("test.json");
	private Stream stream;

	// @author A0096529N
	@Before
	public void setUp() throws Exception {
		if (testFile.exists()) {
			testFile.delete();
		}

		stream = new Stream(testFile.getAbsolutePath());
		String taskName1 = "Code Jarvis";
		stream.addTask(taskName1);
		task1 = stream.stobj.getTask(taskName1);
		Calendar calendar = Calendar.getInstance();
		Date date = simpleDateFormat.parse("20410719000000");
		calendar.setTime(date);
		task1.setDeadline(calendar);
		task1.setDescription("Just\na\nRather\nVery\nIntelligent\nSystem");
		task1.addTag("epic");
		task1.addTag("impossible");

		String taskName2 = "Build IoT";
		stream.addTask(taskName2);
		task2 = stream.stobj.getTask(taskName2);
		Calendar calendar2 = Calendar.getInstance();
		Date date2 = simpleDateFormat.parse("20180101123456");
		calendar2.setTime(date2);
		task2.setDeadline(calendar2);
		task2.setDescription("Internet of Things");
		task2.addTag("epic");
		task2.addTag("popular");
		task2.addTag("urgent");

		map = new HashMap<String, StreamTask>();
		map.put(task1.getTaskName().toLowerCase(), task1);
		map.put(task2.getTaskName().toLowerCase(), task2);

		taskList = new ArrayList<String>();
		taskList.add(task1.getTaskName());
		taskList.add(task2.getTaskName());
	}

	// @author A0096529N
	@Test
	public void testSave() throws IOException {
		stream.save();

		String expectedContent = "{\"taskList\":{\"1\":\"Build IoT\",\"0\":\"Code Jarvis\"},"
				+ "\"allTasks\":[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"done\":false,\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"done\":false,\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]}";
		assertEquals("Saved state", expectedContent, fileToString(testFile));
	}

	// @author A0096529N
	private String fileToString(File file) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			return stringBuilder.toString().trim();
		}
	}
}