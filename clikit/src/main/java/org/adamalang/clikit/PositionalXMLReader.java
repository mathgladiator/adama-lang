/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.clikit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/** XML Reader that associates line and column position to elements **/
public class PositionalXMLReader {
    public static Document readXML(File xmlFile) throws SAXException, ParserConfigurationException, IOException {
        final Document doc;
        final SAXParser parser;
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = dbFactory.newDocumentBuilder();
        doc = builder.newDocument();
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        parser = spf.newSAXParser();
        final DefaultHandler handler = new DefaultHandler() {
            private Locator locator;
            private Stack<Element> elementStack = new Stack<>();
            private StringBuilder text = new StringBuilder();
            @Override
            public void setDocumentLocator(Locator locator) {
                this.locator = locator;
            }
            @Override
            public void characters(char[] ch, int start, int length) {
                text.append(ch, start, length);
            }
            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                addText();
                Element curElem = doc.createElement(qName);
                for (int i = 0; i < atts.getLength(); i++) {
                    curElem.setAttribute(atts.getQName(i), atts.getValue(i));
                }
                curElem.setUserData("lineNumber", String.valueOf(locator.getLineNumber()), null);
                curElem.setUserData("colNumber", String.valueOf(locator.getColumnNumber()), null);
                elementStack.push(curElem);
            }
            @Override
            public void endElement(String uri, String localName, String qName) {
                addText();
                Element curElem = elementStack.pop();
                if (elementStack.isEmpty()) {
                    doc.appendChild(curElem);
                } else {
                    Element parentElem = elementStack.peek();
                    parentElem.appendChild(curElem);
                }
            }

            private void addText() {
                if (text.length() > 0) {
                    Element curElem = elementStack.peek();
                    Node textNode = doc.createTextNode(text.toString());
                    curElem.appendChild(textNode);
                    text.delete(0, text.length());
                }
            }
        };
        parser.parse(xmlFile,handler);
        return doc;
    }
}
