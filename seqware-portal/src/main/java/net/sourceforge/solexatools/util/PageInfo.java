package net.sourceforge.solexatools.util;

public class PageInfo {
	private String startPage;
	private String endPage;
	private String allItems;
	
	private String info;
	private boolean isStart;
	private boolean isEnd;
	
	public PageInfo() {
		this.info = "";
		this.isStart=false;
		this.isEnd=false;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getStartPage() {
		return startPage;
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}

	public String getEndPage() {
		return endPage;
	}

	public void setEndPage(String endPage) {
		this.endPage = endPage;
	}
	
	public String getAllItems() {
		return allItems;
	}

	public void setAllItems(String allItems) {
		this.allItems = allItems;
	}

	public boolean isStart() {
		return isStart;
	}
	
	public boolean getIsStart() {
		return isStart;
	}

	public void setIsStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isEnd() {
		return isEnd;
	}
	
	public boolean getIsEnd(){
		return isEnd;
	}

	public void setIsEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
}
