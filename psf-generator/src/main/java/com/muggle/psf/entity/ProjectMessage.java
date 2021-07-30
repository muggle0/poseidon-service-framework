package com.muggle.psf.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @program: poseidon-generator
 * @description: 表信息
 * @author: muggle
 * @create: 2019-12-05
 **/

public class ProjectMessage {

    private ProjectMessage() {

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
    private String module ;

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

    public static ProjectMessageBuilder builder() {
        return new ProjectMessageBuilder();
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

    public static class ProjectMessageBuilder {
        private ProjectMessage projectMessage;

        public ProjectMessageBuilder() {
            this.projectMessage = new ProjectMessage();
        }

        public ProjectMessageBuilder initType(InitType initType){
            projectMessage.setInitType(initType);
            return this;
        }
        public ProjectMessageBuilder parentPack(String parentPack){
            projectMessage.setParentPack(parentPack);
            return this;
        }
        public ProjectMessageBuilder otherField(Map<String,String> map){
            projectMessage.setOtherField(map);
            return this;
        }
        public ProjectMessageBuilder driver(String driver){
            projectMessage.setDriver(driver);
            return this;
        }
        public ProjectMessageBuilder tableName(List<String> tables){
            projectMessage.setTableName(tables);
            return this;
        }
        public ProjectMessageBuilder author(String author){
            projectMessage.setAuthor(author);
            return this;
        }
        public ProjectMessageBuilder swagger(boolean swagger){
            projectMessage.setSwagger(swagger);
            return this;
        }
        public ProjectMessageBuilder projectPackage(String projectPack){
            projectMessage.setProjectPackage(projectPack);
            return this;
        }
        public ProjectMessageBuilder module(String module){
            projectMessage.setModule(module);
            return this;
        }
        public ProjectMessageBuilder password(String password){
            projectMessage.setPassword(password);
            return this;
        }
        public ProjectMessageBuilder username(String username){
            projectMessage.setUsername(username);
            return this;
        }
        public ProjectMessageBuilder jdbcUrl(String jdbcUrl){
            projectMessage.setJdbcUrl(jdbcUrl);
            return this;
        }
        public ProjectMessageBuilder suffix(String suffix){
            projectMessage.setSuffix(suffix);
            return this;
        }

        public ProjectMessage build() {
            if (StringUtils.isEmpty(projectMessage.getAuthor())) {
                throw new IllegalArgumentException("请设置作者 Author");
            }
            if (StringUtils.isEmpty(projectMessage.getDriver())) {
                throw new IllegalArgumentException("请设置数据库驱动 driver");
            }
            if (StringUtils.isEmpty(projectMessage.getJdbcUrl())) {
                throw new IllegalArgumentException("请设置url链接 jdbcUrl");
            }
            if (StringUtils.isEmpty(projectMessage.getProjectPackage())) {
                throw new IllegalArgumentException("请设置项目包路径 projectPackage");
            }
            if (projectMessage.getModule() == null) {
                projectMessage.setModule("");
            }
            if (projectMessage.getTableName() == null || projectMessage.getTableName().size() < 1) {
                throw new IllegalArgumentException("请设置表名 jdbcUrl");
            }
            if (projectMessage.getInitType()==null){
                projectMessage.setInitType(InitType.SIMPLE);
            }
            return projectMessage;
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