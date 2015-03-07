package linkgrammar.vnpostagger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vn.edu.hut.soict.lienkate.disjunct.Disjunct;
import vn.edu.hut.soict.lienkate.parser.Connection;

/**
 * Note: mutable class
 * @author ngocminh.oss
 *
 */
public class Word {

	private String string;
	private Disjunct disjunct;
	private Language language;
	private List<Connection> connections;
	private Map<String, Object> featureMap = new HashMap<String, Object>();
	private List<String> candidates = new ArrayList<String>();

	public Word(String string, Disjunct disjunct, List<Connection> connections) {
		this(string, disjunct, connections, Language.SOURCE);
	}
	
	public Word(String string, Language language) {
		this(string, new Disjunct(), new LinkedList<Connection>(), language);
	}
	
	public Word(String string, Disjunct disjunct, List<Connection> connections, Language language) {
		super();
		this.string = string;
		this.disjunct = disjunct;
		this.connections = connections;
		this.language = language;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language state) {
		this.language = state;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	public Disjunct getDisjunct() {
		return disjunct;
	}
	
	public void setDisjunct(Disjunct disjunct) {
		this.disjunct = disjunct;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}
	
	public Map<String, Object> getFeatureMap() {
		return featureMap;
	}
	
	public List<String> getCandidates() {
		return candidates;
	}
	
	@Override
	public String toString() {
		return disjunct == null ? string : string + disjunct;
	}

	public boolean isNoun() {
		return disjunct != null && "n".equals(disjunct.getCategory());
	}
	
	public boolean isVerb() {
		return disjunct != null && "v".equals(disjunct.getCategory());
	}
	
	public boolean isAdjective() {
		return disjunct != null && "a".equals(disjunct.getCategory());
	}
	
	public static boolean isRemovalMark(String candidate) {
		return candidate.equals("!");
	}

	public static enum Language {
		SOURCE, DEST
	}

}
