package SourceAPP;

import Database.Database;
import Utils.*;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @description: 歌单来源：QQ音乐
 * @author: HWinZnieJ
 * @create: 2023-09-06 15:28
 **/

public class QQMusic {
    Scanner scanner = new Scanner(System.in); //从标准输入获取数据
    Database database = new Database(); //数据库操作
    Connection conn; //数据库连接
    String[][] localMusic; //存放本地音乐信息的二维数组 [歌曲名][歌手名][专辑名][文件路径]
    ArrayList<String> playListId = new ArrayList<>(); //存放歌单ID
    ArrayList<String> playListName = new ArrayList<>(); //存放歌单名
    Map<String, String> songNum; //存放某ID所对应歌单中的歌曲数量 [歌单ID][歌曲数量]
    Queue<String> selectedPlayListId = new LinkedList<>(); //存放用户选择的歌单序号

    /**
     * 初始化
     */
    public void init() {
        System.out.println("您选择了源格式为【QQ音乐】的歌单");
        Sleep.start(500);

        FileOperation.createDir(new File("./Result/QQMusic"));
        FileOperation.checkDir(new File("./Result/QQMusic"));

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
            System.out.print("请输入QQ音乐数据库文件的绝对路径：");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                conn = database.getConnection("QQMusic");
                if (conn == null) {
                    Logger.error("项目SQLite目录内的QQMusic不存在，请检查！");
                    continue;
                }
                Logger.info("使用项目SQLite目录内的QQMusic");
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
            }
            break;
        }

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery("SELECT * FROM User_Folder_table"); // 读取歌单列表

            while (rs.next()) {
                playListId.add(rs.getString("folderid")); // 保存歌单ID
                playListName.add(rs.getString("foldername")); // 保存歌单名
            }

            songNum = new HashMap<>();

            ArrayList<Integer> listToBeDelete = new ArrayList<>();

            for (int i = 0; i < playListId.size(); i++) {
                //检查歌单是否包含歌曲
                if (stmt.executeQuery("SELECT COUNT(*) FROM User_Folder_Song_table WHERE folderid=" + playListId.get(i)).getInt(1) == 0) {
                    Logger.warning("歌单【" + playListName.get(i) + "】不包含任何歌曲，请您在QQ音乐APP中重新打开该歌单后再试");
                    listToBeDelete.add(i);
//                    playListName.remove(playListName.get(i));
//                    playListId.remove(playListId.get(i));
                } else {
                    rs.close();
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM User_Folder_Song_table WHERE folderid=" + playListId.get(i));
                    songNum.put(playListId.get(i), String.valueOf(rs.getInt(1)));
                }
            }

            //删除不包含歌曲的歌单
            for (int i = listToBeDelete.size() - 1; i >= 0; i--) {
                playListName.remove(playListName.get(listToBeDelete.get(i)));
                playListId.remove(playListId.get(listToBeDelete.get(i)));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取歌单txt文件
     */
    private void readTxtFile() {
        while (true) {
            System.out.print("请输入手机导出的“本地音乐导出.txt”文件绝对路径：");
            String input = scanner.nextLine();
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
        System.out.println("请结合QQ音乐APP中显示的歌单数据，检查以上歌单信息是否正确");
        System.out.print("输入“Y/y”导出全部歌单；\n输入歌单名称前的序号导出所选歌单(可多选，输入示例：1 2 6 7 8 10)；\n输入其他任意字符返回主菜单：");

        String input = scanner.nextLine();
        input = input.toLowerCase();

        if (input.contains(" ")) {
            Collections.addAll(selectedPlayListId, input.split(" "));
            selectedPlayListId.add("-1");
        } else if (input.matches("[0-9]")) {
            selectedPlayListId.add(input);
            selectedPlayListId.add("-1");
        } else if (!input.equals("y")) {
            Logger.info("返回主菜单");
            Sleep.start(500);
            return 1;
        }
        return 0;
    }

    /**
     * 开始匹配
     */
    private void start() {
        double similaritySame; //认定为两个字符串相同的相似度阈值
        while (true) {
            System.out.print("请输入您认为两个字符串相同的相似度阈值(0.0~1.0，默认为0.9)：");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                similaritySame = 0.9;
            } else {
                if (Double.parseDouble(input) < 0.0 || Double.parseDouble(input) > 1.0) {
                    Logger.warning("输入的相似度阈值不在0.0~1.0之间，请重新输入！");
                    continue;
                }
                similaritySame = Double.parseDouble(input);
            }
            break;
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
                Sleep.start(250);

                //遍历歌单中的所有歌曲
                rs = stmt.executeQuery("SELECT * FROM User_Folder_Song_table WHERE folderid='" + playListId.get(i) + "'ORDER BY position");
                while (rs.next()) {
                    String trackId = rs.getString("id"); //QQ音乐歌曲ID
                    rs1 = stmt1.executeQuery("SELECT * FROM Song_table WHERE id=" + trackId); //使用歌曲ID查询歌曲信息
                    songName = rs1.getString("name");
                    songArtist = rs1.getString("singername");
                    songAlbum = rs1.getString("albumname");

//                    double nameSimilarity = 0; //歌曲名相似度
//                    double artistSimilarity = 0; //歌手名相似度
//                    double albumSimilarity = 0; //专辑名相似度

                    Map<String, Double> nameSimilarityArray = new HashMap<>(); //歌曲名相似度键值对
                    Map<String, Double> artistSimilarityArray = new HashMap<>(); //歌手名相似度键值对
                    Map<String, Double> albumSimilarityArray = new HashMap<>(); //专辑名相似度键值对

                    boolean matched = false; //是否匹配成功

                    if (selectedPlayListId.isEmpty())
                        System.out.print("总进度：" + (i + 1) + "/" + playListId.size() + "；当前歌单进度：" + (++num) + "/" + songNum.get(playListId.get(i)));

                    File file = new File("./Result/QQMusic/" + playListName.get(i) + ".txt");
                    //若文件不存在，则创建歌单文件
                    if (!file.exists())
                        file.createNewFile();

                    //遍历本地音乐库中的所有歌曲

                    FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
//                        songName = songName.replace("*", "\\*"); //预防后续代码将*识别为正则表达式
//                        localMusic[j][0] = localMusic[j][0].replace("*", "\\*"); //预防后续代码将*识别为正则表达式

                    //获取歌曲名相似度列表
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
                    for (int k = 0; k < localMusic.length; k++) {
                        albumSimilarityArray.put(String.valueOf(k), StringSimilarityCompare.similarityRatio(songAlbum.toLowerCase(), localMusic[k][2].toLowerCase()));
                    }
                    maxValue = MapSort.getMaxValue(albumSimilarityArray); //获取键值对表中相似度的最大值所在的键值对
                    double songAlbumMaxSimilarity = maxValue.getValue(); //获取相似度的最大值
                    String songAlbumMaxKey = maxValue.getKey(); //获取相似度的最大值对应的专辑名

//                                if (songAlbumMaxSimilarity >= similarity) {
                    if (songNameMaxSimilarity >= similaritySame && songArtistMaxSimilarity >= similaritySame && songAlbumMaxSimilarity >= similaritySame) {
//                                && songNameMaxKey.equals(songArtistMaxKey) && songNameMaxKey.equals(songAlbumMaxKey)) {
                        //歌曲名、歌手名、专辑名均匹配成功
                        Logger.success("歌曲《" + songName + "》匹配成功！歌手名：" + songArtist + "，专辑名：" + songAlbum);
                        String[] header = {"类型  ", "本地音乐", "QQ音乐"};
                        String[][] data = {{"歌名：", localMusic[Integer.parseInt(songNameMaxKey)][0], songName}, {"歌手：", localMusic[Integer.parseInt(songNameMaxKey)][1], songArtist}, {"专辑：", localMusic[Integer.parseInt(songNameMaxKey)][2], songAlbum}};
                        TablePrinter.printTable(header, data, "匹配详情");
                        matched = true;
                        successNum++;
                        fileWriter.write(localMusic[Integer.parseInt(songNameMaxKey)][3] + "\n");
                        fileWriter.close();
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
                        Logger.warning("歌曲《" + songName + "》匹配失败！歌手名：" + songArtist + "，专辑名：" + songAlbum);
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
                        "======歌单" + (i + 1) + " 【" + playListName.get(i) + "】匹配完成，剩余" + (selectedPlayListId.size() - 1) + "个歌单======");
                if ((selectedPlayListId.size() - 1) != 0) {
                    Logger.info("3秒后继续匹配下一歌单");
                    System.out.print("3 ");
                    Sleep.start(1000);
                    System.out.print("2 ");
                    Sleep.start(1000);
                    System.out.print("1");
                    Sleep.start(1000);
                } else
                    System.out.println();
            }
            Logger.success("QQ音乐所有歌单匹配完成，返回主菜单\n");
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 释放资源
     */
    private void afterwards() {
        database.closeConnection(conn);
    }
}
