package linkgrammar.vnpostagger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import vn.edu.hut.soict.lienkate.disjunct.Disjunct;
import vn.edu.hut.soict.lienkate.grammar.Connector;
import vn.edu.hut.soict.lienkate.parser.Connection;
import vn.edu.hut.soict.lienkate.parser.Linkage;
import vn.edu.hut.soict.lienkate.parser.RightComparator;

public final class WordUtils {

	private WordUtils() {
	}

	static List<Word> toWords(String... strings) {
		return toWords(Arrays.asList(strings));
	}

	public static List<Word> toWords(List<String> strings) {
		ArrayList<Word> words = new ArrayList<Word>(strings.size());
		for (int i = 0; i < strings.size(); i++) {
			words.add(new Word(strings.get(i), null));
		}
		return words;
	}
	
	static List<Word> toWords(List<String> strings, Linkage linkage) {
		ArrayList<Word> words = new ArrayList<Word>(strings.size());
		for (int i = 0; i < strings.size(); i++) {
			words.add(new Word(strings.get(i), linkage.getDisjuncts().get(i),
					linkage.link.get(i)));
		}
		return words;
	}
	
	public static List<String> toStrings(List<Word> words) {
		ArrayList<String> strings = new ArrayList<String>(words.size());
		for (Word word : words) {
			strings.add(word.getString());
		}
		return strings;
	}

	public static <T> String join(List<T> words) {
		StringBuilder sb = new StringBuilder();
		for (T word : words) {
			String str = (word instanceof Word ? 
					((Word)word).getString() : word.toString());
			sb.append(str.replace(' ', '_')).append(' ');
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	public static List<Word> splitToWords(String string) {
		String[] chunks = string.split("\\s+");
		return toWords(chunks);
	}

	public static String[] splitAndRemoveUnderscore(String s) {
		String[] words = s.split("\\s+");
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].replace('_', ' ');
		}
		return words;
	}
	
	public static String joinWithUnderscore(String[] words) {
		return joinWithUnderscore(Arrays.asList(words));
	}

	public static String joinWithUnderscore(List<String> words) {
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(word.replace(' ', '_')).append(' ');
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	public static Connection getFirstConnectionOfType(List<Word> words,
			String type) {
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).getConnections() == null) {
				continue;
			}
			for (Connection c : words.get(i).getConnections()) {
				if (c.type().startsWith(type)) {
					return c;
				}
			}
		}
		return null;
	}

	/**
	 * Rebuild disjunct of all words after massive changes of
	 * connections.
	 * @param words
	 */
	public static void rebuildDisjuncts(List<Word> words) {
		clearDisjuncts(words);
		for (Word word : words) {
			Collections.sort(word.getConnections(),
					Collections.reverseOrder(new RightComparator()));
			for (Connection connection : word.getConnections()) {
				if (word.getDisjunct() == null) {
					word.setDisjunct(new Disjunct());
				}
				word.getDisjunct().right()
						.add(Connector.valueOf(connection.type() + "+"));
				if (words.get(connection.right()).getDisjunct() == null) {
					words.get(connection.right()).setDisjunct(new Disjunct());
				}
				words.get(connection.right()).getDisjunct().left()
						.add(Connector.valueOf(connection.type() + "-"));
			}
		}
	}

	private static void clearDisjuncts(List<Word> words) {
		for (Word word : words) {
			if (word.getDisjunct() == null) {
				continue;
			}
			word.getDisjunct().left().clear();
			word.getDisjunct().right().clear();
		}
	}

}
