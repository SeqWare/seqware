package net.sourceforge.solexatools.util;

/**
 * <p>PageInfo class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class PageInfo {
	private String startPage;
	private String endPage;
	private String allItems;
	
	private String info;
	private boolean isStart;
	private boolean isEnd;
	
	/**
	 * <p>Constructor for PageInfo.</p>
	 */
	public PageInfo() {
		this.info = "";
		this.isStart=false;
		this.isEnd=false;
	}

	/**
	 * <p>Getter for the field <code>info</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * <p>Setter for the field <code>info</code>.</p>
	 *
	 * @param info a {@link java.lang.String} object.
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * <p>Getter for the field <code>startPage</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getStartPage() {
		return startPage;
	}

	/**
	 * <p>Setter for the field <code>startPage</code>.</p>
	 *
	 * @param startPage a {@link java.lang.String} object.
	 */
	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}

	/**
	 * <p>Getter for the field <code>endPage</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getEndPage() {
		return endPage;
	}

	/**
	 * <p>Setter for the field <code>endPage</code>.</p>
	 *
	 * @param endPage a {@link java.lang.String} object.
	 */
	public void setEndPage(String endPage) {
		this.endPage = endPage;
	}
	
	/**
	 * <p>Getter for the field <code>allItems</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAllItems() {
		return allItems;
	}

	/**
	 * <p>Setter for the field <code>allItems</code>.</p>
	 *
	 * @param allItems a {@link java.lang.String} object.
	 */
	public void setAllItems(String allItems) {
		this.allItems = allItems;
	}

	/**
	 * <p>isStart.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isStart() {
		return isStart;
	}
	
	/**
	 * <p>Getter for the field <code>isStart</code>.</p>
	 *
	 * @return a boolean.
	 */
	public boolean getIsStart() {
		return isStart;
	}

	/**
	 * <p>Setter for the field <code>isStart</code>.</p>
	 *
	 * @param isStart a boolean.
	 */
	public void setIsStart(boolean isStart) {
		this.isStart = isStart;
	}

	/**
	 * <p>isEnd.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEnd() {
		return isEnd;
	}
	
	/**
	 * <p>Getter for the field <code>isEnd</code>.</p>
	 *
	 * @return a boolean.
	 */
	public boolean getIsEnd(){
		return isEnd;
	}

	/**
	 * <p>Setter for the field <code>isEnd</code>.</p>
	 *
	 * @param isEnd a boolean.
	 */
	public void setIsEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
}
