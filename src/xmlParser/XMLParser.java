package xmlParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
	
	public static NodeList getMasterNodeList(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		Document document = builder.parse(file);
		return document.getDocumentElement().getChildNodes(); 
	}
	
	public static List<Node> getNamedNodes(NodeList list, String nodeName) {
		List<Node> nodeList = new ArrayList<>();
		
		for(int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if(node.getNodeName().equals(nodeName)) nodeList.add(node);
		}

		return nodeList;
	}
	
	public static NodeList getNodesWithin(Node node) {
		return node.getChildNodes();
	}
	
	public static Node getChildNode(Node parentNode, String targetName) {
		//TODO: Add attribute support and then reuse the code that drill down uses to find those attributes.
		NodeList childNodes = parentNode.getChildNodes();
		for(int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			if(childNode.getNodeName().equals(targetName)) return childNode;
		}
		
		return null;
	}
	
	public static List<Node> getChildNodes(Node parentNode, String targetName) {
		//TODO: Add attribute support and then reuse the code that drill down uses to find those attributes.
		List<Node> nodeList = new ArrayList<>();
			
		NodeList childNodes = parentNode.getChildNodes();
		for(int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			if(childNode.getNodeName().equals(targetName)) nodeList.add(childNode);
		}
		
		return nodeList;
	}
	
	public static Node drillDown(Node parentNode, String targetNodeName, String[][] targetNodeAttributes) {
		NodeList childNodes = parentNode.getChildNodes();
		for(int j = 0; j < childNodes.getLength(); j++) {
			Node currentNode = childNodes.item(j);
			if(nodeMatchesCriteria(currentNode, targetNodeName, targetNodeAttributes)) return currentNode;
			
			NodeList grandchildNodes = currentNode.getChildNodes();
			if(grandchildNodes.getLength() > 0) {
				Node potentialTargetNode = drillDown(currentNode, targetNodeName, targetNodeAttributes);
				if(potentialTargetNode != null) return potentialTargetNode;
			}
		}
		return null;
	}
	
	public static boolean nodeMatchesCriteria(Node nodeToTest, String requiredName, String[][] requiredAttributes) {
		if(!nodeToTest.getNodeName().equals(requiredName)) return false;
		String[] nodeAttributes = extractAttributes(nodeToTest);
		
		if(requiredAttributes == null) return true;
		
		for(int i = 0; i < requiredAttributes.length; i++) {
			if(requiredAttributes[i].length != 2) {
				System.err.println("XMLParser.matchesCriteria(): Attribute format incorrect. Returning false.");
				return false;
			}
			
			if(!attributeIsInList(nodeAttributes, requiredAttributes[i][0])) return false;
			else if(!getAttribute(requiredAttributes[i][0], nodeAttributes).equals(requiredAttributes[i][1])) return false;
		}
		
		return true;
	}
	
	public static String[] extractAttributes(Node node) {
		NamedNodeMap map = node.getAttributes();
		int numberOfProperties = map.getLength();
		String[] properties = new String[numberOfProperties * 2];
		
		for(int i = 0; i < numberOfProperties; i++) {
			String[] property = node.getAttributes().item(i).toString().split("=");
			property[1] = property[1].replaceAll("\"", "");
			
			properties[i * 2] = property[0];
			properties[i * 2 + 1] = property[1];
		}
		
		return properties;
	}
	
	public static boolean attributeIsInList(String[] attributes, String attribute) {
		for(int i = 0; i < attributes.length; i++) {
			if(attributes[i].equals(attribute)) return true;
		}
		
		return false;
	}
	
	public static String getAttribute(String attributeToGet, Node node) {
		String[] attributes = extractAttributes(node);
		for(int i = 0; i < attributes.length; i += 2) {
			if(attributes[i].equals(attributeToGet)) return attributes[i + 1];
		}
		
		return "";
	}
	
	public static String getAttribute(String attributeToGet, String[] attributes) {
		for(int i = 0; i < attributes.length; i += 2) {
			if(attributes[i].equals(attributeToGet)) return attributes[i + 1];
		}
		
		return "";
	}
	
	public static String[] extractDataFrom(Node node, String delimeter) {
		return node.getTextContent().split(delimeter);
	}
}
