package com.mygdx.briquebreaker;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Map {
    private int[][] brickDurability;

    public void loadMapFromXML(String filename) {
        try {
            File file = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList brickList = doc.getElementsByTagName("brick");
            int numRows = brickList.getLength();
            int numCols = ((Element) brickList.item(0)).getElementsByTagName("column").getLength();
            brickDurability = new int[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                Element rowElement = (Element) brickList.item(i);
                NodeList columns = rowElement.getElementsByTagName("column");
                for (int j = 0; j < numCols; j++) {
                    Element columnElement = (Element) columns.item(j);
                    brickDurability[i][j] = Integer.parseInt(columnElement.getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[][] getBrickDurability() {
        return brickDurability;
    }

    // Exemple d'utilisation
    public static void main(String[] args) {
        Map map = new Map();
        map.loadMapFromXML("map.xml");

        int[][] brickDurability = map.getBrickDurability();
        for (int i = 0; i < brickDurability.length; i++) {
            for (int j = 0; j < brickDurability[0].length; j++) {
                System.out.print(brickDurability[i][j] + " ");
            }
            System.out.println();
        }
    }
}




