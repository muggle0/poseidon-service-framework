package com.muggle.poseidon.genera;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.muggle.poseidon.entity.ProjectMessage;

/**
 * @program: poseidon-generator
 * @description:
 * @author: muggle
 * @create: 2019-12-05
 **/

public abstract class CodeGenerator extends AutoGenerator {


    private  final ProjectMessage message;

    public ProjectMessage getMessage() {
        return message;
    }

    public CodeGenerator(ProjectMessage message) {
        this.message = message;
    }


    /**
     * 数据库配置
     *
     * @return
     */
    abstract  DataSourceConfig configDataSource();

    /**
     * 类属性配置
     *
     * @return
     */
    abstract  GlobalConfig configGlobal();

    /**
     * 包属性配置
     *
     * @return
     */
    abstract  PackageConfig configPc();

    /**
     * 文件属性配置
     *
     * @return
     */
    abstract  InjectionConfig fileConfig();

    /**
     * 代码模板配置
     *
     * @return
     */
    abstract  TemplateConfig configTemp();

    /**
     * 父类相关配置
     *
     * @return
     */
    abstract  StrategyConfig  configStrategy();

    /**
     * 代码生成步骤拆分
     */
    @Override
    public void execute() {
        // 全局配置
        DataSourceConfig dataSourceConfig = configDataSource();
        GlobalConfig globalConfig = configGlobal();
        PackageConfig packageConfig = configPc();
        InjectionConfig config = fileConfig();
        this.setPackageInfo(packageConfig);
        this.setDataSource(dataSourceConfig);
        this.setGlobalConfig(globalConfig);
        this.setCfg(config);
        TemplateConfig templateConfig = configTemp();
        this.setTemplate(templateConfig);
        StrategyConfig strategyConfig = configStrategy();
        this.setStrategy(strategyConfig);
        FreemarkerTemplateEngine freemarkerTemplateEngine = new FreemarkerTemplateEngine();
        this.setTemplateEngine(freemarkerTemplateEngine);
        if (null == this.config) {
            this.config = new ConfigBuilder(getPackageInfo(), getDataSource(), getStrategy(), getTemplate(), getGlobalConfig());
            if (null != this.injectionConfig) {
                this.injectionConfig.setConfig(this.config);
            }
        }
        getTemplateEngine().init(this.pretreatmentConfigBuilder(this.config));
    }

    public void createCode(){
        getTemplateEngine().mkdirs().batchOutput().open();
    }

}
