/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
