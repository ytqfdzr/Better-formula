<p align="center">
  <img src="icon/icon.png" alt="Better-formula 图标" width="160">
</p>

# Better-formula

一个基于 Jetpack Compose 的 Android 计算应用:定义一次公式,之后每组变量值反复代入计算——类似 Excel 的固定逻辑批量计算,支持中文变量名、多公式切换与计算历史。

## 技术栈

- Kotlin + Jetpack Compose (Material 3)
- Room 本地数据库(KSP)
- kotlinx.serialization
- Navigation Compose
- 自研表达式解析器(Lexer / Parser)

## 环境要求

- Android Studio(Koala 或更新版本)
- JDK 17
- Android SDK:compileSdk 34,minSdk 26

## 构建与运行

1. 克隆仓库:
   
   ```bash
   git clone <repo-url>
   cd myapp
   ```

2. 用 Android Studio 打开项目,等待 Gradle Sync 完成(`local.properties` 会自动生成)。

3. 连接设备或启动模拟器,点击 Run 即可。

命令行构建:

```bash
./gradlew assembleDebug
```

## 项目结构

```
Better-formula/
├── app/src/main/
│   ├── java/com/betterformula/app/
│   │   ├── core/parser/   # 表达式词法/语法解析与求值(Lexer、Parser、Evaluator、Calculator)、公式校验、数值格式化
│   │   ├── data/          # 数据层:Room(db/dao/entity)、仓库(Formula/History/Settings)、JSON 编解码(Codec)、依赖容器(AppContainer)
│   │   ├── ui/            # Compose 界面:main 主界面、formula 公式编辑、history 历史、theme 主题、AppNavHost 导航
│   │   ├── MainActivity.kt              # 应用入口 Activity
│   │   └── BetterFormulaApplication.kt   # Application,初始化 AppContainer
│   ├── res/               # 资源:drawable/values/mipmap(应用图标)
│   └── AndroidManifest.xml
├── icon/icon.png          # README 展示的图标源文件
├── plan/                  # 设计与问题记录文档
├── gradle/  build.gradle.kts  settings.gradle.kts   # Gradle 构建配置
└── README.md
```

## License

 MIT License, see [LICENSE](LICENSE).
