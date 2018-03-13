package com.alex.backtesting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.mysql.jdbc.PreparedStatement;

import db.jdbcmysql;

/**
 * Hello world!
 *
 */
public class App 
{
	final static File folder = new File("C:\\Users\\Alex\\Desktop\\Backtesting\\HSI");
	
    public static void main( String[] args )
    {
//    	String allstock = "0002.HK,0003.HK,0004.HK,0005.HK,0006.HK,0011.HK,0012.HK,0016.HK,0017.HK,0019.HK,0023.HK,0027.HK,0066.HK,0083.HK,0101.HK,0144.HK,0151.HK,0175.HK,0267.HK,0288.HK,0386.HK,0388.HK,0688.HK,0700.HK,0762.HK,0823.HK,0836.HK,0857.HK,0883.HK,0939.HK,0941.HK,0992.HK,1038.HK,1044.HK,1088.HK,1109.HK,1113.HK,1299.HK,1398.HK,1928.HK,1997.HK,2007.HK,2318.HK,2319.HK,2382.HK,2388.HK,2628.HK,3328.HK,3988.HK";
//    	 jdbcmysql test = new jdbcmysql(); 
//    	 for(String SC: allstock.split(",")){
//    		 System.out.println(SC);
//    		 test.SelectAllStockPrice(SC,14);
//    	 }
    	
    	jdbcmysql test = new jdbcmysql(); 
    	test.CaluculateRSIDays();
    		 
        
    }
    
    public void calculateRSI(){
    	
    }
    
    public void insertCsv(){
    	ArrayList<stockprice> AllStockPrice = new ArrayList<stockprice>();
    	
    	for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
               continue;
            } else {
            	AllStockPrice.addAll(readCSVFile(fileEntry.getName().trim()));
            }
        }
    	
    	
        jdbcmysql test = new jdbcmysql(); 
        test.insertTable(AllStockPrice); 
    }
    

    
    public static ArrayList<stockprice> readCSVFile(String csvFile){
    	ArrayList<stockprice> AllStockPrice = new ArrayList<stockprice>(); 
        String line = "";
        String cvsSplitBy = ",";
        int linecount = 0;
        
        System.out.println(csvFile);

        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Alex\\Desktop\\Backtesting\\HSI\\"+ csvFile))) {

            while ((line = br.readLine()) != null) {
            	if(linecount == 0){
            		linecount++;
            		continue;
            	}
            	
            	
            	
            	stockprice currentstockprice = new stockprice();

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                System.out.println(data[0]);
                if(data.length > 7){
                	continue;
                }
                
                try{
                    currentstockprice.setDate(data[0]);
                    currentstockprice.setCode(csvFile.split(".csv")[0]);
                    currentstockprice.setOpen(Double.parseDouble(data[1]));
                    currentstockprice.setHigh(Double.parseDouble(data[2]));
                    currentstockprice.setLow(Double.parseDouble(data[3]));
                    currentstockprice.setClose(Double.parseDouble(data[4]));
                    currentstockprice.setAdj_close(Double.parseDouble(data[5]));
                    currentstockprice.setVolume(Integer.parseInt(data[6]));
                    
                    
                }catch (NumberFormatException e) {
                    continue;
                }
                
                AllStockPrice.add(currentstockprice);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return AllStockPrice;
    }
}
