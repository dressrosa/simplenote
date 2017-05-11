/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.ResourceBundle;

import javax.servlet.http.Part;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 图片上传
 * 
 * @author xiaoyu 2016年3月19日
 */
public class ImgUtils {

	public static String upload(MultipartFile img) throws IllegalStateException, IOException {
		ResourceBundle bundle = ResourceBundle.getBundle("application");
		String imagesDir = bundle.getString("img.imagesDir");
		String disk = bundle.getString("img.disk");
		String realPath = disk + imagesDir;
		File file = new File(realPath);
		// 判断文件夹是否存在
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		// 新文件名
		String extName = img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf("."));
		String newImgName = Long.toString(System.currentTimeMillis()) + new Random().nextInt(1000000) + extName;

		// 将图片保存到指定文件夹
		realPath = realPath + "/" + newImgName;

		// 引用spring官方code
		File newFile = new File(realPath);
		if (!img.isEmpty()) {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
			FileCopyUtils.copy(img.getInputStream(), stream);
			stream.close();
		}
		System.out.println(newFile.getAbsolutePath());
		return getPath(imagesDir + newImgName);
	}

	private static String getPath(String path) {
		String p = StringUtils.replace(path, "\\", "/");
		p = StringUtils.join(StringUtils.split(p, "/"), "/");
		if (!StringUtils.startsWithAny(p, "/") && StringUtils.startsWithAny(path, "\\", "/")) {
			p += "/";
		}
		if (!StringUtils.endsWithAny(p, "/") && StringUtils.endsWithAny(path, "\\", "/")) {
			p = p + "/";
		}
		while (p.endsWith("/")) {// 去掉url最尾的"/"
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
		String suffix = part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
		if (!suffix.matches("[.](jpg|jpeg|png)$")) {
			return null;
		}
		ResourceBundle bundle = ResourceBundle.getBundle("application");
		String imagesDir = bundle.getString("img.imagesDir");
		String disk = bundle.getString("img.disk");
		String thumbnailPath = bundle.getString("img.thumbnailPath");
		String realPath = disk + imagesDir;
		File file = new File(realPath);
		// 判断文件夹是否存在
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		String newImgName = Long.toString(System.currentTimeMillis()) + new Random().nextInt(1000000) + suffix;
		// 将图片保存到指定文件夹
		String thumbnail = disk + imagesDir + thumbnailPath;
		File thumbnailfile = new File(thumbnail);
		// 判断文件夹是否存在
		if (!thumbnailfile.exists() && !thumbnailfile.isDirectory()) {
			thumbnailfile.mkdirs();
		}
		File newthumbnail = new File(thumbnailfile + "/" + newImgName);
		realPath = realPath + newImgName;
		try {
			// 输出缩略图
			Thumbnails.of(part.getInputStream()).size(100, 100).toFile(newthumbnail);
			// 输入图片
			part.write(realPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getPath(imagesDir + newImgName);
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
		ResourceBundle bundle = ResourceBundle.getBundle("application");
		String imagesDir = bundle.getString("img.imagesDir");
		String disk = bundle.getString("img.disk");
		String realPath = disk + imagesDir;

		String newImgName = Long.toString(System.currentTimeMillis()) + new Random().nextInt(1000000) + ".png";
		// 将图片保存到指定文件夹
		realPath = realPath + "/" + newImgName;
		try {
			part.write(realPath);
			// InputStream in = part.getInputStream();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getPath(imagesDir + newImgName);
	}
}
