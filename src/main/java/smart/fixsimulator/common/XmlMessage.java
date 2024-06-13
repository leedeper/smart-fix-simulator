/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Send email to lyziuu@gmail.com for any question.
 *
 */

package smart.fixsimulator.common;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;

import java.io.IOException;

import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * copy from https://stackoverflow.com/questions/50765134/quickfixj-create-message-from-xml-string
 *
 * @author Leedeper
 */
@Slf4j
public class XmlMessage {
    private final String xml;
    private final String delimiter;
    private final Analyzer analyzer;
    private static final String BEGIN = "${";
    private static final String END = "}";
    private List<String> firstThreeField = Arrays.asList("8","9","35");

    public XmlMessage(final String xml, final String delimiter, Analyzer analyzer) {
        this.xml = xml;
        this.delimiter = delimiter;
        this.analyzer = analyzer;
    }

    public String toFixMessage() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        final Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes()));
        //log.debug("original xml before ordered: {}",docToString(doc));
        makeOrder(doc);
        //log.debug("new xml after ordered : {}",docToString(doc));

        final StringBuilder messageBuilder = new StringBuilder();
        build(messageBuilder, doc, "header");
        build(messageBuilder, doc, "body");
        build(messageBuilder, doc, "trailer");
        return messageBuilder.toString();
    }

    private String docToString(Document doc) throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc),new StreamResult(writer));
        return writer.toString();
    }

    private void makeOrder(Document doc){
        NodeList sectionRoot = doc.getElementsByTagName("header");
        NodeList nodeList = sectionRoot.item(0).getChildNodes();
        HashMap<String, Node> foundField=new HashMap();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("field")){
                String tagNum = getTagNumber(node);
                if(firstThreeField.contains(tagNum)){
                    foundField.put(tagNum, node);
                }
            }
        }
        // remove three fields from document
        foundField.forEach((k,v)->{
            v.getParentNode().removeChild(v);
        });

        // put three fields to document
        for(int i=firstThreeField.size()-1; i>=0; i--){
            String tag=firstThreeField.get(i);
            Node n = foundField.get(tag);

            if(n==null){
                String errorMsg="No tag "+tag+" found in xml";
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            Node first = nodeList.item(0);
            first.getParentNode().insertBefore(n,first);

        }
    }

    private void build(final StringBuilder messageBuilder, final Document doc, final String section) {
        final NodeList sectionRoot = doc.getElementsByTagName(section);
        final NodeList sectionChildren = sectionRoot.item(0).getChildNodes();
        build(messageBuilder, sectionChildren);
    }

    private void build(final StringBuilder messageBuilder, final NodeList nodeList) {
        final Set<String> numInGroupTags = getNumInGroupTags(nodeList);
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node.getNodeName().equals("field") && !numInGroupTags.contains(getTagNumber(node))) {
                messageBuilder.append(getTagNumber(node))
                        .append('=')
                        .append(getText(node.getTextContent()))
                        .append(delimiter);
            } else if (node.getNodeName().equals("groups")) {
                final NodeList groupElems = node.getChildNodes();
                messageBuilder.append(getTagNumber(node))
                        .append('=')
                        .append(getGroupCount(groupElems))
                        .append(delimiter);
                for (int j = 0; j < groupElems.getLength(); j++) {
                    build(messageBuilder, groupElems.item(j).getChildNodes());
                }
            }
        }
    }

    private Set<String> getNumInGroupTags(final NodeList nodeList) {
        final Set<String> numInGroupTags = new HashSet<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("groups")) {
                numInGroupTags.add(getTagNumber(nodeList.item(i)));
            }
        }
        return numInGroupTags;
    }

    private String getTagNumber(final Node node) {
        return node.getAttributes().getNamedItem("tag").getTextContent();
    }

    private int getGroupCount(final NodeList groupRoot) {
        int count = 0;
        for (int j = 0; j < groupRoot.getLength(); j++) {
            if (groupRoot.item(j).getNodeName().equals("group")) count++;
        }
        return count;
    }

    // simple to parse ${xxx}
    private String getText(String originalText){
        if(analyzer!=null && originalText.startsWith(BEGIN)){
            String exp = getExpression(originalText);
            return analyzer.analyzer(exp);
        }else{
            return originalText;
        }
    }
    private String getExpression(String originalText){
        int begin=originalText.indexOf(BEGIN);
        int end=originalText.indexOf(END);
        return originalText.substring(begin+BEGIN.length(), end);
    }

    public static interface Analyzer {
        String analyzer(String originalText);
    }
}