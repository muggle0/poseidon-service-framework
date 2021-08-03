package com.muggle.psf;

import com.muggle.psf.entity.ProjectMessage;
import com.muggle.psf.factory.PoseidonCodeFactory;
import com.muggle.psf.genera.SimpleCodeGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> otherfield=new HashMap<>();
        otherfield.put("parentVersion","1.0-SNAPSHOT");
        ProjectMessage build = ProjectMessage.builder().author("muggle").driver("com.mysql.jdbc.Driver").username("root")
                .swagger(true).tableName(Arrays.asList("oa_url_info")).parentPack("com.muggle.base")
                .jdbcUrl("jdbc:mysql:///p_oa?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC")
                .suffix("user").password("root").module("muggle-generator").projectPackage("com.muggle")
                .otherField(otherfield).skipJdbc(true).skipBase(false).skipConfig(false).build();
        PoseidonCodeFactory.init(new SimpleCodeGenerator());
        PoseidonCodeFactory.createProject(build);
    }
}
