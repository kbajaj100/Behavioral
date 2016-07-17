package com.Sequence.Behavioral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Predictor {

	private int pcount;
	private int max; // Number of associations
	private String ante_sql;
	private String con_sql;
	private String SQL;
	private int pop; //mci_id count
	private String MCITable;
	private String carmaTable;
	private String pivotTable;
	private int limit = 100;
	private int insert_start_counter = 1;
	private int MCI_ID;
	private int mci_count;
	
	private String dbUrl;	
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs= null;
	
	DBConn myconn;
	SeqList[] mylist;
	
	public int getmax(int bracket) throws FileNotFoundException, IOException, SQLException{
		
		myconn = new DBConn();
		
		myconn.setDBConn("C:/Props/Sequence/DBprops.properties");

		settablecarma(bracket);
		SQL = "select COUNT(*) count from " + carmaTable; 
		
		max = myconn.execSQL_returnint(SQL);
		
		System.out.println("max is: " + max);
		
		settablepivot(bracket);
		settableMCI(bracket);
		return max;
	}
	

	public int ExecutePredictor(int bracket){
		
		/*
		 * 1. Get the ante and con from each record of carma_all 
		 * 2. Run it against the Pivot table and get back the MCIs
		 * 3. Store the MCIs in an array along with the SU_ind
		 */
				
		mylist = new SeqList[max];
		
		for (int i = 0; i < max; ++i) 
		{
			mylist[i] = new SeqList();
			mylist[i].setseqid(-1);
		}
		
		int breaker = 0, checker;
		
		for(pcount = 1; pcount <= max; ++pcount) //Condition should be max
		{
			checker = 0;
			
			SQL = "select ante_sql code from " + carmaTable + " where TABLE_ID = " + pcount;
			ante_sql = myconn.execSQL_returnString(SQL);
						
			SQL = "select con_sql code from " + carmaTable + " where TABLE_ID = " + pcount;
			con_sql = myconn.execSQL_returnString(SQL);
						
			SQL = "select count(a11.MCI_ID) count from " + pivotTable  + " a11 where " + ante_sql + " and " + con_sql;
			
			mci_count = myconn.execSQL_returnint(SQL);
			
			System.out.println("pcount is: " + pcount);
			System.out.println("mci_count is: " + mci_count);
			
			if (mci_count > 0)
			{
				mylist[pcount].setseqid(pcount);				
				mylist[pcount].initiate_mci_array(mci_count);
				
				SQL = "select MCI_ID code from " + pivotTable + " where " + ante_sql + " and " + con_sql;
	
				dbUrl = myconn.getdbUrl();
				
				
				try {
					//Step 1. Connection to the db
					conn = DriverManager.getConnection(dbUrl);
				
					// Create statement object
					stmt = conn.createStatement();
				
					// 3. Execute SQL query
					rs = stmt.executeQuery(SQL);
					
					//4. Process result set
					while (rs.next()){
						
						MCI_ID = rs.getInt("code");
						System.out.println("MCI_ID: " + MCI_ID);
						
						if (checker < mci_count)
						{
							mylist[pcount].assign_mci(checker, MCI_ID); 
							++checker;
							
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

				System.out.println("breaker is: " + breaker);
				++breaker;	
				
				if (breaker >= limit)
				{
					insertMCI(breaker);	
					breaker = 0;
				}
				
			}	
		}
		
		insertMCI(breaker);	
		mylist = null;
		
		
		return pcount;
	}

	
	private void setSU_Ind() {
		// TODO Auto-generated method stub
		
		SQL = "update a11 set a11.SU_Ind = a12.SU_Ind from " + MCITable + " a11 join dbo.mci_rank a12 on (a11.MCI_ID = a12.MCI_ID)";
		myconn.execSQL(SQL);
	}

	private int insertMCI(int breaker) {
		// TODO Auto-generated method stub
		SQL = "insert into " + MCITable + " (Sequence_ID, MCI_ID) values ";
		String insertvalue = "";
		
		int size;
		
		for(int i = insert_start_counter, x = 0; i <= pcount; ++i)
		{		
			if(mylist[i].getseqid() != -1)
			{
				size = mylist[i].getsize();
				System.out.println("size is: " + size);

				for (int j = 0; j < size; ++j)
				{	
					System.out.println("seqid is: " + mylist[i].getseqid());
					System.out.println("mci is: " + mylist[i].getmci(j));
							
					insertvalue = insertvalue + "(" + mylist[i].getseqid() + "," + mylist[i].getmci(j) + ")";
				
					if ((x < breaker) || (j < size-1))
						insertvalue = insertvalue + ",";
				}
				
				System.out.println("x is: " + x);
				
				if(x == breaker)
				{	
					System.out.println(insertvalue);
					SQL = SQL + insertvalue;
					System.out.println(SQL);
					
					myconn.execSQL(SQL);
				}
				else 
				++x;
			}
		}		
		
		insert_start_counter = pcount;
		setSU_Ind();
		return 1;
		
	}

	private void settableMCI(int bracket) {
		// TODO Auto-generated method stub
		
		MCITable = "dbo.carma_all_MCI_" + bracket + "M";
		System.out.println(MCITable);
		/*if (bracket == 10)
			MCITable = "dbo.carma_10_pct";
		else if (bracket == 20)
			MCITable = "dbo.carma_20_pct";
		else if (bracket == 80) 
			MCITable = "dbo.carma_80_pct";
		else 
			MCITable = "dbo.carma_All";*/
	}

	private void settablecarma(int bracket) {
		// TODO Auto-generated method stub
		
		carmaTable = "dbo.carma_all_" + bracket + "M";
		System.out.println(MCITable);
	}

	private void settablepivot(int bracket)
	{
		pivotTable = "dbo.MCI_DX_Pivot_" + bracket + "M"; 
	}
}	
