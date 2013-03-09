package ratson.genimageexplorer;

public class PatternFormatException extends Exception {

	public PatternFormatException(String string) {
		super("Wrong pattern format:\n"+string);
	}

}
