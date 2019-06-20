package com.github.dge1992.mongo.controller;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 小眼睛带鱼
 * @Description
 * @Date 2019/6/20
 **/
@RequestMapping("/gridFs")
@RestController
public class GridFsController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    static final Integer STEAM_BYTE_LENGTH = 1024;

    /**
     * @author dongganen
     * @date 2019/6/20
     * @desc: 上传文件
     */
    @RequestMapping("/uploadFile")
    public Object uploadFile() {
        Resource file = new FileSystemResource("E:\\pdf\\机构改革后的单位简称和序列.pdf");
        try {
            gridFsTemplate.store(file.getInputStream(),
                    file.getFilename().substring(0, file.getFilename().lastIndexOf(".")),
                    file.getFilename().substring(file.getFilename().lastIndexOf(".")));
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
        return "上传成功";
    }

    /**
     * @author dongganen
     * @date 2019/6/20
     * @desc: 下载文件
     */
    @RequestMapping("/downloadFile")
    public void downloadFile(HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(new GridFsCriteria("_id").is("5d0b2feb823f95323c2df53d")));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //转换成资源
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
//        //创建临时文件
//        File temp = File.createTempFile(gridFsResource.getFilename(), gridFsResource.getContentType());
//        temp.deleteOnExit();
//        OutputStream outputStream = new FileOutputStream(temp);
//        //gridFsResource写入到临时文件
//        try {
//            outputStream.write(IOUtils.toByteArray(gridFsResource.getInputStream()));
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            outputStream.close();
//        }
        responseBody(gridFsResource, response);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * @author dongganen
     * @date 2019/6/20
     * @desc: 删除文件
     */
    @RequestMapping("/deleteFile")
    public Object deleteFile(){
        gridFsTemplate.delete(new Query(new GridFsCriteria("_id").is("5d0b2fb4823f9503c42f1042")));
        return "删除成功";
    }

    /**
     * @author dongganen
     * @date 2019/6/20
     * @desc: 获取文件列表
     */
    @RequestMapping("/selectFileList")
    public Object selectFileList(){
        GridFSFindIterable gridFSFiles = gridFsTemplate.find(new Query());
        List<GridFSFile> list = new ArrayList<>();
        MongoCursor<GridFSFile> iterator = gridFSFiles.iterator();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * 处理文件下载response
     *
     * @param gridFsResource
     * @param response
     * @throws UnsupportedEncodingException
     */
    public static void responseBody(GridFsResource gridFsResource, HttpServletResponse response) throws IOException {
        if (gridFsResource != null) {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(gridFsResource.getFilename() + gridFsResource.getContentType(), "UTF-8"));
            responseFile(response, gridFsResource.getInputStream());
        }
    }

    /**
     * 处理文件下载response
     *
     * @param response
     * @param is
     */
    public static void responseFile(HttpServletResponse response, InputStream is) {
        try (OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[STEAM_BYTE_LENGTH];
            while (is.read(buffer) != -1) {
                os.write(buffer);
            }
            os.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
