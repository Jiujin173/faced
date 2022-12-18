package com.example.faced.controller;

import com.example.faced.util.ConnectUtil;
import com.example.faced.util.FFmpegTest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;


@Controller
public class OutputController {

    @RequestMapping("/output")
    public String output(HttpServletRequest req, HttpServletResponse res, @RequestParam("file") MultipartFile file) throws Exception {
        //保存到本地图片视频
        String folderPath = "D:/ab";
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(file.getOriginalFilename());
        if (file.getOriginalFilename().contains("mp4")){
            type = "video";
        }
        String remoteFilePath = "/home/jiujin/Downloads/faced/input/";
        String name = file.getOriginalFilename();
        FileOutputStream fileOutputStream = new FileOutputStream(folderPath + "/input/" + name);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();

        System.out.println("1--------------");

        //本地到虚拟机
        ConnectUtil connectUtil = new ConnectUtil("192.168.107.128", "jiujin", "123456", "utf-8");
        connectUtil.uploadFile(folderPath + "/input/" + name, remoteFilePath);
        String result = connectUtil.exec("python3 /home/jiujin/Downloads/faced/face --input " + remoteFilePath + name + " --save");
        if (type.contains("image")) {
            connectUtil.downloadFile("/home/jiujin/output.png", folderPath + "/output/");
        } else if (type.contains("video")) {
            connectUtil.downloadFile("/home/jiujin/output.avi", folderPath + "/output/");
        } else {
            System.out.println("错误");
        }

        System.out.println("2----------------");


        //虚拟机处理完到本地
        FileInputStream fileInputStream = null;

        if (type.contains("image")) {
            fileInputStream = new FileInputStream(folderPath + "/output/output.png");
        } else if (type.contains("video")) {
            FFmpegTest fFmpegTest = new FFmpegTest(folderPath + "/output/output.avi", folderPath + "/output/");
            System.out.println(folderPath + "/output/output.mp4");
            fileInputStream = new FileInputStream(folderPath + "/output/output.mp4");
        } else {
            System.out.println("错误");
        }


        System.out.println("3--------");


        //处理数据到前端展示

        System.out.println("4----------------");

        if (type.contains("image")) {
            int size = fileInputStream.available(); // 得到文件大小
            byte data[] = new byte[size];
            fileInputStream.read(data); // 读数据
            fileInputStream.close();
            fileInputStream = null;
            OutputStream outputStream = null;
            outputStream = res.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
            outputStream = null;
            res.setContentType("image/png");
        } else if (type.contains("video")) {
            req.getSession().setAttribute("filePath", folderPath + "/output/output.mp4");
            res.sendRedirect("/videos");
        } else {
            System.out.println("错误");
        }
        return null;
    }


}



