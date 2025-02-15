/*
 * Copyright 2001-2023 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package rife.engine;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.junit.jupiter.api.Test;
import rife.resources.ResourceFinderClasspath;
import rife.resources.exceptions.ResourceFinderErrorException;
import rife.tools.*;
import rife.tools.exceptions.FileUtilsErrorException;
import rife.tools.exceptions.SerializationUtilsErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAnnotations {
    @Test
    void testDefaultValuesIn()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationInSite())) {
            try (final var webClient = new WebClient()) {
                assertEquals("""
                                        
                    0
                    defaultCookie
                    -2
                    defaultCookie2
                    -3
                    null
                    null
                    null
                    null
                    null
                    null
                    defaultParam
                    -4
                    defaultParam2
                    -5
                    defaultPathInfo
                    -6
                    defaultRequestAttribute
                    -7
                    defaultRequestAttribute2
                    -8
                    defaultSessionAttribute
                    -9
                    defaultSessionAttribute2
                    -10
                    defaultHeader
                    -11
                    defaultHeader2
                    -12
                    propval1
                    defaultProp2
                    propval1
                    null,null,null,0,null, ,null,false,null,0,null,0.0,null,0.0,null,0,null,-24,null,null,null,null,null,null,null,null,null,null,null""", webClient.getPage(new WebRequest(new URL("http://localhost:8181/get"), HttpMethod.GET)).getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testAnnotationInCookiesParamsAttributesHeaders()
    throws IOException, SerializationUtilsErrorException {
        try (final var server = new TestServerRunner(new AnnotationInSite())) {
            try (final var webClient = new WebClient()) {
                webClient.getCookieManager().addCookie(new Cookie("localhost", "stringCookie", "cookie1"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "intCookie", "2"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "cookie2", "cookie3"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "cookie3", "4"));
                var request = new WebRequest(new URL("http://localhost:8181/get/info/27"), HttpMethod.GET);
                request.setRequestParameters(List.of(
                    new NameValuePair("generate", "true"),
                    new NameValuePair("stringParam", "value5"),
                    new NameValuePair("intParam", "6"),
                    new NameValuePair("param2", "value7"),
                    new NameValuePair("param3", "8"),
                    new NameValuePair("enum", "WEDNESDAY"),
                    new NameValuePair("string", "the string"),
                    new NameValuePair("stringbuffer", "the stringbuffer"),
                    new NameValuePair("int", "23154"),
                    new NameValuePair("integer", "893749"),
                    new NameValuePair("char", "u"),
                    new NameValuePair("character", "R"),
                    new NameValuePair("boolean", "y"),
                    new NameValuePair("booleanObject", "no"),
                    new NameValuePair("byte", "120"),
                    new NameValuePair("byteObject", "21"),
                    new NameValuePair("double", "34878.34"),
                    new NameValuePair("doubleObject", "25435.98"),
                    new NameValuePair("float", "3434.76"),
                    new NameValuePair("floatObject", "6534.8"),
                    new NameValuePair("long", "34347897"),
                    new NameValuePair("longObject", "2335454"),
                    new NameValuePair("short", "32"),
                    new NameValuePair("shortObject", "12"),
                    new NameValuePair("date", "2005-08-20 09:44"),
                    new NameValuePair("dateFormatted", "Sat 20 Aug 2005 09:44:00"),
                    new NameValuePair("datesFormatted", "Sun 21 Aug 2005 11:06:14"),
                    new NameValuePair("datesFormatted", "Mon 17 Jul 2006 16:05:31"),
                    new NameValuePair("instant", "2006-08-20 08:44"),
                    new NameValuePair("instantFormatted", "Sun 20 Aug 2006 08:44:00"),
                    new NameValuePair("instantsFormatted", "Tue 21 Aug 2007 10:06:14"),
                    new NameValuePair("instantsFormatted", "Thu 17 Jul 2008 15:05:31"),
                    new NameValuePair("serializableParam", SerializationUtils.serializeToString(new BeanImpl.SerializableParam(13, "Thirteen"))),
                    new NameValuePair("serializableParams", SerializationUtils.serializeToString(new BeanImpl.SerializableParam(9, "Nine"))),
                    new NameValuePair("serializableParams", SerializationUtils.serializeToString(new BeanImpl.SerializableParam(91, "NinetyOne")))
                ));
                request.setAdditionalHeader("stringHeader", "value17");
                request.setAdditionalHeader("intHeader", "18");
                request.setAdditionalHeader("header2", "value19");
                request.setAdditionalHeader("header3", "20");
                assertEquals("""
                                        
                    0
                    cookie1
                    2
                    cookie3
                    4
                    null
                    null
                    null
                    null
                    null
                    null
                    value5
                    6
                    value7
                    8
                    27
                    27
                    value9
                    10
                    value11
                    12
                    value13
                    14
                    value15
                    16
                    value17
                    18
                    value19
                    20
                    propval1
                    defaultProp2
                    propval1
                    WEDNESDAY,the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,null,null,null,null,Sat 20 Aug 2005 09:44:00,Sun 21 Aug 2005 11:06:14,Mon 17 Jul 2006 16:05:31,Sun 20 Aug 2006 08:44:00,Tue 21 Aug 2007 10:06:14,Thu 17 Jul 2008 15:05:31,13:Thirteen,9:Nine,91:NinetyOne""", webClient.getPage(request).getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testAnnotationBodyIn()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationInSite())) {
            try (final var webClient = new WebClient()) {
                var request = new WebRequest(new URL("http://localhost:8181/post"), HttpMethod.POST);
                request.setHttpMethod(HttpMethod.POST);
                request.setRequestBody("theBody");
                request.setAdditionalHeader("Content-Type", "text/plain");
                assertEquals("""
                    theBody
                    0
                    defaultCookie
                    -2
                    defaultCookie2
                    -3
                    null
                    null
                    null
                    null
                    null
                    null
                    defaultParam
                    -4
                    defaultParam2
                    -5
                    defaultPathInfo
                    -6
                    defaultRequestAttribute
                    -7
                    defaultRequestAttribute2
                    -8
                    defaultSessionAttribute
                    -9
                    defaultSessionAttribute2
                    -10
                    defaultHeader
                    -11
                    defaultHeader2
                    -12
                    propval1
                    defaultProp2
                    propval1
                    null,null,null,0,null, ,null,false,null,0,null,0.0,null,0.0,null,0,null,-24,null,null,null,null,null,null,null,null,null,null,null""", webClient.getPage(request).getWebResponse().getContentAsString());

                request.setRequestBody("836");
                assertEquals("""
                    836                   
                    836
                    defaultCookie
                    -2
                    defaultCookie2
                    -3
                    null
                    null
                    null
                    null
                    null
                    null
                    defaultParam
                    -4
                    defaultParam2
                    -5
                    defaultPathInfo
                    -6
                    defaultRequestAttribute
                    -7
                    defaultRequestAttribute2
                    -8
                    defaultSessionAttribute
                    -9
                    defaultSessionAttribute2
                    -10
                    defaultHeader
                    -11
                    defaultHeader2
                    -12
                    propval1
                    defaultProp2
                    propval1
                    null,null,null,0,null, ,null,false,null,0,null,0.0,null,0.0,null,0,null,-24,null,null,null,null,null,null,null,null,null,null,null""", webClient.getPage(request).getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testFileUploadIn()
    throws IOException, ResourceFinderErrorException {
        try (final var server = new TestServerRunner(new AnnotationInSite())) {
            try (final var webClient = new WebClient()) {
                var request = new WebRequest(new URL("http://localhost:8181/form"), HttpMethod.GET);
                HtmlPage page = webClient.getPage(request);
                var form = page.getForms().get(0);

                HtmlFileInput file1 = form.getInputByName("uploadedFile");
                file1.setValueAttribute("file1.txt");
                file1.setContentType("text/plain");
                file1.setData("File 1 Data".getBytes());

                HtmlFileInput file2 = form.getInputByName("file");
                file2.setValueAttribute("file2.txt");
                file2.setContentType("text/plain");
                file2.setData("File 2 Data".getBytes());

                HtmlFileInput file3 = form.getInputByName("fileString");
                file3.setValueAttribute("file3.txt");
                file3.setContentType("text/plain");
                file3.setData("File 3 Data".getBytes());

                HtmlFileInput file4 = form.getInputByName("file2");
                file4.setValueAttribute("file4.txt");
                file4.setContentType("text/plain");
                file4.setData("File 4 Data".getBytes());

                HtmlFileInput file5 = form.getInputByName("file3");
                file5.setValueAttribute("file5.txt");
                file5.setContentType("text/plain");
                file5.setData("File 5 Data".getBytes());

                HtmlFileInput file6 = form.getInputByName("file4");
                file6.setValueAttribute("file6.txt");
                file6.setContentType("text/plain");
                file6.setData("File 6 Data".getBytes());

                HtmlFileInput string_file = form.getInputByName("stringFile");
                string_file.setValueAttribute("somedesign.html");
                string_file.setContentType("text/html");
                string_file.setData("this is some html content".getBytes(StandardCharsets.UTF_8));

                byte[] image_bytes = ResourceFinderClasspath.instance().useStream("uwyn.png", new InputStreamUser<>() {
                    public byte[] useInputStream(InputStream stream)
                    throws InnerClassException {
                        try {
                            return FileUtils.readBytes(stream);
                        } catch (FileUtilsErrorException e) {
                            throwException(e);
                        }

                        return null;
                    }
                });
                HtmlFileInput bytesFile = form.getInputByName("bytesFile");
                bytesFile.setValueAttribute("someimage.png");
                bytesFile.setContentType("image/png");
                bytesFile.setData(image_bytes);

                HtmlFileInput streamFile = form.getInputByName("streamFile");
                streamFile.setValueAttribute("somefile.png");
                streamFile.setData(image_bytes);

                page = page.getHtmlElementById("submit").click();
                assertEquals("""
                                        
                    0
                    defaultCookie
                    -2
                    defaultCookie2
                    -3
                    file1.txt, text/plain, File 1 Data
                    File 2 Data
                    File 3 Data
                    file4.txt, text/plain, File 4 Data
                    File 5 Data
                    File 6 Data
                    defaultParam
                    -4
                    defaultParam2
                    -5
                    defaultPathInfo
                    -6
                    defaultRequestAttribute
                    -7
                    defaultRequestAttribute2
                    -8
                    defaultSessionAttribute
                    -9
                    defaultSessionAttribute2
                    -10
                    defaultHeader
                    -11
                    defaultHeader2
                    -12
                    propval1
                    defaultProp2
                    propval1
                    null,null,null,0,null, ,null,false,null,0,null,0.0,null,0.0,null,0,null,-24,null,this is some html content,true,someimage.png,true,null,null,null,null,null,null""", page.getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testDefaultValuesOut()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationOutSite())) {
            try (final var webClient = new WebClient()) {
                HtmlPage page = webClient.getPage(new WebRequest(new URL("http://localhost:8181/get"), HttpMethod.GET));
                var cookie_manager = webClient.getCookieManager();
                assertEquals("defaultCookie", cookie_manager.getCookie("stringCookie").getValue());
                assertEquals("-2", cookie_manager.getCookie("intCookie").getValue());
                assertEquals("defaultCookie2", cookie_manager.getCookie("cookie2").getValue());
                assertEquals("-3", cookie_manager.getCookie("cookie3").getValue());
                assertEquals("defaultHeader", page.getWebResponse().getResponseHeaderValue("stringHeader"));
                assertEquals("-8", page.getWebResponse().getResponseHeaderValue("intHeader"));
                assertEquals("defaultHeader2", page.getWebResponse().getResponseHeaderValue("header2"));
                assertEquals("-9", page.getWebResponse().getResponseHeaderValue("header3"));
                assertEquals("defaultBody" +
                             "-1" +
                             "defaultRequestAttribute" +
                             "-4" +
                             "defaultRequestAttribute2" +
                             "-5" +
                             "defaultSessionAttribute" +
                             "-6" +
                             "defaultSessionAttribute2" +
                             "-7", page.getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testGeneratedValuesOut()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationOutSite())) {
            try (final var webClient = new WebClient()) {
                HtmlPage page = webClient.getPage(new WebRequest(new URL("http://localhost:8181/get?generate=true"), HttpMethod.GET));
                var cookie_manager = webClient.getCookieManager();
                assertEquals("value3", cookie_manager.getCookie("stringCookie").getValue());
                assertEquals("4", cookie_manager.getCookie("intCookie").getValue());
                assertEquals("value5", cookie_manager.getCookie("cookie2").getValue());
                assertEquals("6", cookie_manager.getCookie("cookie3").getValue());
                assertEquals("value15", page.getWebResponse().getResponseHeaderValue("stringHeader"));
                assertEquals("16", page.getWebResponse().getResponseHeaderValue("intHeader"));
                assertEquals("value17", page.getWebResponse().getResponseHeaderValue("header2"));
                assertEquals("18", page.getWebResponse().getResponseHeaderValue("header3"));
                assertEquals("value1" +
                             "2" +
                             "value7" +
                             "8" +
                             "value9" +
                             "10" +
                             "value11" +
                             "12" +
                             "value13" +
                             "14", page.getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testDefaultValuesInOut()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationInOutSite())) {
            try (final var webClient = new WebClient()) {
                HtmlPage page = webClient.getPage(new WebRequest(new URL("http://localhost:8181/get"), HttpMethod.GET));
                var cookie_manager = webClient.getCookieManager();
                assertEquals("defaultCookie", cookie_manager.getCookie("stringCookie").getValue());
                assertEquals("-2", cookie_manager.getCookie("intCookie").getValue());
                assertEquals("defaultCookie2", cookie_manager.getCookie("cookie2").getValue());
                assertEquals("-3", cookie_manager.getCookie("cookie3").getValue());
                assertEquals("defaultHeader", page.getWebResponse().getResponseHeaderValue("stringHeader"));
                assertEquals("-8", page.getWebResponse().getResponseHeaderValue("intHeader"));
                assertEquals("defaultHeader2", page.getWebResponse().getResponseHeaderValue("header2"));
                assertEquals("-9", page.getWebResponse().getResponseHeaderValue("header3"));
                assertEquals("""
                                        
                    0
                    defaultCookie
                    -2
                    defaultCookie2
                    -3
                    defaultRequestAttribute
                    -4
                    defaultRequestAttribute2
                    -5
                    defaultSessionAttribute
                    -6
                    defaultSessionAttribute2
                    -7
                    defaultHeader
                    -8
                    defaultHeader2
                    -9
                    0defaultRequestAttribute
                    -4
                    defaultRequestAttribute2
                    -5
                    defaultSessionAttribute
                    -6
                    defaultSessionAttribute2
                    -7
                    """, page.getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testGeneratedInValuesInOut()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationInOutSite())) {
            try (final var webClient = new WebClient()) {
                webClient.getCookieManager().addCookie(new Cookie("localhost", "stringCookie", "cookie1"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "intCookie", "2"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "cookie2", "cookie3"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "cookie3", "4"));
                var request = new WebRequest(new URL("http://localhost:8181/post?generateIn=true"), HttpMethod.POST);
                request.setHttpMethod(HttpMethod.POST);
                request.setRequestBody("theBody");
                request.setAdditionalHeader("Content-Type", "text/plain");
                request.setAdditionalHeader("stringHeader", "value15");
                request.setAdditionalHeader("intHeader", "16");
                request.setAdditionalHeader("header2", "value17");
                request.setAdditionalHeader("header3", "18");
                HtmlPage page = webClient.getPage(request);
                assertEquals("""
                    theBody
                    0
                    cookie1
                    2
                    cookie3
                    4
                    inValue7
                    1008
                    inValue11
                    1012
                    inValue13
                    1014
                    inValue15
                    1016
                    value15
                    16
                    value17
                    18
                    theBody0inValue7
                    1008
                    inValue11
                    1012
                    inValue13
                    1014
                    inValue15
                    1016
                    """, page.getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testGeneratedOutValuesInOut()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationInOutSite())) {
            try (final var webClient = new WebClient()) {
                webClient.getCookieManager().addCookie(new Cookie("localhost", "stringCookie", "cookie1"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "intCookie", "2"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "cookie2", "cookie3"));
                webClient.getCookieManager().addCookie(new Cookie("localhost", "cookie3", "4"));
                var request = new WebRequest(new URL("http://localhost:8181/post?generateOut=true"), HttpMethod.POST);
                request.setHttpMethod(HttpMethod.POST);
                request.setRequestBody("theBody");
                request.setAdditionalHeader("Content-Type", "text/plain");
                request.setAdditionalHeader("stringHeader", "value15");
                request.setAdditionalHeader("intHeader", "16");
                request.setAdditionalHeader("header2", "value17");
                request.setAdditionalHeader("header3", "18");
                HtmlPage page = webClient.getPage(request);
                assertEquals("""
                    theBody
                    0
                    cookie1
                    2
                    cookie3
                    4
                    defaultRequestAttribute
                    -4
                    defaultRequestAttribute2
                    -5
                    defaultSessionAttribute
                    -6
                    defaultSessionAttribute2
                    -7
                    value15
                    16
                    value17
                    18
                    outValue12002outValue7
                    2008
                    outValue9
                    2010
                    outValue11
                    2012
                    outValue13
                    2014
                    """, page.getWebResponse().getContentAsString());
            }
        }
    }

    @Test
    void testParametersSite()
    throws IOException {
        try (final var server = new TestServerRunner(new AnnotationParametersSite())) {
            try (final var webClient = new WebClient()) {
                HtmlPage page = webClient.getPage("http://localhost:8181/out?switchRoute=1");
                assertEquals("http://localhost:8181/in?stringParam=value1&intParam=222&param3=444&param2=value3&theBean_boolean=true&theBean_booleanObject=false&theBean_byte=120&theBean_byteObject=21&theBean_char=u&theBean_character=R&" +
                             "theBean_date=20050820094400000-0500&theBean_dateFormatted=Sat%2020%20Aug%202005%2009%3A44%3A00&theBean_datesFormatted=Sun%2021%20Aug%202005%2011%3A06%3A14&theBean_datesFormatted=Mon%2017%20Jul%202006%2016%3A05%3A31&" +
                             "theBean_double=34878.34&theBean_doubleObject=25435.98&theBean_enum=WEDNESDAY&theBean_float=3434.76&theBean_floatObject=6534.8&" +
                             "theBean_instant=20060820084400000-0500&theBean_instantFormatted=Sun%2020%20Aug%202006%2008%3A44%3A00&theBean_instantsFormatted=Tue%2021%20Aug%202007%2010%3A06%3A14&theBean_instantsFormatted=Thu%2017%20Jul%202008%2015%3A05%3A31&" +
                             "theBean_int=23154&theBean_integer=893749&theBean_long=34347897&theBean_longObject=2335454&theBean_serializableParam=H4sIAAAAAAAA%2F1vzloG1uIhBrSgzLVUvNS89My9Vzyk1Mc8ztyBHJTi1KDMxJ7MqMSknNSCxKDE3weLM44cO844wMTB5MrDnleYmpRbF%2BzCwF5cUZealx5cwCPlkJZYl6uck5qXrB4MFrSsKGBgYeEsYOEIyMotKUlPzAIrLw2dzAAAA&theBean_serializableParams=H4sIAAAAAAAA%2F1vzloG1uIhBrSgzLVUvNS89My9Vzyk1Mc8ztyBHJTi1KDMxJ7MqMSknNSCxKDE3weLM44cO844wMTB5MrDnleYmpRbF%2BzCwF5cUZealx5cwCPlkJZYl6uck5qXrB4MFrSsKGBgYOEsYWPyAZgMAeibMn28AAAA%3D&theBean_serializableParams=H4sIAAAAAAAA%2F1vzloG1uIhBrSgzLVUvNS89My9Vzyk1Mc8ztyBHJTi1KDMxJ7MqMSknNSCxKDE3weLM44cO844wMTB5MrDnleYmpRbF%2BzCwF5cUZealx5cwCPlkJZYl6uck5qXrB4MFrSsKGBgYoksYOP2AZpdU%2BuelAgDwv1ildAAAAA%3D%3D&theBean_short=32&theBean_shortObject=12&theBean_string=theString&theBean_stringbuffer=the%20stringbuffer", page.getWebResponse().getContentAsString());

                page = webClient.getPage(page.getWebResponse().getContentAsString());
                assertEquals("""
                    value1
                    222
                    value3
                    444
                    WEDNESDAY,theString,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,null,null,null,null,Sat 20 Aug 2005 09:44:00,Sun 21 Aug 2005 11:06:14,Mon 17 Jul 2006 16:05:31,Sun 20 Aug 2006 08:44:00,Tue 21 Aug 2007 10:06:14,Thu 17 Jul 2008 15:05:31,13:Thirteen,9:Nine,91:NinetyOne""", page.getWebResponse().getContentAsString());

                page = webClient.getPage("http://localhost:8181/out?switchRoute=2");
                assertEquals("http://localhost:8181/pathinfo/some/222/444/theString?stringParam=value1&param2=value3&theBean_boolean=true&theBean_booleanObject=false&theBean_byte=120&theBean_byteObject=21&theBean_char=u&theBean_character=R&" +
                             "theBean_date=20050820094400000-0500&theBean_dateFormatted=Sat%2020%20Aug%202005%2009%3A44%3A00&theBean_datesFormatted=Sun%2021%20Aug%202005%2011%3A06%3A14&theBean_datesFormatted=Mon%2017%20Jul%202006%2016%3A05%3A31&" +
                             "theBean_double=34878.34&theBean_doubleObject=25435.98&theBean_enum=WEDNESDAY&theBean_float=3434.76&theBean_floatObject=6534.8&" +
                             "theBean_instant=20060820084400000-0500&theBean_instantFormatted=Sun%2020%20Aug%202006%2008%3A44%3A00&theBean_instantsFormatted=Tue%2021%20Aug%202007%2010%3A06%3A14&theBean_instantsFormatted=Thu%2017%20Jul%202008%2015%3A05%3A31&" +
                             "theBean_int=23154&theBean_integer=893749&theBean_long=34347897&theBean_longObject=2335454&theBean_serializableParam=H4sIAAAAAAAA%2F1vzloG1uIhBrSgzLVUvNS89My9Vzyk1Mc8ztyBHJTi1KDMxJ7MqMSknNSCxKDE3weLM44cO844wMTB5MrDnleYmpRbF%2BzCwF5cUZealx5cwCPlkJZYl6uck5qXrB4MFrSsKGBgYeEsYOEIyMotKUlPzAIrLw2dzAAAA&theBean_serializableParams=H4sIAAAAAAAA%2F1vzloG1uIhBrSgzLVUvNS89My9Vzyk1Mc8ztyBHJTi1KDMxJ7MqMSknNSCxKDE3weLM44cO844wMTB5MrDnleYmpRbF%2BzCwF5cUZealx5cwCPlkJZYl6uck5qXrB4MFrSsKGBgYOEsYWPyAZgMAeibMn28AAAA%3D&theBean_serializableParams=H4sIAAAAAAAA%2F1vzloG1uIhBrSgzLVUvNS89My9Vzyk1Mc8ztyBHJTi1KDMxJ7MqMSknNSCxKDE3weLM44cO844wMTB5MrDnleYmpRbF%2BzCwF5cUZealx5cwCPlkJZYl6uck5qXrB4MFrSsKGBgYoksYOP2AZpdU%2BuelAgDwv1ildAAAAA%3D%3D&theBean_short=32&theBean_shortObject=12&theBean_stringbuffer=the%20stringbuffer", page.getWebResponse().getContentAsString());

                page = webClient.getPage(page.getWebResponse().getContentAsString());
                assertEquals("""
                    value1
                    222
                    value3
                    444
                    WEDNESDAY,theString,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12,null,null,null,null,Sat 20 Aug 2005 09:44:00,Sun 21 Aug 2005 11:06:14,Mon 17 Jul 2006 16:05:31,Sun 20 Aug 2006 08:44:00,Tue 21 Aug 2007 10:06:14,Thu 17 Jul 2008 15:05:31,13:Thirteen,9:Nine,91:NinetyOne""", page.getWebResponse().getContentAsString());
            }
        }
    }
}
