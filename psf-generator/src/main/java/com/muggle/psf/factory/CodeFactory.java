package com.muggle.psf.factory;

import com.muggle.psf.constant.GlobalConstant;
import com.muggle.psf.entity.ProjectMessage;
import com.muggle.psf.genera.CodeGenerator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.muggle.psf.constant.CodePath.*;
import static com.muggle.psf.constant.GlobalConstant.*;

/**
 * Description
 * Date 2021/7/30
 * Created by muggle
 */
public class CodeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFactory.class);

    private static CodeGenerator codeGenerator;


    public static void init(CodeGenerator codeGenerator) {
        CodeFactory.codeGenerator = codeGenerator;
    }

    public static void createCode(ProjectMessage projectMessage) {
        if (codeGenerator == null) {
            throw new IllegalArgumentException("代码生成器未初始化");
        }
        codeGenerator.init(projectMessage);
        codeGenerator.createCode();
        LOGGER.info("生成生成 业务类》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
    }

    public static void createMainClass(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath.concat(GlobalConstant.SEPARATION).concat(MAIN_CLASS));
        if (!file.exists()) {
            throw new IllegalStateException("未找到文件 mainClass.java.ftl");
        }
        cfg.setDirectoryForTemplateLoading(new File(filePath));
        Template template = cfg.getTemplate(MAIN_CLASS);
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(GlobalConstant.SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(GlobalConstant.SEPARATION);
        }
        path.append(GlobalConstant.MAVEN_SRC_FILE).append(GlobalConstant.SEPARATION);
        path.append(projectMessage.getProjectPackage().replace(".", "/")).append("/");
        String module = projectMessage.getModule();
        String className = underline2Camel(module, true);

        path.append(className).append("Application").append(".java");
        File mainClass = new File(path.toString());
        if (!mainClass.getParentFile().exists()) {
            mainClass.getParentFile().mkdirs();
        }
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mainClass)));
        HashMap<String, String> model = new HashMap<>();
        projectMessage.getOtherField().put("className", className.concat("Application"));
        template.process(projectMessage, out);
        LOGGER.info("生成mainclass》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
    }

    public static void createConfig(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        List<String> allFile = getAllFile(filePath, false);
        cfg.setDirectoryForTemplateLoading(new File(filePath));
        for (String templatePath : allFile) {
            StringBuilder classPath = new StringBuilder();
            classPath.append(System.getProperty(USER_DIR)).append(SEPARATION);
            if (!StringUtils.isEmpty(projectMessage.getModule())) {
                classPath.append(projectMessage.getModule());
            }
            String tempSubPath = templatePath.substring(templatePath.indexOf("/psf-others/") + "/psf-others/".length()).replace(FM_PERFIX, "");
            classPath.append(MAVEN_SRC_FILE.concat(SEPARATION)).append(projectMessage.getProjectPackage().replace(".", SEPARATION))
                    .append(SEPARATION).append(tempSubPath);
            Template template = cfg.getTemplate(tempSubPath + ".ftl");
            File classFile = new File(classPath.toString());
            if (!classFile.getParentFile().exists()) {
                classFile.getParentFile().mkdirs();
            }
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(classPath.toString()))));
            String packageName = projectMessage.getProjectPackage().concat(".").concat(tempSubPath.substring(0, tempSubPath
                    .lastIndexOf('/')).replace("/", "."));
            projectMessage.getOtherField().put("packageName", packageName);
            template.process(projectMessage, out);
            LOGGER.info("生成config类》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
        }

    }

    public static void createBanner(ProjectMessage projectMessage) throws IOException {
        File banner = new File(System.getProperty(USER_DIR) + SEPARATION + projectMessage.getModule() + MAVEN_RESOURECES_FILE + BANNER);
        if (!banner.exists()) {
            if (!banner.getParentFile().exists()) {
                banner.getParentFile().mkdirs();
            }
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
                String logo = "# 项目介绍\n" +
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
        cfg.setDirectoryForTemplateLoading(new File(filePath));
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
        template.process(projectMessage, out);
        LOGGER.info("生成pom文件》》》》》》》》》》》》");
    }

    public static void createProperties(String properties,Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath.concat(SEPARATION).concat(properties).concat(APPLICATION));
        cfg.setDirectoryForTemplateLoading(new File(filePath));
        if (!file.exists()) {
            File propertiesFile = new File(System.getProperty(USER_DIR).concat(SEPARATION).concat(projectMessage.getModule())
                    .concat(MAVEN_RESOURECES_FILE).concat(SEPARATION).concat(properties).concat(".properties"));
            if (!propertiesFile.exists()){
                propertiesFile.createNewFile();
            }
            return;
        }
        Template template = cfg.getTemplate(properties.concat(".properties.ftl"));
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(SEPARATION);
        }
        path.append(MAVEN_RESOURECES_FILE).append(SEPARATION).append(properties).append(".properties");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
        template.process(projectMessage, out);
        LOGGER.info("生成 properties 》》》》》》》》》》》》");
    }

    public static void createLogback(Configuration cfg, String filePath, ProjectMessage projectMessage) throws IOException, TemplateException {
        File file = new File(filePath.concat(SEPARATION).concat("logback.xml.ftl"));
        cfg.setDirectoryForTemplateLoading(new File(filePath));
        if (!file.exists()) {
            File propertiesFile = new File(System.getProperty(USER_DIR).concat(SEPARATION).concat(projectMessage.getModule())
                    .concat(MAVEN_RESOURECES_FILE).concat(SEPARATION).concat("logback.xml"));
            if (!propertiesFile.exists()){
                propertiesFile.createNewFile();
            }
            return;
        }
        Template template = cfg.getTemplate("logback.xml.ftl");
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(SEPARATION);
        }
        path.append(MAVEN_RESOURECES_FILE).append(SEPARATION).append("logback.xml");
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path.toString()))));
        template.process(projectMessage, out);
        LOGGER.info("生成 logback.xml 》》》》》》》》》》》》");
    }

    @SuppressWarnings("all")
    private static List<String> getAllFile(String directoryPath, boolean isAddDirectory) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (isAddDirectory) {
                    list.add(file.getAbsolutePath());
                }
                list.addAll(getAllFile(file.getAbsolutePath(), isAddDirectory));
            } else if (file.getPath().endsWith(FM_PERFIX)) {
                list.add(file.getPath().replace("\\", SEPARATION));
            }
        }
        return list;
    }

    @SuppressWarnings("all")
    private static String underline2Camel(String line, boolean... firstIsUpperCase) {
        String str = "";

        if (StringUtils.isBlank(line)) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            String[] strArr;
            if (!line.contains("-") && firstIsUpperCase.length == 0) {
                sb.append(line.substring(0, 1).toLowerCase()).append(line.substring(1));
                str = sb.toString();
            } else if (!line.contains("-") && firstIsUpperCase.length != 0) {
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
