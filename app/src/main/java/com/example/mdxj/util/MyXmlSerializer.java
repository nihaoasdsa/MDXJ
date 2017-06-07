package com.example.mdxj.util;

import android.content.Context;
import android.util.Xml;

import com.example.mdxj.jsonbean.XmlParam;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

/**
 * Created by 008 on 2017/6/7 0007.
 */
public class MyXmlSerializer {
    //定义xml方法
    public static ArrayList<XmlParam> readXml(Context context, InputStream xmLPath) throws Throwable {
        ArrayList<XmlParam> channeInfos = new ArrayList<>();
        XmlParam channelifo = null;
        InputStream inputStream = null;
        //为Pull解释器设置要解析的XML数据
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
        inputStream = xmLPath;
        xmlPullParser.setInput(inputStream, "utf-8");
        int evenType = xmlPullParser.getEventType();
        //判断 当正在解析时进行判断
        while (evenType != XmlPullParser.END_DOCUMENT) {
            switch (evenType) {
                case XmlPullParser.START_TAG:
                    String tag = xmlPullParser.getName();
                    if (tag.equalsIgnoreCase("table")) {
                        channelifo = new XmlParam();
                    } else if (channelifo != null) {
                        if (tag.equalsIgnoreCase("column")) {
                            if (xmlPullParser.getAttributeValue(0).equals("id")) {
                                channelifo.setId(xmlPullParser.nextText().trim());
                            } else if (xmlPullParser.getAttributeValue(0).equals("accountname")) {
                                channelifo.setAccountname(xmlPullParser.nextText().trim());
                            } else if (xmlPullParser.getAttributeValue(0).equals("accountpsw")) {
                                channelifo.setAccountpsw(xmlPullParser.nextText().trim());
                            }
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xmlPullParser.getName().equalsIgnoreCase("table")&& channelifo!= null) {
                        channeInfos.add(channelifo);
                        channelifo= null;
                    }
                    break;
                default:break;
            }
            evenType=xmlPullParser.next();
            int i=evenType;
        }
            inputStream.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

            return channeInfos;
        }
    }
