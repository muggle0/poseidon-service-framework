package com.muggle.psf.factory;

import com.muggle.psf.constant.GlobalConstant;
import com.muggle.psf.entity.ProjectMessage;
import com.muggle.psf.genera.CodeGenerator;
import freemarker.template.Configuration;
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
import java.util.Map;

import static com.muggle.psf.constant.CodePath.BANNER;
import static com.muggle.psf.constant.CodePath.MAIN_CLASS;
import static com.muggle.psf.constant.CodePath.POM;
import static com.muggle.psf.constant.CodePath.README;
import static com.muggle.psf.constant.GlobalConstant.SEPARATION;
import static com.muggle.psf.constant.GlobalConstant.USER_DIR;

/**
 * Description
 * Date 2021/7/30
 * Created by muggle
 */
public class CodeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFactory.class);

    private static  CodeGenerator codeGenerator;


    public static void init(CodeGenerator codeGenerator){
        CodeFactory.codeGenerator=codeGenerator;
    }

    public static void createCode(){
        if (codeGenerator==null){
            throw new IllegalArgumentException("代码生成器未初始化");
        }
        if (Boolean.getBoolean("skipJdbc")){
            return;
        }
        codeGenerator.createCode();
    }

    public static void createMainClass(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath.concat(GlobalConstant.SEPARATION).concat(MAIN_CLASS) );
        if (!file.exists()) {
            throw new IllegalStateException("未找到文件 mainClass.java.ftl");
        }
        Template template = cfg.getTemplate(MAIN_CLASS);
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(GlobalConstant.SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(GlobalConstant.SEPARATION);
        }
        path.append(GlobalConstant.MAVEN_SRC_FILE).append(GlobalConstant.SEPARATION);
        path.append(projectMessage.getProjectPackage().replace(".","/")).append("/");
        String module = projectMessage.getModule();
        String className = underline2Camel(module, true);

        path.append(className).append("Application").append(".java");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
        HashMap<String, String> model = new HashMap<>();
        projectMessage.getOtherField().put("className",className.concat("Application"));
        template.process(projectMessage,out);
    }

    public static void createBanner(ProjectMessage projectMessage) throws IOException {
        File banner = new File(System.getProperty(USER_DIR) + SEPARATION + projectMessage.getModule() + BANNER);
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
            LOGGER.info("生成banner》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
        }
    }

    public static void createReadme(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath.concat(README));
        if (!file.exists()) {
            File readme = new File(System.getProperty(USER_DIR) + SEPARATION + projectMessage.getModule() + "/readme.md");
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
            path.append(System.getProperty(USER_DIR)).append(SEPARATION);
            if (!StringUtils.isEmpty(projectMessage.getModule())) {
                path.append(projectMessage.getModule()).append(SEPARATION);
            }
            path.append("readme.md");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
            template.process(projectMessage, out);
            LOGGER.info("生成 readme.md》》》》》》》》》》》》》》》》》");
        }
    }

    public static void createPom(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath.concat(POM));
        if (!file.exists()) {
            throw new IllegalStateException("未找到文件 pom.xml.ftl");
        }
        Template template = cfg.getTemplate("pom.xml.ftl");
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(SEPARATION);
        }
        path.append("pom.xml");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
        template.process(projectMessage,out);
        LOGGER.info("生成pom文件》》》》》》》》》》》》");
    }

    @SuppressWarnings("all")
    private static String underline2Camel(String line, boolean... firstIsUpperCase) {
        String str = "";

        if (StringUtils.isBlank(line)) {
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
