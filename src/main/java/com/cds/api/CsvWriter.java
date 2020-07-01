package com.cds.api;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public class CsvWriter {
    public boolean convert2CSV(List<Map<String,Object>> exportList, String fileName, String delimiter) {
        BufferedWriter fw;
        try{
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName+".csv"), "euc-kr"));
            Map<String,Object> header = exportList.get(0);
            // write Header
            String headerStr = "";
            for(String key : header.keySet()) {
                headerStr = headerStr + "\"" + key + "\"" + delimiter;
            }

            headerStr = headerStr.substring(0, headerStr.length()-1);
            fw.write(headerStr);
            fw.write(System.getProperty("line.separator"));

            for (Map<String,Object> map : exportList) {
                String line = "";

                // write data
                for (String key : map.keySet()) {
                    String data="";
                    if (map.get(key) != null) {
                        data = map.get(key).toString();
                    }
                    data.replace(",","_");
                    if(data.length()==0 || data.toUpperCase().equals("NULL")){
                        line = line + "\"\"" + delimiter;
                    }else {
                        line = line + "\""+ data + "\""+ delimiter;
                    }
                }
                line = line.substring(0, line.length()-1);
                fw.write(line);
                fw.write(System.getProperty("line.separator"));
            }
            fw.flush();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

