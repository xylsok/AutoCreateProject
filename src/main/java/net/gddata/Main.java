package net.gddata;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by knix on 17/4/8.
 */
public class Main {

    public static void main(String[] args) {


        Properties properties = new Properties();
        File file = new File("info.properties");
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);

            try {
                properties.load(fis);
                String proPath = properties.getProperty("project.path");
                String proName = properties.getProperty("project.name");
                String proMavenDir = properties.getProperty("project.mavendir");
                String proDir = properties.getProperty("project.dir");

                List<String> list = new ArrayList();
                for (int i = 1; i <= 10; i++) {
                    String dir = properties.getProperty("project.subdir" + i);
                    if (!"".equals(dir) && null != dir) {
                        list.add(dir);
                    }
                }

                if ("".equals(proPath)) {
                    System.out.println("Error:存放路径不能为空");
                    return;
                }
                if ("".equals(proName)) {
                    System.out.println("Error:项目名不能为空");
                    return;
                }
                if ("".equals(proMavenDir)) {
                    System.out.println("Error:Maven固定目录不能为空");
                    return;
                }
                if ("".equals(proDir)) {
                    System.out.println("Error:包路径不能为空");
                    return;
                }

                String projectPath = proPath + proName;//项目路径


                File file1 = new File(projectPath);

                boolean isCreate = true;
                if (file1.exists()) {
                    isCreate = false;
                    Scanner sc = new Scanner(System.in);
                    System.out.println("该路径存在相同项目名称: " + proName + " 确定要删除重建吗？（y/n）");
                    String value = sc.nextLine();
                    if ("n".equals(value.toLowerCase())) {
                        System.out.println("程序已终止");
                        return;
                    } else if ("y".equals(value.toLowerCase())) {
                        isCreate = true;
                    } else if (!"y".equals(value.toLowerCase()) && !"n".equals(value.toLowerCase())) {
                        System.out.println("输入无效程序退出");
                        return;
                    }
                }
                if (isCreate) {
                    deleteOldProjectDir(file1.getPath());
                    System.out.println("正在创建: " + file1.getPath());
                    if (!file1.mkdir()) {
                        System.out.println("目录创建失败");
                    }
                    String mavenPath = projectPath + "/" + proMavenDir;
                    File file22 = new File(mavenPath);
                    System.out.println("正在创建: " + proMavenDir);
                    file22.mkdirs();

                    String javaDir = mavenPath + "java";
                    String resourcesDir = mavenPath + "resources";
                    System.out.println("正在创建: " + "java");
                    new File(javaDir).mkdir();
                    System.out.println("正在创建: " + "resources");
                    new File(resourcesDir).mkdir();

                    String newProDir = proDir.replaceAll("\\.", "/");
                    String dir = javaDir + "/" + newProDir;
                    System.out.println("正在创建: " + newProDir);
                    for (String s : list) {
                        System.out.println("正在创建: " + newProDir + "/" + s);
                        new File(dir + "/" + s).mkdirs();
                    }

                    //开始copy 文件
                    String projectPath1 = getProjectPath();

                    File file2 = new File(projectPath1 + "/application.properties");
                    if (!file2.exists() || !file2.isFile()) {
                        System.out.println("Error: application.properties不存在");
                    }
                    File file3 = new File(projectPath + "/" + proMavenDir + "resources/application.properties");


                    try {
                        System.out.println("正在拷贝: application.properties ");
                        copyFile(file2.getPath(), file3.getPath());

                        System.out.println("正在拷贝: pom.xml  ");
                        copyFile(projectPath1 + "/pom.xml", projectPath + "/pom.xml");

                        System.out.println("正在拷贝: JooqDao.java");

                        File dao = new File(projectPath + "/" + proMavenDir + "java/" + newProDir + "/dao");
                        if (!dao.exists() || !dao.isDirectory()) {
                            System.out.println("没找到dao目录，不执行copy JooqDao.java");
                        } else {
                            copyFile(projectPath1 + "/JooqDao.java", projectPath + "/" + proMavenDir + "java/" + newProDir + "/dao/JooqDao.java");
                        }

                        System.out.println("正在拷贝: README.md  ");
                        copyFile(projectPath1 + "/README.md", projectPath + "/README.md");

                        System.out.println("正在拷贝: .gitignore ");
                        copyFile(projectPath1 + "/.gitignore", projectPath + "/.gitignore");

                        System.out.println("正在拷贝: logback.xml  ");
                        copyFile(projectPath1 + "/logback.xml", projectPath + "/" + proMavenDir + "resources/logback.xml");

                        System.out.println("正在拷贝: Main.java");
                        copyFile(projectPath1 + "/Main.java", projectPath + "/" + proMavenDir + "java/" + newProDir + "/Main.java");

                        System.out.println("正在拷贝: Swagger2.java");
                        copyFile(projectPath1 + "/Swagger2.java", projectPath + "/" + proMavenDir + "java/" + newProDir +"/Swagger2.java");

                        System.out.println("完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


                fis.close();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        }

    }


    public static void copyFile(String srcFileName, String destFileName) throws Exception {
        File srcFile = new File(srcFileName);


        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件及文件夹
     *
     * @param path
     * @return
     */
    public static String deleteOldProjectDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File subFile : files) {
                    if (subFile.isDirectory())
                        deleteOldProjectDir(subFile.getPath());
                    else
                        subFile.delete();
                }
            }
            file.delete();
        } else {
            return "目录不存在";
        }
        return "SUCCESS";
    }


    /**
     * 获取项目所在路径(包括jar)
     *
     * @return
     */
    public static String getProjectPath() {

        java.net.URL url = Main.class.getProtectionDomain().getCodeSource()
                .getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar"))
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        java.io.File file = new java.io.File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }


}
