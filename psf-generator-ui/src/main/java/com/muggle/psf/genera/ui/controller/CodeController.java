package com.muggle.psf.genera.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muggle.psf.genera.constant.GlobalConstant;
import com.muggle.psf.genera.entity.CodeCommand;
import com.muggle.psf.genera.entity.ProjectMessage;
import com.muggle.psf.genera.factory.CodeCommandInvoker;
import com.muggle.psf.genera.genera.CodeGenerator;
import com.muggle.psf.genera.genera.SimpleCodeGenerator;
import com.muggle.psf.genera.ui.MyUIcodeCommand;
import com.muggle.psf.genera.ui.ProjectMessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 * Date 2021/8/5
 * Created by muggle
 */
@RestController
@RequestMapping("/ponseidon")
public class CodeController {
    @Autowired(required = false)
    List<CodeCommand> codeCommands;
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeController.class);


    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String create(@RequestBody final ProjectMessageVO projectMessageVO) {
        try {
            final SimpleCodeGenerator simpleCodeGenerator = new SimpleCodeGenerator(this.convertMessage(projectMessageVO));
            final CodeCommandInvoker invoker = new CodeCommandInvoker(simpleCodeGenerator);

            invoker.popCommond("createPom");
            if (!CollectionUtils.isEmpty(codeCommands)) {
                codeCommands.forEach(invoker::addCommond);
            }
            if (!projectMessageVO.getExcloudCommonds().contains("createUIpom")) {
                invoker.addCommond(new MyUIcodeCommand());
            }
            projectMessageVO.getExcloudCommonds().forEach(invoker::popCommond);
            invoker.addCommond(new CodeCommand() {
                @Override
                public String getName() {
                    return "serializ";
                }

                @Override
                public void excute(final CodeGenerator codeGenerator) throws Exception {
                    final StringBuilder path = new StringBuilder();
                    path.append(System.getProperty(GlobalConstant.USER_DIR)).append(GlobalConstant.SEPARATION).append("projectMessageVO.json");
                    final File file = new File(path.toString());
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try (final FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        final ObjectMapper mapper = new ObjectMapper();
                        fileOutputStream.write(mapper.writeValueAsString(projectMessageVO).getBytes());
                        LOGGER.info("==========> [持久化提交数据]");
                    } catch (final FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            invoker.execute();
            return "{\"result\":\"success\"}";
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "{\"result\":\" 生成代码发生错误，错误原因：" + e.getMessage() + "\"}";
        }
    }


    @GetMapping(value = "/message", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getMessage() {
        final StringBuilder path = new StringBuilder();
        path.append(System.getProperty(GlobalConstant.USER_DIR)).append(GlobalConstant.SEPARATION).append("projectMessageVO.json");
        final File file = new File(path.toString());
        if (!file.exists()) {
            return "{}";
        }
        try (final FileReader fileReader = new FileReader(file)) {
            final BufferedReader buffer = new BufferedReader(fileReader);
            String line = null;
            final StringBuilder result = new StringBuilder();
            while ((line = buffer.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private ProjectMessage convertMessage(final ProjectMessageVO projectMessageVO) {
        final ProjectMessage build = ProjectMessage.builder().author(projectMessageVO.getAuthor())
            .driver(projectMessageVO.getDriver()).otherField(projectMessageVO.getOtherField())
            .jdbcUrl(projectMessageVO.getJdbcUrl()).parentPack(projectMessageVO.getParentPack())
            .password(projectMessageVO.getPassword()).projectPackage(projectMessageVO.getProjectPackage())
            .suffix(projectMessageVO.getSuffix()).swagger(projectMessageVO.isSwagger())
            .tableName(projectMessageVO.getTableName()).username(projectMessageVO.getUsername())
            .module(projectMessageVO.getModule()).build();
        if (CollectionUtils.isEmpty(build.getOtherField())) {
            final Map<String, String> map = new HashMap<>();
            build.setOtherField(map);
        }
        if (build.getOtherField().get("parentVersion") == null) {
            build.getOtherField().put("parentVersion", "1.0-SNAPSHOT");
        }
        return build;
    }
}
