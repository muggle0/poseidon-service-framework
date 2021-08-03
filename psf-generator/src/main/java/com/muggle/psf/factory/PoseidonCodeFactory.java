package com.muggle.psf.factory;

import com.muggle.psf.constant.GlobalConstant;
import com.muggle.psf.entity.ProjectMessage;
import com.muggle.psf.genera.CodeGenerator;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Description
 * Date 2021/7/29
 * Created by muggle
 */
public class PoseidonCodeFactory extends CodeFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoseidonCodeFactory.class);

    public static void createProject(ProjectMessage projectMessage) throws IOException, TemplateException {
        if (!(Boolean.getBoolean("skipJdbc") || projectMessage.isSkipJdbc())) {
            LOGGER.info("skipJdbc== false》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
            createCode(projectMessage);
        }
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
        if (!(Boolean.getBoolean("skipBase") || projectMessage.isSkipBase())) {
            LOGGER.info("skipBase== false》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
            String filePath = CodeGenerator.class.getClassLoader().getResource(GlobalConstant.CLOGBAL_DIR).getFile();
            createBanner(projectMessage);
            createMainClass(cfg, filePath, projectMessage);
            createPom(cfg, filePath, projectMessage);
            createProperties("bootstrap", cfg, filePath, projectMessage);
            createProperties("application", cfg, filePath, projectMessage);
            createReadme(cfg, filePath, projectMessage);
            createLogback(cfg, filePath, projectMessage);
        }
        if (!(Boolean.getBoolean("skipConfig") || projectMessage.isSkipConfig())) {
            LOGGER.info("skipConfig== false》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
            String filePath = CodeGenerator.class.getClassLoader().getResource(GlobalConstant.OTHER).getFile();
            createConfig(cfg, filePath, projectMessage);
        }


    }


}
