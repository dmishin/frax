package ratson.genimageexplorer.generators.janino;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ScriptReference {
	public String script="";
	public File file=null;
	public boolean changed=false;
	
	public void load(File script) throws IOException{
		file = script;
		
		BufferedReader br = new BufferedReader(new FileReader(script));
		
		StringBuffer newScript = new StringBuffer();
		while (true){
			String line = br.readLine();
			if (line == null) break;
			newScript.append(line);
			newScript.append("\n");
		}
		br.close();
		
		this.script = newScript.toString();
		changed = false;
	}
	
	public void set(String script){
		if (this.script != script){
			this.script = script;
			changed = true;
		}
	}
	
	public void save() throws IOException{
		BufferedWriter wrt = new BufferedWriter(new FileWriter(file));
		wrt.write(script);
		wrt.close();
		changed = false;
	}
	
	public void saveas(File f) throws IOException{
		file = f;
		save();
	}
	
	public String getName(){
		if (file != null){
			return file.getName();
		}else{
			return "unnamed";
		}
	}

	public String getScript() {
		return script;
	}
	
	
}
