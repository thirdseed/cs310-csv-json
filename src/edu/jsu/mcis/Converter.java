package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.apache.commons.lang3.ArrayUtils;

public class Converter {
    
    /*
    
        Consider the following CSV data:
        
        "ID","Total","Assignment 1","Assignment 2","Exam 1"
        "111278","611","146","128","337"
        "111352","867","227","228","412"
        "111373","461","96","90","275"
        "111305","835","220","217","398"
        "111399","898","226","229","443"
        "111160","454","77","125","252"
        "111276","579","130","111","338"
        "111241","973","236","237","500"
        
        The corresponding JSON data would be similar to the following (tabs and
        other whitespace have been added for clarity).  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings, and which values should be encoded as integers!
        
        {
            "colHeaders":["ID","Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160",
            "111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }
    
    */
    
    private static final String COL_HEADER_NAME = "colHeaders";
    private static final String ROW_HEADER_NAME = "rowHeaders";
    private static final String DATA_ROWS_NAME = "data";
    
    /**
     * Generates a formatted JSON String from a given CSV String
     * @param csvString CSV String to translate to JSON; expects CSV form to be
     * fashioned like a table: its first line should comprise of "Column
     * Headers"; subsequent lines should define rows, with the 0 index value on
     * each row defining its "Row Header"
     * @return The given CSV String as a JSON String containing column header
     * and row header fields, with an additional field containing an array of
     * data rows
     */
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        try {
            
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();
            Iterator<String[]> iterator = full.iterator();

            JSONObject jsonOutputObject = new JSONObject();
            
            String[] currCSV_Row = iterator.next();

            JSONArray JA_DataArrays = new JSONArray();
            JSONArray JA_RowHeaders = new JSONArray();

            
            /* Arrange: Column-Header */
            /*
             * Per this assignment, the first CSV lines always contains column
             * headers
             */
            
            jsonOutputObject.put(COL_HEADER_NAME, stringArrayToJsonArray(currCSV_Row));
            
            
            /* Arrange: Row-Header/Data Row fields */
            /*
             * Per this assignment, subsequent CSV lines always contain data row
             * information
             */
             
            while (iterator.hasNext()) {
                
                // 0 index is row header, remaining indicies are row's data values
                currCSV_Row = iterator.next();
                
                JA_RowHeaders.add(currCSV_Row[0]);
                
                // to hold row values on this row
                JSONArray JA_DataRow = new JSONArray();
                
                for (int i = 1; i <  currCSV_Row.length; ++i) {
                    JA_DataRow.add(Integer.parseInt(currCSV_Row[i]));
                }
                
                JA_DataArrays.add(JA_DataRow);
            }
                                  
            jsonOutputObject.put(ROW_HEADER_NAME, JA_RowHeaders);
            jsonOutputObject.put(DATA_ROWS_NAME, JA_DataArrays);
            
            
            /* Submit arrangements */
            
            return jsonOutputObject.toJSONString().trim();
            
        }        
        catch(Exception e) { return e.toString(); }
        
    }
    
    /**
     * Generates a formatted CSV String from a given JSON String
     * @param jsonString JSON String to translate to a table-like CSV: expects
     * JSON to define row and column headers; further expects JSON to
     * collectively define row data fields, with each data row expressed as a
     * JSON array
     * @return 
     */
    public static String jsonToCsv(String jsonString) {
        
        try {
            
            JSONObject originJsonObject = (JSONObject)(JSONValue.parse(jsonString));
            
            // list to hold each (to-be) CSV line as an array
            List<String[]> parsedRowArrays = new ArrayList<>();
            
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\n');
            
            
            /* Arrange: Column Headers */
            
            parsedRowArrays.add(JsonArrayToStringArray((JSONArray)originJsonObject.get(COL_HEADER_NAME)));
            
            
            /* Arrange: Data Row & Row Header pairs */

            JSONArray JA_DataArrays = (JSONArray)originJsonObject.get(DATA_ROWS_NAME);
            JSONArray JA_RowHeaders = (JSONArray)originJsonObject.get(ROW_HEADER_NAME);

            for (int i = 0; i < JA_RowHeaders.size(); ++i) {
                
                // append row header onto array of its corresponding row data
                parsedRowArrays.add(ArrayUtils.insert(
                        0,
                        JsonArrayToStringArray((JSONArray)JA_DataArrays.get(i)),
                        JA_RowHeaders.get(i).toString()
                ));
                
            }
            
            
            /* Submit arrangements */
            
            csvWriter.writeAll(parsedRowArrays);
            
            return writer.toString().trim();
            
        }
        catch(Exception e) { return e.toString(); }
        
    }
    
    /**
     * Given a String[], produces a one-dimensional JSONArray containing
     * equivalent data
     * @param SA String[] to be translated to a one-dimensional JSONArray
     * @return JSONArray with equivalent underlying data to the passed String[]
     */
    private static JSONArray stringArrayToJsonArray(String[] SA) {
        
        JSONArray JA = new JSONArray();
        
        for (int i = 0; i < SA.length; ++i) {
            JA.add(SA[i]);
        }
        
        return JA;
        
    }
    
    /**
     * Given a one-dimensional JSONArray, produces a String[] containing
     * equivalent data
     * @param JA one-dimensional JSONArray to be translated to String[]
     * @return A String[] equivalent to the passed JSONArray's underlying data
     */
    private static String[] JsonArrayToStringArray(JSONArray JA) {
        
        String[] SA = new String[JA.size()];
        
        for (int i = 0; i < JA.size(); ++i) {
            SA[i] = (JA.get(i).toString());
        }
        
        return SA;
        
    }

}