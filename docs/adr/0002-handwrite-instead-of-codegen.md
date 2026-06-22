# 手写业务代码而非使用若依在线代码生成器

若依平台的核心低代码特性是代码生成器（从数据库表一键生成 CRUD 全栈代码），但生成器需在 Web 界面人工操作。本项目改由 Agent 直接编写代码，并严格遵循若依生成器的标准产物结构与规范（Controller/ServiceImpl/Mapper/domain/Vue、@PreAuthorize 权限注解、BaseEntity、AjaxResult 等），使手写代码在形态上与生成产物一致。

## Consequences

- 失去"一键生成"的演示动作，但代码规范与平台一致，总报告仍可讲"基于若依平台规范快速开发"的低代码叙事。
- 后续新增业务表时，由 Agent 按同一套骨架补齐代码，而非走生成器界面。
