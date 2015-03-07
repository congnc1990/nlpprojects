import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static final String CONFIG_FILE = "config.properties";
	Properties props = new Properties();
	public Config() {
		Load(CONFIG_FILE);
	}
	
	public Config(String config) {
		Load(config);
	}
	
	public boolean Load(String config)
	{
		FileInputStream in = null;
		try {
			in = new FileInputStream(config);
			props.load(in);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		}
		return false;
	}
	
	public String getDictionaryFile()
	{
		return props.getProperty("DICTIONARY", "./vndict/dictionary.txt");
	}
	
	public int getDefaultMaxResults()
	{
		return Integer.parseInt(props.getProperty("DEFAULT_MAX_RESULTS", "3"), 10);
	}
}
