package kobic.msb.swing.thread.callable.obj;

import java.util.List;

public class NGSRnaListObj {
	private Object[] header;
	private List<Object[]> readedAllObjList;
	private List<Object[]> choosedRnaObjList;
	
	public NGSRnaListObj(Object[] header, List<Object[]> readedAllObjList, List<Object[]> choosedRnaObjList ) {
		this.header = header;
		this.readedAllObjList = readedAllObjList;
		this.choosedRnaObjList = choosedRnaObjList;
	}

	public Object[] getHeader() {
		return header;
	}

	public void setHeader(Object[] header) {
		this.header = header;
	}

	public List<Object[]> getReadedAllObjList() {
		return readedAllObjList;
	}

	public void setReadedAllObjList(List<Object[]> readedAllObjList) {
		this.readedAllObjList = readedAllObjList;
	}

	public List<Object[]> getChoosedRnaObjList() {
		return choosedRnaObjList;
	}

	public void setChoosedRnaObjList(List<Object[]> choosedRnaObjList) {
		this.choosedRnaObjList = choosedRnaObjList;
	}
}
