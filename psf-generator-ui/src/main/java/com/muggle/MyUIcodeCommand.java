package com.muggle;

import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.muggle.poseidon.entity.CodeCommand;
import com.muggle.poseidon.entity.ProjectMessage;
import com.muggle.poseidon.genera.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Description
 * Date 2021/8/6
 * Created by muggle
 */
public class MyUIcodeCommand implements CodeCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyUIcodeCommand.class);

    @Override
    public String getName() {
        return "createUIpom";
    }

    @Override
    public void excute(CodeGenerator codeGenerator) throws Exception {
        final AbstractTemplateEngine templateEngine =codeGenerator.getTemplateEngine();
        ProjectMessage projectMessage =  codeGenerator.getMessage();
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty("user.dir")).append("/");
        if (!StringUtils.isEmpty(projectMessage.getModule())) {
            path.append(projectMessage.getModule()).append("/");
        }

        path.append("pom.xml");
        File pom = new File(path.toString());
        if (!pom.exists() && !pom.getParentFile().exists()) {
            pom.getParentFile().mkdirs();
        }
        templateEngine.writer(converMessage(projectMessage), "/psf-extend/pom.xml.ftl", path.toString());
        LOGGER.info("==========> [生成 pom 文件]");
    }
    private static Map<String, Object> converMessage(ProjectMessage projectMessage) throws IllegalAccessException {
        Map<String, Object> map = new HashMap();
        Class<?> clazz = projectMessage.getClass();
        Field[] var3 = clazz.getDeclaredFields();
        int var4 = var3.length;
        for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(projectMessage);
            map.put(fieldName, value);
        }
        return map;
    }
}
