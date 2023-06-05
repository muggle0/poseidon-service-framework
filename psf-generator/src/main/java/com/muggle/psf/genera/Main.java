package com.muggle.psf.genera;

import com.muggle.psf.genera.entity.ProjectMessage;
import com.muggle.psf.genera.factory.CodeCommandInvoker;
import com.muggle.psf.genera.genera.SimpleCodeGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(final String[] args) throws Exception {
        final Map<String, String> otherfield = new HashMap<>();
        otherfield.put("parentVersion", "1.0-SNAPSHOT");
        final ProjectMessage build = ProjectMessage.builder().author("muggle").driver("com.mysql.jdbc.Driver").username("root")
            .swagger(true).tableName(Arrays.asList("oa_url_info")).parentPack("com.muggle.base")
            .jdbcUrl("jdbc:mysql:///p_oa?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC")
            .suffix("user").password("root").module("muggle-generator").projectPackage("com.muggle")
            .otherField(otherfield).build();
        final CodeCommandInvoker invoker = new CodeCommandInvoker(new SimpleCodeGenerator(build));
//        invoker.popCommond("createCode");
        invoker.execute();
    }
}
