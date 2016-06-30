package com.Sequence.Behavioral;

public class SeqList {

	int[] mci_list;
	int sequence_ID;
	
	public void initiate_mci_array(int mci_count) {
		// TODO Auto-generated method stub
	
		mci_list = new int[mci_count];
		
		for (int i = 0; i < mci_count; ++i)
			mci_list[i] = 0;
	}

	public void assign_mci(int checker, int mci) {
		// TODO Auto-generated method stub
		
		mci_list[checker] = mci;
		
	}

	public void setseqid(int seqid) {
		// TODO Auto-generated method stub
		sequence_ID = seqid;
	}

	public int getseqid() {
		// TODO Auto-generated method stub
		return sequence_ID;
	}

	public int getmci(int j) {
		// TODO Auto-generated method stub
		return mci_list[j];
	}

	public int getsize() {
		// TODO Auto-generated method stub
		return mci_list.length;
	}
	
	
}
