/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import com.xiaoyu.simplenote.common.utils.StringUtil;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 图片上传
 * 
 * @author xiaoyu 2016年3月19日
 */
public class ImgUtils {

    private final static Logger logger = LoggerFactory.getLogger(ImgUtils.class);

    private static final long App_Id = 1253813687;
    private static final String Secret_Id = "AKIDMdrzwiXi6KSVYtD86wd9UdlkW6Ui2aFD";
    private static final String Secret_Key = "2ta5QZXJuJXZb2bkmdPNI1d3GI8mHahh";

    public static String upload(MultipartFile img) throws IllegalStateException, IOException {
        final ResourceBundle bundle = ResourceBundle.getBundle("application");
        final String imagesDir = bundle.getString("img.imagesDir");
        final String disk = bundle.getString("img.disk");
        String realPath = disk + imagesDir;
        final File file = new File(realPath);
        // 判断文件夹是否存在
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        // 新文件名
        final String extName = img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf("."));
        final String newImgName = Long.toString(System.currentTimeMillis()) + new Random().nextInt(1000000) + extName;

        // 将图片保存到指定文件夹
        realPath = realPath + "/" + newImgName;

        // 引用spring官方code
        final File newFile = new File(realPath);
        if (!img.isEmpty()) {
            final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
            FileCopyUtils.copy(img.getInputStream(), stream);
            stream.close();
        }
        logger.info(newFile.getAbsolutePath());
        return ImgUtils.getPath(imagesDir + newImgName);
    }

    private static String getPath(String path) {
        String p = StringUtil.replace(path, "\\", "/");
        p = StringUtil.join(StringUtil.split(p, "/"), "/");
        if (!StringUtil.startsWithAny(p, "/") && StringUtil.startsWithAny(path, "\\", "/")) {
            p += "/";
        }
        if (!StringUtil.endsWithAny(p, "/") && StringUtil.endsWithAny(path, "\\", "/")) {
            p = p + "/";
        }
        // 去掉url最尾的"/"
        while (p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        return p;
    }

    /**
     * servlet3.0 part上传法
     * 
     * @author xiaoyu
     * @param part
     * @return
     * @time 2016年3月29日下午2:54:23
     */
    public static String saveImg(Part part) {
        // 限制只要图片
        final String suffix = part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
        if (!suffix.matches("[.](jpg|jpeg|png)$")) {
            return null;
        }
        final ResourceBundle bundle = ResourceBundle.getBundle("application");
        final String imagesDir = bundle.getString("img.imagesDir");
        final String disk = bundle.getString("img.disk");
        final String thumbnailPath = bundle.getString("img.thumbnailPath");
        String realPath = disk + imagesDir;
        final File file = new File(realPath);
        // 判断文件夹是否存在
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        final String newImgName = Long.toString(System.currentTimeMillis()) + new Random().nextInt(1000000) + suffix;
        // 将图片保存到指定文件夹
        final String thumbnail = disk + imagesDir + thumbnailPath;
        final File thumbnailfile = new File(thumbnail);
        // 判断文件夹是否存在
        if (!thumbnailfile.exists() && !thumbnailfile.isDirectory()) {
            thumbnailfile.mkdirs();
        }
        final File newthumbnail = new File(thumbnailfile + "/" + newImgName);
        realPath = realPath + newImgName;
        try {
            // 输出缩略图
            Thumbnails.of(part.getInputStream()).size(100, 100).toFile(newthumbnail);
            // 输入图片
            part.write(realPath);
        } catch (final IOException e) {
            logger.error(e.toString());
        }
        return ImgUtils.getPath(imagesDir + newImgName);
    }

    /**
     * 图片预上传到缓存中
     * 
     * @author xiaoyu
     * @param p
     * @return
     * @time 2016年3月31日下午6:37:51 TODO 待考虑
     */
    public static String saveImgEhcache(Part part) {
        final ResourceBundle bundle = ResourceBundle.getBundle("application");
        final String imagesDir = bundle.getString("img.imagesDir");
        final String disk = bundle.getString("img.disk");
        String realPath = disk + imagesDir;

        final String newImgName = Long.toString(System.currentTimeMillis()) + new Random().nextInt(1000000) + ".png";
        // 将图片保存到指定文件夹
        realPath = realPath + "/" + newImgName;
        try {
            part.write(realPath);
            // InputStream in = part.getInputStream();
        } catch (final IOException e) {
            logger.error(e.toString());
        }
        return ImgUtils.getPath(imagesDir + newImgName);
    }

    public static String saveImgToTencentOss(Part part) {
        // 限制只要图片
        final String suffix = part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
        if (!suffix.matches("[.](jpg|jpeg|png)$")) {
            return null;
        }
        final long appId = App_Id;
        final String secretId = Secret_Id;
        final String secretKey = Secret_Key;
        // 设置要操作的bucket
        final String bucketName = "xiaoyu1";
        // 初始化秘钥信息
        final Credentials cred = new Credentials(appId, secretId, secretKey);

        // 初始化客户端配置
        final ClientConfig clientConfig = new ClientConfig();
        // 设置bucket所在的区域，比如华南园区：gz； 华北园区：tj；华东园区：sh ；
        clientConfig.setRegion("tj");
        // 初始化cosClient
        final COSClient client = new COSClient(clientConfig, cred);
        final String newImgName = new Random().nextInt(1_000) + Long.toString(System.currentTimeMillis())
                + new Random().nextInt(1_000) + suffix;
        byte[] contentBuffer = null;
        try {
            final InputStream stream = part.getInputStream();
            contentBuffer = new byte[stream.available()];
            stream.read(contentBuffer);
        } catch (final IOException e) {
            logger.error(e.toString());
        }
        final UploadFileRequest f = new UploadFileRequest(bucketName, "/" + newImgName, contentBuffer);
        return client.uploadSingleFile(f);
    }

    @SuppressWarnings("unchecked")
    public static String saveImgToTencentOss(byte[] bytes) {
        final long appId = App_Id;
        final String secretId = Secret_Id;
        final String secretKey = Secret_Key;
        // 设置要操作的bucket
        final String bucketName = "xiaoyu1";
        // 初始化秘钥信息
        final Credentials cred = new Credentials(appId, secretId, secretKey);
        // 初始化客户端配置
        final ClientConfig clientConfig = new ClientConfig();
        // 设置bucket所在的区域，比如华南园区：gz； 华北园区：tj；华东园区：sh ；
        clientConfig.setRegion("tj");
        // 初始化cosClient
        final COSClient client = new COSClient(clientConfig, cred);
        final String newImgName = new Random().nextInt(1_000) + Long.toString(System.currentTimeMillis())
                + new Random().nextInt(1_000) + ".png";
        final UploadFileRequest f = new UploadFileRequest(bucketName, "/" + newImgName, bytes);

        Map<String, Object> map = (Map<String, Object>) JSON.parse(client.uploadSingleFile(f));
        String path = null;
        if (map.get("code").equals(0)) {
            Map<String, String> urlMap = (Map<String, String>) map.get("data");
            path = urlMap.get("source_url");
            return path;
        }
        return "";
    }
}
