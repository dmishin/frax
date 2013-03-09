package ratson.genimageexplorer.generators.janino;

import ratson.utils.Strings;

public class CodePreprocessor {
	private static String[] originals = new String[]{
		"sin",
		"cos",
		"tan",
		"exp",
		"log",
		"pow",
		"sinh",
		"cosh",
		"tanh",
		"asin",
		"acos",
		"atan",
		"atan2",
		"floor",
		"ceil",
		"abs",
		"sqrt",
		////////////////
		"asinh",
		"acosh",
		"atanh",
		"sqr",
		"frac",
		"mod"
	};
	private static String[] replacements = new String[]{
		"Math.sin",
		"Math.cos",
		"Math.tan",
		"Math.exp",
		"Math.log",
		"Math.pow",
		"Math.sinh",
		"Math.cosh",
		"Math.tanh",
		"Math.asin",
		"Math.acos",
		"Math.atan",
		"Math.atan2",
		"Math.floor",
		"Math.ceil",
		"Math.abs",
		"Math.sqrt",

		"ratson.utils.Utils.asinh",
		"ratson.utils.Utils.acosh",
		"ratson.utils.Utils.atanh",
		"ratson.utils.Utils.sqr",
		"ratson.utils.Utils.frac",
		"ratson.utils.Utils.mod"
	};
	public String preprocess(String code){
		return Strings.replaceAllWords(code, originals, replacements);
		
	}
	
	public static String test() {
		StringBuffer s = new StringBuffer("Preprocessor:{");
		for (int i = 0; i<originals.length; ++i){
			s.append(String.format("\t\"%s\"->\"%s\"\n", originals[i], replacements[i]));
		}
		return s.toString();
	}
	public static void main(String[] args) {
		System.out.println("Testing preprocessor");
		System.out.println(test());
		
	}
}
