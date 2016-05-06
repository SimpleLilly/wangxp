package com.funtalk.pojo.rightmanage;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class LocalDataTableCol implements Serializable {

    /** identifier field */
    private String tableid;

    /** identifier field */
    private String columnseq;

    /** identifier field */
    private String columnname;

    /** identifier field */
    private String columntype;

    /** identifier field */
    private String columncomment;

    /** identifier field */
    private String isindex;

    /** identifier field */
    private String isquery;

    /** identifier field */
    private String configration;

    /** identifier field */
    private String linktableid;

    /** identifier field */
    private String isorder;

    /** identifier field */
    private String style;

    /** identifier field */
    private String isnull;

    /** identifier field */
    private String ismuti;

    /** identifier field */
    private String helpid;

    /** identifier field */
    private String defaultvalue;
    
    private String edittype;

    public LocalDataTableCol(String columnseq, String columnname, String columntype, String isindex) {
		super();
		this.columnseq = columnseq;
		this.columnname = columnname;
		this.columntype = columntype;
		this.isindex = isindex;
	}

	/** full constructor */
    public LocalDataTableCol(String tableid, String columnseq, String columnname, String columntype, String columncomment, String isindex, String isquery, String configration, String linktableid, String isorder, String style, String isnull, String ismuti, String helpid, String defaultvalue,String edittype) {
        this.tableid = tableid;
        this.columnseq = columnseq;
        this.columnname = columnname;
        this.columntype = columntype;
        this.columncomment = columncomment;
        this.isindex = isindex;
        this.isquery = isquery;
        this.configration = configration;
        this.linktableid = linktableid;
        this.isorder = isorder;
        this.style = style;
        this.isnull = isnull;
        this.ismuti = ismuti;
        this.helpid = helpid;
        this.defaultvalue = defaultvalue;
        this.edittype = edittype;
        this.setDefaultvalue(defaultvalue,edittype);
    }

    /** default constructor */
    public LocalDataTableCol() {
    }

    public String getTableid() {
        return this.tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public String getColumnseq() {
        return this.columnseq;
    }

    public void setColumnseq(String columnseq) {
        this.columnseq = columnseq;
    }

    public String getColumnname() {
        return this.columnname;
    }

    public void setColumnname(String columnname) {
        this.columnname = columnname;
    }

    public String getColumntype() {
        return this.columntype;
    }

    public void setColumntype(String columntype) {
        this.columntype = columntype;
    }

    public String getColumncomment() {
        return this.columncomment;
    }

    public void setColumncomment(String columncomment) {
        this.columncomment = columncomment;
    }

    public String getIsindex() {
        return this.isindex;
    }

    public void setIsindex(String isindex) {
        this.isindex = isindex;
    }

    public String getIsquery() {
        return this.isquery;
    }

    public void setIsquery(String isquery) {
        this.isquery = isquery;
    }

    public String getConfigration() {
        return this.configration;
    }

    public void setConfigration(String configration) {
        this.configration = configration;
    }

    public String getLinktableid() {
        return this.linktableid;
    }

    public void setLinktableid(String linktableid) {
        this.linktableid = linktableid;
    }

    public String getIsorder() {
        return this.isorder;
    }

    public void setIsorder(String isorder) {
        this.isorder = isorder;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getIsnull() {
        return this.isnull;
    }

    public void setIsnull(String isnull) {
        this.isnull = isnull;
    }

    public String getIsmuti() {
        return this.ismuti;
    }

    public void setIsmuti(String ismuti) {
        this.ismuti = ismuti;
    }

    public String getHelpid() {
        return this.helpid;
    }

    public void setHelpid(String helpid) {
        this.helpid = helpid;
    }

    public String getDefaultvalue() {
        return this.defaultvalue;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }
    
    public void setDefaultvalue(String defaultvalue,String edittype){
    	
    	if(edittype!=null&&edittype.trim().equals("1")){
    		this.defaultvalue = "0";
    	}else{
    		this.defaultvalue = defaultvalue;
    	}
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("tableid", getTableid())
            .append("columnseq", getColumnseq())
            .append("columnname", getColumnname())
            .append("columntype", getColumntype())
            .append("columncomment", getColumncomment())
            .append("isindex", getIsindex())
            .append("isquery", getIsquery())
            .append("configration", getConfigration())
            .append("linktableid", getLinktableid())
            .append("isorder", getIsorder())
            .append("style", getStyle())
            .append("isnull", getIsnull())
            .append("ismuti", getIsmuti())
            .append("helpid", getHelpid())
            .append("defaultvalue", getDefaultvalue())
            .toString();
    }

	public String getEdittype() {
		return edittype;
	}

	public void setEdittype(String edittype) {
		
		this.edittype = edittype;
		
		this.setDefaultvalue(this.defaultvalue,edittype);
	}

}
