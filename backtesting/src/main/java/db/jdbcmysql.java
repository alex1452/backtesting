package db; 
 
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.alex.backtesting.stockprice; 
 
public class jdbcmysql {

  final static Logger logger = Logger.getLogger(jdbcmysql.class);
	
  private Connection con = null; //Database objects 
  //連接object 
  private Statement stat = null; 
  //執行,傳入之sql為完整字串 
  private ResultSet rs = null;
  private ResultSet rs2 = null;
  private ResultSet rs3 = null; 
  //結果集 
  private PreparedStatement pst = null; 
  //執行,傳入之sql為預儲之字申,需要傳入變數之位置 
  //先利用?來做標示
  
  private PreparedStatement pst2 = null; 
  
  private PreparedStatement pst3 = null;
  
  private String createdbSQL = "CREATE TABLE User (" + 
    "    id     INTEGER " + 
    "  , name    VARCHAR(20) " + 
    "  , passwd  VARCHAR(20))"; 
  
  private String insertdbSQL = "insert into User(id,name,passwd) " + 
      "select ifNULL(max(id),0)+1,?,? FROM User"; 
  
  private String insertdbSQLStock = "INSERT INTO stockrice (Date, Code, Open, High, Low, Close, Adj_Close, Volume)"
  		+ "VALUES (?,?,?,?,?,?,?,?);";
  
  private String insertRSISQL = "INSERT INTO ta (Date,Code, RSI14) VALUES (?, ?, ?);";
  
  private String selectStockSQLSQL = "SELECT * FROM stockrice s inner join ta t on s.Date = t.Date And s.Code = t.Code;"; 
  
  private String selectAllStockPriceSQL = "SELECT * FROM stockrice WHERE Code = ? order by Date";
  
  private String selectAllStockSQL = "SELECT * FROM stockrice WHERE Date = ? and Code = ? order by Date";
  
  public jdbcmysql() 
  { 
    try { 
      Class.forName("com.mysql.jdbc.Driver"); 
      //註冊driver 
      con = DriverManager.getConnection( 
      "jdbc:mysql://localhost/hkstockbacktesting?useUnicode=true&characterEncoding=Big5", 
      "root",""); 
      //取得connection
 
//jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
//localhost是主機名,test是database名
//useUnicode=true&characterEncoding=Big5使用的編碼 
      
    } 
    catch(ClassNotFoundException e) 
    { 
      System.out.println("DriverClassNotFound :"+e.toString()); 
    }//有可能會產生sqlexception 
    catch(SQLException x) { 
      System.out.println("Exception :"+x.toString()); 
    } 
    
  } 
  //建立table的方式 
  //可以看看Statement的使用方式 
  public void createTable() 
  { 
    try 
    { 
      stat = con.createStatement(); 
      stat.executeUpdate(createdbSQL); 
    } 
    catch(SQLException e) 
    { 
      System.out.println("CreateDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
  } 
  //新增資料 
  //可以看看PrepareStatement的使用方式 
  public void insertTable(ArrayList<stockprice> AllStockPrice) 
  { 
	for (stockprice sp :AllStockPrice) {
		
    try 
    { 
      pst = con.prepareStatement(insertdbSQLStock);
      
      Date utilDate;
	  utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(sp.getDate());
	  java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); 
      pst.setDate(1, sqlDate); 
      
      pst.setString(2, sp.getCode()); 
      pst.setDouble(3, sp.getOpen());
      pst.setDouble(4, sp.getHigh());
      pst.setDouble(5, sp.getLow());
      pst.setDouble(6, sp.getClose());
      pst.setDouble(7, sp.getAdj_close());
      pst.setInt(8, sp.getVolume());
      
      pst.executeUpdate(); 
    } 
    catch(SQLException e) 
    { 
      System.out.println("InsertDB Exception :" + e.toString()); 
    } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
    finally 
    { 
      Close(); 
    }
	}
  } 
  //刪除Table, 
  //跟建立table很像 
  public void CaluculateRSIDays() 
  { 
	String rsi[][];
	int rsidaysafter[] = {5,10,15,20};
	Map<String,Date> rsionHoldMap = new HashMap<String,Date>();
	
    try 
    { 
      stat = con.createStatement();
      rs = stat.executeQuery(selectStockSQLSQL);
      while(rs.next()){

    	  
    	  if(rs.getDouble("RSI14")<31){
    		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		  Date sdt = sdf.parse("2018-02-14");
        	  Date dt = rs.getDate("Date");
        	  Calendar c = Calendar.getInstance(); 
        	  c.setTime(dt);
        	  
        	  for(int rsidayafter : rsidaysafter){
        		  do{
                	  c.add(Calendar.DATE, rsidayafter);

                	  dt = c.getTime();
        			  pst2 = con.prepareStatement(selectAllStockSQL);
                      java.sql.Date dtsql = new java.sql.Date(dt.getTime());
                      pst2.setDate(1, dtsql);
                      pst2.setString(2, rs.getString("Code"));
                      rs2 = pst2.executeQuery();
    	              
                      //System.out.println(rs.getDate("Date") + "," + rs.getString("Code") +"," + dtsql);
    	              
    	              if(dt.compareTo(sdt)>=0){
    	            	  break;
    	              }
    	              
    	              if(rsionHoldMap.containsKey(rs.getString("Code"))){
    	            	  if(givenTwoDates(rsionHoldMap.get(rs.getString("Code")),dt)<5){
    	            		  //System.out.println(dt + "," + rsionHoldMap.get(rs.getString("Code")));
    	            		  //System.out.println("CP2: "+ givenTwoDates(dt,rsionHoldMap.get(rs.getString("Code"))));
    	            		  continue;
    	            	  }
    	            	  else{
    	            		  rsionHoldMap.remove(rs.getString("Code"));
    	            	  }
    	              }
    	              
    	              //System.out.println("checkpoint1");
    	              
    	              if (rs2.next() == false) {
    	            	  c.add(Calendar.DATE, 1);
    	            	  //System.out.println("CP1");
    	              }
    	              else {
    	                	Double endClose = rs2.getDouble("Close");
    	                	Double Changes = (endClose - rs.getDouble("Close"))/(rs.getDouble("Close"))*100;
    	                    System.out.println(rs.getDate("Date") + "," + rs2.getDate("Date") + "," + rs.getString("Code")+ "," + rs.getString("RSI14") + "," + rs.getDouble("Close") + "," + endClose +"," + Changes);
    	                    rsionHoldMap.put(rs.getString("Code"), rs.getDate("Date"));
    	                    break;
    	              }
                  } while(true);
        	  }
    	  }
      }
    } 
    catch(SQLException e) 
    { 
      System.out.println("Select Exception :" + e.toString()); 
    } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
    finally 
    { 
      Close(); 
    } 
  } 
  
  public long givenTwoDates(Date firstDate,Date secondDate)
		  throws ParseException {
		      long diffInMillies = secondDate.getTime() - firstDate.getTime();
		    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		 
		   return diff;
		}
 
  public void CaluculateRSI(String StockCode, int RSIDays) 
  { 
	int RSIDaysCount = RSIDays;
    try 
    { 
      pst2 = con.prepareStatement(selectAllStockPriceSQL);
      pst2.setString(1, StockCode);
      rs = pst2.executeQuery();
      
      Double PreClose = 0.00000;
      
      Double RSIPov = 0.00000;
      Double RSINav = 0.00000;
      
      Double RSICountPov = 0.00000;
      Double RSICountNav = 0.00000;
      
      Double RSIAvgPov = 0.00000;
      Double RSIAvgNav = 0.00000;
      
      Double RSI = 0.00000;
      
      while(rs.next()) 
      {
    	  
    	  if(rs.getDouble("Close")- PreClose>=0){
    		  RSIPov = rs.getDouble("Close") - PreClose;
    		  RSINav = 0.00000; 
    	  }
    	  else{
    		  RSINav = Math.abs(rs.getDouble("Close") - PreClose);
    		  RSIPov = 0.00000;
    	  }
    	  
    	  System.out.println(RSIPov + " : " + RSINav);
    	  
    	  if(RSIDaysCount>0){
    		  if(RSIDaysCount == RSIDays){
    			  RSIPov = 0.00000;
    			  RSINav = 0.00000; 
    		  }
    		  RSIDaysCount--;
    		  RSICountPov = RSICountPov + RSIPov;
    		  RSICountNav = RSICountNav + RSINav;
    		  
    		  RSIAvgPov = RSICountPov/RSIDays;
    		  RSIAvgNav = RSICountNav/RSIDays;
    		  
    		  if(RSIDaysCount == 1){
    			  RSI =100-100/(1+((RSIAvgPov)/(RSIAvgNav)));
    		  }
    			  
    		  else
    			  RSI = 0.00000;
    	  }
    	  else{
    		  RSIAvgPov = (RSIAvgPov*(RSIDays-1)+RSIPov)/RSIDays;
    		  RSIAvgNav = (RSIAvgNav*(RSIDays-1)+RSINav)/RSIDays;
    		  RSI =100-100/(1+((RSIAvgPov)/(RSIAvgNav)));
    	  }
    		  

    	  //System.out.println(rs.getDate("Date").toGMTString() + " : " + RSI);
    	  pst3 = con.prepareStatement(insertRSISQL);
    	  pst3.setDate(1, rs.getDate("Date"));
    	  pst3.setString(2, rs.getString("Code"));
    	  pst3.setDouble(3, RSI);
    	  pst3.executeUpdate();
    	  
    	  PreClose = rs.getDouble("Close");
      } 
    } 
    catch(SQLException e) 
    { 
      System.out.println("InsertDB Exception :" + e.toString()); 
    } 
    finally 
    { 
      Close(); 
    } 
  } 
  //完整使用完資料庫後,記得要關閉所有Object 
  //否則在等待Timeout時,可能會有Connection poor的狀況 
  private void Close() 
  { 
    try 
    { 
      if(rs!=null) 
      { 
        rs.close(); 
        rs = null; 
      } 
      if(rs2!=null) 
      { 
        rs2.close(); 
        rs2 = null; 
      } 
      if(rs3!=null) 
      { 
        rs3.close(); 
        rs3 = null; 
      } 
      if(stat!=null) 
      { 
        stat.close(); 
        stat = null; 
      } 
      if(pst!=null) 
      { 
        pst.close(); 
        pst = null; 
      } 
      if(pst2!=null) 
      { 
        pst2.close(); 
        pst2 = null; 
      } 
      
      if(pst3!=null) 
      { 
        pst3.close(); 
        pst3 = null; 
      } 
    } 
    catch(SQLException e) 
    { 
      System.out.println("Close Exception :" + e.toString()); 
    } 
  } 
  
 
}