package ratson.utils;

public class Strings {
	public static String replaceAll(String str, String[] orig, String[] repls){
		if (orig.length != repls.length)
			throw new RuntimeException("Numbers of the originals and replacements not match");
		StringBuffer dest = new StringBuffer();
		replaceAll(str, orig, repls, 0, dest);
		return dest.toString();
	}
	
	public static String replaceAllWords(String str, String[] orig, String[] repls){
		if (orig.length != repls.length)
			throw new RuntimeException("Numbers of the originals and replacements not match");
		StringBuffer dest = new StringBuffer();
		replaceAllWords(str, orig, repls, 0, dest);
		return dest.toString();
	}

	private static void replaceAll(String str, String[] origs, String[] repls, int replIndex, StringBuffer dest){
		
		if (replIndex >= origs.length){ //noting left to replace
			dest.append(str);
			return;
		}
		
		String orig = origs[replIndex];
		String repl = repls[replIndex];
		
		int pos = 0;
		
		
		int orig_len = orig.length();
		if (orig_len == 0)
			throw new  RuntimeException("Original string can not be empty");
		
		while (pos < str.length()){
			//find the occurence
			int occurence = str.indexOf(orig, pos);
			if (occurence == -1)
				break;//noting found, finish
			//copy the original part, procedssed further
			replaceAll(str.substring(pos, occurence),origs, repls,replIndex + 1, dest); 
			//copy the replacement
			dest.append(repl);
			//move next
			pos = occurence + orig_len;
		}
		//copy the remainder
		replaceAll(str.substring(pos),origs, repls,replIndex + 1, dest); 
	}
	
	/**replacing all occurenses that are whole word*/
	private static void replaceAllWords(String str, String[] origs, String[] repls, int replIndex, StringBuffer dest){
		
		if (replIndex >= origs.length){ //noting left to replace
			dest.append(str);
			return;
		}
		
		String orig = origs[replIndex];
		String repl = repls[replIndex];
		
		int pos = 0;
		
		
		int orig_len = orig.length();
		if (orig_len == 0)
			throw new  RuntimeException("Original string can not be empty");
		
		while (pos < str.length()){
			//find the occurence
			int occurence = findWordOccurence(str, orig, pos);
			if (occurence == -1)
				break;//noting found, finish
			
			//copy the original part, procedssed further
			replaceAllWords(str.substring(pos, occurence),origs, repls,replIndex + 1, dest); 
			//copy the replacement
			dest.append(repl);
			//move next
			pos = occurence + orig_len;
		}
		//copy the remainder
		replaceAllWords(str.substring(pos),origs, repls,replIndex + 1, dest); 
	}
	
	private static int findWordOccurence(String str, String orig, int pos) {
		// TODO Auto-generated method stub
		int occurence;
		while (true){
			occurence = str.indexOf(orig, pos);
			if (occurence == -1)//noting found
				return -1;
			//check word boundary
			if (isLiteralAt(str, occurence-1) || isLiteralAt(str, occurence+orig.length())){
				//No word boundary
				pos = occurence +1;
			}else{
				//Ok, it is a word boundary
				return occurence;
			}

		}
	}

	/**Checks, whether there is a identified character in the string at given position.
	 * position may be outside strinrg bounds
	 * @param str
	 * @param idx
	 * @return
	 */
	private static boolean isLiteralAt(String str, int idx) {
		if (idx <0)
			return false;
		if (idx >= str.length())
			return false;
		char c = str.charAt(idx);
		if ('a'<=c && c<='z' || 'A'<=c && c<='Z' || '0'<=c && c<='9' || c=='_' || c=='.')
			return true;
		return false;
	}

	private static void replaceAll(String s, String orig, String repl, StringBuffer buf){
		
		int pos = 0;
		int orig_len = orig.length();
		if (orig_len == 0)
			throw new  RuntimeException("Original string can not be empty");
		
		while (pos < s.length()){
			//find the occurence
			int occurence = s.indexOf(orig, pos);
			if (occurence == -1)
				break;//noting found, finish
			//copy the original part
			buf.append(s.subSequence(pos, occurence));
			//copy the replacement
			buf.append(repl);
			//move next
			pos = occurence + orig_len;
		}
		//copy the remainder
		buf.append(s.subSequence(pos, s.length()));
	}
	
	public static void main(String[] args) {
		System.out.println("Testing string replecemtn code");
		String s = "Hello, this is a test string";
		System.out.println("Original:"+s);
		StringBuffer sb = new StringBuffer();
		replaceAll(s,"s","AAA", sb);
		System.out.println("Replaced:"+sb.toString());
		System.out.println("ReplAll:"+replaceAll(s, 
				new String[]{"Hell","test", "u"}, 
				new String[]{"Fuck", "fucking","AAA"}
		));
		System.out.println("ReplAllW:"+replaceAllWords(s, 
				new String[]{"Hell","test", "a"}, 
				new String[]{"Fuck", "fucking","AAA"}
		));
	}
}
