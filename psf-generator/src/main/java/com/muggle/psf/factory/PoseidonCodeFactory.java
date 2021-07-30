package com.muggle.psf.factory;

import com.muggle.psf.entity.ProjectMessage;
import com.muggle.psf.genera.SimpleCodeGenerator;
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
import java.util.List;

/**
 * Description
 * Date 2021/7/29
 * Created by muggle
 */
public class PoseidonCodeFactory extends CodeFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoseidonCodeFactory.class);

    public static void generate(ProjectMessage projectMessage) {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>> 生成项目初始化文件 <<<<<<<<<<<<<<<<<");
        createCode();
        switch (projectMessage.getInitType()) {
            case ALL:
                doAll(projectMessage);
                break;
            case NORMAL:
                doNormal(projectMessage);
                break;
            case SIMPLE:
                break;
            default:
                break;
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>> 项目初始化文件生成完成 <<<<<<<<<<<<<<<<<");
    }

    private static void doNormal(ProjectMessage tableMessage) {
        Configuration cfg =new Configuration(Configuration.VERSION_2_3_28);
        try {
            String filePath = SimpleCodeGenerator.class.getClassLoader().getResource("template/").getFile();
            cfg.setDirectoryForTemplateLoading(new File(filePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            createPom(cfg, filePath, tableMessage);
            createReadme(cfg, filePath, tableMessage);
            createBanner(cfg, filePath, tableMessage);
            createMainClass(cfg,filePath,tableMessage);
        } catch (IOException e) {
            LOGGER.error("读取模板异常", e);
        } catch (TemplateException e) {
            LOGGER.error("读取模板异常", e);
        }
    }

    private static void doAll(ProjectMessage projectMessage) {
        doNormal(projectMessage);
        String filePath = SimpleCodeGenerator.class.getClassLoader().getResource("others/").getFile();
        List<String> allFile = getAllFile(filePath, false);
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
            cfg.setDirectoryForTemplateLoading(new File(filePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            for (String templatePath : allFile) {
                StringBuilder classPath = new StringBuilder();
                classPath.append(System.getProperty("user.dir")).append("/");
                if (!StringUtils.isEmpty(projectMessage.getModule())) {
                    classPath.append(projectMessage.getModule()).append("/");
                }
                String tempSubPath = templatePath.substring(templatePath.indexOf("/others/") + "/others/".length()).replace(".ftl", "");
                classPath.append("/src/main/java/").append(projectMessage.getProjectPackage().replace(".","/"))
                    .append("/").append(tempSubPath);
                Template template = cfg.getTemplate(tempSubPath+".ftl");
                File classFile = new File(classPath.toString());
                if (!classFile.getParentFile().exists()){
                    classFile.getParentFile().mkdirs();
                }
                Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(classPath.toString()))));
                template.process(projectMessage.getOtherField(),out);
            }
        } catch (IOException e) {
            LOGGER.error("读取模板异常", e);
        } catch (TemplateException e) {
            LOGGER.error("读取模板异常", e);
        }
    }

    private static List<String> getAllFile(String directoryPath, boolean isAddDirectory) {
        List<String> list = new ArrayList<String>();
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
            } else {
                list.add(file.getPath().replace("\\","/"));
            }
        }
        return list;
    }
}
