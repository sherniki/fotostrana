package configuration.utils.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import configuration.IConfiguration;
import configuration.utils.Writer;

/**
 * Записує конфігурацію у XML
 * 
 */
public class XMLWriter extends Writer {
	final String SEPARATOR_NODE = ".";
	final String SEPARATOR_ATTRIBUTE = ":";
	private List<String> tabs=new ArrayList<String>();
	private String stepTab = "   ";
		
	public XMLWriter(IConfiguration configuration) {
		this.configuration = configuration;
		tabs.add("");
	}

	private String getTab(int level) {
		if (level < 0)
			return "";
		if (level >= tabs.size()) {
			for (int i = tabs.size(); i <= level; i++) {
				tabs.add(tabs.get(i - 1) + stepTab);
			}
		}
		return tabs.get(level);
	}
	
	public void setStepTab(String newStep){
		stepTab=newStep;
	}

	private void printDomTree(Node node, FileWriter writer, int level)
			throws IOException {
		if (node == null)
			return;
		int type = node.getNodeType();
		switch (type) {

		case Node.DOCUMENT_NODE: {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\r\n");
			printDomTree(((Document) node).getDocumentElement(), writer, 0);
			break;
		}

		case Node.ELEMENT_NODE: {
			writer.write(getTab(level) + "<");
			writer.write(node.getNodeName());
			if (node.getNodeValue() != null)
				writer.write(node.getNodeValue());
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++)
				printDomTree(attrs.item(i), writer, level);

			if (node.hasChildNodes()) {
				NodeList children = node.getChildNodes();
				if (children.getLength() > 1)
					writer.write(">" + "\r\n");

				else
					writer.write(">");
				for (int i = 0; i < children.getLength(); i++)
					printDomTree(children.item(i), writer, level + 1);
			} else
				writer.write(">");
			if (node.getChildNodes().getLength() > 1)
				writer.write(getTab(level) + "</");
			else
				writer.write("</");
			writer.write(node.getNodeName());
			writer.write('>' + "\r\n");

			break;
		}

		case Node.ATTRIBUTE_NODE: {
			writer.write(" " + node.getNodeName() + "=\""
					+ ((Attr) node).getValue() + "\"");
			break;
		}

		case Node.ENTITY_REFERENCE_NODE: {
			writer.write("&");
			writer.write(node.getNodeName());
			writer.write(";");
			break;
		}

		// print text
		case Node.TEXT_NODE: {
			writer.write(node.getNodeValue());
			break;
		}

		case Node.COMMENT_NODE: {
			writer.write("<!--");
			writer.write(node.getNodeValue());
			writer.write("-->");
			break;
		}

		}
	} // printDomTree(Node)

	@Override
	public void save(String path) throws IOException,
			ParserConfigurationException {

		DocumentBuilderFactory builderFactory = null;
		try {
			builderFactory = DocumentBuilderFactory.newInstance();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DocumentBuilder builderDocument = builderFactory.newDocumentBuilder();
		Document document = builderDocument.newDocument();

		List<String> allKey = configuration.getAllKey();
		Collections.sort(allKey);
		for (String key : allKey) {
			Node highNode = document;
			String[] names = key.split("\\" + SEPARATOR_NODE);
			if (names.length > 0) {
				// ���� �������� �������
				String lastElement = names[names.length - 1];
				boolean hasAttribute = false;
				String attribute = null;
				if (lastElement.indexOf(SEPARATOR_ATTRIBUTE) > 0) {
					String[] attributes = lastElement
							.split(SEPARATOR_ATTRIBUTE);
					hasAttribute = true;
					names[names.length - 1] = attributes[0];
					attribute = attributes[1];
				}
				for (int i = 0; i < names.length; i++) {
					Node currentNode = null;
					NodeList childList = highNode.getChildNodes();
					if (childList != null)
						for (int j = 0; j < childList.getLength(); j++)
							if (childList.item(j).getNodeName()
									.equals(names[i])) {
								currentNode = childList.item(j);
								break;
							}
					if (currentNode == null) {
						Element newElement = document.createElement(names[i]);
						try {
							highNode.appendChild(newElement);
						} catch (Exception e) {
						}

						currentNode = newElement;
					}
					highNode = currentNode;
				}
				if (hasAttribute) {
					Element element = (Element) highNode;
					element.setAttribute(attribute, configuration.getValue(key));
				} else {
					highNode.setTextContent(configuration.getValue(key));
				}
			}
		}

		FileWriter writer = new FileWriter(new File(path));
		printDomTree(document, writer, 0);
		writer.close();
	}
}