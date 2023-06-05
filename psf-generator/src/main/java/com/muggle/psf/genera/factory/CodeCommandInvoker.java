package com.muggle.psf.genera.factory;

import com.muggle.psf.genera.entity.CodeCommand;
import com.muggle.psf.genera.genera.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Description
 * Date 2021/8/5
 * Created by muggle
 */
public class CodeCommandInvoker extends CodeFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeCommandInvoker.class);

    private Queue<CodeCommand> commandQueue;

    public CodeCommandInvoker(final CodeGenerator codeGenerator) {
        this.commandQueue = new LinkedList<>();
        init(codeGenerator);
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createCode";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createCode();
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createPom";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createPom();
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createApplication";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createProperties("application");
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createBootstrap";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createProperties("bootstrap");
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createBanner";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createBanner();
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createConfig";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createConfig();
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createLogback";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createLogback();
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createMainClass";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createMainClass();
            }
        });
        commandQueue.add(new CodeCommand() {
            @Override
            public String getName() {
                return "createReadme";
            }

            @Override
            public void excute(final CodeGenerator codeGenerator) throws Exception {
                createReadme();
            }
        });

    }

    public void execute() {
        commandQueue.forEach(codeCommand -> {
            try {
                codeCommand.excute(getCodeGenerator());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
        LOGGER.info("==========> [代码生成完毕]");
    }

    public void addCommond(final CodeCommand codeCommand) {
        commandQueue.add(codeCommand);
    }

    public void popCommond(final String commandName) {
        final Iterator<CodeCommand> iterator = commandQueue.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getName().equals(commandName)) {
                iterator.remove();
            }
        }
    }

}
