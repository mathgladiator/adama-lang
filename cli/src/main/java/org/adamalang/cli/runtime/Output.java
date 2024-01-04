/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ANSI;
import org.adamalang.common.ColorUtilTools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/** Represents the different types of outputs, STDOUT changes based on class **/
public class Output {
    private boolean color = true;
    private boolean json = false;

    public Output(String[] args) {
        for (String arg: args) {
            switch (arg) {
                case "--no-color":
                    color = false;
                    break;
                case "--json":
                    json = true;
                    break;
            }
        }
    }

    public class YesOrError {
        public void out() {
            System.out.println(ColorUtilTools.prefix("\u2705", ANSI.Green));
        }
    }

    public YesOrError makeYesOrError() {
        return new YesOrError();
    }

    public class JsonOrError {
        private ArrayList<ObjectNode> objList = new ArrayList<>();

        public void reset() {
            objList.clear();
        }

        public void add(ObjectNode item) {
            objList.add(item);
        }

        public void out() {
             // In the case of json only
            if (json) {
                System.out.println("[");
                for (int i = 0; i < objList.size(); i++) {
                    ObjectNode json = objList.get(i);
                    String[] lines = json.toPrettyString().split("\n");
                    for (int j = 0; j < lines.length ; j++) {
                        String outputStr = " " + lines[j];
                        if (i < objList.size() - 1 && j == lines.length - 1) {
                            outputStr += ",";
                        }
                        System.out.println(outputStr);
                    }
                }
                System.out.println("]");
                return;
            }
            ANSI headingColor = ANSI.Normal;
            ANSI valueColor = ANSI.Normal;
            if (color) {
                headingColor = ANSI.Yellow;
                valueColor = ANSI.Green;
            }
            ArrayList<StringBuilder> table = new ArrayList<>();
            for (int i = 0; i < 3 + (2*objList.size()); i++) {
              table.add(new StringBuilder());
            }
            int fields = 0;
            if (objList.size() > 0) {
                fields = objList.get(0).size();
            }
            int[] longestEach = new int[fields];
            String[] headers = new String[fields];
            String[][] values = new String[objList.size()][fields];
            // Populate the longest for Each
            for (int i = 0; i < objList.size() ; i++) {
                ObjectNode json = objList.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = json.fields();
                int index = 0;
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String header = item.getKey();
                    headers[index] = header;
                    JsonNode value = item.getValue();
                    String textValue = value.isTextual() ? value.textValue() : value.toString();
                    values[i][index] = textValue;
                    int cellLength = textValue.length() > header.length() ? textValue.length() : header.length();
                    if (longestEach[index] < cellLength)
                        longestEach[index] = cellLength;
                    index++;
                }
            }
             // Create heading table
             table.get(0).append(ColorUtilTools.prefix("\u250C", headingColor));
             table.get(1).append(ColorUtilTools.prefix("\u2502", headingColor));
             table.get(table.size()-1).append(ColorUtilTools.prefix("\u2514", valueColor));
             for (int i = 0; i < longestEach.length; i++) {
                 table.get(0).append(ColorUtilTools.prefix("\u2500".repeat(longestEach[i]+2), headingColor));
                 table.get(table.size()-1).append(ColorUtilTools.prefix("\u2500".repeat(longestEach[i]+2), valueColor));
                 if (i < longestEach.length - 1) {
                     table.get(0).append(ColorUtilTools.prefix("\u252C", headingColor));
                     table.get(table.size()-1).append(ColorUtilTools.prefix("\u2534", valueColor));
                 }
                 int spaces = (longestEach[i] + 2 - headers[i].length());
                 int leftPad = (spaces)/2;
                 int rightPad = leftPad;
                 if ((spaces) % 2 == 1) {
                    rightPad++;
                }
                 table.get(1).append(" ".repeat(leftPad)).append(ColorUtilTools.prefixBold(headers[i], ANSI.Normal)).append(" ".repeat(rightPad)).append(ColorUtilTools.prefix("\u2502", headingColor));
             }
             for (int i = 0; i < values.length ; i++) {
                for (int j = 0; j < values[i].length ; j++) {
                    String value = values[i][j];
                    int spaces = (longestEach[j] + 2 - value.length());
                    int leftPad = (spaces)/2;
                    int rightPad = leftPad;
                    if ((spaces) % 2 == 1) {
                        rightPad++;
                    }
                    if (j == 0)
                        table.get(i*2 + 2).append(ColorUtilTools.prefix("\u251C", valueColor));
                    table.get(i*2 + 2).append(ColorUtilTools.prefix("\u2504".repeat(longestEach[j]+2), valueColor));
                    if (j == longestEach.length - 1)
                        table.get(i*2 + 2).append(ColorUtilTools.prefix("\u2524", valueColor));
                    if (j < longestEach.length - 1)
                        table.get(i*2 + 2).append(ColorUtilTools.prefix("\u253C", valueColor));
                    table.get(i*2 + 3).append(ColorUtilTools.prefix("\u2502", valueColor));
                    table.get(i*2 + 3).append(" ".repeat(leftPad)).append(value).append(" ".repeat(rightPad));
                    if (j == longestEach.length - 1)
                        table.get(i*2 + 3).append(ColorUtilTools.prefix("\u2502", valueColor));

                    }
             }
             table.get(0).append(ColorUtilTools.prefix("\u2510", headingColor));
             table.get(table.size()-1).append(ColorUtilTools.prefix("\u2518", valueColor));
             for (StringBuilder sb : table) {
                 System.out.println(sb);
             }
        }
    }

    public JsonOrError makeJsonOrError() {
        return new JsonOrError();
    }

}
