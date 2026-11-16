package com.xyazh.kanake.util.xml;

import com.xyazh.kanake.util.xml.rendernode.RenderNodeBody;
import com.xyazh.kanake.util.xml.rendernode.RenderNode;
import com.xyazh.kanake.util.xml.rendernode.RenderNodeImg;
import com.xyazh.kanake.util.xml.rendernode.RenderNodeText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Xml {
    protected static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    protected static DocumentBuilder BUILDER = null;
    protected Element root = null;

    static {
        try {
            BUILDER = FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Xml(File file) {
        if (BUILDER == null) {
            return;
        }
        Document document = null;
        try {
            document = BUILDER.parse(file);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        if (document == null) {
            return;
        }
        this.root = document.getDocumentElement();
    }

    public RenderNode getRootRenderNode(){
        if(this.root == null){
            return null;
        }
        return this.nodeHandler(this.root);
    }

    protected RenderNode nodeHandler(Element element) {
        String name = element.getNodeName();
        System.out.println(name);
        RenderNode renderNode;
        switch (name) {
            case "body":
                renderNode = new RenderNodeBody(element);
                break;
            case "text":
                renderNode = new RenderNodeText(element);
                break;
            case "img":
                renderNode = new RenderNodeImg(element);
                break;
            default:
                renderNode = new RenderNode(element);
        }
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element subElement = (Element) node;
                this.nodeHandler(subElement, renderNode);
            }
        }
        return renderNode;
    }

    protected void nodeHandler(Element element, RenderNode parentNode) {
        String name = element.getNodeName();
        System.out.println(name);
        RenderNode thisNode;
        switch (name) {
            case "body":
                thisNode = new RenderNodeBody(element,parentNode);
                break;
            case "text":
                thisNode = new RenderNodeText(element,parentNode);
                break;
            case "img":
                thisNode = new RenderNodeImg(element,parentNode);
                break;
            default:
                thisNode = new RenderNode(element,parentNode);
        }
        parentNode.addSubNode(thisNode);
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element subElement = (Element) node;
                this.nodeHandler(subElement, thisNode);
            }
        }
    }
}
