package com.muggle.psf.genera.ui;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description
 * Date 2021/8/6
 * Created by muggle
 */
public class ProjectMessageVO implements Serializable {

    private static final long serialVersionUID = -9181651224434497416L;
    /**
     * 数据库密码
     */
    private String password;


    /**
     * 资料库用户名
     */
    private String username;

    /**
     * 数据库连接
     */
    private String jdbcUrl;

    /**
     * 包后缀
     */
    private String suffix;

    /**
     * 项目模块名
     */
    private String module;

    private String projectPackage;

    /**
     * 是否生成swagger 注解
     */
    private boolean swagger;

    private String author;

    private List<String> tableName;

    private String driver;

    private Map<String, String> otherField;

    private String parentPack;

    private List<String> excloudCommonds;

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(final String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    public String getModule() {
        return module;
    }

    public void setModule(final String module) {
        this.module = module;
    }

    public String getProjectPackage() {
        return projectPackage;
    }

    public void setProjectPackage(final String projectPackage) {
        this.projectPackage = projectPackage;
    }

    public boolean isSwagger() {
        return swagger;
    }

    public void setSwagger(final boolean swagger) {
        this.swagger = swagger;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public List<String> getTableName() {
        return tableName;
    }

    public void setTableName(final List<String> tableName) {
        this.tableName = tableName;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(final String driver) {
        this.driver = driver;
    }

    public Map<String, String> getOtherField() {
        return otherField;
    }

    public void setOtherField(final Map<String, String> otherField) {
        this.otherField = otherField;
    }

    public String getParentPack() {
        return parentPack;
    }

    public void setParentPack(final String parentPack) {
        this.parentPack = parentPack;
    }

    public List<String> getExcloudCommonds() {
        return excloudCommonds;
    }

    public void setExcloudCommonds(final List<String> excloudCommonds) {
        this.excloudCommonds = excloudCommonds;
    }
}
