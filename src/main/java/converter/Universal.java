package converter;

import com.alibaba.fastjson.JSON;
import database.Database;
import utils.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 歌单来源：统一调用
 * @author: HWinZnieJ
 * @create: 2023-09-06 15:28
 **/

public class Universal {
    private static String SOURCE_ENG; //歌单来源英文名
    private static String SOURCE_CHN; //歌单来源中文名
    private static String DATABASE_NAME; //数据库名
    private static String SONG_LIST_TABLE_NAME; //歌单表名
    private static String SONG_LIST_ID; //歌单表的歌单ID列名
    private static String SONG_LIST_NAME; //歌单表的歌单名列名
    private static String SONG_LIST_SONG_INFO_TABLE_NAME; //歌单歌曲信息表名
    private static String SONG_LIST_SONG_INFO_PLAYLIST_ID; //歌单歌曲信息表中的歌单ID字段名
    private static String SONG_LIST_SONG_INFO_SONG_ID; //歌单歌曲信息表中的歌曲ID字段名
    private static String SONG_INFO_TABLE_NAME; //歌曲信息表名
    private static String SORT_FIELD; //歌单中歌曲的排序方式
    private static String SONG_INFO_SONG_ID; //歌曲信息表的歌曲ID字段名
    private static String SONG_INFO_SONG_NAME; //歌曲信息表的歌曲名字段名
    private static String SONG_INFO_SONG_ARTIST; //歌曲信息表的歌手名字段名
    private static String SONG_INFO_SONG_ALBUM; //歌曲信息表的专辑名字段名

    Scanner scanner = new Scanner(System.in); //从标准输入获取数据
    Database database = new Database(); //数据库操作
    Connection conn; //数据库连接
    String[][] localMusic; //存放本地音乐信息的二维数组 [歌曲名][歌手名][专辑名][文件路径]
    ArrayList<String> playListId = new ArrayList<>(); //存放歌单ID
    ArrayList<String> playListName = new ArrayList<>(); //存放歌单名
    Map<String, String> songNum; //存放某ID所对应歌单中的歌曲数量 [歌单ID][歌曲数量]
    Queue<String> selectedPlayListId = new LinkedList<>(); //存放用户选择的歌单序号
    Properties prop; //存放配置文件
    Date date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 初始化
     */
    public void init(String sourceApp) {
        switch (sourceApp) {
            case "QQMusic" -> {
                SOURCE_ENG = "QQMusic";
                SOURCE_CHN = "QQ音乐";
                DATABASE_NAME = "QQMusic";
                SONG_LIST_TABLE_NAME = "User_Folder_table";
                SONG_LIST_ID = "folderid";
                SONG_LIST_NAME = "foldername";
                SONG_LIST_SONG_INFO_TABLE_NAME = "User_Folder_Song_table";
                SONG_LIST_SONG_INFO_PLAYLIST_ID = "folderid";
                SONG_LIST_SONG_INFO_SONG_ID = "id";
                SONG_INFO_TABLE_NAME = "Song_table";
                SORT_FIELD = "position";
                SONG_INFO_SONG_ID = "id";
                SONG_INFO_SONG_NAME = "name";
                SONG_INFO_SONG_ARTIST = "singername";
                SONG_INFO_SONG_ALBUM = "albumname";
            }
            case "CloudMusic" -> {
                SOURCE_ENG = "CloudMusic";
                SOURCE_CHN = "网易云音乐";
                DATABASE_NAME = "cloudmusic.db";
                SONG_LIST_TABLE_NAME = "playlist";
                SONG_LIST_ID = "_id";
                SONG_LIST_NAME = "name";
                SONG_LIST_SONG_INFO_TABLE_NAME = "playlist_track";
                SONG_LIST_SONG_INFO_PLAYLIST_ID = "playlist_id";
                SONG_LIST_SONG_INFO_SONG_ID = "track_id";
                SONG_INFO_TABLE_NAME = "track";
                SORT_FIELD = "track_order";
                SONG_INFO_SONG_ID = "id";
                SONG_INFO_SONG_NAME = "name";
                SONG_INFO_SONG_ARTIST = "artists";
                SONG_INFO_SONG_ALBUM = "album_name";
            }
            case "KugouMusic" -> {
                SOURCE_ENG = "KuGouMusic";
                SOURCE_CHN = "酷狗音乐";
                DATABASE_NAME = "kugou_music_phone_v7.db";
                SONG_LIST_TABLE_NAME = "kugou_playlists";
                SONG_LIST_ID = "_id";
                SONG_LIST_NAME = "name";
                SONG_LIST_SONG_INFO_TABLE_NAME = "playlistsong";
                SONG_LIST_SONG_INFO_PLAYLIST_ID = "plistid";
                SONG_LIST_SONG_INFO_SONG_ID = "songid";
                SONG_INFO_TABLE_NAME = "kugou_songs";
                SORT_FIELD = "cloudfileorderweight";
                SONG_INFO_SONG_ID = "_id";
                SONG_INFO_SONG_NAME = "trackName";
                SONG_INFO_SONG_ARTIST = "artistName";
                SONG_INFO_SONG_ALBUM = "albumName";
            }
            case "KuwoMusic" -> {
                SOURCE_ENG = "KuWoMusic";
                SOURCE_CHN = "酷我音乐";
                DATABASE_NAME = "kwplayer.db";
                SONG_LIST_TABLE_NAME = "v3_list";
                SONG_LIST_ID = "id";
                SONG_LIST_NAME = "showname";
                SONG_LIST_SONG_INFO_TABLE_NAME = "v3_music";
                SONG_LIST_SONG_INFO_PLAYLIST_ID = "listid";
                SONG_LIST_SONG_INFO_SONG_ID = "rid";
                SONG_INFO_TABLE_NAME = "v3_music";
                SORT_FIELD = "id";
                SONG_INFO_SONG_ID = "rid";
                SONG_INFO_SONG_NAME = "name";
                SONG_INFO_SONG_ARTIST = "artist";
                SONG_INFO_SONG_ALBUM = "album";
            }
        }
        Logger.info("您选择了源格式为【" + SOURCE_CHN + "】的歌单");
        Sleep.start(500);

        //读取配置文件
        prop = PropertiesRelated.read();

        FileOperation.createDir(new File("./Result/" + SOURCE_ENG));
        FileOperation.checkDir(new File("./Result/" + SOURCE_ENG));

        readTxtFile();
        readDatabase();
        if (prepareSummary() == 1) return;
        start();
        afterwards();
    }

    /**
     * 读取数据库，并处理数据，获取歌单ID与歌单名
     */
    private void readDatabase() {
        while (true) {
            if (prop.getProperty("databasePath") != null) {
                System.out.println("\t直接回车使用上次输入的路径【" + prop.getProperty("databasePath") + "】");
                System.out.print("请输入" + SOURCE_CHN + "数据库文件的绝对路径：");
            } else
                System.out.print("请输入" + SOURCE_CHN + "数据库文件的绝对路径，" +
                        "\n或将数据库文件复制到项目的SQLite目录后，按下回车：");
            String input = scanner.nextLine();

            if (input.isEmpty() && prop.getProperty("databasePath") == null) {
                conn = database.getConnection(DATABASE_NAME);
                if (conn == null) {
                    Logger.error("项目SQLite目录内的" + DATABASE_NAME + "不存在，请检查！");
                    continue;
                }
                Logger.info("使用项目SQLite目录内的" + DATABASE_NAME + "文件");
            } else if (input.isEmpty() && prop.getProperty("databasePath") != null) {
                input = prop.getProperty("databasePath");
                File path = new File(input);
                if (!path.exists()) {
                    Logger.error("上次使用路径" + input + "的数据库文件不存在，请检查并重新输入！");
                    continue;
                }
                conn = database.getConnection(input);
                if (conn == null) {
                    Logger.error("上次使用路径" + input + "的数据库文件无法读取，请检查并重新输入！");
                    continue;
                }
                Logger.info("使用上次输入的路径" + input + "的数据库文件");
            } else {
                File path = new File(input);
                if (!path.exists()) {
                    Logger.error("指定路径" + input + "的数据库文件不存在，请检查并重新输入！");
                    continue;
                }
                conn = database.getConnection(input);
                if (conn == null) {
                    Logger.error("指定路径" + input + "的数据库文件无法读取，请检查并重新输入！");
                    continue;
                }
                Logger.info("使用指定路径" + input + "的数据库文件");
                PropertiesRelated.save("databasePath", input);
                if (!input.isEmpty())
                    Logger.success("已将您本次输入的路径保存至配置文件");
            }
            break;
        }
        Sleep.start(800);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;
            if (SOURCE_ENG.equals("KuWoMusic"))
                rs = stmt.executeQuery("SELECT " + SONG_LIST_ID + ", " + SONG_LIST_NAME + " FROM " + SONG_LIST_TABLE_NAME + " WHERE uid NOT NULL"); // 读取歌单列表
            else
                rs = stmt.executeQuery("SELECT " + SONG_LIST_ID + ", " + SONG_LIST_NAME + " FROM " + SONG_LIST_TABLE_NAME); // 读取歌单列表

            while (rs.next()) {
                playListId.add(rs.getString(SONG_LIST_ID)); // 保存歌单ID
                playListName.add(rs.getString(SONG_LIST_NAME)); // 保存歌单名
            }

            songNum = new HashMap<>();

            ArrayList<Integer> listToBeDelete = new ArrayList<>();

            for (int i = 0; i < playListId.size(); i++) {
                //检查歌单是否包含歌曲
                if (stmt.executeQuery("SELECT COUNT(*) FROM " + SONG_LIST_SONG_INFO_TABLE_NAME + " WHERE " + SONG_LIST_SONG_INFO_PLAYLIST_ID + "=" + playListId.get(i)).getInt(1) == 0) {
                    Logger.warning("歌单【" + playListName.get(i) + "】不包含任何歌曲，请您在" + SOURCE_CHN + "APP中重新打开该歌单后再试");
                    listToBeDelete.add(i);
//                    playListName.remove(playListName.get(i));
//                    playListId.remove(playListId.get(i));
                } else {
                    rs.close();
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM " + SONG_LIST_SONG_INFO_TABLE_NAME + " WHERE " + SONG_LIST_SONG_INFO_PLAYLIST_ID + "=" + playListId.get(i));
                    songNum.put(playListId.get(i), String.valueOf(rs.getInt(1)));
                }
            }

            //删除不包含歌曲的歌单
            for (int i = listToBeDelete.size() - 1; i >= 0; i--) {
                playListName.remove(playListName.get(listToBeDelete.get(i)));
                playListId.remove(playListId.get(listToBeDelete.get(i)));
            }

        } catch (SQLException e) {
            Logger.error("很抱歉！程序运行出现错误，请重试\n错误详情：" + e);
        }
    }

    /**
     * 读取歌单txt文件
     */
    private void readTxtFile() {
        boolean readFromConfig = false; //是否从配置文件中读取路径
        while (true) {
            if (prop.getProperty("musicOutputPath") != null) {
                System.out.println("\t直接回车使用上次输入的路径【" + prop.getProperty("musicOutputPath") + "】");
            }
            System.out.print("请输入手机导出的“本地音乐导出.txt”文件绝对路径：");
            String input = scanner.nextLine();
            if (input.isEmpty() && prop.getProperty("musicOutputPath") != null) {
                input = prop.getProperty("musicOutputPath");
                readFromConfig = true;
            }
            input = FileOperation.deleteQuotes(input);
            try {
                String[] localMusicFile = Files.readString(Paths.get(input)).split("\n");
                localMusic = new String[localMusicFile.length][4];
                int a = 0;
                for (String i : localMusicFile) {
                    localMusic[a][0] = i.split("#\\*#")[0];
                    localMusic[a][1] = i.split("#\\*#")[1];
                    localMusic[a][2] = i.split("#\\*#")[2];
                    localMusic[a][3] = i.split("#\\*#")[3];
                    a++;
                }
                PropertiesRelated.save("musicOutputPath", input);
                Logger.success("文件解析成功");
                if (!readFromConfig)
                    Logger.success("已将您本次输入的路径保存至配置文件");
                break;
            } catch (Exception e) {
                Logger.error("无法读取指定路径【" + input + "】的文件，请检查！错误信息：" + e);
                continue;
            }
        }
    }

    /**
     * 输出准备完成的相关信息
     */
    private int prepareSummary() {
        Logger.info("共读取到" + playListId.size() + "个有效歌单");
        for (int i = 0; i < playListId.size(); i++) {
            System.out.println("\t歌单" + (i + 1) + ". 【" + playListName.get(i) + "】，包含" + songNum.get(playListId.get(i)) + "首歌曲");
        }
        System.out.println("请结合" + SOURCE_CHN + "APP中显示的歌单数据，检查以上歌单信息是否正确");

        while (true) {
            System.out.print("""
                    \t输入“Y/y”导出全部歌单；
                    \t输入歌单名称前的序号导出所选歌单(可多选，输入示例：1 2 6 7 8 10)；
                    \t输入其他任意字符返回主菜单
                    请选择：""");
            String input = scanner.nextLine();
            input = input.toLowerCase();

            try {
                if (input.contains(" ")) {
//            String[] parts = input.split(" ");
//            //将数组转换为List
//            List<String> list = Arrays.asList(parts);
//            //对List进行排序
//            Collections.sort(list);
//            //将排序后的List转换为数组
//            selectedPlayListId.addAll(list);
                    // 使用Stream将字符串分割、转换为整数、排序、再转换回字符串
                    selectedPlayListId = Arrays.stream(input.split(" "))
                            .map(Integer::parseInt)
                            .sorted()
                            .map(String::valueOf)
                            .collect(Collectors.toCollection(LinkedList::new));
                    if (Integer.parseInt(((LinkedList<?>) selectedPlayListId).get(selectedPlayListId.size() - 1).toString()) > playListId.size()) {
                        Logger.error("输入的歌单序号超出范围，请重新输入！");
                        Sleep.start(500);
                        continue;
                    }
                    selectedPlayListId.add("-1");
                } else if (input.matches("[0-9]+")) {
                    if (Integer.parseInt(input) > playListId.size()) {
                        Logger.error("输入的歌单序号超出范围，请重新输入！");
                        Sleep.start(500);
                        continue;
                    }
                    selectedPlayListId.add(input);
                    selectedPlayListId.add("-1");
                } else if (!input.equals("y")) {
                    Logger.info("返回主菜单");
                    Sleep.start(500);
                    return 1;
                }
            } catch (Exception e) {
                Logger.error("输入有误，请重新输入！");
                Sleep.start(500);
                continue;
            }
            return 0;
        }
    }

    /**
     * 开始匹配
     */
    private void start() {
        double similaritySame; //认定为两个字符串相同的相似度阈值
        while (true) {
            System.out.print("请输入您认为两首歌的信息相同的相似度阈值(0.0~1.0，默认为0.85)：");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                similaritySame = 0.85;
            } else {
                if (Double.parseDouble(input) < 0.0 || Double.parseDouble(input) > 1.0) {
                    Logger.warning("输入的相似度阈值不在0.0~1.0之间，请重新输入！");
                    continue;
                }
                similaritySame = Double.parseDouble(input);
            }
            break;
        }

        boolean parenthesesRemoval = false; //是否启用括号内内容去除
        if (prop.getProperty("parenthesesRemoval") != null) {
            if (prop.getProperty("parenthesesRemoval").equals("true")) {
                Logger.info("您已在配置文件中【启用】括号去除");
                Sleep.start(500);
                parenthesesRemoval = true;
            } else if (prop.getProperty("parenthesesRemoval").equals("false")) {
                Logger.info("您已在配置文件中【禁用】括号去除");
                Sleep.start(500);
                parenthesesRemoval = false;
            }
        } else {
            while (true) {
                System.out.print("是否启用括号去除？启用此功能（应该）可以大幅提升外语歌曲的识别正确率 (y/N)：");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("y")) {
                    parenthesesRemoval = true;
//                    PropertiesRelated.save("parenthesesRemoval", "true");
                    Logger.info("已启用括号去除");
//                    Logger.success("已将您的选择保存至配置文件");
                } else if (input.equalsIgnoreCase("n") || input.isEmpty()) {
                    parenthesesRemoval = false;
//                    PropertiesRelated.save("parenthesesRemoval", "false");
                    Logger.info("已禁用括号去除");
//                    Logger.success("已将您的选择保存至配置文件");
                } else {
                    Logger.warning("输入有误，请重新输入！");
                    continue;
                }
                Sleep.start(500);
                break;
            }
        }

        Logger.info("开始匹配");
        Sleep.start(300);
        try {
            for (int i = 0; i < playListId.size(); i++) { //遍历读取到的所有歌单
                if (!selectedPlayListId.isEmpty()) { //获取用户选择的歌单
                    //如果用户选择的歌单序号不等于当前歌单序号，则跳过当前歌单
                    if (Integer.parseInt(selectedPlayListId.peek()) - 1 != i)
                        continue;
                    else
                        selectedPlayListId.poll(); //如果用户选择的歌单序号等于当前歌单序号，则弹出当前歌单序号
                }

                Statement stmt = conn.createStatement();
                Statement stmt1 = conn.createStatement();
                ResultSet rs;
                ResultSet rs1;

                String songName; //歌曲名
                String songArtist; //歌手名
                String songAlbum; //专辑名
                int num = 0;
                int successNum = 0;

                Logger.info("======正在匹配歌单【" + playListName.get(i) + "】======");
                date = new Date();
                MarkdownLog.date(dateFormat.format(date));
                MarkdownLog.playListTitle(playListName.get(i));
                Sleep.start(300);

                boolean disableAlbumNameMatch = false; //是否禁用专辑名称匹配

                if (SOURCE_ENG.equals("KugouMusic") && !playListName.get(i).equals("我喜欢")) {
                    System.out.println("""
                            \t检测到您正在匹配【酷狗音乐】的非【我喜欢】歌单，
                            \t由于酷狗音乐自身原因，非【我喜欢】歌单中歌曲的专辑信息未保存到数据库中，
                            \t建议对此类歌单禁用【专辑名称匹配】功能，以提升自动匹配成功率
                            \t(禁用后，专辑名称的相似度将始终显示为100%)""");
                    System.out.print("是否对歌单【" + playListName.get(i) + "】禁用专辑名称匹配? (Y/n)");
                    while (true) {
                        String input = scanner.nextLine();
                        if (input.isEmpty() || input.equalsIgnoreCase("y")) {
                            Logger.info("已禁用专辑名称匹配");
                            disableAlbumNameMatch = true;
                            break;
                        } else if (input.equalsIgnoreCase("n")) {
                            Logger.info("专辑名称匹配保持启用");
                            disableAlbumNameMatch = false;
                            break;
                        } else {
                            Logger.warning("输入有误，请重新输入！");
                        }
                    }
                }

                Sleep.start(300);

                boolean allYes = false;
                boolean allNo = false;

                //遍历歌单中的所有歌曲
                rs = stmt.executeQuery("SELECT " + SONG_LIST_SONG_INFO_SONG_ID + " FROM " + SONG_LIST_SONG_INFO_TABLE_NAME + " WHERE " + SONG_LIST_SONG_INFO_PLAYLIST_ID + "='" + playListId.get(i) + "'ORDER BY " + SORT_FIELD);
                while (rs.next()) {
                    String trackId = rs.getString(SONG_LIST_SONG_INFO_SONG_ID); //歌曲ID
                    rs1 = stmt1.executeQuery("SELECT " + SONG_INFO_SONG_NAME + ", " + SONG_INFO_SONG_ARTIST + ", " + SONG_INFO_SONG_ALBUM + " FROM " + SONG_INFO_TABLE_NAME + " WHERE " + SONG_INFO_SONG_ID + "=" + trackId); //使用歌曲ID查询歌曲信息

                    songName = rs1.getString(SONG_INFO_SONG_NAME);
                    if (songName == null) songName = "";

                    songArtist = rs1.getString(SONG_INFO_SONG_ARTIST);
                    if (songArtist == null) songArtist = "";
                    //网易云音乐歌手名为JSON格式，需要特殊处理
                    if (SOURCE_ENG.equals("CloudMusic"))
                        songArtist = JSON.parseObject(songArtist.substring(1, songArtist.length() - 1)).getString("name");
                    songArtist = songArtist.replaceAll(" ?& ?", "/").replaceAll("、", "/");

                    songAlbum = rs1.getString(SONG_INFO_SONG_ALBUM);
                    if (songAlbum == null) songAlbum = "";

//                    if (parenthesesRemoval) {
//                        songName = songName.replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "");
//                        songArtist = songArtist.replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "");
//                        songAlbum = songAlbum.replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "");
//                    }

//                    double nameSimilarity = 0; //歌曲名相似度
//                    double artistSimilarity = 0; //歌手名相似度
//                    double albumSimilarity = 0; //专辑名相似度

                    Map<String, Double> nameSimilarityArray = new HashMap<>(); //歌曲名相似度键值对
                    Map<String, Double> artistSimilarityArray = new HashMap<>(); //歌手名相似度键值对
                    Map<String, Double> albumSimilarityArray = new HashMap<>(); //专辑名相似度键值对

                    boolean matched = false; //是否匹配成功

//                    if (selectedPlayListId.isEmpty())
//                        System.out.print("总进度：" + (i + 1) + "/" + playListId.size() + "；当前歌单进度：" + (++num) + "/" + songNum.get(playListId.get(i)));

                    File file = new File("./Result/" + SOURCE_ENG + "/" + playListName.get(i) + ".txt");
                    //若文件不存在，则创建歌单文件
                    if (!file.exists())
                        file.createNewFile();

                    //遍历本地音乐库中的所有歌曲

                    FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
//                        songName = songName.replace("*", "\\*"); //预防后续代码将*识别为正则表达式
//                        localMusic[j][0] = localMusic[j][0].replace("*", "\\*"); //预防后续代码将*识别为正则表达式

                    //获取歌曲名相似度列表
                    if (parenthesesRemoval)
                        for (int k = 0; k < localMusic.length; k++) {
                            nameSimilarityArray.put(String.valueOf(k), StringSimilarityCompare.similarityRatio(songName.replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "").toLowerCase(), localMusic[k][0].replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "").toLowerCase()));
                        }
                    else
                        for (int k = 0; k < localMusic.length; k++) {
                            nameSimilarityArray.put(String.valueOf(k), StringSimilarityCompare.similarityRatio(songName.toLowerCase(), localMusic[k][0].toLowerCase()));
                        }

                    Map.Entry<String, Double> maxValue = MapSort.getMaxValue(nameSimilarityArray); //获取键值对表中相似度的最大值所在的键值对
                    double songNameMaxSimilarity = maxValue.getValue(); //获取相似度的最大值
                    String songNameMaxKey = maxValue.getKey(); //获取相似度的最大值对应的歌曲在localMusic数组中的位置

//                        if (songNameMaxSimilarity >= similarity) {
//                        songArtist = songArtist.replace("*", "\\*");
//                        localMusic[j][1] = localMusic[j][1].replace("*", "\\*");

                    //获取歌手名相似度列表
                    if (parenthesesRemoval)
                        for (int k = 0; k < localMusic.length; k++) {
                            artistSimilarityArray.put(String.valueOf(k), StringSimilarityCompare.similarityRatio(songArtist.replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "").toLowerCase(), localMusic[k][1].replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "").toLowerCase()));
                        }
                    else
                        for (int k = 0; k < localMusic.length; k++) {
                            artistSimilarityArray.put(String.valueOf(k), StringSimilarityCompare.similarityRatio(songArtist.toLowerCase(), localMusic[k][1].toLowerCase()));
                        }
                    maxValue = MapSort.getMaxValue(artistSimilarityArray); //获取键值对表中相似度的最大值所在的键值对
                    double songArtistMaxSimilarity = maxValue.getValue(); //获取相似度的最大值
                    String songArtistMaxKey = maxValue.getKey(); //获取相似度的最大值对应的歌手名

//                            if (songArtistMaxSimilarity >= similarity) {
//                        songAlbum = songAlbum.replace("*", "\\*");
//                        localMusic[j][2] = localMusic[j][2].replace("*", "\\*");

                    //获取专辑名相似度列表
                    double songAlbumMaxSimilarity;
                    if (!disableAlbumNameMatch) {
                        if (parenthesesRemoval)
                            for (int k = 0; k < localMusic.length; k++) {
                                albumSimilarityArray.put(String.valueOf(k), StringSimilarityCompare.similarityRatio(songAlbum.replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "").toLowerCase(), localMusic[k][2].replaceAll("(?i) ?\\((?!inst|[^()]* ver)[^)]*\\) ?", "").toLowerCase()));
                            }
                        else
                            for (int k = 0; k < localMusic.length; k++) {
                                albumSimilarityArray.put(String.valueOf(k), StringSimilarityCompare.similarityRatio(songAlbum.toLowerCase(), localMusic[k][2].toLowerCase()));
                            }
                        maxValue = MapSort.getMaxValue(albumSimilarityArray); //获取键值对表中相似度的最大值所在的键值对
                        songAlbumMaxSimilarity = maxValue.getValue(); //获取相似度的最大值
                        String songAlbumMaxKey = maxValue.getKey(); //获取相似度的最大值对应的专辑名
                    } else {
                        songAlbumMaxSimilarity = 1.0;
                    }

//                                if (songAlbumMaxSimilarity >= similarity) {
                    System.out.println();
                    if (songNameMaxSimilarity >= similaritySame && songArtistMaxSimilarity >= similaritySame && songAlbumMaxSimilarity >= similaritySame) {
//                                && songNameMaxKey.equals(songArtistMaxKey) && songNameMaxKey.equals(songAlbumMaxKey)) {
                        //歌曲名、歌手名、专辑名均匹配成功
                        Logger.success("第" + (++num) + "首，共" + songNum.get(playListId.get(i)) + "首，歌曲《" + songName + "》匹配成功！歌手：" + songArtist + "，专辑：" + songAlbum);
                        String[] header = {"类型", SOURCE_CHN, "本地音乐", "相似度"};
                        String[][] data = {{"歌名", songName, localMusic[Integer.parseInt(songNameMaxKey)][0], String.format("%.1f%%", songNameMaxSimilarity * 100)}, {"歌手", songArtist, localMusic[Integer.parseInt(songNameMaxKey)][1], String.format("%.1f%%", songArtistMaxSimilarity * 100)}, {"专辑", songAlbum, localMusic[Integer.parseInt(songNameMaxKey)][2], String.format("%.1f%%", songAlbumMaxSimilarity * 100)}};
                        TablePrinter.printTable(header, data, "匹配详情");
                        matched = true;
                        successNum++;
                        fileWriter.write(localMusic[Integer.parseInt(songNameMaxKey)][3] + "\n");
                        fileWriter.close();
                        MarkdownLog.succeedConvertResult(header, data, num, songNum.get(playListId.get(i)));
//                        break;
                        /*} else if (songNameMaxSimilarity >= similaritySame && (songArtistMaxSimilarity >= similarityMaybe || songAlbumMaxSimilarity >= similarityMaybe)) {
//                            //歌曲名相同，歌手名、专辑名中的一或多项相似，给用户判断是否添加到列表
                            Logger.warning("检测到转换冲突");
                            DecimalFormat df = new DecimalFormat("0.00");
                            String[] header = {"类型  ", "本地音乐", "网易云音乐", "最大相似度"};
                            String[][] data = {{"歌名：", localMusic[Integer.parseInt(songNameMaxKey)][0], songName, df.format(songNameMaxSimilarity)}, {"歌手：", localMusic[Integer.parseInt(songArtistMaxKey)][1], songArtist, df.format(songArtistMaxSimilarity)}, {"专辑：", localMusic[Integer.parseInt(songAlbumMaxKey)][2], songAlbum, df.format(songAlbumMaxSimilarity)}};
                            TablePrinter.printTable(header, data, "匹配详情");
                            Logger.info("是否添加到歌单？(Y/n)");
//                            input = scanner.nextLine();
                            input = "n";
                            if (input.isEmpty() || input.equalsIgnoreCase("y")) {
                                fileWriter.write(localMusic[Integer.parseInt(songNameMaxKey)][3] + "\n");
                                fileWriter.close();
                                Logger.success("歌曲《" + songName + "》匹配成功！歌手名：" + songArtist + "，专辑名：" + songAlbum);
                                matched = true;
                                successNum++;
                                break;
                            } else if (input.equalsIgnoreCase("n")) {
                                Logger.warning("歌曲《" + songName + "》匹配失败！歌手名：" + songArtist + "，专辑名：" + songAlbum);
                            }*/
                    } else {
                        //歌曲名、歌手名、专辑名中的一或多项匹配失败
                        Logger.warning("第" + (++num) + "首，共" + songNum.get(playListId.get(i)) + "首，歌曲《" + songName + "》匹配失败！歌手：" + songArtist + "，专辑：" + songAlbum);

                        String[] header = {"类型", SOURCE_CHN, "本地音乐", "相似度"};
                        String[][] data = {{"歌名", songName, localMusic[Integer.parseInt(songNameMaxKey)][0], String.format("%.1f%%", songNameMaxSimilarity * 100)}, {"歌手", songArtist, localMusic[Integer.parseInt(songNameMaxKey)][1], String.format("%.1f%%", songArtistMaxSimilarity * 100)}, {"专辑", songAlbum, localMusic[Integer.parseInt(songNameMaxKey)][2], String.format("%.1f%%", songAlbumMaxSimilarity * 100)}};
                        TablePrinter.printTable(header, data, "匹配详情");
                        String input;
                        if (!allYes && !allNo) {
                            System.out.println("\tY/y/直接回车：按照表格中的信息添加到歌单");
                            System.out.println("\tN/n：不添加到歌单");
                            System.out.println("\t输入歌曲相关信息，自行手动完成匹配");
                            System.out.println("若在输入的选项前加上【A/a】，本次选择的操作将应用于当前歌单的所有后续歌曲");
                            System.out.print("请选择您的操作：");
                            input = scanner.nextLine().toLowerCase();
                        } else if (allYes) {
                            input = "y";
                            Logger.success("使用默认操作：添加到歌单");
                        } else {
                            input = "n";
                            Logger.warning("使用默认操作：不添加到歌单");
                            MarkdownLog.failedConvertResult(songName, songArtist, songAlbum, num, songNum.get(playListId.get(i)));
                        }

                        if (input.endsWith("n")) {
                            if (!allNo) {
                                Logger.warning("已跳过");
                                MarkdownLog.failedConvertResult(songName, songArtist, songAlbum, num, songNum.get(playListId.get(i)));
                                Sleep.start(300);
                            }
                            if (input.startsWith("a")) {
                                allNo = true;
                                Logger.info("当前歌单的所有后续歌曲，遇到转换冲突时将默认跳过，不添加到歌单");
                                MarkdownLog.info("======默认跳过已启用======");
                                Sleep.start(1000);
                            }
                            continue;
                        } else if (input.isEmpty() || input.endsWith("y")) {
                            if (!allYes) {
                                Logger.success("已添加到歌单");
                                Sleep.start(300);
                            }
                            if (input.startsWith("a")) {
                                allYes = true;
                                Logger.info("当前歌单的所有后续歌曲，遇到转换冲突时将默认添加到歌单");
                                MarkdownLog.info("======默认添加已启用======");
                                Sleep.start(1000);
                            }

                            matched = true;
                            successNum++;
                            fileWriter.write(localMusic[Integer.parseInt(songNameMaxKey)][3] + "\n");
                            fileWriter.close();
                            MarkdownLog.succeedConvertResult(header, data, num, songNum.get(playListId.get(i)));
                        } else {
                            while (true) {
                                String[][] manualSearchResult = FindStringArray.findStringArray(localMusic, input);
                                for (int j = 0; j < manualSearchResult.length; j++) {
                                    System.out.println("\t" + (j + 1) + ". " + manualSearchResult[j][0] + " - " + manualSearchResult[j][1] + " - " + manualSearchResult[j][2]);
                                }
                                if (manualSearchResult.length == 0) {
                                    Logger.error("未找到匹配的歌曲，请重新输入！");
                                    Sleep.start(300);
                                    System.out.print("请输入歌曲相关信息；或输入N/n跳过当前歌曲（不添加到歌单）：");
                                    input = scanner.nextLine().toLowerCase();
                                    continue;
                                }
                                System.out.print("请选择正确的歌曲序号；或输入N/n跳过当前歌曲（不添加到歌单）；或重新输入歌曲信息：");
                                String choice = scanner.nextLine().toLowerCase();
                                if (choice.matches("[0-9]+")) {
                                    if (Integer.parseInt(choice) > manualSearchResult.length || Integer.parseInt(choice) < 1) {
                                        Logger.error("输入错误，请重新输入！");
                                        continue;
                                    }
                                    Logger.success("已添加到歌单");
                                    matched = true;
                                    successNum++;
                                    fileWriter.write(manualSearchResult[Integer.parseInt(choice) - 1][3] + "\n");
                                    fileWriter.close();
                                    data = new String[][]{{"歌名", songName, manualSearchResult[Integer.parseInt(choice) - 1][0], "手动匹配"}, {"歌手", songArtist, manualSearchResult[Integer.parseInt(choice) - 1][1], "手动匹配"}, {"专辑", songAlbum, manualSearchResult[Integer.parseInt(choice) - 1][2], "手动匹配"}};
                                    MarkdownLog.succeedConvertResult(header, data, num, songNum.get(playListId.get(i)));
                                    Sleep.start(300);
                                    break;
                                } else if (choice.equals("n")) {
                                    Logger.warning("已跳过");
                                    MarkdownLog.failedConvertResult(songName, songArtist, songAlbum, num, songNum.get(playListId.get(i)));
                                    Sleep.start(300);
                                    break;
                                }
//                                else if (choice.equals("r")) {
//                                    System.out.print("请输入歌曲相关信息：");
//                                    input = scanner.nextLine().toLowerCase();
//                                    continue;
//                                }
                                else {
                                    input = choice;
//                                    Logger.error("输入错误，请重新输入！");
                                    continue;
                                }
                            }

                        }


//                        break;
//                            //歌曲名、歌手名、专辑名中的一或多项匹配失败
//                            Logger.warning("检测到转换冲突");
//                            DecimalFormat df = new DecimalFormat("0.00");
//                            String[] header = {"类型  ", "本地音乐", "网易云音乐", "最大相似度"};
//                            String[][] data = {{"歌名：", localMusic[Integer.parseInt(songNameMaxKey)][0], songName, df.format(songNameMaxSimilarity)}, {"歌手：", localMusic[Integer.parseInt(songArtistMaxKey)][1], songArtist, df.format(songArtistMaxSimilarity)}, {"专辑：", localMusic[Integer.parseInt(songAlbumMaxKey)][2], songAlbum, df.format(songAlbumMaxSimilarity)}};
//                            TablePrinter.printTable(header, data, "匹配详情");
//                            Logger.info("是否添加到歌单？(Y/n)");
////                            input = scanner.nextLine();
//                            input = "n";
//                            if (input.isEmpty() || input.equalsIgnoreCase("y")) {
//                                fileWriter.write(localMusic[Integer.parseInt(songNameMaxKey)][3] + "\n");
//                                fileWriter.close();
//                                Logger.success("\n歌曲《" + songName + "》匹配成功！歌手名：" + songArtist + "，专辑名：" + songAlbum);
//                                matched = true;
//                                successNum++;
//                                break;
//                            } else if (input.equalsIgnoreCase("n")) {
//                                Logger.warning("\n歌曲《" + songName + "》匹配失败！歌手名：" + songArtist + "，专辑名：" + songAlbum);
//                            }
                    }
//                                } else { //歌曲名匹配失败
//                            Logger.warning("歌曲“" + songName + "”未匹配到歌名相似度大于" + similarity + "的歌曲，当前最大相似度：" + maxValue);
//                                }
//                            }
//                        }

//                    if (!matched)
//                        Logger.warning("歌曲《" + songName + "》匹配失败！歌手名：" + songArtist + "，专辑名：" + songAlbum);
                }
                System.out.println("\n======共" + songNum.get(playListId.get(i)) + "首，成功" + successNum + "首======\n" +
                        "======歌单" + (i + 1) + "【" + playListName.get(i) + "】匹配完成，剩余" + (selectedPlayListId.size() - 1) + "个歌单======");
                if ((selectedPlayListId.size() - 1) > 0) {
                    Logger.info("3秒后继续匹配下一歌单");
                    System.out.print("3 ");
                    Sleep.start(1000);
                    System.out.print("2 ");
                    Sleep.start(1000);
                    System.out.println("1");
                    Sleep.start(1000);
                } else
                    System.out.println();
            }
            Logger.success(SOURCE_CHN + "所有歌单匹配完成，返回主菜单\n");
        } catch (SQLException | IOException | NullPointerException e) {
            Logger.error("很抱歉！程序运行出现错误，请重试\n错误详情：" + e);
        }
    }


    /**
     * 释放资源
     */
    private void afterwards() {
        database.closeConnection(conn);
    }
}
