package com.mygdx.briquebreaker;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Map {
    private int[][] brickDurability;

    public void printMap() {
        for (int i = 0; i < brickDurability.length; i++) {
            for (int j = 0; j < brickDurability[i].length; j++) {
                System.out.print("{"+i+""+j+"} ");
            }
            System.out.println();
        }
    }

    public void loadMapFromXML(String filename) {
        try {
            File file = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList rowList = doc.getElementsByTagName("row");
            int numRows = rowList.getLength();
            brickDurability = new int[numRows][];

            for (int i = 0; i < numRows; i++) {
                Element rowElement = (Element) rowList.item(i);
                NodeList brickList = rowElement.getElementsByTagName("brick");
                Element brickElement = (Element) brickList.item(0);  // assuming one <brick> per <row>
                NodeList columns = brickElement.getElementsByTagName("column");
                int numCols = columns.getLength();
                brickDurability[i] = new int[numCols];
                for (int j = 0; j < numCols; j++) {
                    brickDurability[i][j] = Integer.parseInt(columns.item(j).getTextContent());
                    System.out.print(brickDurability[i][j] + " ");

                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int[][] getBrickDurability() {
        return brickDurability;
    }


}
