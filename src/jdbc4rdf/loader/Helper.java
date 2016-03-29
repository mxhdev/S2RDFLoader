package jdbc4rdf.loader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Helper {

	
		  
	
	/*
	public static String cleanPredicate(String pred) {
		pred = pred.replace(":", "__");
		
		return pred;
	}
	*/
	
	
	
	public static String getPartName(String p) {
		// special characters
		p = p.replaceAll("[:]|[#]|[-]|[/]|[.]", "_");
		// brackets
		p = p.replaceAll("[<]|[>]", "");
		
		return p;
	}
	
	
	public static String cleanObject(String obj, boolean isTimestamp) {
		String result = obj;
		
		// if there might be some kind of type definition
		if (obj.contains("^^")) {
			
			final Pattern p = Pattern.compile("(?<=\").*?(?=\")");
			final Matcher matcher = p.matcher(obj);
			
			result = matcher.group(1);
			
			if (isTimestamp) {
				result = result.replace("T", " ");
			}
		}
		
		
		return result;
	}
	
	
	public static String getRatio(int x1, int x2) {
		
		final String res = String.valueOf( ( ((float) x1) / ((float) x2) ) );
		
		return res;
		
	}
}
