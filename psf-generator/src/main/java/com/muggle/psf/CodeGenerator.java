package com.muggle.psf;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @program: poseidon-generator
 * @description:
 * @author: muggle
 * @create: 2019-12-05
 **/

public abstract class CodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenerator.class);

    private TableMessage message;

    public CodeGenerator(TableMessage message) {
        this.message = message;
    }

    abstract void connect();

    /**
     * 数据库配置
     *
     * @param dsc
     * @return
     */
    abstract DataSourceConfig configDataSource(TableMessage dsc);

    /**
     * 类属性配置
     *
     * @param message 代码生成实体信息
     * @return
     */
    abstract GlobalConfig configGlobal(TableMessage message);

    /**
     * 包属性配置
     *
     * @param message
     * @return
     */
    abstract PackageConfig configPc(TableMessage message);

    /**
     * 文件属性配置
     *
     * @param message
     * @return
     */
    abstract InjectionConfig fileConfig(TableMessage message);

    /**
     * 代码模板配置
     *
     * @param message
     * @return
     */
    abstract TemplateConfig configTemp(TableMessage message);

    /**
     * 父类相关配置
     *
     * @param message
     * @return
     */
    abstract StrategyConfig configStrategy(TableMessage message);

    /**
     * 实际执行生成代码的方法
     */
    public void createCode() {
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        DataSourceConfig dataSourceConfig = configDataSource(this.message);
        GlobalConfig globalConfig = configGlobal(this.message);
        PackageConfig packageConfig = configPc(this.message);
        InjectionConfig config = fileConfig(this.message);
        mpg.setPackageInfo(packageConfig);
        mpg.setDataSource(dataSourceConfig);
        mpg.setGlobalConfig(globalConfig);
        mpg.setCfg(config);
        TemplateConfig templateConfig = configTemp(this.message);
        mpg.setTemplate(templateConfig);
        StrategyConfig strategyConfig = configStrategy(message);
        mpg.setStrategy(strategyConfig);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();

    }

    public void createProjectConfig(TableMessage tableMessage) {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>> 生成项目配置文件 <<<<<<<<<<<<<<<<<");
        Configuration configuration = new Configuration();
        Template template = null;
        Writer out = null;
        try {
            template = configuration.getTemplate("webConfig.java.ftl");
            File docFile = new File(System.getProperty("user.dir")+"/"+message.getModule() + "/src/main/java/test");
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            // 输出文件
            template.process(tableMessage, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

}
