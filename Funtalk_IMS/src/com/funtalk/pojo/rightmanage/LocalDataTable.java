package com.funtalk.pojo.rightmanage;

import java.io.Serializable;

import net.sf.json.JSONObject;

import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class LocalDataTable implements Serializable {

    /** identifier field */
    private String tableid;

    /** identifier field */
    private String tablename;

    /** identifier field */
    private String tablecomment;

    /** identifier field */
    private String tableparent;

    /** identifier field */
    private String isconfig;

    /** identifier field */
    private String ishelp;

    /** identifier field */
    private String datasource;

    /** full constructor */
    public LocalDataTable(String tableid, String tablename, String tablecomment, String tableparent, String isconfig, String ishelp, String datasource) {
        this.tableid = tableid;
        this.tablename = tablename;
        this.tablecomment = tablecomment;
        this.tableparent = tableparent;
        this.isconfig = isconfig;
        this.ishelp = ishelp;
        this.datasource = datasource;
    }

    /** default constructor */
    public LocalDataTable() {
    }

    public String getTableid() {
        return this.tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public String getTablename() {
        return this.tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTablecomment() {
        return this.tablecomment;
    }

    public void setTablecomment(String tablecomment) {
        this.tablecomment = tablecomment;
    }

    public String getTableparent() {
        return this.tableparent;
    }

    public void setTableparent(String tableparent) {
        this.tableparent = tableparent;
    }

    public String getIsconfig() {
        return this.isconfig;
    }

    public void setIsconfig(String isconfig) {
        this.isconfig = isconfig;
    }

    public String getIshelp() {
        return this.ishelp;
    }

    public void setIshelp(String ishelp) {
        this.ishelp = ishelp;
    }

    public String getDatasource() {
        return this.datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("tableid", getTableid())
            .append("tablename", getTablename())
            .append("tablecomment", getTablecomment())
            .append("tableparent", getTableparent())
            .append("isconfig", getIsconfig())
            .append("ishelp", getIshelp())
            .append("datasource", getDatasource())
            .toString();
    }
    

}
