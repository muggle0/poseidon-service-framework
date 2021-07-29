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
import java.util.HashMap;

/**
 * Description
 * Date 2021/7/29
 * Created by muggle
 */
public class CodeFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFactory.class);


    public static void simpleGenerate(ProjectMessage tableMessage){
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

    private static void doSimple(ProjectMessage tableMessage) {
    }

    private static void doNormal(ProjectMessage tableMessage) {
        Configuration cfg =new Configuration(Configuration.VERSION_2_3_28);
        Template template = null;
        Writer out = null;
        try {
            String filePath = SimpleCodeGenerator.class.getClassLoader().getResource("template/").getFile();
            cfg.setDirectoryForTemplateLoading(new File(filePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            createPom(cfg, filePath, tableMessage);
            createReadme(cfg, filePath, tableMessage);
            createBanner(cfg, filePath, tableMessage);
            createMainClass(cfg,filePath,tableMessage);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("读取模板异常", e);
        } catch (TemplateException e) {
            LOGGER.error("读取模板异常", e);
            e.printStackTrace();
        }
    }

    private static void createMainClass(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath + "/mainClass.java.ftl");
        if (!file.exists()) {
            throw new IllegalStateException("未找到文件 mainClass.java.ftl");
        }
        Template template = cfg.getTemplate("mainClass.java.ftl");
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty("user.dir")).append("/");
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append("/");
        }
        path.append("/src/main/java/");
        path.append(projectMessage.getProjectPackage().replace(".","/")).append("/");
        String module = projectMessage.getModule();
        String className = underline2Camel(module, true);

        path.append(className).append("Application").append(".java");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
        HashMap<String, String> model = new HashMap<>();
        model.put("className",className.concat("Application"));
        template.process(model,out);
    }

    private static void createBanner(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException {
        File banner = new File(System.getProperty("user.dir") + "/" + projectMessage.getModule() + "/banner.txt");
        if (!banner.exists()) {
            FileOutputStream fileOutputStream = new FileOutputStream(banner);
            String logo = "                                                                                 /$$       /$$\n" +
                "                                                                                |__/      | $$\n" +
                "                                          /$$$$$$   /$$$$$$   /$$$$$$$  /$$$$$$  /$$  /$$$$$$$  /$$$$$$  /$$$$$$$\n" +
                "                                         /$$__  $$ /$$__  $$ /$$_____/ /$$__  $$| $$ /$$__  $$ /$$__  $$| $$__  $$\n" +
                "                                        | $$  \\ $$| $$  \\ $$|  $$$$$$ | $$$$$$$$| $$| $$  | $$| $$  \\ $$| $$  \\ $$\n" +
                "                                        | $$  | $$| $$  | $$ \\____  $$| $$_____/| $$| $$  | $$| $$  | $$| $$  | $$\n" +
                "                                        | $$$$$$$/|  $$$$$$/ /$$$$$$$/|  $$$$$$$| $$|  $$$$$$$|  $$$$$$/| $$  | $$\n" +
                "                                        | $$____/  \\______/ |_______/  \\_______/|__/ \\_______/ \\______/ |__/  |__/\n" +
                "                                        | $$\n" +
                "                                        | $$\n" +
                "                                        |__/";
            fileOutputStream.write(logo.getBytes());
        }
    }

    private static void createReadme(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath + "/readme.md.ftl");
        if (!file.exists()) {
            File readme = new File(System.getProperty("user.dir") + "/" + projectMessage.getModule() + "/readme.md");
            if (!readme.exists()) {
                // 写标志
                FileOutputStream fileOutputStream = new FileOutputStream(readme);
                String logo ="# 项目介绍\n" +
                    "\n" +
                    "# 概要设计\n" +
                    "\n" +
                    "# 注意事项\n" +
                    "\n" +
                    "# 代码规范\n" +
                    "\n";
                fileOutputStream.write(logo.getBytes());
            }
        } else {
            Template template = cfg.getTemplate("readme.md.ftl");
            StringBuilder path = new StringBuilder();
            path.append(System.getProperty("user.dir")).append("/");
            if (!StringUtils.isEmpty(projectMessage.getModule())) {
                path.append(projectMessage.getModule()).append("/");
            }
            path.append("readme.md");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
            template.process(projectMessage, out);
        }
    }

    private static void createPom(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath + "/pom.xml.ftl");
        if (!file.exists()) {
            throw new IllegalStateException("未找到文件 pom.xml.ftl");
        }
        Template template = cfg.getTemplate("pom.xml.ftl");
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty("user.dir")).append("/");
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append("/");
        }
        path.append("pom.xml");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
        template.process(projectMessage,out);
    }

    private static void doAll(ProjectMessage projectMessage) {

    }

    public void projectInit(){

    }

    private static String underline2Camel(String line, boolean ... firstIsUpperCase) {
        String str = "";

        if(StringUtils.isBlank(line)){
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            String [] strArr;
            if(!line.contains("-") && firstIsUpperCase.length == 0){
                sb.append(line.substring(0, 1).toLowerCase()).append(line.substring(1));
                str = sb.toString();
            } else if (!line.contains("-") && firstIsUpperCase.length != 0){
                if (!firstIsUpperCase[0]) {
                    sb.append(line.substring(0, 1).toLowerCase()).append(line.substring(1));
                    str = sb.toString();
                } else {
                    sb.append(line.substring(0, 1).toUpperCase()).append(line.substring(1));
                    str = sb.toString();
                }
            } else if (line.contains("-") && firstIsUpperCase.length == 0) {
                strArr = line.split("-");
                for (String s : strArr) {
                    sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
                }
                str = sb.toString();
                str = str.substring(0, 1).toLowerCase() + str.substring(1);
            } else if (line.contains("-") && firstIsUpperCase.length != 0) {
                strArr = line.split("-");
                for (String s : strArr) {
                    sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
                }
                if (!firstIsUpperCase[0]) {
                    str = sb.toString();
                    str = str.substring(0, 1).toLowerCase() + str.substring(1);
                } else {
                    str = sb.toString();
                }
            }
        }
        return str;
    }
}
