package com.muggle.psf.factory;

import com.muggle.psf.constant.GlobalConstant;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.muggle.psf.constant.GlobalConstant.FM_PERFIX;
import static com.muggle.psf.constant.GlobalConstant.MAVEN_SRC_FILE;
import static com.muggle.psf.constant.GlobalConstant.OTHER;
import static com.muggle.psf.constant.GlobalConstant.SEPARATION;
import static com.muggle.psf.constant.GlobalConstant.USER_DIR;

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
            String filePath = SimpleCodeGenerator.class.getClassLoader().getResource(GlobalConstant.CLOGBAL_DIR).getFile();
            cfg.setDirectoryForTemplateLoading(new File(filePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            createPom(cfg, filePath, tableMessage);
            createReadme(cfg, filePath, tableMessage);
            createBanner(tableMessage);
            createMainClass(cfg,filePath,tableMessage);
        } catch (IOException | TemplateException e) {
            LOGGER.error("读取模板异常", e);
        }
    }

    private static void doAll(ProjectMessage projectMessage) {
        doNormal(projectMessage);
        String filePath = SimpleCodeGenerator.class.getClassLoader().getResource(GlobalConstant.OTHER).getFile();
        List<String> allFile = getAllFile(filePath, false);
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
            cfg.setDirectoryForTemplateLoading(new File(filePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
            for (String templatePath : allFile) {
                StringBuilder classPath = new StringBuilder();
                classPath.append(System.getProperty(USER_DIR)).append(SEPARATION);
                if (!StringUtils.isEmpty(projectMessage.getModule())) {
                    classPath.append(projectMessage.getModule());
                }
                String tempSubPath = templatePath.substring(templatePath.indexOf("/others/") + "/others/".length()).replace(FM_PERFIX, "");
                classPath.append(MAVEN_SRC_FILE.concat(SEPARATION)).append(projectMessage.getProjectPackage().replace(".",SEPARATION))
                    .append(SEPARATION).append(tempSubPath);
                Template template = cfg.getTemplate(tempSubPath+".ftl");
                File classFile = new File(classPath.toString());
                if (!classFile.getParentFile().exists()){
                    classFile.getParentFile().mkdirs();
                }
                Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(classPath.toString()))));
                String packageName = projectMessage.getProjectPackage().concat(".").concat(tempSubPath.substring(0, tempSubPath
                    .lastIndexOf('/')).replace("/", "."));
                projectMessage.getOtherField().put("packageName",packageName);
                template.process(projectMessage,out);
            }
        } catch (IOException | TemplateException e) {
            LOGGER.error("读取模板异常", e);
        }
    }



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
            } else if (file.getPath().endsWith(FM_PERFIX)){
                list.add(file.getPath().replace("\\",SEPARATION));
            }
        }
        return list;
    }

}
