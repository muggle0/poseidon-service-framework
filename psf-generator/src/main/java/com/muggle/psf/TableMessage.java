package com.muggle.psf;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @program: poseidon-generator
 * @description: 表信息
 * @author: muggle
 * @create: 2019-12-05
 **/

public class TableMessage {

    private TableMessage() {

    }

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
    private String module = "";

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

    /**
     * 初始化类型，标准，简化，全部
     */
    private InitType initType;

    public static TableMessageBuilder builder() {
        return new TableMessageBuilder();
    }

    public InitType getInitType() {
        return initType;
    }

    public void setInitType(InitType initType) {
        this.initType = initType;
    }

    public static enum InitType {
        NORMAL, SIMPLE, ALL
    }

    public static class TableMessageBuilder {
        private TableMessage tableMessage;

        public TableMessageBuilder() {
            this.tableMessage = new TableMessage();
        }

        public TableMessage build() {
            if (StringUtils.isEmpty(tableMessage.getAuthor())) {
                throw new IllegalArgumentException("请设置作者 Author");
            }
            if (StringUtils.isEmpty(tableMessage.getDriver())) {
                throw new IllegalArgumentException("请设置数据库驱动 driver");
            }
            if (StringUtils.isEmpty(tableMessage.getJdbcUrl())) {
                throw new IllegalArgumentException("请设置url链接 jdbcUrl");
            }
            if (tableMessage.getModule() == null) {
                tableMessage.setModule("");
            }
            if (tableMessage.getTableName() == null || tableMessage.getTableName().size() < 1) {
                throw new IllegalArgumentException("请设置表名 jdbcUrl");
            }
            if (tableMessage.getInitType()==null){
                tableMessage.setInitType(InitType.SIMPLE);
            }
            return tableMessage;
        }
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isSwagger() {
        return swagger;
    }

    public void setSwagger(boolean swagger) {
        this.swagger = swagger;
    }

    public List<String> getTableName() {
        return tableName;
    }

    public void setTableName(List<String> tableName) {
        this.tableName = tableName;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Map<String, String> getOtherField() {
        return otherField;
    }

    public void setOtherField(Map<String, String> otherField) {
        this.otherField = otherField;
    }

    public String getParentPack() {
        return parentPack;
    }

    public void setParentPack(String parentPack) {
        this.parentPack = parentPack;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getProjectPackage() {
        return projectPackage;
    }

    public void setProjectPackage(String projectPackage) {
        this.projectPackage = projectPackage;
    }
}
