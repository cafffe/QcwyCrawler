package com.kmgh;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;


/**
 * 文件操作工具
 * 
 * @author 001375
 * 
 */
public class FileUtil {

	private static final long serialVersionUID = 2199809678678449107L;
	/**
	 * 判断文件夹是否存在，不存在，级联创建
	 * 
	 * @author 001375
	 * @param path
	 * @return void
	 */
	public static void mkDir(String path) {
		File folder = new File(path);
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
	}

	/**
	 * 判断文件是否删除成功
	 * 
	 * @param path
	 * @param request
	 * @return boolean
	 */
	public static boolean rmFile(String path) {
		System.out.println(path);
		Boolean isSuccess = false;
		File file = new File(path);
		if (file.exists()) {
			isSuccess = file.delete();
		}
		return isSuccess;
	}

	/**
	 * 通过时间戳和随机码生成文件名�?
	 * 
	 * @param fileName
	 * @return
	 */

	public static String chFileName(String fileName) {
		java.util.Random r = new java.util.Random();
		String[] ex = fileName.split("\\.");
		// 生成10000�?99999之间的随机数
		int ram = r.nextInt(90000) + 10000;
		fileName = String.valueOf(System.currentTimeMillis() + "" + ram);
		fileName = fileName + "." + ex[ex.length - 1];
		return fileName.toLowerCase();
	}


	/**
	 * 接受表单上传文件，返回表单信�?
	 * 
	 * @param path
	 * @param request
	 * @return Map<String,String>
	 */
	public static Map<String, String> UpfileFromFormIstream(String path, HttpServletRequest request) {
		Map<String, String> caseMap = new HashMap<String, String>();
		String filePath = null;
		try {
			// 首先，判断是否multipart编码类型
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					// 得到表单域的名称
					String upfile = item.getFieldName();
					// 得到表单域的值（这是�?个输入流�?
					InputStream stream = item.openStream();
					// 如果是普通表单域
					if (item.isFormField()) {
						String value = Streams.asString(stream, request.getCharacterEncoding());
						caseMap.put(upfile, value);
					} else { // 如果是文�?
						if (stream.available() != 0) {// 如果文件域没有�?�择文件，则忽略处理
							String filename = item.getName(); // 得到上传的文件名�?
							if (filename != null) {
								// 因为在IE下面，上传的文件还包含有此文件在客户端机器的路径
								// �?以，要把这个路径去掉，只取文件名
								filename = FilenameUtils.getName(filename);
							}
							// 判断目录是否存在，不在新建目�?
							mkDir(path);
							// 将上传文件的输入流输出到磁盘的文件上
							caseMap.put("filename", filename);
							filename = chFileName(filename);
							Streams.copy(stream, new FileOutputStream(path + filename), true);
							filePath = path + filename;
							String[] ex = filename.split("\\.");
							String exe = ex[ex.length - 1].toLowerCase();
							if (exe.equals("jpg") || exe.equals("png") || exe.equals("gif") || exe.equals("bmp")) {
								caseMap.put("title_pic", filename);
							} else {
								caseMap.put("file_url", filename);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caseMap;
	}

	public static Map<String, Object> UpMultiFileFromStream(String path, HttpServletRequest request) {
		Map<String, Object> caseMap = new HashMap<String, Object>();
		List<String> pics = new ArrayList<String>();
		try {
			// 首先，判断是否multipart编码类型
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					// 得到表单域的名称
					String upfile = item.getFieldName();
					// 得到表单域的值（这是�?个输入流�?
					InputStream stream = item.openStream();
					// 如果是普通表单域
					if (item.isFormField()) {
						String value = Streams.asString(stream, request.getCharacterEncoding());
						caseMap.put(upfile, value);
					} else { // 如果是文�?
						if (stream.available() != 0) {// 如果文件域没有�?�择文件，则忽略处理
							String filename = item.getName(); // 得到上传的文件名�?
							if (filename != null) {
								// 因为在IE下面，上传的文件还包含有此文件在客户端机器的路径
								// �?以，要把这个路径去掉，只取文件名
								filename = FilenameUtils.getName(filename);
							}
							// 判断目录是否存在，不在新建目�?
							mkDir(path);
							// 将上传文件的输入流输出到磁盘的文件上
							caseMap.put("filename", filename);
							filename = chFileName(filename);
							Streams.copy(stream, new FileOutputStream(path + filename), true);
							String[] ex = filename.split("\\.");
							String exe = ex[ex.length - 1].toLowerCase();
							if (exe.equals("jpg") || exe.equals("png") || exe.equals("gif") || exe.equals("bmp")) {
								caseMap.put("title_pic", filename);
								pics.add(filename);
							} else {
								caseMap.put("file_url", filename);
							}
						}
					}
				}// end iter
				caseMap.put("pics", pics);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caseMap;
	}


	/**
	 * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功�?)
	 * 
	 * @param res
	 *            原字符串
	 * @param filePath
	 *            文件路径
	 * @return 成功标记
	 */
	public static void string2File(String content, String filePath, String fileName) {
		CreateFile(filePath, fileName);
		FileWriter writer = null;
		try {
			// 打开�?个写文件器，构�?�函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(filePath + fileName, false);
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将字符串以UTF-8写入指定文件
	 * 
	 * @param res
	 *            字符�?
	 * @param filePath
	 *            文件路径
	 * @return 成功标记
	 */
	public static void writeToFile(String fileContent, String filePath, String fileName) {
		// 若目录不存在,创建文件目录
		CreateDir(filePath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fileContent = new String(fileContent.getBytes("UTF-8")).trim();
			fw = new FileWriter(filePath + fileName);
			bw = new BufferedWriter(fw);
			bw.write("<p><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head></p>"
					+ "\n" + fileContent);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将字符串以UTF-8写入指定文件
	 * 
	 * @param res
	 *            字符�?
	 * @param filePath
	 *            文件路径
	 * @return 成功标记
	 */
	public static void writeStr2File(String fileContent, String filePath, String fileName) {
		// 若目录不存在,创建文件目录
		CreateDir(filePath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fileContent = new String(fileContent.getBytes("UTF-8")).trim();
			fw = new FileWriter(filePath + fileName);
			bw = new BufferedWriter(fw);
			bw.write(fileContent);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件目录创建
	 * 
	 * @param filePath
	 *            文件路径（可以包含文件名称）
	 * @return
	 */
	public static boolean CreateDir(String filePath) {
		// 返回是否创建文件目录成功
		String fileFullPath = filePath;

		try {
			File FolderPath = new File(fileFullPath);
			if (!FolderPath.exists()) {
				FolderPath.mkdirs();
			}
			FolderPath = null;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 文件创建
	 * 
	 * @param filePath
	 *            文件路径（包含文件名称）
	 * @param fileContent
	 *            文件的内�?
	 * @param fileEncoding
	 *            文件的编�?
	 * @return
	 */
	public static boolean CreateFile(String filePath, String fileContent) {
		// 创建文件目录
		boolean fileDir = CreateDir(filePath);

		boolean blResult = false;

		if (fileDir) {
			File file = new java.io.File(filePath + fileContent);
			if (!file.exists()) {
				// 创建文件
				// OutputStreamWriter out = null;
				File FolderPath = new File(filePath + fileContent);
				try {
					FolderPath.createNewFile();
					blResult = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			blResult = false;
		}
		return blResult;
	}

	/**
	 * <�?句话功能�?�?> 拷贝文件 <功能详细描述>
	 * 
	 * @see [类�?�类#方法、类#成员]
	 */
	public static void copyFile(String source, String dest) {
		try {
			File in = new File(source);
			File out = new File(dest);
			FileInputStream inFile = new FileInputStream(in);
			FileOutputStream outFile = new FileOutputStream(out);
			byte[] buffer = new byte[1024];
			int i = 0;
			while ((i = inFile.read(buffer)) != -1) {
				outFile.write(buffer, 0, i);
			}// end while
			inFile.close();
			outFile.close();
		}// end try
		catch (Exception e) {
			e.printStackTrace();

		}// end catch
	}// end copyFile

	/**
	 * <�?句话功能�?�?> 处理目录 <功能详细描述>
	 * 
	 * @see [类�?�类#方法、类#成员]
	 */
	public static void copyDict(String source, String dest) {
		String source1;
		String dest1;

		File[] file = (new File(source)).listFiles();
		if (file != null && file.length > 0) {
			for (int i = 0; i < file.length; i++)
				if (file[i].isFile()) {
					source1 = source + "/" + file[i].getName();
					dest1 = dest + "/" + file[i].getName();
					copyFile(source1, dest1);
				}// end if
			for (int i = 0; i < file.length; i++)
				if (file[i].isDirectory()) {
					source1 = source + "/" + file[i].getName();
					dest1 = dest + "/" + file[i].getName();
					File dest2 = new File(dest1);
					dest2.mkdir();
					copyDict(source1, dest1);
				}// end if
		}

	}// end copyDict

	/**
	 * <�?句话功能�?�?> 删除目录下所有文�? <功能详细描述>
	 * 
	 * @see [类�?�类#方法、类#成员]
	 */
	public static void delFiles(String filepath, boolean isDelDir) throws IOException {
		File f = new File(filepath);// 定义文件路径
		if (f.exists() && f.isDirectory()) {// 判断是文件还是目�?
			File delFile[] = f.listFiles();
			int fileNum = delFile.length;
			if (0 != fileNum) {// 若目录下有文件则删除全部文件
				for (int i = 0; i < fileNum; i++) {
					delFile[i].delete();// 删除文件
				}
			}
			// 删除空目�?
			if (isDelDir) {
				f.delete();
			}
		} else if (f.exists() && f.isFile()) {
			f.delete();
		}
	}

	/**
	 * <�?句话功能�?�?> 删除文件 <功能详细描述>
	 * 
	 * @see [类�?�类#方法、类#成员]
	 */
	public static void delFile(String filepath) throws IOException {
		File file = new File(filepath);
		if (file.exists()) {
			file.delete();

		}
	}

	/**
	 * 
	 * @param files
	 *            文件列表
	 * @throws IOException
	 *             异常
	 */
	public static void delFiles(List<File> files) throws IOException {
		for (File file : files) {
			if (file.exists()) {
				file.delete();

			}
		}
	}

	/**
	 * 清空文件�?
	 * 
	 * @param dir
	 */
	public static void deleteFolder(String dir) {
		File delfolder = new File(dir);
		File oldFile[] = delfolder.listFiles();
		try {
			for (int i = 0; i < oldFile.length; i++) {
				if (oldFile[i].isDirectory()) {
					deleteFolder(dir + oldFile[i].getName() + "//"); // 递归清空子文件夹
				}
				oldFile[i].delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */

	public static String UpSinglefileFromFormIstream(HttpServletRequest request) {
		String filePath = null;
		try {
			// 首先，判断是否multipart编码类型
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					// 得到表单域的名称
					String upfile = item.getFieldName();
					// 得到表单域的值（这是�?个输入流�?
					InputStream stream = item.openStream();
					// 如果是普通表单域
					if (item.isFormField()) {
						String value = Streams.asString(stream, request.getCharacterEncoding());
					} else { // 如果是文�?
						if (stream.available() != 0) {// 如果文件域没有�?�择文件，则忽略处理
							String filename = item.getName(); // 得到上传的文件名�?
							if (filename != null) {
								// 因为在IE下面，上传的文件还包含有此文件在客户端机器的路径
								// �?以，要把这个路径去掉，只取文件名
								filename = FilenameUtils.getName(filename);
							}
							// 判断目录是否存在，不在新建目�?
							String path = request.getRealPath("/backend/tempfile/");
							// 将上传文件的输入流输出到磁盘的文件上
							deleteFolder(path);
							mkDir(path);
							filename = chFileName(filename);
							Streams.copy(stream, new FileOutputStream(path + "/" + filename), true);
							filePath = filename;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */

	public static String upFileByswf(String path, HttpServletRequest request) {
		String result = "";
		String fileName = "";
		try {

			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				// 得到表单域的名称
				String upfile = item.getFieldName();
				// 得到表单域的值（这是�?个输入流�?
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					continue;
				} else {
					// MD5 md5 = new MD5();
					String type = item.getName().split("\\.")[1].toLowerCase();// 获取文件类型
					String oldName = item.getName().trim().toLowerCase();
					mkDir(path);
					Streams.copy(stream, new FileOutputStream(path + "/" + oldName), true);
					if (type.equals("jpg") || type.equals("png") || type.equals("gif") || type.equals("bmp")) {
						result = oldName + "," + oldName;
					} else {
						result = oldName + "||" + oldName;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 若文件上传过程异�?,则删除残余文�?
			if (fileName != null && fileName.length() > 0) {
				FileUtil.rmFile(path + "/" + fileName);
			}

		}
		return result;
	}

	public static Map<String, String> upNewsByswf(String path, HttpServletRequest request) {
		// 用Map保存两个种文件名
		Map<String, String> fileNameMaps = new HashMap<String, String>();
		String result = "";
		String fileName = "";
		try {

			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				// 得到表单域的名称
				String upfile = item.getFieldName();
				// 得到表单域的值（这是�?个输入流�?
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					continue;
				} else {
					String type = item.getName().split("\\.")[1].toLowerCase();// 获取文件类型
					String oldName = item.getName().trim().toLowerCase();
					String newName = chFileName(item.getName());
					fileNameMaps.put("filename", oldName);
					fileNameMaps.put("fileMapname", newName);
					fileName = newName;
					mkDir(path);
					Streams.copy(stream, new FileOutputStream(path + "/" + newName), true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 若文件上传过程异�?,则删除残余文�?
			if (fileName != null && fileName.length() > 0) {
				FileUtil.rmFile(path + "/" + fileName);
			}

		}
		return fileNameMaps;
	}

	/**
	 * 获取文件下的文件 按照 名字，路径的键�?�对放到Map�?
	 * 
	 * @param file
	 */

	public static Map<String, String> fileList(File file, Map<String, String> fileMap) {
		// 获取该目录下�?有文件名
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					String[] tempB = f.getAbsolutePath().split("\\" + String.valueOf(File.separatorChar));
					String tempBstr = tempB[tempB.length - 1].toLowerCase().trim();
					if (tempBstr.equals("video") || tempBstr.equals("card") || tempBstr.equals("case")
							|| tempBstr.equals("ebook")) {
						File[] subFiles = (new File(f.getAbsolutePath())).listFiles();

						for (File subF : subFiles) {
							String[] tempArr = subF.getName().split("\\.");
							String ext = tempArr[tempArr.length - 1].toLowerCase().trim();
							if (!ext.equals("db") && !ext.equals("html")) {
								fileMap.put(subF.getName().trim(), subF.getPath());
							}
							fileList(subF, fileMap);
						}
					}
				}
			}
		}
		return fileMap;
	}

	public static String strFilter(String str) {
		str = StringEscapeUtils.escapeHtml(str);
		str = StringEscapeUtils.escapeJava(str);
		str = StringEscapeUtils.escapeSql(str);
		return str;
	}

	public static String unStrFilter(String str) {
		str = StringEscapeUtils.unescapeHtml(str);
		str = StringEscapeUtils.unescapeJava(str);

		return str;
	}

	/**
	 * 得到网页中图片的地址
	 */
	public static String getImgSrc(String htmlStr) {
		String img = "";
		Pattern p_image;
		Matcher m_image;
		String pics = null;

		// String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址

		String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(htmlStr);
		while (m_image.find()) {
			img = img + "," + m_image.group();
			// Matcher m =
			// Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src

			Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);

			if (m.find()) {
				pics = m.group(1);
			}
		}
		return pics;
	}

	public static void writeFile(String serverUrl, String targetPath, String fileName) {
		URL url = null;
		try {
			url = new URL(serverUrl);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStream is = null;
		try {
			is = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStream os = null;
		File f = new File(targetPath);
		f.mkdirs();
		try {
			os = new FileOutputStream(targetPath + "/" + fileName);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			try {
				while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static List<File> getFileList(String filePath) {
		File[] files = (new File(filePath)).listFiles();
		List<File> fileList = new ArrayList<File>();
		if (files != null && files.length > 0) {
			for (File file : files) {
				fileList.add(file);
			}
		}
		return fileList;
	}

	public static void delDir(String filePath) {
		try {
			delAllFile(filePath); // 删除完里面所有内�?
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.out.println("删除文件夹操作出�?");
			e.printStackTrace();

		}

	}

	private static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文�?
				delDir(path + "/" + tempList[i]);// 再删除空文件�?
			}
		}
	}

	public static void download(HttpServletRequest request, HttpServletResponse response, String fileName,
			Boolean delete) throws IOException {

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			response.reset();
			// 告诉客户端允许断点续传多线程连接下载
			// 响应的格式是:
			// Accept-Ranges: bytes
			response.setHeader("Accept-Ranges", "bytes");
			long p = 0;
			long l = 0;

			// File file = new File(fileName);
			URI uri = new URI(fileName);
			File file = new File(uri.toString());
			String name = (file != null ? file.getPath() : null);
			System.out.println("name�?" + file.getAbsolutePath());
			System.out.println("file.getPath():" + file.getPath());
			System.out.println("length():" + file.length());
			System.out.println("getName�?" + file.getName());
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(response.getOutputStream());

			byte[] buff = new byte[2048];
			// 写明要下载的文件的大�?
			l = file.length();

			if (request.getHeader("Range") != null) // 客户端请求的下载的文件块的开始字�?
			{
				// 如果是下载文件的范围而不是全�?,向客户端声明支持并开始文件块下载
				// 要设置状�?
				// 响应的格式是:
				// HTTP/1.1 206 Partial Content
				response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);// 206

				// 从请求中得到�?始的字节
				// 请求的格式是:
				// Range: bytes=[文件块的�?始字节]-
				String pstr = request.getHeader("Range").replaceAll("bytes=", "");
				String ps = pstr.split("-")[0];
				p = Long.parseLong(ps);
			}
			// 下载的文�?(或块)长度
			// 响应的格式是:
			// Content-Length: [文件的�?�大小] - [客户端请求的下载的文件块的开始字节]
			response.setHeader("Content-Length", new Long(l - p).toString());
			if (p != 0) {
				// 不是从最�?始下�?,
				// 响应的格式是:
				// Content-Range: bytes [文件块的�?始字节]-[文件的�?�大�? - 1]/[文件的�?�大小]
				response.setHeader(
						"Content-Range",
						"bytes " + new Long(p).toString() + "-" + new Long(l - 1).toString() + "/"
								+ new Long(l).toString());
			}
			// 使客户端直接下载
			// 响应的格式是:
			// Content-Type: application/octet-stream
			response.setContentType("application/octet-stream");
			// 为客户端下载指定默认的下载文件名�?
			// 响应的格式是:
			// Content-Disposition: attachment;filename="[文件名]"
			// response.setHeader("Content-Disposition",
			// "attachment;filename=\"" + s.substring(s.lastIndexOf("\\") + 1)
			// + "\""); //经测�? RandomAccessFile 也可以实�?,有兴趣可将注释去�?,并注释掉
			// FileInputStream 版本的语�?
			response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
			bis.skip(p);
			response.setCharacterEncoding("UTF-8");
			int bytesRead;
			while ((bytesRead = bis.read(buff)) != -1) {
				try {
					bos.write(buff, 0, bytesRead);
				} catch (Exception e) {

				}

			}
			bos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null)
				bos.close();
			if (bis != null)
				bis.close();
			if (delete)
				FileUtil.delFile(fileName);
		}
	}

	/**
	 * 获取资源文件
	 * 
	 * @param resName
	 *            资源文件名称
	 * @param keyName
	 *            路径key名称
	 * @return [参数说明]
	 * @return String [返回类型说明]
	 * @see [类�?�类#方法、类#成员]
	 */
	public static String getFilePath(String resName, String keyName) {
		ConfigurationRead cfr = ConfigurationRead.getInstance();
		Properties prop = cfr.propertiesCreate(resName);
		String path = prop.getProperty(keyName).trim();
		return path;
	}

	public String getClassPath() {
		String strClassName = getClass().getName();
		String strPackageName = "";
		if (getClass().getPackage() != null) {
			strPackageName = getClass().getPackage().getName();
		}
		System.out.println("ClassName:" + strClassName);
		System.out.println("PackageName:" + strPackageName);
		String strClassFileName = "";
		if (!"".equals(strPackageName)) {
			strClassFileName = strClassName.substring(strPackageName.length() + 1, strClassName.length());
		} else {
			strClassFileName = strClassName;
		}
		System.out.println("ClassFileName:" + strClassFileName);
		URL url = null;
		url = getClass().getResource(strClassFileName + ".class");
		String strURL = url.toString();
		System.out.println(strURL);
		strURL = strURL.substring(strURL.indexOf('/') + 1, strURL.lastIndexOf('/'));
		strURL = strURL.replace("com/archermind/mdm/util", "");
		strURL = strURL.replace("/", "\\");
		return strURL.toString();
	}

	public static void main(String[] args) {
		FileUtil file = new FileUtil();
		System.out.println(file.getClassPath());
		System.out.println(FileUtil.getFilePath("config.properties", "doctor_img_upload"));
	}

	/**
	 * 图片BASE64 编码
	 */
	public static String getPicBASE64(String picPath) {
		String content = null;
		try {
			FileInputStream fis = new FileInputStream(picPath);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			content = Base64.encodeBase64String(bytes); // 具体的编码方�?
			fis.close();
			// System.out.println(content.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 更新用户图像
	 * 
	 * @param path
	 * @param request
	 * @return
	 */
	public static String updateUserPic(String path, HttpServletRequest request) {
		String fileName = "";
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			if (iter.hasNext()) {
				FileItemStream item = iter.next();
				// 得到表单域的值（这是�?个输入流�?
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					return fileName;
				} else {
					String newName = chFileName(item.getName());
					fileName = newName;
					mkDir(path);
					Streams.copy(stream, new FileOutputStream(path + newName), true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 若文件上传过程异�?,则删除残余文�?
			if (fileName != null && fileName.length() > 0) {
				FileUtil.rmFile(path + "/" + fileName);
			}

		}
		return fileName;
	}

	public static void downloadFile(HttpServletResponse response, String filePath) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			File file = new File(filePath);
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while ((bytesRead = bis.read(buff)) != -1) {
				try {
					bos.write(buff, 0, bytesRead);
				} catch (Exception e) {

				}
			}
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (bos != null) {
				bos.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

	
	/**
     * 数据输入流转字节
     * 
     * @param inputStream 输入流的引用
     * @return byte[] 输入流的字节内容
     * @see
     */
    public static byte[] streamToByte(InputStream is)
        throws Exception
    {
        byte[] readed = null;
        int totallength = 0;
        byte[] readedtemp = new byte[1024];
        int truelen = 0;
        while ((truelen = is.read(readedtemp, 0, readedtemp.length)) != -1)
        {
            totallength += truelen;
            if (totallength - truelen > 0)
            {
                byte[] newReaded = new byte[totallength];
                System.arraycopy(readed, 0, newReaded, 0, totallength - truelen);
                readed = newReaded;
            }
            else
                readed = new byte[truelen];
            System.arraycopy(readedtemp, 0, readed, totallength - truelen, truelen);
        }
        readedtemp = null;
        return readed;
    }
}
