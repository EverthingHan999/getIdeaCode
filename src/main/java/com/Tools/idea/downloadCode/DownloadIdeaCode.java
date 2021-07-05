package com.Tools.idea.downloadCode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DownloadIdeaCode {
    private static String url = "http://idea.medeming.com/a/jihuoma1.zip";
    private static String code = "";
    private static String filePath = getPath() + "IDEA专用激活码.zip";
    private static String fileName = "2018.2";
    /**
     * 1、下载zip文件
     * 2、提取zip中的文件中的code
     * 3、将激活码复制到系统剪贴板中
     */

    //下载激活码文件
    private static Boolean downloadFile(){
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[10240];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1){
                fos.write(buffer,0,ch);
            }
            is.close();
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Boolean getIdeaCodeFile(){
        try {
            ZipFile zipFile = new ZipFile(new File(filePath), Charset.forName("GBK"));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()){
                ZipEntry zipEntry = entries.nextElement();
                String name = zipEntry.getName();
                if (name.contains(fileName)){
                    InputStream is = zipFile.getInputStream(zipEntry);
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while (null != (line = br.readLine())){
                        code += line;
                    }
                    br.close();
                    isr.close();
                    is.close();
                    zipFile.close();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        Boolean isDownloadFile = downloadFile();
        if (isDownloadFile){
            Boolean isIdeaCodeFile = getIdeaCodeFile();
            if (isIdeaCodeFile) {
                new File(filePath).delete();
                StringSelection selection = new StringSelection(code);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                JOptionPane.showMessageDialog(null, "获取激活码成功，请直接ctrl+v食用!", "提示", JOptionPane.YES_OPTION);
            } else {
                JOptionPane.showMessageDialog(null, "文件名被改了!", "警告", JOptionPane.YES_OPTION);
            }
        }
    }
    //获取当前jar包路径
    public static String getPath()
    {
        String path = DownloadIdeaCode.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if(System.getProperty("os.name").contains("dows"))
        {
            path = path.substring(1,path.length());
        }
        if(path.contains("jar"))
        {
            path = path.substring(0,path.lastIndexOf("."));
            return path.substring(0,path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }
}
