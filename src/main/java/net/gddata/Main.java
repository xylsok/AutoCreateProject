package net.gddata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        File file = new File("/Users/zhangzf/Codes/javaProject/AutoCreateProject/src/main/resources/info.properties");
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
                if (file1.exists()) {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("该路径存在相同项目名称: " + proName + " 确定要删除重建吗？（y/n）");
                    String value = sc.nextLine();
                    if ("n".equals(value.toLowerCase())) {
                        System.out.println("程序已终止");
                        return;
                    } else if ("y".equals(value.toLowerCase())) {
                        deleteOldProjectDir(file1.getPath());
                        if (!file1.mkdir()) {
                            System.out.println("目录创建失败");
                        }
                        String mavenPath = projectPath + "/" + proMavenDir;
                        File file2 = new File(mavenPath);
                        file2.mkdirs();

                        String javaDir = mavenPath + "java";
                        String resourcesDir = mavenPath + "resources";

                        new File(javaDir).mkdir();
                        new File(resourcesDir).mkdir();

                        String newProDir = proDir.replaceAll("\\.", "/");
                        String dir = javaDir + "/" + newProDir;

                        for (String s : list) {
                            new File(dir + "/" + s).mkdirs();
                        }
                    }
                    if (!"y".equals(value.toLowerCase()) && !"n".equals(value.toLowerCase())) {
                        System.out.println("输入无效程序退出");
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
}
