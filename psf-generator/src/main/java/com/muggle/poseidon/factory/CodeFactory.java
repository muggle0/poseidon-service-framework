package com.muggle.poseidon.factory;

import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.muggle.poseidon.constant.GlobalConstant;
import com.muggle.poseidon.entity.ProjectMessage;
import com.muggle.poseidon.genera.CodeGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.muggle.poseidon.constant.CodePath.APPLICATION;
import static com.muggle.poseidon.constant.CodePath.BANNER;
import static com.muggle.poseidon.constant.CodePath.LOGBACK;
import static com.muggle.poseidon.constant.CodePath.MAIN_CLASS;
import static com.muggle.poseidon.constant.CodePath.POM;
import static com.muggle.poseidon.constant.GlobalConstant.FM_PERFIX;
import static com.muggle.poseidon.constant.GlobalConstant.MAVEN_RESOURECES_FILE;
import static com.muggle.poseidon.constant.GlobalConstant.MAVEN_SRC_FILE;
import static com.muggle.poseidon.constant.GlobalConstant.OTHER;
import static com.muggle.poseidon.constant.GlobalConstant.SEPARATION;
import static com.muggle.poseidon.constant.GlobalConstant.USER_DIR;

/**
 * Description
 * Date 2021/7/30
 * Created by muggle
 */
public class CodeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFactory.class);

    private static CodeGenerator codeGenerator;


    public static void init(CodeGenerator codeGenerator) {
        codeGenerator.execute();
        CodeFactory.codeGenerator = codeGenerator;
    }

    public static void createCode() {
        if (codeGenerator == null) {
            throw new IllegalArgumentException("代码生成器未初始化");
        }
        codeGenerator.createCode();
        LOGGER.info("生成 业务类》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
    }

    public static void createMainClass( ) throws Exception {
        ProjectMessage projectMessage = codeGenerator.getMessage();
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
        AbstractTemplateEngine freemarkerTemplateEngine = codeGenerator.getTemplateEngine();
        projectMessage.getOtherField().put("className", className.concat("Application"));
        File mainClass = new File(path.toString());
        if (!mainClass.exists()) {
            if (!mainClass.getParentFile().exists()) {
                mainClass.getParentFile().mkdirs();
            }
        }
        freemarkerTemplateEngine.writer(converMessage(projectMessage),MAIN_CLASS,path.toString());
        LOGGER.info("生成 启动类》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
    }

    private static Map<String, Object> converMessage(ProjectMessage projectMessage) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = projectMessage.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(projectMessage);
            map.put(fieldName, value);
        }
        return map;
    }

    public static void createConfig() throws Exception {
        ProjectMessage projectMessage = codeGenerator.getMessage();
        URL resource = CodeGenerator.class.getClassLoader().getResource(OTHER);
        List<String> allFile = getAllFile(resource);
        for (String templatePath : allFile) {
            StringBuilder classPath = new StringBuilder();
            classPath.append(System.getProperty(USER_DIR)).append(SEPARATION);
            if (!StringUtils.isEmpty(projectMessage.getModule())) {
                classPath.append(projectMessage.getModule());
            }
            String javaFileName = templatePath.replace(FM_PERFIX, "");
            classPath.append(MAVEN_SRC_FILE.concat(SEPARATION)).append(projectMessage.getProjectPackage().replace(".", SEPARATION))
                    .append(SEPARATION).append(javaFileName);
            File classFile = new File(classPath.toString());
            if (!classFile.getParentFile().exists()) {
                classFile.getParentFile().mkdirs();
            }
            AbstractTemplateEngine freemarkerTemplateEngine = codeGenerator.getTemplateEngine();
            String packageName = projectMessage.getProjectPackage().concat(".").concat(javaFileName.substring(0, javaFileName
                    .lastIndexOf('/')).replace("/", "."));
            projectMessage.getOtherField().put("packageName", packageName);

            freemarkerTemplateEngine.writer(converMessage(projectMessage),"/psf-others/".concat(javaFileName)
                    .concat(FM_PERFIX),classPath.toString());
        }
        LOGGER.info("生成config类》》》》》》》》》》》》》》》》》》》》》》》》》》》》");

    }

    public static void createBanner() throws IOException {
        ProjectMessage projectMessage = codeGenerator.getMessage();
        File banner = new File(System.getProperty(USER_DIR) + SEPARATION + projectMessage.getModule() + MAVEN_RESOURECES_FILE + BANNER);
        if (!banner.exists()) {
            if (!banner.getParentFile().exists()) {
                banner.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(banner);){
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
    }

    public static void createReadme() throws IOException {
        ProjectMessage projectMessage = codeGenerator.getMessage();
        File readme = new File(System.getProperty(USER_DIR) + SEPARATION + projectMessage.getModule() + "/readme.md");
        if (!readme.exists()) {
            if (!readme.getParentFile().exists()) {
                readme.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(readme)) {
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
            LOGGER.info("生成readme 》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
        }
}

    public static void createPom( ) throws Exception {
        ProjectMessage projectMessage = codeGenerator.getMessage();
        AbstractTemplateEngine freemarkerTemplateEngine = codeGenerator.getTemplateEngine();
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(SEPARATION);
        }
        path.append("pom.xml");
        File pom = new File(path.toString());
        if (!pom.exists()) {
            if (!pom.getParentFile().exists()) {
                pom.getParentFile().mkdirs();
            }
        }
        freemarkerTemplateEngine.writer(converMessage(projectMessage),POM,path.toString());
        LOGGER.info("生成pom文件》》》》》》》》》》》》");
    }

    public static void createProperties(String properties) throws Exception {
        ProjectMessage projectMessage = codeGenerator.getMessage();
        AbstractTemplateEngine freemarkerTemplateEngine = codeGenerator.getTemplateEngine();
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(SEPARATION);
        }
        path.append(MAVEN_RESOURECES_FILE).append(SEPARATION).append(properties).append(".properties");
        File propertiesFile = new File(path.toString());
        if (!propertiesFile.exists()) {
            if (!propertiesFile.getParentFile().exists()) {
                propertiesFile.getParentFile().mkdirs();
            }
        }
        freemarkerTemplateEngine.writer(converMessage(projectMessage),APPLICATION.replace("#",properties),path.toString());
        LOGGER.info("生成 properties 》》》》》》》》》》》》");
    }

    public static void createLogback( ) throws Exception {
        ProjectMessage projectMessage = codeGenerator.getMessage();
        AbstractTemplateEngine freemarkerTemplateEngine = codeGenerator.getTemplateEngine();
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty(USER_DIR)).append(SEPARATION);
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append(SEPARATION);
        }
        path.append(MAVEN_RESOURECES_FILE).append(SEPARATION).append("logback-spring.xml");
        File logback = new File(path.toString());
        if (!logback.exists()) {
            if (!logback.getParentFile().exists()) {
                logback.getParentFile().mkdirs();
            }
        }
        freemarkerTemplateEngine.writer(converMessage(projectMessage),LOGBACK,path.toString());
        LOGGER.info("生成 logback.xml 》》》》》》》》》》》》");
    }

    @SuppressWarnings("all")
    private static List<String> getAllFile(URL url) throws IOException {
        List<String> list = new ArrayList<>();
        if (url.getProtocol().equals("file")){
            String filePath = url.getPath();
            List<String> allFile = getAllFile(filePath);
            ArrayList<String> result = new ArrayList<>();
            allFile.forEach(str->result.add(str.replace(filePath,"")));
            return result;
        }
        String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
        URL jarURL = new URL(jarPath);
        JarURLConnection jarURLConnection = (JarURLConnection) jarURL.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntrys = jarFile.entries();
        String suffix = url.toString().substring(url.toString().lastIndexOf("!/")+2);
        while (jarEntrys.hasMoreElements()) {
            JarEntry entry = jarEntrys.nextElement();
            String name = entry.getName();
            if (name.startsWith(suffix)&&!entry.isDirectory()) {
                list.add(entry.getName().replace(suffix,""));
            }
        }
        return list;
    }

    private static List<String> getAllFile(String directoryPath) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                list.addAll(getAllFile(file.getAbsolutePath()));
            } else if (file.getPath().endsWith(FM_PERFIX)) {
                list.add(SEPARATION.concat(file.getPath().replace("\\", SEPARATION)));
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
    public static CodeGenerator getCodeGenerator(){
        return codeGenerator;
    }
}
