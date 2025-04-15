package com.example.bc_praca_x.helpers;

import android.util.Log;

public class HTMLStyler {

    public static String styleHTML(String content) {
        return "<html><head>" +
                "<meta charset=\"UTF-8\">" +
                "<style>" +
                "body {font-size: 20px; text-align: center; margin: 0; padding: 0; word-wrap: break-word;}\n" +

                "p{\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "}\n" +

                "ol, ul {\n" +
                "    text-align: left;\n" +
                "    list-style-position: inside;\n" +
                "    padding-left: 0;\n" +
                "    margin-left: 0;\n" +
                "}\n" +

                "li[data-list=\"ordered\"] {\n" +
                "    list-style-type: decimal;\n" +
                "    margin-left: 20px;\n" +
                "}\n" +

                "\n" +
                "li[data-list=\"bullet\"] {\n" +
                "    list-style-type: disc;\n" +
                "    margin-left: 20px;\n" +
                "}" +

                "blockquote {\n" +
                "    position: relative;\n" +
                "    margin: 1.5em 0px;\n" +
                "    padding: 0.5em 10px;\n" +
                "    font-style: italic;\n" +
                "    color: #555;\n" +
                "    border-left: 5px solid #ccc;\n" +
                "    background: #f9f9f9;\n" +
                "}\n" +

                "blockquote:before {\n" +
                "    content: \"\\201C\";\n" +
                "    font-size: 3em;\n" +
                "    line-height: 0.1em;\n" +
                "    position: absolute;\n" +
                "    left: 10px;\n" +
                "    top: -5px;\n" +
                "    color: #ccc;\n" +
                "}\n" +

                ".ql-code-block {\n" +
                "    background-color: black;\n" +
                "    color: white;\n" +
                "    padding-left: 15px;\n" +
                "    padding-top: 8px;\n" +
                "    text-align: left !important;\n" +
                "    font-weight: bold;\n" +
                "}\n" +
                "</style></head><body>" + content + "</body></html>";
    }

}
