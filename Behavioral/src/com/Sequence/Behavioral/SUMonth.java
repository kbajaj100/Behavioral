package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SUMonth {

	private int count;
	private int carma_max; // Number of associations
	private int MCI;
	private String SQL;
	private int Month; 
	private String dbUrl;
	private int limit = 121900;
	private int PA; //Paid Amount
	
	DBConn myconn;
	Carma[] mycarma;
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs= null;
	
	MCIList[] myList;
	
	public int getmax() throws FileNotFoundException, IOException, SQLException{
		
		myconn = new DBConn();
		
		myconn.setDBConn("C:/Props/Sequence/DBprops.properties");
		
		SQL = "select COUNT(*) count from dbo.MCI_DX_Pivot where SU_Ind = 3";
		
		carma_max = myconn.execSQL_returnint(SQL);
		
		myList = new MCIList[carma_max];
		
		for(int i = 0; i< carma_max; ++i)
			myList[i] = new MCIList();
			
		return carma_max;
	}
	
	public int getPAfromDB(){
		
		System.out.println();
		System.out.println();
		System.out.println("Executing getPAfromDB");
		
		for(int i = 0; i < carma_max; ++i)
		{
			PA = 0;
			
			SQL = "select Continuum_Month_Key Month, SUM(Paid_Amount) PA from dbo.ahci_claims_match_neg where MCI_ID = " + myList[i].getMCI() + " group by Continuum_Month_Key order by Continuum_Month_Key";
			System.out.println("MCI is: " + myList[i].getMCI());
			
			try {
				//Step 1. Connection to the db
				conn = DriverManager.getConnection(dbUrl);
		
				// Create statement object
				stmt = conn.createStatement();
		
				// 3. Execute SQL query
				rs = stmt.executeQuery(SQL);
			
				//4. Process result set
				while (rs.next()){
				
					Month = rs.getInt("Month");
					System.out.println("Month is: " + Month);
					
					PA = PA + rs.getInt("PA");
					System.out.println("PA is: " + PA);
					
					if (PA >= limit){
						myList[i].setMonth(Month);
						break;
					}
				}
			}
		
			// Handle any errors that may have occurred.
			catch (Exception e) {
				e.printStackTrace();
			}
			
			finally {
				//close(myConn, myStmt, myRS);
				if (rs   != null) try { rs.close();   } catch(Exception e) {}
				if (stmt != null) try { stmt.close(); } catch(Exception e) {}
				if (conn != null) try { conn.close(); } catch(Exception e) {}
			}
		}
		
		return 0;
	}
	
	public int getMCIfromDB(){
		
		dbUrl = myconn.getdbUrl();
		
		SQL = "select distinct MCI_ID count from dbo.MCI_DX_Pivot where SU_Ind = 3";
		
		int i = 0;
		
		try {
			//Step 1. Connection to the db
			conn = DriverManager.getConnection(dbUrl);
		
			// Create statement object
			stmt = conn.createStatement();
		
			// 3. Execute SQL query
			rs = stmt.executeQuery(SQL);
			
			//4. Process result set
			while (rs.next()){
				
				MCI = rs.getInt("count");
				System.out.println("MCI is: " + count);
				
				myList[i].setMCI(MCI);
				++i;
			}
		}
		
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
			//close(myConn, myStmt, myRS);
			if (rs   != null) try { rs.close();   } catch(Exception e) {}
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (conn != null) try { conn.close(); } catch(Exception e) {}
		}
		
		return 0;
	}
	
	public int updateMonth(){
		
		for(int i = 0; i < carma_max; ++i){
			
			SQL = "update dbo.MCI_Month_Continuum_Pivot set Month_SU = " + myList[i].getMonth() + " where MCI_ID = " + myList[i].getMCI();
			myconn.execSQL(SQL);
		}
		
		return 0;
		
	}
}
