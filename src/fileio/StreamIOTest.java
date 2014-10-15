package fileio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import model.StreamTask;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exception.StreamIOException;

//@author A0096529N
public class StreamIOTest {

	private static final String FAIL_EXCEPTION_MESSAGE = "%1$s, caused by %2$s - %3$s";
	private static final String CREATEFILE_EXCEPTION_MESSAGE = "Test file could not be created "
			+ "- %1$s\nDelete the file if already present.";
	private static final String CHECKFILE_EXCEPTION_MESSAGE = "Check file could not be created "
			+ "- %1$s\nDelete the file if already present.";
	private static final String CHECK_FILE = "streamtestCheckFile.json";
	private static final String TEST_SAVE_LOCATION = "streamtest.json";
	private StreamTask task1, task2;
	private HashMap<String, StreamTask> map;

	@Before 
	public void setUp() throws Exception {
		StreamIO.SAVE_LOCATION = TEST_SAVE_LOCATION;
		File saveFile = new File(StreamIO.SAVE_LOCATION);
		if (!saveFile.createNewFile()) {
			throw new IOException(String.format(CREATEFILE_EXCEPTION_MESSAGE, saveFile.getAbsolutePath()));
		}

		task1 = new StreamTask("Code Jarvis");
		Calendar calendar = Calendar.getInstance();
		Date date = StreamIO.dateFormat.parse("20410719000000");
		calendar.setTime(date);		// instead of Calendar.set(), for loadTest, serialized calendar.
		task1.setDeadline(calendar);
		task1.setDescription("Just\na\nRather\nVery\nIntelligent\nSystem");
		task1.addTag("epic");
		task1.addTag("impossible");

		task2 = new StreamTask("Build IoT");
		Calendar calendar2 = Calendar.getInstance();
		Date date2 = StreamIO.dateFormat.parse("20180101123456");
		calendar2.setTime(date2);	// instead of Calendar.set(), for loadTest, serialized calendar.
		task2.setDeadline(calendar2);
		task2.setDescription("Internet of Things");
		task2.addTag("epic");
		task2.addTag("popular");
		task2.addTag("urgent");

		map = new HashMap<String, StreamTask>();
		map.put(task1.getTaskName().toLowerCase(), task1);
		map.put(task2.getTaskName().toLowerCase(), task2);


		String fileContent = "[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]";
		
		File checkFile = new File(CHECK_FILE);
		try {
			stringToFile(checkFile, fileContent);
		} catch (IOException e) {
			throw new IOException(String.format(CHECKFILE_EXCEPTION_MESSAGE, e.getMessage()), e);
		}
	}
	@After 
	public void tearDown() throws Exception {
		new File(StreamIO.SAVE_LOCATION).delete();
		new File(CHECK_FILE).delete();
	}

	@Test
	public void saveTest() {
		String testMessage = "Write map to file";
		String expectedFileContent = "[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]";
		try {
			File saveFile = new File(StreamIO.SAVE_LOCATION);
			StreamIO.save(map);
			assertEquals(testMessage, expectedFileContent, fileToString(saveFile));
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		} catch (IOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "IOException", e.getMessage()));
		}
	}
	@Test
	public void loadTest() {
		String testMessage = "Load map from file";
		StreamIO.SAVE_LOCATION = CHECK_FILE;
		try {
			String expectedMap = serializeTaskMap(map);
			String actualMap = serializeTaskMap(StreamIO.load());
			System.out.println(expectedMap);
			System.out.println(actualMap);
			assertEquals(testMessage, expectedMap, actualMap);
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		} finally {
			StreamIO.SAVE_LOCATION = TEST_SAVE_LOCATION;
		}
	}

	@Test 
	public void mapToJsonTest() {
		String testMessage = "Map to JSON conversion";
		String expectedJsonString = "[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]";
		try {
			assertEquals(testMessage, expectedJsonString, StreamIO.mapToJson(map).toString());
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		}
	}

	@Test 
	public void taskToJsonTest() {
		testOneTaskToJson("Task to JSON conversion - Code Jarvis", 
				"{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
						+ "\"taskName\":\"Code Jarvis\","
						+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"}", task1);
		testOneTaskToJson("Task to JSON conversion - Build IoT", 
				"{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
						+ "\"taskName\":\"Build IoT\","
						+ "\"taskDescription\":\"Internet of Things\"}", task2);
	}
	private void testOneTaskToJson(String testMessage, String expected, StreamTask task) {
		try {
			assertEquals(testMessage, expected, StreamIO.taskToJson(task).toString());
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		}
	}

	@Test
	public void formatDateTest() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
		testOneFormatDate("Format date 11:22:33 30/05/1980", "19800530112233", simpleDateFormat.parse("19800530112233"));
		testOneFormatDate("Format date 23:00:33 30/12/2055", "20551230230033", simpleDateFormat.parse("20551230230033"));
		testOneFormatDate("Format date 17:09:00 02/10/2014", "21041002170900", simpleDateFormat.parse("21041002170900"));

		Calendar calendar = Calendar.getInstance();
		calendar.set(1965, 7, 9, 0, 0, 0);
		testOneFormatCalendar("Format calendar 00:00:00 09/08/1965", "19650809000000", calendar);
		calendar.set(2000, 0, 1, 12, 34, 56);
		testOneFormatCalendar("Format calendar 12:34:56 01/01/2000", "20000101123456", calendar);
		calendar.set(2014, 11, 24, 23, 59, 59);
		testOneFormatCalendar("Format calendar 23:59:59 24/12/2014", "20141224235959", calendar);
	}
	private void testOneFormatDate(String testMessage, String expected, Date date) {
		assertEquals(testMessage, expected, StreamIO.formatDate(date));
	}
	private void testOneFormatCalendar(String testMessage, String expected, Calendar calendar) {
		assertEquals(testMessage, expected, StreamIO.formatDate(calendar));
	}

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
	private void stringToFile(File destin, String content) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(content.getBytes());
		}
	}
	private String serializeTaskMap(HashMap<String, StreamTask> taskMap) {
		JSONObject taskMapJson = new JSONObject(taskMap);
		return taskMapJson.toString();
	}
}
