package com.muggle.psf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                doSimple(tableMessage);
                break;
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>> 项目初始化文件生成完成 <<<<<<<<<<<<<<<<<");
    }

    private static void doSimple(TableMessage tableMessage) {

    }

    private static void doNormal(TableMessage tableMessage) {

    }

    private static void doAll(TableMessage tableMessage) {

    }
}
