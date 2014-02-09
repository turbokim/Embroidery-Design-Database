import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class EmbroideryDB {
	
	
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		static final String DB_URL = "jdbc:mysql://localhost:3306/";
		static final String DATABASE = "Embroidery";
		private String USER;
		private String PASS;
		
		private static EmbroideryDB _db = null;
		private Statement statement = null;
		private Connection conn = null;
		
		/**
		 * 
		 * @return
		 */
		public static EmbroideryDB getDb() {
			if (_db == null) {
				_db = new EmbroideryDB();
			}
			
			return _db;
	
		}
		
		/**
		 * This now reads from the src/DATA/file so it should work right out of the box
		 * 
		 * 
		 * @param data
		 * @param table
		 * @throws SQLException
		 */
		private void populateTables(String data, String table) throws SQLException{
			System.out.println("Populating Table " + table + "....");
			

			String designs = ("LOAD DATA LOCAL INFILE " + "'"+data+"'" 
							+ " INTO TABLE " + table
							+ " FIELDS TERMINATED BY ',' " 
							+ " OPTIONALLY ENCLOSED BY '\"'"
							+ "LINES TERMINATED BY '\\n'");	
			
			
//			String designs = ("LOAD DATA INFILE " + "'"+data+"'" 
//							+ " INTO TABLE " + table
//							+ " FIELDS TERMINATED BY ',' " 
//							+ "LINES TERMINATED BY '\\n'");			
			try {				
				statement.execute(designs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		/**
		 * 
		 * @return
		 * @throws Exception
		 */
		public boolean connect() throws Exception{
			boolean ableToConnect = false;			
			
			try{
				Class.forName(JDBC_DRIVER);
				
				System.out.println("Connecting to database...");				
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				
				System.out.println("Creating Database...");
				statement = conn.createStatement();
				String query = "CREATE DATABASE IF NOT EXISTS " + DATABASE;
				statement.execute(query);
				System.out.println("Database created successfully...");
				conn.close();
				conn = DriverManager.getConnection(DB_URL+DATABASE, USER, PASS);				
				ableToConnect = true;		
				
			}catch(SQLException ex){
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			
			}finally{
				if(statement != null){
					statement.close();
				}
			}
			
			return ableToConnect;
		}
		
		/**
		 * Creates and Populates the Tables.
		 * 
		 * Each table is removed and recreated if it already exists to 
		 * get a clean start. After the tables are created they are
		 * populated with a local text file.
		 * 
		 * @throws SQLException
		 */
		public void createTables() throws SQLException{
			
			
			String machines = "CREATE TABLE machines ( "
					+"Make VARCHAR(45) not NULL, "
					+"Model VARCHAR(45) not NULL, "
					+"Speed INT(11) DEFAULT NULL, "
					+"MaxHeight decimal(4 , 2 ) DEFAULT NULL,"
					+"MaxWidth decimal(4 , 2 ) DEFAULT NULL,"					
					+"format SET('ART','DST','EXP','HUS','JEF','PCS','PES',"
					+"'SEW','CND','VIP','XXX','VP3','PEC','PCQ') DEFAULT NULL, "
					+"MaxNeedles INT(11) DEFAULT NULL, "
					+"PRIMARY KEY (Make, Model))";
			
			String designs = "CREATE TABLE designs ( "
					+"`DesignBrand` varchar(45) NOT NULL, "
					+"`DesignID` varchar(45) NOT NULL, "
					+"`Genre` varchar(45) DEFAULT NULL, "
					+"`Height` decimal(4 , 2 ) DEFAULT NULL, "
					+"`Width` decimal(4 , 2 ) DEFAULT NULL, "					
					+"`SubGenre` varchar(45) DEFAULT NULL, "
					+"`DesignPrice` decimal(4 , 2 ) DEFAULT NULL, "						
					+"`DesignName` varchar(45) DEFAULT NULL, "
					+"`Colors` SET('White','Black','Gray','Dk. Gray','Lt. Gray',"
					+"'Brown','Dk. Brown','Lt. Brown','Gold','Yellow','Dk. Yellow','Lt. Yellow',"
					+"'Red','Dk. Red','Lt. Red','Blue','Dk. Blue','Lt. Blue','Green',"
					+"'Dk. Green','Lt. Green','Purple','Orange','Dk. Orange','Pink') DEFAULT NULL, "
					+"`StitchCount` int(11) DEFAULT NULL, "
					+"PRIMARY KEY (`DesignBrand` , `DesignID`)) " ;	
			
			String threads = "CREATE TABLE `thread` ("
		    		+"`ThreadBrand` varchar(45) NOT NULL,"
		    		+"`ThreadID` varchar(45) NOT NULL,"
		    		+"`ThreadType` varchar(45) DEFAULT NULL,"
		    		+"`Weight` int(11) DEFAULT NULL,"
		    		+"`ThreadPrice` decimal(4 , 2 ) DEFAULT NULL,"
		    		+"`ColorName` varchar(45) DEFAULT NULL,"
		    		+"`Color` varchar(45) DEFAULT NULL, "
		    		+"PRIMARY KEY (`ThreadBrand` , `ThreadID`))";			
			
			String needles = "CREATE TABLE `needles` ("		    		
					+"`ID` varchar(45) NOT NULL,"
					+"`NeedleBrand` varchar(45) DEFAULT NULL,"
		    		+"`NeedleSize` int(11) DEFAULT NULL,"
		    		+"`NeedleType` varchar(45) DEFAULT NULL,"
		    		+"`NeedlePrice` decimal(4 , 2 ) DEFAULT NULL,"
		    		+"PRIMARY KEY (`ID`))";
			
			String dFormats = "CREATE  TABLE `DesignFormats` ("
					 +"`Vender` VARCHAR(45) NOT NULL,"
					 +"format SET('ART','DST','EXP','HUS','JEF','PCS','PES',"
					 +"'SEW','CND','VIP','XXX','VP3','PEC','PCQ') DEFAULT NULL, "					
					 +"PRIMARY KEY (`Vender`) )";
			try {
				statement = conn.createStatement();					
				statement.executeUpdate("DROP TABLE IF EXISTS machines");
				statement.execute(machines);				
				statement.executeUpdate("DROP TABLE IF EXISTS designs");							
				statement.execute(designs);
				statement.executeUpdate("DROP TABLE IF EXISTS thread");				
				statement.execute(threads);
				statement.executeUpdate("DROP TABLE IF EXISTS needles");				
				statement.execute(needles);
				statement.executeUpdate("DROP TABLE IF EXISTS DesignFormats");
				statement.execute(dFormats);				
			
				populateTables("src/DATA/needles.txt", "needles");
				populateTables("src/DATA/thread.txt", "thread");
				populateTables("src/DATA/machines.txt", "machines");
				populateTables("src/DATA/designs.txt", "designs");
				populateTables("src/DATA/designFormats", "designFormats");
			} catch (SQLException e) {
				
				e.printStackTrace();
			}finally{
				
				if(statement != null){
					statement.close();
				}
			}
			
			
		}
		
		/**
		 * Returns an array of Makes and Models of machines
		 * 
		 * @param query
		 * @return
		 */
		public String[] machineList(String query){
			List<String> list = new ArrayList<String>();			
			
			ResultSet rs = null;
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(query);					
				
				while(rs.next()){
					list.add(rs.getString("Make")+ "  " + rs.getString("Model"));					
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}			
			return list.toArray(new String[list.size()]);
		}

		/**
		 * Returns a list of a specified column.
		 * 
		 * @param query
		 * @param column
		 * @return
		 */
		public String[] getList(String query, String column){
			ResultSet rs = null;
			List<String> list = new ArrayList<String>();
			List<String> otherList = new ArrayList<String>();
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(query);
				while(rs.next()){
					otherList.add(rs.getString(column));
				}
				for(int i = 1; i < otherList.size(); i++) {
					list.add(otherList.get(i));
				}
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			return list.toArray(new String[list.size()]);
			
		}
		
		/**
		 * Returns a basic query
		 * 
		 * @param query
		 * @return
		 * @throws SQLException
		 */
		public String execute(String query) throws SQLException{
			ResultSet rs = null;
			String ret = "";
			
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(query);
				ResultSetMetaData meta = rs.getMetaData();
				
				int colCnt = meta.getColumnCount();
				for(int i = 1; i <= colCnt; i++){
					ret += meta.getColumnName(i) + "\t";
				}
				ret += "\r\n";
				while (rs.next()) {
					
					for (int i = 1; i <= meta.getColumnCount(); i++) {
						ret += rs.getString(i) + "\t";
				}
						
				ret += "\r\n";
				}
				
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			} finally {
				if(statement != null){
					statement.close();
				}
			}
			return ret;
		
		}
		
		public String[] getMachineDimensions(String make, String model) {
			
			String query = "select MaxHeight, MaxWidth from machines " +
					"where Make = '"+ make +"' AND Model = '" +
							model + "'";
			List<String> list = new ArrayList<String>();			
			
			ResultSet rs = null;
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(query);					
				
				while(rs.next()){
					list.add(rs.getString("MaxHeight"));
					list.add(rs.getString("MaxWidth"));
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}			
			return list.toArray(new String[list.size()]);
		}
		/**
		 * Queries the database for the machine formats of each model
		 * 
		 * @param make The make of the machine.
		 * @param model The model of the machine.
		 * @return String array of machine formats of model
		 */
		public String[] getMachineFormats(String make, String model) {		
			
			String queryString = "select format from machines where Make = '" + make + "' AND Model = '" + model + "'";
			String unsplit = "";
			ResultSet rs = null;
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(queryString);
				
				while(rs.next()){
					unsplit = rs.getString("format");
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			return unsplit.split("[,]");
		}
		/**
		 * Queries the database for the colors of the designs.
		 * 
		 * @param query
		 * @return
		 */
		public String[] getDesignColors(String brand, String designText) {			
			
			String query = "Select Colors from designs where DesignBrand = '" +
					brand +
					"' AND DesignID = '" + designText + "'";
					
			String unsplit = "";
			ResultSet rs = null;
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(query);
				
				while(rs.next()){
					unsplit = rs.getString("Colors");
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			return unsplit.split("[,]");
		}
		
		public Double getPriceByID(String query) {
			ResultSet rs = null;
			Double price = 0.00;
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(query);
				while(rs.next()){
					price = rs.getDouble(1);
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			return price;
		}

		public void setUser(String text) {
			USER = text;
			
		}

		public void setPass(String text) {
			PASS = text;
			
		}
		
}
