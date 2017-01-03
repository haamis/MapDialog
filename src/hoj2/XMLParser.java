package hoj2;

// Luokka, joka j‰sent‰‰ karttapalvelimelta saadun XML-tiedoston.

import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser {
	
	static List<Layer> layers = new ArrayList<Layer>(); // Lista, jossa s‰ilytet‰‰n tiedot karttapalvelun kerroksista.
	
	
	// Metodi, joka hoitaa j‰sent‰misen.
	
	public static List<Layer> parse() throws Exception {
		
		// Luodaan uusi Document-olio ja haetaan palvelimelta XML-tiedot.
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse("http://demo.mapserver.org/cgi-bin/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities");
		
		// K‰yd‰‰n l‰pi dokumenttia kunnes tulee vastaan halutut Layer-tagit.
		
		NodeList nodeList = doc.getDocumentElement().getChildNodes();
		nodeList = lookFor(nodeList, "Capability");
		nodeList = lookFor(nodeList, "Layer");
		List<NodeList> layerNodes = new ArrayList<NodeList>(lookForAll(nodeList, "Layer"));
		
		// Tallenetaan kerroksien tiedot listaan ja palautetaan lista.
		
		for (NodeList nList : layerNodes) {
			layers.add(parseLayerInfo(nList));
		}
		return layers;
	}
	
	// Apumetodi, joka etsii halutun elementin ja palauttaa ensimm‰isen, jonka lˆyt‰‰.
	
	public static NodeList lookFor(NodeList nodeList, String name) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName() == name) {
				NodeList childNodes = node.getChildNodes();
				return childNodes;
			}
		}
		return null;
	}
	
	// Apumetodi, joka etsii halutun elementin ja palauttaa kaikki, jotka lˆyt‰‰.
	
	public static List<NodeList> lookForAll(NodeList nodeList, String name) {
		List<NodeList> returnList = new ArrayList<NodeList>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName() == name) {
				NodeList childNodes = node.getChildNodes();
				returnList.add(childNodes);
			}
		}
		return returnList;
	}
	
	// Apumetodi, joka parsii Layer-tagin alla olevat name ja title -tagit.
	
	public static Layer parseLayerInfo (NodeList nodeList) {
		Layer returnLayer = new Layer();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				String nodeName = node.getNodeName();
				if (nodeName == "Name") {
					returnLayer.name = node.getFirstChild().getTextContent();
				} else if (nodeName == "Title") {
					returnLayer.title = node.getFirstChild().getTextContent();
				}
			}
		}
		return returnLayer;
	}
	
	// main-metodia voi k‰ytt‰‰ luokan toiminnallisuuden testaamiseen.
	
	public static void main (String[] args) {
		try {
			parse();
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Hajoo siihen sit.");
		}
	}
}

// Layer-luokkaa k‰ytet‰‰n tietorakenteena karttakerrosten tietojen tallentamiseen.

class Layer {
	String name;
	String title;
}
