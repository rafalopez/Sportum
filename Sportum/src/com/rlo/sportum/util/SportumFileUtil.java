package com.rlo.sportum.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Environment;

import com.rlo.sportum.modelo.Localizacion;

/**
 * Clase encargada de todo lo que tenga que ver con la lectura / escritura de ficheros de Sportum.
 * 
 * @author rafa
 *
 */
public class SportumFileUtil {

	@SuppressWarnings("unused")
	private static final String TAG = SportumFileUtil.class.getSimpleName();
	
	private static final String OMIT_XML_DECLARATION = "false";
	private static final String INDENT = "true";
	
	public static final String DIRECTORIO_SPORTUM = "Sportum";
	public static final String PREFIJO_ARCHVO_XML = "actividad_";
	public static final String EXTENSION_ARCHIVO_XML = ".xml";
	
	public static final String E_ACTIVIDAD = "actividad";
	public static final String E_PUNTO = "p";
	public static final String E_LON = "lon";
	public static final String E_LAT = "lat";
	public static final String E_ALT = "alt";
	public static final String E_TIEMPO = "tiempo";
	public static final String E_DIST = "dist";
	
	private Context mCtx;
	
	
	public SportumFileUtil(Context ctx) {
		mCtx = ctx;
	}
	
	/**
	 * Almacena una lista de localizaciones en formato XML en disco.
	 * 
	 * @param id Identificador de la actividad
	 * @param localizacionLst Lista de localizaciones del recorrido de la actividad.
	 * 
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void guardarArchivoXML(long id, List<Localizacion> localizacionLst) throws ParserConfigurationException, TransformerException, IOException {
		
		String contenido = generarContenidoXML(localizacionLst);
		
		// Cada fichero tiene un nombre único
		String nombreFich = PREFIJO_ARCHVO_XML + id + EXTENSION_ARCHIVO_XML;
        
        FileOutputStream fos = null;
        // Primero se intenta almacenar en el dispositivo externo
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sdDir = Environment.getExternalStorageDirectory();
			File sportumDir = new File(sdDir.getAbsolutePath(), DIRECTORIO_SPORTUM);
			if (!sportumDir.exists()) {
				sportumDir.mkdir();
			}
			File fichero = new File(sportumDir.getAbsolutePath(), nombreFich);
			fichero.createNewFile();
			fos = new FileOutputStream(fichero);
		}
		else { // Sino, se almacena en la memoria interna
			fos = mCtx.openFileOutput(nombreFich, Context.MODE_PRIVATE);
		}

		fos.write(contenido.getBytes());
		fos.close();
	}
	
	/**
	 * Obtiene una lista de {@link Localizacion} a partir de un fichero XML almacenado en disco
	 * 
	 * @param id Identificador de la actividad
	 * @return Lista de {@link Localizacion} si todo ha ido bien, null en caso contrario.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<Localizacion> leerArchivoXML(long id) throws SAXException, IOException {
		
		List<Localizacion> localizacionLst = null;
		
		String nombreFich = PREFIJO_ARCHVO_XML + id + EXTENSION_ARCHIVO_XML;
		
		FileInputStream fis = null;
		// Primero se comprueba si el fichero existe en la memoria externa
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				|| Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			File sdDir = Environment.getExternalStorageDirectory();
			File sportumDir = new File(sdDir.getAbsolutePath(), DIRECTORIO_SPORTUM);
			File fichero = new File(sportumDir.getAbsolutePath(), nombreFich);
			if (fichero.exists() && fichero.canRead()) {
				fis = new FileInputStream(fichero);
			}
		}
		
		if (fis == null) { // Sino, se comprueba si existe en la memoria interna
			fis = mCtx.openFileInput(nombreFich);
		}
		
		// Para indicarle que vamos a usar SAX v.2
		System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
		
		LectorXML lectorXML = new LectorXML();
		lectorXML.leer(fis);
		localizacionLst = lectorXML.getLocalizacionLst();
		
		return localizacionLst;
	}
	
	/**
	 * Borra el fichero XML cuyo id se indica como parámetro.
	 * 
	 * @param id Identificador de la actividad para la cual se borra el fichero de la ruta.
	 */
	public void borrarArchivoXML(long id) {
		
		String nombreFich = PREFIJO_ARCHVO_XML + id + EXTENSION_ARCHIVO_XML;
		
		boolean borrado = false;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sdDir = Environment.getExternalStorageDirectory();
			File sportumDir = new File(sdDir.getAbsolutePath(), DIRECTORIO_SPORTUM);
			File fichero = new File(sportumDir.getAbsolutePath(), nombreFich);
			if (fichero.exists() && fichero.canRead()) {
				if (fichero.delete()) {
					borrado = true;
				}
			}
		}
		
		if (!borrado) {
			mCtx.deleteFile(nombreFich);
		}
	}
	
	/**
	 * Genera un String con el contenido XML antes de escribirlo en un fichero.
	 * @param localizacionLst Lista de {@link Localizacion} para la cual se genera el contenido XML
	 * @return String con el contenido generado.
	 * 
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private String generarContenidoXML(List<Localizacion> localizacionLst) throws ParserConfigurationException, TransformerException {
		
		Document doc = crearDocumentoActividad(localizacionLst);
		String contenidoXML = transformarDocumentoAString(doc);
		
		return contenidoXML;
	}
	
	/**
	 * Crea un objeto de tipo {@link Document} de JDOM con la información del XML
	 * 
	 * @param localizacionLst Lista de {@link Localizacion} con la cual se generará el arbol JDOM
	 * 
	 * @return Objeto de tipo {@link Document}
	 * 
	 * @throws ParserConfigurationException
	 */
	private Document crearDocumentoActividad(List<Localizacion> localizacionLst) throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		
		Element eActividad = doc.createElement(E_ACTIVIDAD);
		
		for (Localizacion loc : localizacionLst) {
			eActividad.appendChild(crearElementoPunto(doc, loc));
		}
		
		doc.appendChild(eActividad);
		
		return doc;
	}
	
	private Element crearElementoPunto(Document doc, Localizacion loc) {
		
		Element ePunto = doc.createElement(E_PUNTO);
		
		Element eLon = doc.createElement(E_LON);
		eLon.setTextContent(String.valueOf(loc.getLongitud()));
		ePunto.appendChild(eLon);
		
		Element eLat = doc.createElement(E_LAT);
		eLat.setTextContent(String.valueOf(loc.getLatitud()));
		ePunto.appendChild(eLat);
		
		Element eAlt = doc.createElement(E_ALT);
		eAlt.setTextContent(String.valueOf(loc.getAltitud()));
		ePunto.appendChild(eAlt);
		
		Element eTiempo = doc.createElement(E_TIEMPO);
		eTiempo.setTextContent(String.valueOf(loc.getTiempo()));
		ePunto.appendChild(eTiempo);
		
		Element eDist = doc.createElement(E_DIST);
		eDist.setTextContent(String.valueOf(loc.getDistancia()));
		ePunto.appendChild(eDist);
		
		return ePunto;
	}
	
	private String transformarDocumentoAString(Document doc) throws TransformerException {
		
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, OMIT_XML_DECLARATION);
        trans.setOutputProperty(OutputKeys.INDENT, INDENT);

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        
        return sw.toString();
	}
	
}
