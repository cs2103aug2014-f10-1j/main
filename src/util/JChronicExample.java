package util;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;
import util.StreamUtil;

public class JChronicExample {

	public static void main(String[] args) {
		String[] examples = { "7 hours from now", "7 hours later",
				"1 hour 30 minutes from now", "7 minutes from now",
				"7 minutes ago", "7 hours ago", "tomorrow 2.15pm",
				"next thursday noon", "last monday evening", "dec 13 midnight",
				"nov 13", "dec 13 10", "dec next year", "last june",
				"next july", "dec 16 next year", "june 10 last year" };
		for (String s : examples) {
			Span x;
			try {
				x = Chronic.parse(s);
				System.out.println("\"" + s + "\"\t parsed to: "
						+ StreamUtil.getCalendarWriteUp(x.getBeginCalendar()));
			} catch (Exception e) {
				System.out.println("\"" + s + "\" cannot be parsed");
			}
		}
	}

}
