package com.mh.yifenban.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XmlUtil {


    /**
     * 解析微信服务器发过来的xml格式的消息将其转换为map
     * @param in
     * @return
     */
    public static Map<String,Object> parseXML(InputStream in){
        //解析结果存储在hashmap
        Map<String,Object> map=new HashMap<>();
        try {
            //读取输入流
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(in);
            //得到XML的根元素
            Element root = document.getRootElement();

            Iterator iterator = root.elementIterator();
            while (iterator.hasNext()){

                Element element = (Element) iterator.next();
                map.put(element.getName(),element.getStringValue());

            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return map;
    }

}
