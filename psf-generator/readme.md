## 使用手册

1. 下载项目到本地：
```shell script
git cloen https://github.com/Poseidon-Service-Framework/poseidon-generator.git
```

2. 执行安装命令

```shell script
mvn install
```
3. 添加依赖到你的maven 项目中

```shell script
<dependency>
    <groupId>com.muggle</groupId>
    <artifactId>psf-generator</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

执行以下代码（根据实际情况填写包信息和数据库信息）：

```java
public class Main {
    public static void main(String[] args) throws Exception {
        Map<String, String> otherfield=new HashMap<>();
        otherfield.put("parentVersion","1.0-SNAPSHOT");
        ProjectMessage build = ProjectMessage.builder().author("muggle").driver("com.mysql.jdbc.Driver").username("root")
                .swagger(true).tableName(Arrays.asList("oa_url_info")).parentPack("com.muggle.base")
                .jdbcUrl("jdbc:mysql:///p_oa?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC")
                .suffix("user").password("root").module("muggle-generator").projectPackage("com.muggle")
                .otherField(otherfield).skipJdbc(false).skipBase(false).skipConfig(false).build();
        CodeCommandInvoker invoker = new CodeCommandInvoker( new SimpleCodeGenerator(build));
//        invoker.popCommond("createCode");
        invoker.execute();
    }
}
```
## 功能说明
1. 指定项目基类包 `parentPack`，该属性用于生成 entity controller 时指定对应的父类名称。

2. 模块名 `module` 用于多模块项目，如果你的项目是单体项目可以忽略该字段。

3. 内置指令，用户可以选择性的创建文件，默认全部创建，可以使用方法invoker.popCommond("指令名称"); 移除内置的指令

内置指令说明：

- createCode 生成业务代码，包括controller entity mapper service 代码
- createPom 生成pom 文件
- createApplication 生成application。properties
- createBootstrap 生成 bootstrap.properties
- createBanner 生成banner.txt
- createConfig 生成 配置类
- createLogback 生成 spring-lobbacg-spring.xml
- createMainClass 生成启动类
- createReadme 生成readme.md

## 代码扩展

每个人对代码生成的需求不同，如果该工具默认生成的代码不能满足你的需求，那么你可以尝试自己修改代码模板，
来满足你的需求，你可以直接在源码中找 `resources\psf-others\config` , `resources\psf-template` 
两个文件夹中的模板进行修改，而 controller ，service 等模板需要你自己创建文件夹 resources\templates,在其下添加对应的模板。
模板名称和内容参考 mybatis-plus-generator源码中的templates。
在其中创建 mapper.xml.ftl 等类似文件以覆盖 mybatisplus generator 中的模板，当然你也可以以同样的方式覆盖，poseidon generator 中的模板。
你还可以在 psf-others文件夹下根据自己的包名创建文件夹添加模板文件，程序会根据你创建的文件夹以及模板在你的基础包下新建包和类。
    
## 自定义指令

实现 'codeCommand' 方法，并 调用 CodeCommandInvoker#addCommond 方法将指令加入队列中。

通过指令你可以实现代码创建过程中自定义的逻辑。

示例：

```java

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
```


该代码定义了一个 `createUIpom` 指令，会读取`/psf-extend/pom.xml.ftl` 下的模板创建pom。

## qa
问题一：代码生成工具直接依赖在项目中会不会造成代码的隐患，

答：处理得当不会，比如该工具在生成的pom文件中配置如下：

```xml
 <profiles>
        <profile>
            <id>local</id>
            <dependencies>
                <dependency>
                    <groupId>com.muggle</groupId>
                    <artifactId>poseidon-generator</artifactId>
                    <version>1.0.4-release</version>
                </dependency>
            </dependencies>
        </profile>
        <profile>
            <id>normal</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
    </profiles>
```
编译的profile不是 local就不会把poseidon-generator 依赖编进项目的jar包中，相当于没有依赖 poseidon-generator ，因此不造成安全隐患

问题二：有没有可视化界面

答：有，参看 https://github.com/Poseidon-Service-Framework/poseidon-generator-ui。