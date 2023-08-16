package com.muggle.psf.genera.entity;

import com.muggle.psf.genera.genera.CodeGenerator;

/**
 * Description
 * Date 2021/8/5
 * Created by muggle
 */
public interface CodeCommand {
    String getName();

    void excute(CodeGenerator codeGenerator) throws Exception;
}
