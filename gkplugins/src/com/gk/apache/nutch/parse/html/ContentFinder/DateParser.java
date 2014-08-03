package com.gk.apache.nutch.parse.html.ContentFinder;

import com.gk.apache.nutch.parse.html.HtmlParser;
import com.ibm.icu.text.SimpleDateFormat;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.hadoop.conf.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DateParser
{
private final String DATEPARSER_FILEKEY = "gk.dateparser.file";
private Document xdoc;

public DateParser()
{
String fileRules = HtmlParser.conf_static.get("gk.dateparser.file");

InputStream metadataStream = HtmlParser.conf_static.getConfResourceAsInputStream(fileRules);


DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
DocumentBuilder dBuilder = null;
try
{
dBuilder = dbFactory.newDocumentBuilder();
}
catch (ParserConfigurationException e1)
{
e1.printStackTrace();
}
try
{
this.xdoc = dBuilder.parse(metadataStream);
}
catch (SAXException e)
{
e.printStackTrace();
}
catch (IOException e)
{
e.printStackTrace();
}
}

public Date parse(String date)
{
regexResult monthResult = XDocMatcher("month", date);
date = date.replace(monthResult.matchedPart, "");
regexResult yearResult = XDocMatcher("year", date);
date = date.replace(yearResult.matchedPart, "");
regexResult dayResult = XDocMatcher("day", date);

SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
try
{
return format.parse(yearResult.replaceBy + "/" + monthResult.replaceBy + "/" + dayResult.replaceBy);
}
catch (ParseException e)
{
e.printStackTrace();
}
return null;
}

private regexResult XDocMatcher(String tag, String date)
{
NodeList tags = this.xdoc.getElementsByTagName(tag);
for (int i = 0; i < tags.getLength(); i++)
{
Node node = tags.item(i);
String reg_ex = node.getTextContent();
Pattern pattern = Pattern.compile(reg_ex);
Matcher matcher = pattern.matcher(date);
if (matcher.find())
{
String matchPart = matcher.group();
regexResult res = new regexResult();
res.matchedPart = matchPart;
if (node.getAttributes().getNamedItem("value") != null) {
res.replaceBy = node.getAttributes().getNamedItem("value").getNodeValue();
} else {
res.replaceBy = node.getTextContent();
}
return res;
}
}
return null;
}

public class regexResult
{
public String matchedPart;
public String replaceBy;

public regexResult() {}
}
}