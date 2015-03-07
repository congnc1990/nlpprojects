package linkgrammar.vnpostagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import utils.StringUtils;
import vn.edu.hut.soict.lienkate.dictionary.LazyDictionary;
import vn.edu.hut.soict.lienkate.dictionary.VietDictionaryReader;
import vn.edu.hut.soict.lienkate.dictionary.LazyDictionary.CategoryExpression;
import vn.edu.hut.soict.lienkate.parser.Linkage;
import vn.edu.hut.soict.lienkate.parser.NewLinkageDrawer;
import vn.edu.hut.soict.lienkate.parser.exprprune.ExpressionPruneParser;
import vn.hus.nlp.tokenizer.VietTokenizer;

public class JLinkGrammar {
	private static final String DICT_PATH = "vndict/dictionary.txt";
	private static final String TOKENIZER_CONFIG = "tokenizer.properties";
	
    private static VietTokenizer tokenizer = null;
    private static LazyDictionary dict = null;
    private static ExpressionPruneParser parser = null;
    private static String working_dir = null;
    private static String dictpath = DICT_PATH;
    
    public static String getJarContainingFolder(Class aclass) throws Exception {
    	  CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();
    	  File jarFile;
    	  if (codeSource.getLocation() != null) {
    	    jarFile = new File(codeSource.getLocation().toURI());
    	  }
    	  else {
    	    String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
    	    String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
    	    jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
    	    jarFile = new File(jarFilePath);
    	  }
    	  return jarFile.getParentFile().getAbsolutePath();
    	}
    
    public static String getJarFolder()
    {
    	if (working_dir == null)
			try {
				working_dir = System.getProperty("user.dir");//getJarContainingFolder(JLinkGrammar.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				working_dir = System.getProperty("user.dir");
			}
    	return working_dir;
    }
    
    public JLinkGrammar()
    {
    	System.out.println("WORKING_DIR: "+getJarFolder()); 
    }
    
    public void SetDictionaryPath(String dictpath)
    {
    	JLinkGrammar.dictpath = dictpath;
    }
    
    public static VietTokenizer getTokenizer(boolean bDetectSentences) {
        if (tokenizer == null) {
        	String tokenizer_config = getJarFolder() + "\\" + TOKENIZER_CONFIG;
        	System.out.println("tokenizer_config: "+tokenizer_config);
            tokenizer = new VietTokenizer(tokenizer_config);
        }
        if (bDetectSentences)
        	tokenizer.turnOnSentenceDetection();
        else
            tokenizer.turnOffSentenceDetection();
        return tokenizer;
    }
    
    public static LazyDictionary getDictionary() throws IOException, ParseException 
    {
    	if (dict == null)	{
    		//String dictpath = getJarFolder() + "\\" +DICT_PATH;
    		dict = new VietDictionaryReader().read(dictpath);
    	}
    	return dict;
    }
    
    public static ExpressionPruneParser getParser() throws IOException, ParseException
    {
    	if (parser == null)	{
    		parser = new ExpressionPruneParser(getDictionary());
    	}
    	return parser;
    }
    
    public static void reloadDictionary() throws IOException, ParseException
    {
		dict = new VietDictionaryReader().read(dictpath);
		parser = new ExpressionPruneParser(dict);
    }
    
    public LinkedList<String> GetWords(String sentence)
	{
        LinkedList<String> words;
        String tokenizedString = getTokenizer(false).segment(sentence);
        String[] arr = tokenizedString.split("\\s+");
        for (int i=0; i< arr.length; i++)
            arr[i] = arr[i].replace('_', ' ');
        words = new LinkedList<String>(Arrays.asList(arr));
        return words;
	}
    
    public LinkedList<List<String>> GetTokenizedSentences(String textInput)
    {
    	LinkedList<List<String>> listsentences = new LinkedList<List<String>>();
        String[] tokenizedSentences = getTokenizer(true).tokenize(textInput);
        for (String sentence : tokenizedSentences)
        {
        	sentence = sentence.replace(" _ ", "_");
        	String[] words = sentence.split("\\s+");
        	for (int i=0; i< words.length; i++)
        		words[i] = words[i].replace('_', ' ');
        	listsentences.add(new LinkedList<String>(Arrays.asList(words)));
        }
        return listsentences;
    }
    
	public String parse(String textInput, int nMaxResults) throws Exception {
		String output = "", retstring;
		ExpressionPruneParser parser = getParser();
		if (nMaxResults <1)
			nMaxResults = 3;
		parser.setMaxResults(nMaxResults);
		
		List<List<String>> sentences = GetTokenizedSentences(textInput);//readLinearList();
		
		int sentCount = 0;
		long start = System.currentTimeMillis();
		for (List<String> words : sentences) {
			retstring = "**** Phân tích câu: " + WordUtils.joinWithUnderscore(words);
			output += (retstring + "\r\n");
			System.out.println(retstring);
			boolean bAllWordsFound = true;
			for (String word : words)
			{
				Collection<CategoryExpression> exprStr =
						getDictionary().getRaw(word);
				if (exprStr == null)
				{
					bAllWordsFound = false;
					output += String.format("Từ \"%s\" không có trong từ điển !", word);
				}
			}
			sentCount++;
			if (!bAllWordsFound)
				continue;
			List<Linkage> linkages = Collections.emptyList();
			for (int cost = 0; cost < 4 && linkages.isEmpty(); cost++) {
				//	Parse all words in a sentences.
				linkages = parser.parse(words, cost);
			}
			if (!linkages.isEmpty()) {
				output+= "Found "+linkages.size()+ " linkages \r\n";
				retstring = NewLinkageDrawer.toString(linkages, words);
				System.out.println(retstring);
				output+=(retstring + "\r\n");
			}
			else
				output+= "No linkage found ! \r\n";
		}
		long stop = System.currentTimeMillis();
		retstring = "**** Thời gian phân tích: " + (stop-start)/1000.0/sentCount + "s";
		System.out.println(retstring);
		output+=(retstring + "\r\n");
		return output;
	}

	private List<List<String>> readLinearList() throws IOException {
		BufferedReader in = null;
		try {
			LinkedList<List<String>> sentences = new LinkedList<List<String>>();
			in = new BufferedReader(new FileReader("data/speed-test.txt"));
			String line;
			while ((line = in.readLine()) != null) {
				line=line.trim();
				if (line.startsWith("#")) {
					continue;
				}
				String[] chunks = line.split("\\s+");
				for (int i = 0; i < chunks.length; i++) {
					chunks[i] = chunks[i].replace('_', ' ');
				}
				sentences.add(Arrays.asList(chunks));
			}
			return sentences;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}
