package ratson.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;

public class NanoXML {
	private NanoXML(){};

	public static IXMLElement parseFile( File file ) throws IllegalAccessException, FileNotFoundException, IOException, XMLException{ 
	    // Load from XML
		IXMLParser parser;
		try {
			parser = XMLParserFactory.createDefaultXMLParser();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		IXMLReader xr = StdXMLReader.fileReader(file.getAbsolutePath());
		parser.setReader(xr);
		IXMLElement xml = (IXMLElement)parser.parse();
		return xml;
	}
	public static IXMLElement parseStream( Reader reader ) throws IllegalAccessException, FileNotFoundException, IOException, XMLException{
		IXMLParser parser;
		try {
			parser = XMLParserFactory.createDefaultXMLParser();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		
		IXMLReader xr = new StdXMLReader(reader);
		parser.setReader(xr);
		IXMLElement xml = (IXMLElement)parser.parse();
		reader.close();
		return xml;
		
	}
	
}
