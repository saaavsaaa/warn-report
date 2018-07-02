package cn.tellwhy.util;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by aaa on 17-11-23.
 */
public class DocReader {
    public static Map getDoc(InputStream inputStream, String rootKey) throws ParserConfigurationException, IOException, SAXException {
        
        //获取解析工厂
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //创建解析器对象
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        //获取Document对象
        Document d = db.parse(inputStream);
        //通过Document对象的方法获取相应的节点列表
//        NodeList nl = d.getElementsByTagName("ToUserName");
//        Node node = nl.item(0);
        //获取指定节点内容
//        String content = node.getTextContent();
//        System.out.println(content);
    
        Element e = d.getDocumentElement();
        return getValues(e);
    }
    
    private static Map getValues(Element e){
        Map<String, String> kvs = new HashMap();
        if (e.hasChildNodes()) {
            NodeList subList = e.getChildNodes();
            for (int i = 0; i < subList.getLength(); i++) {
                Node n = subList.item(i);
                if (n instanceof Element) {
                    kvs.put(((Element) n).getTagName(), n.getTextContent());
                }
            }
        }
        return kvs;
    }
    
    private static void printNode(Element e) {
        if (e.hasChildNodes()) {
            NodeList subList = e.getChildNodes();
            for (int i = 0; i < subList.getLength(); i++) {
                Node n = subList.item(i);
                if (n instanceof Element) {
                    System.out.println(((Element) n).getTagName());
                    System.out.println(n.getTextContent());
                    printNode((Element) n);
                }
            }
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("<").append(e.getNodeName());
            if (e.hasAttributes()) {
                NamedNodeMap attr = e.getAttributes();
                for (int i = 0; i < attr.getLength(); i++) {
                    sb.append(" ").append(attr.item(i).getNodeName()).append("=\"").append(attr.item(i).getNodeValue()).append("\"");
                }
            }
            sb.append(">");
            
            String content = e.getNodeValue();
            if (StringUtils.isNotEmpty(content)) {
                sb.append(content);
            }
            sb.append("</" + e.getNodeName() + ">");
            System.out.println(sb);
            
        }
    }
    
    private void list(Node node) {
        if(node instanceof Element)
        {
            System.out.println(node.getNodeName());
        }
        NodeList list = node.getChildNodes();
        for(int i = 0;i< list.getLength();i++){
            Node node1 =list.item(i);
            list(node1);
        }
    }
    
    Stack<String> tag = new Stack<>();
    String currentTag = "";
    boolean checking = false;
    
    public static String getDocNode(String txt, String expression){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (validate(c)){
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private static boolean validate(char c){
        
        return true;
    }
    
    public static String getContent(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String content = "";
        int tChar = 0;
        while ((tChar = inputStreamReader.read()) != -1) {
            char c = (char)tChar;
            content += c;
        }
        inputStreamReader.close();
        return content;
    }
    
    public static String getCData(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String content = "";
        int tChar = 0;
        String line = "";
        String key = "";
        boolean keyLetAdd = false;
        while ((tChar = inputStreamReader.read()) != -1) {
            char c = (char)tChar;
            content += c;
            if (tChar == 10){
                System.out.println(content);
                content = "";
                key = "";
                line = "";
            } else if (line.isEmpty()){
                if (key.isEmpty() || keyLetAdd){
                    if (keyLetAdd){
                        if (c == '>'){
                            keyLetAdd = false;
                            continue;
                        }
                        key += c;
                    }
                    if (!keyLetAdd){
                        if (c == '<'){
                            keyLetAdd = true;
                            continue;
                        }
                    }
                }
            }
            
            char vc = getCDataValue(c);
            if (vc == 0){
                if (line.isEmpty()){
                    continue;
                }
                System.out.println(key + " = " + line);
                key = "";
                line = "";
                continue;
            }
            line += vc;
        }
        inputStreamReader.close();
        return content;
    }
    
    private static int index = 0;
    private static boolean status = false;
    private static boolean start = false;
    private static final char[] CDATA = new char[]{'C', 'D', 'A', 'T', 'A', '['};
    
    private static boolean validateCDataValue(char c){
        if (index > 0 && !status){
            index = 0;
            return status = false;
        }
        if (c == CDATA[CDATA.length - 1] && status){
            start = true;
            index = 0;
            return status = false;
        }
        if (c == CDATA[index]){
            index++;
            return status = true;
        }
        return status = false;
    }
    
    private static char getCDataValue(char c){
        if (start){
            if (c == ']'){
                start = false;
                return 0;
            }
            return c;
        }
        validateCDataValue(c);
        return 0;
    }
}
