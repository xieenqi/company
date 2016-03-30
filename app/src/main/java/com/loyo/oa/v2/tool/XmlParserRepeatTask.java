package com.loyo.oa.v2.tool;

import com.loyo.oa.v2.beans.RepeatTaskCaseModel;
import com.loyo.oa.v2.beans.RepeatTaskModel;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.List;


/**
 * 重复任务,三联选择框XML解析类
 *
 * created by yyy on 2016-03-28
 * */


public class XmlParserRepeatTask extends DefaultHandler {

    /**
     * 存储所有的解析对象
     */
    private List<RepeatTaskModel> provinceList = new ArrayList<RepeatTaskModel>();

    public XmlParserRepeatTask() {

    }

    public List<RepeatTaskModel> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
        // 当读到第一个开始标签的时候，会触发这个方法
    }

    RepeatTaskModel caseModel = new RepeatTaskModel();
    RepeatTaskCaseModel timeModel = new RepeatTaskCaseModel();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // 当遇到开始标记的时候，调用这个方法
        if ("case".equals(qName)) {
            caseModel = new RepeatTaskModel();
            caseModel.setName(attributes.getValue(0));
            caseModel.setTimeKind(new ArrayList<RepeatTaskCaseModel>());
        } else if ("timekind".equals(qName)) {
            timeModel = new RepeatTaskCaseModel();
            timeModel.setName(attributes.getValue(0));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // 遇到结束标记的时候，会调用这个方法
        if ("timekind".equals(qName)) {
            caseModel.getTimeKind().add(timeModel);
        } else if ("case".equals(qName)) {
            provinceList.add(caseModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }

}
