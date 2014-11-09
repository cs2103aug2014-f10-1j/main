package util;

import java.awt.Font;

import javax.swing.ImageIcon;

/**
 * A class to contain all external loaded files (images, fonts).
 * 
 * @version V0.5
 */
public class StreamExternals {

	public static ImageIcon HEADER;
	public static ImageIcon ICON_DONE;
	public static ImageIcon ICON_NOT_DONE;
	public static ImageIcon ICON_OVERDUE;
	public static ImageIcon ICON_HI_RANK;
	public static ImageIcon ICON_MED_RANK;
	public static ImageIcon ICON_LOW_RANK;
	public static ImageIcon ICON_CALENDAR;
	public static ImageIcon ICON_NULL_CALENDAR;
	public static Font FONT_TITLE;
	public static Font FONT_CONSOLE;

	public static void init(ImageIcon header, ImageIcon doneIcon,
			ImageIcon notDoneIcon, ImageIcon overdueIcon, ImageIcon hiRankIcon,
			ImageIcon medRankIcon, ImageIcon lowRankIcon, ImageIcon calIcon,
			ImageIcon nullCalIcon, Font titleFont, Font consoleFont) {
		HEADER = header;
		ICON_DONE = doneIcon;
		ICON_NOT_DONE = notDoneIcon;
		ICON_OVERDUE = overdueIcon;
		ICON_HI_RANK = hiRankIcon;
		ICON_MED_RANK = medRankIcon;
		ICON_LOW_RANK = lowRankIcon;
		ICON_CALENDAR = calIcon;
		ICON_NULL_CALENDAR = nullCalIcon;
		FONT_TITLE = titleFont;
		FONT_CONSOLE = consoleFont;
	}

}
