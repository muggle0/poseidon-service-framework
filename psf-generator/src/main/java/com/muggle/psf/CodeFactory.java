package com.muggle.psf;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Description
 * Date 2021/7/29
 * Created by muggle
 */
public class CodeFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFactory.class);


    public static void simpleGenerate(TableMessage tableMessage){
        LOGGER.info(">>>>>>>>>>>>>>>>>>>> 生成项目初始化文件 <<<<<<<<<<<<<<<<<");
        CodeGenerator codeGenerator = new SimpleCodeGenerator(tableMessage);
        codeGenerator.createCode();
        switch (tableMessage.getInitType()){
            case ALL:
                doAll(tableMessage);
                break;
            case NORMAL:
                doNormal(tableMessage);
                break;
            case SIMPLE:
//                doSimple(tableMessage);
                break;
            default:
                break;
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>> 项目初始化文件生成完成 <<<<<<<<<<<<<<<<<");
    }

    private static void doSimple(TableMessage tableMessage) {
    }

    private static void doNormal(TableMessage tableMessage) {
        Configuration cfg =new Configuration(Configuration.VERSION_2_3_28);
        Template template = null;
        Writer out = null;
        try {
            String filePath = SimpleCodeGenerator.class.getClassLoader().getResource("template/").getFile();
            cfg.setDirectoryForTemplateLoading(new File(filePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            createPom(cfg,filePath,tableMessage);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("读取模板异常",e);
        } catch (TemplateException e) {
            LOGGER.error("读取模板异常",e);
            e.printStackTrace();
        }
    }

    private static void createPom(Configuration cfg, String filePath, TableMessage tableMessage) throws IOException, TemplateException {
        File file = new File(filePath + "/pom.xml.ftl");
        if (!file.exists()){
            throw new IllegalStateException("未找到文件 pom.xml.ftl");
        }
        Template template = cfg.getTemplate("pom.xml.ftl");
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty("user.dir")).append("/");
        if (!StringUtils.isEmpty(tableMessage.getModule())){
            path.append(tableMessage.getModule()).append("/");
        }
        path.append("pom.xml");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
        template.process(tableMessage,out);
    }

    private static void doAll(TableMessage tableMessage) {

    }

    public void projectInit(){

    }
}
