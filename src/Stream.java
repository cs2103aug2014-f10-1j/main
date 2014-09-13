public class Stream {

	private static StreamObject st;

	public void initialize() {
		st = new StreamObject();
	}

	/**
	 * Adds a new task to the task list.
	 * 
	 * @author Wilson Kurniawan
	 * @command must include the new task to be added
	 */
	public void addTask(String newTask) throws Exception {
		st.addTask(newTask);
	}

	/**
	 * Checks whether a specific task is included in the task list. Mainly for
	 * testing purpose.
	 * 
	 * @author Wilson Kurniawan
	 */
	public Boolean hasTask(String task) {
		return st.hasTask(task);
	}

}
