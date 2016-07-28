package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EndCom {

	private int max; // Number of patients
	private int MCI; // patient ID
	private String SQL;
	private int pop; //DX count
	private String dbUrl;
	private String EndComb;
	private int limit = 10;
	private int insert_start_counter = 1;
	
	DBConn myconn;
	ComList[] myComList;
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs= null;
	
	public int getmax() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
	
		myconn = new DBConn();
		
		myconn.setDBConn("C:/Props/Sequence/DBprops.properties");
		
		SQL = "select count(distinct MCI_ID) count from dbo.MCI_DX_Pivot where SU_Ind = 3";
			
		max = myconn.execSQL_returnint(SQL);
		
		myComList = new ComList[max];
		
		for (int i = 0; i < max; ++i)
			myComList[i] = new ComList();
		
		return max;
	}
	
	public int setMCIandDXNum(){
		
		// Get the result set of MCI and DXNum
		// for loop to insert the MCI and DXNum into the array
			
		SQL = "select a11.MCI_ID MCI, count(distinct a11.DX_Category) count " + 
			  "from dbo.ahci_claims_match_neg a11 " + 
			  "join dbo.MCI_DX_Pivot a12 on (a11.MCI_ID = a12.MCI_ID) " +
			  "where a11.NSU_YEAR between 2001 and 2003 and a12.SU_Ind = 3 " + 
			  "group by a11.MCI_ID " +
			  "order by a11.MCI_ID";

		dbUrl = myconn.getdbUrl();

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
						
				MCI = rs.getInt("MCI");
				pop = rs.getInt("count");
				//System.out.println("MCI is: " + pop);
						
				myComList[i].setMCI(MCI);
				myComList[i].setnumDX(pop);
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

	public void setEndComb(){

		int first;
		
		for(int i = 0; i < max; ++i){

			first = 1;
			
			SQL = "select distinct rtrim(DX_Category) code " + 
			"from dbo.ahci_claims_match_neg " +
			"where MCI_ID = " + myComList[i].getMCI() + " " +
			"order by rtrim(DX_Category)";

			System.out.println(SQL);
	
			try {
				//Step 1. Connection to the db
				conn = DriverManager.getConnection(dbUrl);

				// Create statement object
				stmt = conn.createStatement();

				// 3. Execute SQL query
				rs = stmt.executeQuery(SQL);

				//4. Process result set
				while (rs.next()){

					if(first == 1)
						EndComb = rs.getString("code");
					else EndComb = EndComb + "->" + rs.getString("code");

					first = 0;
				
				}
				
				SQL = "insert into dbo.MCI_END_COMBINATION " + 
				"(MCI_ID, COMBINATION) " +
				"values(" + myComList[i].getMCI() + ",'" + EndComb + "')";
				
				System.out.println(SQL);
				myconn.execSQL(SQL);
				
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
		
	}
	
}
