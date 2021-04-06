package com.muggle.psf;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: poseidon-generator
 * @description:
 * @author: muggle
 * @create: 2019-12-06
 **/

public class SimpleCodeGenerator extends CodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCodeGenerator.class);

    public SimpleCodeGenerator(TableMessage message) {
        super(message);
        LOGGER.info("==========> [启动代码模板生成器，注意如果数据库无该表则不生成对应的类]");
    }

    @Override
    void connect() {

    }


    @Override
    DataSourceConfig configDataSource(TableMessage message) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(message.getJdbcUrl());
        // dsc.setSchemaName("public");
        dsc.setDriverName(message.getDriver());
        dsc.setUsername(message.getUsername());
        dsc.setPassword(message.getPassword());

        return dsc;
    }

    @Override
    GlobalConfig configGlobal(TableMessage message) {
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath+"/"+message.getModule()+ "/src/main/java");
        gc.setAuthor(message.getAuthor());
        gc.setOpen(false);
        // 实体属性 Swagger2 注解
        gc.setSwagger2(message.isSwagger());
        return gc;
    }



    @Override
    PackageConfig configPc(TableMessage message) {
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(message.getSuffix());
        pc.setParent(message.getProjectPackage());
        return pc;
    }

    @Override
    InjectionConfig fileConfig(final TableMessage message) {
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {

            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return System.getProperty("user.dir")+"/"+message.getModule() + "/src/main/resources/mapper/" + message.getSuffix()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        injectionConfig.setFileOutConfigList(focList);
        return injectionConfig;
    }

    @Override
    TemplateConfig configTemp(TableMessage message) {
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别

        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();
        templateConfig.setXml(null);
        return templateConfig;
    }

    @Override
    StrategyConfig configStrategy(TableMessage message) {

        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        String basePack = message.getParentPack();
        String entityClass=basePack+".BaseBean";
        strategy.setSuperEntityClass(entityClass);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 公共父类
        String controlClass=basePack+".BaseController";
        strategy.setSuperControllerClass(controlClass);
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns("id");
        List<String> tableName = message.getTableName();
        String[] strings = new String[tableName.size()];
        tableName.toArray(strings);
        strategy.setInclude(strings);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(message.getSuffix() + "_");
        return strategy;
    }

    public static void main(String[] args) {
        TableMessage tableMessage = new TableMessage();
        tableMessage.setUsername("root");
        tableMessage.setSwagger(true);
        tableMessage.setTableName(Arrays.asList("oa_url_info"));
        tableMessage.setAuthor("muggle");
        tableMessage.setParentPack("xxxx");
        tableMessage.setProjectPackage("com.muggle");
        tableMessage.setDriver("com.mysql.jdbc.Driver");
        tableMessage.setJdbcUrl("jdbc:mysql:///p_oa?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        tableMessage.setSuffix("user");
        tableMessage.setModule("muggle-generator");
        tableMessage.setPassword("root");
        tableMessage.setSwagger(true);
        SimpleCodeGenerator simpleCodeGeneratorTemplate = new SimpleCodeGenerator(tableMessage);
        simpleCodeGeneratorTemplate.createCode();

        // 生成配置类
        simpleCodeGeneratorTemplate.createProjectConfig(tableMessage);
    }
}
