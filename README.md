# ReCalc(再算)

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
app/src/main/java/com/example/recalc/
├── core/parser/   # 表达式词法与语法解析
└── ...            # UI、数据层
```

 ## License

 MIT License, see [LICENSE](LICENSE).
