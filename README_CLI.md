<h3 align="right">中文 / <a href="README_ENG.md">English</a></h3>
<p align="center">
    <img src="markdownResources/cover.png" alt="cover" align=center />
</p>
    <h2 align="center">椒盐歌单助手</h2>
    <h2 align="center" style="padding-top: 0">命令行（CLI）版</h2>

---

### ⚠️请注意⚠️

- 由于Windows终端的字符编码有问题，故CLI版**无法**在Windows上使用，只能在Linux、Mac OS等系统上使用。
    - 如果你的歌单中的歌曲信息**全为[ASCII字符](https://baike.baidu.com/item/ASCII/309296)**，那也可以在Windows上使用CLI版。
- 本项目相比前后端重构版，缺失了：
    - 友好的用户界面
    - 选择匹配模式（默认为**分离**）
    - 自定义是否启用**歌手匹配**与**专辑匹配**

### 需要使用的硬件与软件

#### 硬件

- 电脑 *1 （系统不限，安装了Java运行环境\[JRE\]即可）
- Android设备：
    - 若使用的主力设备**已获取**Root权限，则只需要一台即可，**无视**后文中主力机与备用机的区分。
    - 若使用的主力设备**未获取**Root权限，则需要两台设备：
        - 一台获取了Root权限的Android设备，真机或虚拟机皆可，后文中称其为**备用设备**。
        - 一台主力Android设备，后文中称其为**主力设备**。

#### 软件

本项目需要配合以下软件一起使用：

- 受支持的[在线音乐平台](README.md#音乐平台的选择)的Android客户端（均为普通版本，选择自己使用的一个或多个平台）
    - [网易云音乐](https://music.163.com/)**（推荐）**
    - [QQ音乐](https://y.qq.com/)**（推荐）**
    - [酷狗音乐](https://www.kugou.com/)
    - [酷我音乐](https://www.kuwo.cn/)
- 音乐标签
    - [Windows](https://www.cnblogs.com/vinlxc/p/11347744.html)
    - [Android](https://www.coolapk.com/apk/com.xjcheng.musictageditor)**（推荐）**
- 获取ID3标签（下载Release页面中的app-release.apk即可）
    - [Android (项目介绍)](https://github.com/Winnie0408/MusicID3TagGetter)
    - [Android (下载页面)](https://github.com/Winnie0408/MusicID3TagGetter/releases)
- 文件管理器（选择一个即可）
    - [MT管理器](https://www.coolapk.com/apk/bin.mt.plus)**（推荐）**
    - [ES文件浏览器](https://www.coolapk.com/apk/com.estrongs.android.pop)
    - [MiXplorer](https://mixplorer.com)
- Android虚拟机（当前使用的主力设备**未获取**Root权限时需要，选择一个即可）
    - [VMOS Pro](https://www.coolapk.com/apk/com.vmos.pro)：在手机上使用的虚拟机 ~~（可能需要使用VIP版，详情查看该应用的酷安评论区）~~
    - [MuMu模拟器](https://mumu.163.com)：在电脑上使用的虚拟机
- 椒盐音乐（或糖醋音乐）

## 使用方法

> **Note**
>
> **强烈推荐**您将本README文件完整阅读后，再进行相关操作！

### 0. 准备工作

安装上述软件

- **在线音乐平台客户端**与**文件管理器**，安装到有Root权限的设备（或虚拟机）上。
- **音乐标签**、**获取ID3标签**、**椒盐音乐**、文件管理器（可选），安装到主力设备上。

### 1. 获取在线音乐平台的歌单数据

**（在备用设备上操作）**

1. 打开需要使用的在线音乐平台客户端APP。
2. 登录账号。
3. **依次**点击进入自己的所有歌单（或者需要导出的歌单），并滑动到歌单页的**最底部**，加载当前歌单的所有歌曲。
4. 重复上述步骤，直到要所有导出的歌单**都加载过一次**。
5. 主动关闭在线音乐平台客户端（在软件菜单中选择**关闭**\[推荐\]，或直接在后台界面中将其划掉）。
6. 打开文件管理器，**授予Root权限**，进入在线音乐平台客户端的**数据目录**，找到**databases**文件夹，找到指定的数据库文件。<br>
   *若觉得各个软件的数据目录比较难找，可以使用MT管理器快速定位：`点击左上角菜单-点击安装包提取-选择需要的音乐APP-点击数据目录1`，即可快速跳转到数据目录。*
    - 网易云音乐
        - 数据目录：`/data/user/0/com.netease.cloudmusic/databases`
        - 数据库文件：`cloudmusic.db`
    - QQ音乐
        - 数据目录： `/data/user/0/com.tencent.qqmusic/databases`
        - 数据库文件：`QQMusic`
    - 酷狗音乐
        - 数据目录：`/data/user/0/com.kugou.android/databases`
        - 数据库文件：`kugou_music_phone_v7.db`
    - 酷我音乐
        - 数据目录：`/data/user/0/cn.kuwo.player/databases`
        - 数据库文件：`kwplayer.db`
7. 将数据库文件发送到电脑上。

### 2. 获取本地音乐的标签（ID3 Tag）信息

> **Warning**
>
> 本步骤会**覆盖**您本地音乐的ID3标签信息，**请谨慎操作**！
>
> 若您之前已经自行匹配（或修改）过歌曲的ID3信息，可跳过本步骤。
>
> 若后续匹配结果不理想，再重新进行此步骤即可。

**（在主力设备上操作）**

1. 将音乐文件保存在手机里（相信您已经完成这个步骤了）。
2. 打开**音乐标签**APP。
    1. 点击右上角**刷新**按钮，令其扫描手机中的音乐文件。
    2. 点击左上角**菜单**按钮，点击弹出菜单底部的**设置**。
    3. 点击**组合标签源**，**仅启用**与歌单来源平台**对应的**数据源，点击确定（比如，歌单来源平台为**网易云音乐**，则只启用**网易云**标签源，其他标签源都应**禁用**，若歌单来源平台为**酷狗音乐**，则启用**QQ与酷我**标签源，且QQ的优先级**高于**酷我）。
    4. 返回到音乐标签主界面，点击右下角的**编辑**按钮，点击**自动匹配标签**。
    5. 在弹出的对话框中，**仅勾选**标题、艺术家、专辑，**并同时启用**其右侧的覆盖选项，按需调整“网络搜索线程数”，点击确定。
    6. 等待音乐标签批量匹配完成。
3. 打开**获取ID3标签**APP。
    1. 点击下方的**选择目录**按钮，根据提示授予所需权限。
    2. 选择音乐存放的目录（具体选择方式请查看[这里](https://github.com/Winnie0408/MusicID3TagGetter/blob/master/README.md#使用方法)），点击屏幕底部的**使用此文件夹**，在弹出的对话框中点击**允许**。
    3. 等待软件扫描并导出手机中音乐的ID3标签信息。
    4. 前往软件的**导出目录**（手机存储目录中的Download）查看导出的ID3标签信息文件**本地音乐导出.txt**。
    5. 将导出的ID3标签信息文件发送到电脑上。

### 3. 进行歌单转换操作

**（在电脑上操作）**

1. 运行本项目（[怎么运行？](README.md#项目的使用与运行)以下方式三选一即可）。
    - 使用Java IDE（如IntelliJ IDEA、Eclipse等）从源码运行（需要电脑已配置JDK \[Java开发工具包\]）。
    - 使用Maven从源码编译、构建，并运行JAR包。
    - 下载并运行JAR包（需要电脑已安装JRE \[Java运行环境\]）。
2. 新建**SQLite**目录，并将步骤1.6中获取到的数据库文件复制进去 **（可选，注意不要更改数据库的文件名）**。
    - 若从源码运行，则在项目根目录下新建**SQLite**目录。
    - 若从JAR包或EXE文件运行，则在JAR包或EXE文件同级目录下新建**SQLite**目录。
3. 根据本项目的提示，进行文字输入或选择操作。
4. 等待您选择的所有歌单转换完成。
5. 使用本地或在线的Markdown编辑器，查看转换过程中在项目根目录生成的日志文件`ConvertLog.md`，查看转换过程是否出现错误或意外。**（可选）**
6. 获取转换结果文件（文件名为歌单的名称），将其发送到主力Android设备上。
    - 若从源码运行，则在项目根目录下的**Result**目录中。
    - 若从JAR包或EXE文件运行，则在JAR包或EXE文件同级目录下的**Result**目录中。

### 4. 将歌单导入椒盐音乐

**（在主力设备上操作）**

1. 打开**椒盐音乐**APP。
2. 右滑或点击右上角菜单按钮，进入**菜单**，点击**歌单**。
3. 点击**导入歌单 (.txt)**。
4. **逐个选择**转换结果文件，将其导入到椒盐音乐中。
5. 在椒盐音乐中查看导入的歌单。

## 其他事项

### 项目的使用与运行

#### 1. 使用Java IDE（如IntelliJ IDEA、Eclipse等）从源码运行

1. 克隆或下载本项目的源码。

```bash
git clone git@github.com:Winnie0408/SaltPlayerConverter.git
```

2. 使用Java IDE打开本并信任项目。
3. 打开项目根目录下的`pom.xml`文件，使用IDE自带的Maven工具下载项目所需的依赖。（推荐[配置Maven源为阿里云或其他国内镜像](README.md#配置maven镜像源)，以加快下载速度）。
4. 运行项目中的`src/main/java/Main.java`文件。

#### 2. 使用Maven从源码编译、并运行JAR包

1. 克隆或下载本项目的源码。

```bash
git clone git@github.com:Winnie0408/SaltPlayerConverter.git
```

2. 进入项目目录，使用Maven编译项目。（推荐[配置Maven源为阿里云或其他国内镜像](README.md#配置maven镜像源)，以加快下载速度）。

```bash
mvn clean package
```

3. 等待编译完成，控制台输出`BUILD SUCCESS`，进入项目中的`target`目录，运行以`.jar`结尾的文件。

```bash
java -jar [FileName].jar
```

#### 3. 下载并运行JAR包

1. 在项目的[Release页面](https://github.com/Winnie0408/SaltPlayerConverter/releases)，找到最新版本，下载以`.jar`结尾的文件。
2. 运行刚刚下载的JAR包。

```bash
java -jar [FileName].jar
```

### 配置Maven镜像源

#### Windows

- 使用自行安装的Maven
    1. 在Maven的安装目录中，找到并打开`conf/settings.xml`文件（没有的话就自行创建一个）。
    2. 在该文件的`<mirrors></mirrors>`节点中添加子节点。
- 使用IDE自带的Maven
    1. 进入`C:\Users\[Username]\.m2`目录，找到并打开`settings.xml`文件（没有的话就自行创建一个）。
    2. 在该文件的`<mirrors></mirrors>`节点中添加子节点。

#### Linux

1. 进入`/etc/maven/conf`目录，找到并打开`settings.xml`文件。
2. 在该文件的`<mirrors></mirrors>`节点中添加子节点。

#### 可用子节点（添加一个或多个皆可）

- 阿里云

```xml

<mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

- 网易

```xml

<mirror>
    <id>netease</id>
    <url>http://maven.netease.com/repository/public/</url>
    <mirrorOf>central</mirrorOf>
</mirror>
  ```

- 中国科学技术大学USTC

```xml

<mirror>
    <id>ustc</id>
    <url>http://mirrors.ustc.edu.cn/maven/maven2/</url>
    <mirrorOf>central</mirrorOf>
</mirror>
 ```

- [其他镜像源](https://blog.csdn.net/qq_38217990/article/details/129257106)

#### 完整配置文件示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <!-- 阿里云仓库 -->
        <mirror>
            <id>alimaven</id>
            <mirrorOf>central</mirrorOf>
            <name>aliyun maven</name>
            <url>http://maven.aliyun.com/nexus/content/repositories/central/</url>
        </mirror>


        <!-- 中央仓库1 -->
        <mirror>
            <id>repo1</id>
            <mirrorOf>central</mirrorOf>
            <name>Human Readable Name for this Mirror.</name>
            <url>https://repo1.maven.org/maven2/</url>
        </mirror>


        <!-- 中央仓库2 -->
        <mirror>
            <id>repo2</id>
            <mirrorOf>central</mirrorOf>
            <name>Human Readable Name for this Mirror.</name>
            <url>https://repo2.maven.org/maven2/</url>
        </mirror>

        <mirror>
            <id>repo2</id>
            <mirrorOf>central</mirrorOf>
            <name>Human Readable Name for this Mirror.</name>
            <url>https://search.maven.org/</url>
        </mirror>
    </mirrors>

</settings>
```

### 配置文件

本项目在运行时会读取并写入**项目根目录**下的`config.properties`文件，当该文件不存在时会自动创建。

#### 配置文件的格式

`配置名=配置值`

（如：`version=0.99`、`enableStatistic=true`）

#### 可使用的配置项

- `uuid`: 为这台电脑生成的唯一ID。将程序第一次运行，且`enableStatistic`值不为`false`时生成。 **强烈不建议**您手动修改本项的值。
- `enableStatistic`: 是否启用**统计数据发送**功能。
    - `true`: **启用**统计数据发送功能，程序会在**每转换完一个歌单后**，向统计分析服务器发送本次转换的相关统计信息，该信息**不包含您的任何敏感信息**，只会发送部分程序运行效率相关的数据[（包含哪些数据?）](README.md#发送的统计数据)。
    - `false`: **禁用**统计数据发送功能，程序**不会**向统计分析服务器发送任何数据，**不会连接互联网**；若**第一次运行程序时**就将其值设置为`false`，则不会生成`uuid`。
    - 配置文件中不包含该项 或 值为其他文本: 同`true`。
- `.*DatabasePath`: 用于保存您上一次输入的数据库文件的绝对路径。若您将数据库文件存放在项目的SQLite目录下，并在程序运行时直接`回车`，则该项不会生成。
- `musicOutputPath`: 用于保存您上一次输入的`本地音乐导出.txt`文件的绝对路径。
- `enableParenthesesRemoval`: 是否启用**括号内容去除**功能。启用此功能可以大幅提升外语歌曲的识别正确率[（为什么?）](README.md#括号内容去除)。
    - `true`: **启用**括号内容去除功能。
    - `false`: **禁用**括号内容去除功能。
    - 配置文件中不包含该项 或 值为其他文本: 忽略本项的值，并在运行时询问用户是否启用括号内容去除功能。

### 发送的统计数据

- 当前电脑的唯一ID
- 开始转换的时间
- 结束转换的时间
- 歌单来源
- 当前歌单包含的歌曲数量
- 匹配成功的歌曲数
- 自动匹配成功歌曲数量
- 使用的相似度阈值
- 括号去除是否启用
- 专辑匹配是否启用
- 最终保存了多少首歌曲

### 括号内容去除

大部分音乐平台对外语歌曲信息的命名方式一般为： `外文 (中文翻译)`或`外文 (歌曲来源、歌曲版本等)`。如`City Of Stars (From "La La Land" Soundtrack)`、`CALL ME BABY (叫我) (Chinese Ver.)`、`桜色舞うころ (樱花纷飞时)`。

启用此功能可以将字符串中的括号部分删去，只保留外文名，即：`外文`。如：`City Of Stars`、`CALL ME BABY`、`桜色舞うころ`，以此提高自动匹配成功率。

但需要注意，部分歌曲会在歌名后用括号注明歌曲版本：`歌名 (歌曲版本)`。如`曾经我也想过一了百了 (Live)`、`TruE (Ed Ver.)`，在这种情况下，若启用了本功能，会将其变成：`曾经我也想过一了百了`、`TruE`，继而**可能会出现匹配错误**。

请您根据您的实际情况，决定是否使用本功能。

### 音乐平台的选择

#### **网易云音乐** 与/或 **QQ音乐**

这两个平台的歌曲信息正确率较高，且较为完整、权威，可以有效提高自动匹配的成功率。

#### 酷狗音乐

该平台歌曲信息不太符合规范，合唱歌曲的艺术家名使用`、`分隔，且括号、斜杠的使用比较混乱，且**非【我喜欢】歌单**中歌曲的专辑信息不会保存到数据库中，导致匹配精确度下降，不太建议使用。

#### 酷我音乐

该平台歌曲信息不太符合规范，合唱歌曲的艺术家名使用`&`分隔，且括号、斜杠的使用比较混乱，且有很多用户自行上传的歌曲，这些歌曲的ID3信息大部分都不完整且不合规范，可能导致匹配精确度下降，不太建议使用。

### 相似度阈值

程序认为两个字符串**相同**的相似度大小，详情：

若当前阈值为0.8：

- **相同**<br>
  字符串1：想いの眠るゆりかご (回忆长眠的摇篮)<br>
  字符串2：想いの眠るゆりかご (回忆长眠的摇篮)<br>
  相似度：1.0

- **相同**<br>
  字符串1：伤感 II<br>
  字符串2：伤感 I<br>
  相似度：0.8

- **不相同**<br>
  字符串1：I'M OK<br>
  字符串2：I AM OK<br>
  相似度：0.7142857142857143

- **不相同**<br>
  字符串1：BANG BANG BANG (뱅뱅뱅)<br>
  字符串2：BANG BANG BANG<br>
  相似度：0.7

- **不相同**<br>
  字符串1：이 사랑 (这份爱) (Inst.)<br>
  字符串2：이 사랑 (这份爱)<br>
  相似度：0.5555555555555556

- **不相同**<br>
  字符串1：aaabbbccc<br>
  字符串2：abcabcabc<br>
  相似度：0.33333333333333337

## 赞助与支持

🥰🥰🥰

如果这个项目对您有所帮助，您可以给我一颗免费的⭐，或者请我喝杯咖啡！<br>
非常感谢您的支持！ <br>
⬇️⬇️⬇️<br>
<a href="markdownResources/Alipay WeChatPay.jpg">
<img src="markdownResources/Sponsorship.png" width = "150" height = "90" alt="Sponsorship.jpg" align=center />
</a>

