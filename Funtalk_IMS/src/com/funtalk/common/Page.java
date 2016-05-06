package com.funtalk.common;

import java.util.List;

/**
 *
 */
public class Page {

    //��ҳȫ�ֱ��� begin
    private int curPage = 1; //��ǰҳ��
    private int rowCount; //�ܼ�¼��
    private int pageSize = 10; //ÿҳ����
    private int pageCount; //�ܵ�ҳ��
    private int beginPos; //����������ݵ���ʼλ��
    private int endPos; //����������ݵ���ֹλ��
    private List list;

    public Page()
    {
    }

    public Page(int curPage)
    {
        if (curPage < 1)
        {
            curPage = 1;
        }
        this.curPage = curPage;
    }

    public Page(int curPage, List list)
    {
        this.curPage = curPage;
        this.list = list;
    }
    
    public int getRowCount(){

        return this.rowCount;
    }

    public int getPageCount(){
    	this.pageCount = (rowCount + pageSize - 1) / pageSize;
        return pageCount;
    }
    
    public int getBeginPos(){
        return beginPos = (curPage - 1) * pageSize + 1;
    }


	public Page(int curPage, int rowCount, int pageSize) {
		super();
		this.curPage = curPage;
		this.rowCount = rowCount;
		this.pageSize = pageSize;
	}

	/**
	 * @return the curPage
	 */
	public int getCurPage() {
		return curPage;
	}

	/**
	 * @param curPage the curPage to set
	 */
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	/**
	 * @return the endPos
	 */
	public int getEndPos() {
		
		endPos = beginPos + pageSize - 1;
        if (endPos > rowCount)
        {
            endPos = rowCount;
            //��ҳ end
        }
		return endPos;
	}


	/**
	 * @return the list
	 */
	public List getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List list) {
		this.list = list;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
    
}
