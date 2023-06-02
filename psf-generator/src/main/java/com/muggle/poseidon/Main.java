package com.muggle.poseidon;

import com.muggle.poseidon.entity.ProjectMessage;
import com.muggle.poseidon.factory.CodeCommandInvoker;
import com.muggle.poseidon.genera.SimpleCodeGenerator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Map<String, String> otherfield=new HashMap<>();
        otherfield.put("parentVersion","1.0-SNAPSHOT");
        ProjectMessage build = ProjectMessage.builder().author("muggle").driver("com.mysql.jdbc.Driver").username("root")
                .swagger(true).tableName(Arrays.asList("oa_url_info")).parentPack("com.muggle.base")
                .jdbcUrl("jdbc:mysql:///p_oa?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC")
                .suffix("user").password("root").module("muggle-generator").projectPackage("com.muggle")
                .otherField(otherfield).build();
        CodeCommandInvoker invoker = new CodeCommandInvoker( new SimpleCodeGenerator(build));
//        invoker.popCommond("createCode");
        invoker.execute();
    }
}
