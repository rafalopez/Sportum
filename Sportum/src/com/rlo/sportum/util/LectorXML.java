package com.rlo.sportum.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.rlo.sportum.modelo.Localizacion;

/**
 * Obtiene la información de la ruta de una actividad almacenada en disco
 * en formato XML mediante el uso de SAX, pasando dicho XML a un formato de objetos.
 * 
 * @author rafa
 *
 */
public class LectorXML extends DefaultHandler {

	@SuppressWarnings("unused")
	private static final String TAG = LectorXML.class.getSimpleName();
	
	private XMLReader xmlReader;
	private String str;
	private List<Localizacion> localizacionLst;
	private Localizacion loc;

	public LectorXML() throws SAXException {
		xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);
	}
	
	public void leer(InputStream is) throws IOException, SAXException {
		localizacionLst = new ArrayList<Localizacion>();
		xmlReader.parse(new InputSource(is));
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(SportumFileUtil.E_PUNTO)) {
			loc = new Localizacion();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		str = new String(ch, start, length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals(SportumFileUtil.E_PUNTO)) {
			localizacionLst.add(loc);
		}
		else if (localName.equals(SportumFileUtil.E_LON)) {
			loc.setLongitud(Double.valueOf(str));
		}
		else if (localName.equals(SportumFileUtil.E_LAT)) {
			loc.setLatitud(Double.valueOf(str));
		}
		else if (localName.equals(SportumFileUtil.E_ALT)) {
			loc.setAltitud(Double.valueOf(str));
		}
		else if (localName.equals(SportumFileUtil.E_TIEMPO)) {
			loc.setTiempo(Long.valueOf(str));
		}
		else if (localName.equals(SportumFileUtil.E_DIST)) {
			loc.setDistancia(Float.valueOf(str));
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	public List<Localizacion> getLocalizacionLst() {
		return localizacionLst;
	}
	
}
