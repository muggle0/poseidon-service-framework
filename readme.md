# 1. psf 规范

idea 需下载插件 snarlint 、checkstyle 、save actions 三个插件辅助代码去除异味 

## 1.1 分支管理规范

代码分支管理遵从git flow 规范

master 分支保证是最新最稳定的代码，develop 用于开发环境部署。

迭代流程

master 拉分支 feature/版本号 -》 开发完成-》合并到dev分支测试 -》自测联调完成-》feature 分支升级为 release分支测试验收-》验收有bug 拉hotfix

分支修复-》 修复完成，可以发版合并到master 并发版。


注意代码合并方向，feature可以合并到dev dev不可以合并到feature master 可以合并到dev dev 不可以合并到master；
master 用于下载运行，release分支是待发布分支，当完全验证结束后应该合并到master分支，develop 用于测试

## 1.2 代码规范

在doc 文档中有一个 `myCodeStyle.xml` 的文件，通过checkStyle 插件配置上该文件，每次commit 之前应该通过checkStyle 去除代码异味。

在idea 插件中搜索checkstyle-idea,重启后在settings-tools-checkstyle 中配置，注意选择版本号未8.10以下。

checkstyle 的具体用法可百度学习。

该插件的目的是为了统一代码风格和，提高代码的工整性，

## 1.3 commit 规范
遵从 git-commit Angular 规范

# 2. 资源说明
docker 仓库地址
maven 仓库地址

# 3. 组件划分

## 3.1 psf-code-generator

## 3.2 psf-api-gateway

## 3.3 psf-auth-center

## 3.4 psf-cloud-starter

## psf-log-center

## psf-xxl-job

## psf-transactional

# 4. devOps


# 5. 结合openspug