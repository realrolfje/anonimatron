package com.rolfje.anonimatron;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LoremIpsum {
	public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
			+ "Maecenas mattis lectus sed magna elementum vel fermentum felis malesuada. "
			+ "Suspendisse quis dui non risus elementum blandit id at nunc. "
			+ "Fusce eleifend lorem quis est aliquet eu vestibulum velit placerat. "
			+ "Aliquam eu lacus urna. In elementum enim ac augue imperdiet et dapibus sapien imperdiet. "
			+ "Ut sed tellus quis turpis lobortis rhoncus. Donec ac tempor augue. "
			+ "Quisque eu neque diam. Nunc libero sapien, tempor sit amet porta ac, malesuada feugiat risus. "
			+ "Pellentesque posuere dolor nisl, non euismod erat. Sed odio arcu, interdum non tempus ut, ultricies vitae sem. "
			+ "In et massa lorem. In ut blandit dolor. Vivamus eu rutrum odio. "
			+ "Etiam aliquam rutrum turpis, congue commodo diam auctor nec. "
			+ "Donec eleifend, arcu et elementum cursus, orci enim consectetur turpis, ut fermentum justo purus nec lacus. "
			+ "Vivamus non mauris vel tortor placerat feugiat at et neque. Aenean vitae nisl sed eros consequat facilisis. "
			+ "Duis lacinia dignissim augue, ut vestibulum nunc faucibus non. Proin viverra ullamcorper pharetra. "
			+ "Etiam non convallis massa. Sed est massa, elementum eget rhoncus dignissim, placerat vel nisl. "
			+ "Mauris ac ligula sed elit rhoncus ullamcorper. "
			+ "Nullam interdum nisl vel ante consectetur ullamcorper aliquet lacus semper. "
			+ "Ut nibh nibh, fermentum eget dapibus nec, scelerisque ac tortor. Nulla non volutpat urna. "
			+ "Quisque cursus ullamcorper lorem, vitae porttitor ligula cursus id. "
			+ "Pellentesque ut tortor ac dolor venenatis tincidunt imperdiet sit amet metus. "
			+ "Mauris in ante vitae nunc sodales sagittis eu sit amet dui. "
			+ "Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. "
			+ "Vivamus auctor, lectus ut auctor dictum, justo mauris hendrerit orci, id gravida justo lacus et sem. "
			+ "Curabitur non nisl sit amet erat rutrum ornare non vitae massa. "
			+ "In ac ipsum nec ante feugiat euismod vel et felis. "
			+ "Sed laoreet, quam ac posuere egestas, risus felis mollis risus, at laoreet nibh turpis quis urna. "
			+ "Donec ut arcu vel justo elementum dignissim. Mauris sit amet mauris tortor, a tempor enim. "
			+ "Vestibulum ac ligula eget ipsum pulvinar fermentum a ut massa. "
			+ "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. "
			+ "Donec non tortor vel nulla bibendum iaculis ut eget quam. "
			+ "Curabitur purus sem, interdum vel pharetra quis, condimentum et erat. "
			+ "Sed leo neque, dictum sollicitudin dapibus eget, imperdiet non elit. "
			+ "Donec consequat porttitor ante, vel feugiat odio dictum quis. Suspendisse potenti. "
			+ "Duis ac ligula nibh, nec cursus neque. Sed commodo tristique eros vel lobortis. "
			+ "In hac habitasse platea dictumst. "
			+ "Morbi convallis, arcu ac facilisis pellentesque, ipsum mi rutrum augue, sit amet tincidunt nisi leo id arcu. "
			+ "Nunc erat dolor, hendrerit nec bibendum a, sollicitudin vel ante. "
			+ "Nulla dapibus turpis in risus molestie dapibus. Nam nec magna sed diam iaculis congue suscipit sit amet elit. "
			+ "Curabitur vel enim quam. Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
			+ "Cras fermentum ornare neque, vitae gravida felis convallis a. Donec nec lacus sapien. "
			+ "Vestibulum ac magna nunc, sed adipiscing mi. "
			+ "Quisque consectetur metus sit amet sem vehicula quis euismod enim eleifend. "
			+ "Donec in nibh non nisl gravida elementum non convallis mi. Nunc sit.";

	private static List<String> LOREM = null;
	private static int index = 0;

	static {
		LOREM = new ArrayList<String>();
		StringTokenizer t = new StringTokenizer(LOREM_IPSUM);
		while (t.hasMoreElements()) {
			String word = (String) t.nextElement();
			LOREM.add(word);
		}
	}

	public static String getWords(int numberOfWords) {
		String text = "";
		for (int i = 0; i < numberOfWords; i++) {
			text += ' ' + getNextWord();
		}
		return text.trim();
	}

	public static String getParagraphs(int numberOfParagraphs) {
		String text = "";

		int paragraphs = 0;
		while (paragraphs < numberOfParagraphs) {
			while (!text.endsWith(".")) {
				text += getNextWord();
			}
			text += "\n\n";
			paragraphs++;
		}
		return text.trim();
	}

	private static String getNextWord() {
		String word = LOREM.get(index);
		if (index == LOREM.size()) {
			index = 0;
		} else {
			index++;
		}
		return word;
	}

}