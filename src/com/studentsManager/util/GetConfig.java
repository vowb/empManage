package com.studentsManager.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.parsers.SAXParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.*;

/**
 * properties配置文件操作工具类
 * @author 23602
 *2018年11月21日 上午8:20:48
 */


public class GetConfig {

	//读取配置文件信息
	public static Map<String,String> readProperty(String path) {
		//规范路径并且获取File对象
		File file = new File(URLDecoder.decode(path));
		Properties properties = new Properties();
		FileInputStream fis = null;
		BufferedReader br = null;
		Map<String,String> propertyMap = new HashMap<String,String>();
		System.out.println(file.getPath());
		if(file.exists()) {
			try {
				fis = new FileInputStream(file);
				//用UTF-8编码格式读取文件
				br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
				//加载属性配置文件
				properties.load(fis);
				//获取全部属性名称
				Enumeration<String> pnames = (Enumeration<String>) properties.propertyNames();
				while(pnames.hasMoreElements()) {
					String key = pnames.nextElement();
					propertyMap.put(key,properties.getProperty(key));
				}

			} catch (IOException e) {
				System.out.println("读取配置文件失败");
				e.printStackTrace();
			}finally {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return propertyMap;
	}

	//保存配置文件
	public static boolean writeProperty(File file,Map<String,String> propertyMap,String comments) {
		Properties properties = new Properties();
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		Set<String> keys = propertyMap.keySet();
		Iterator<String> it = keys.iterator();
		boolean flag = false;
		//判断文件路径是否存在
		//if(!file.exists())file.mkdir();
		while(it.hasNext()) {
			String key = it.next();
			properties.setProperty(key, propertyMap.get(key));
			try {
				//boolean a =file.createNewFile();
				//System.out.println(a);
				fos = new FileOutputStream(file);
				bw = new BufferedWriter(new OutputStreamWriter(fos,"UTF-8"));
				properties.store(fos,comments);
				flag = true;
			} catch (IOException e) {
				System.out.println("配置文件持久化失败");
				e.printStackTrace();
			}finally {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return flag;

	}

	//读取xml配置信息
	public static List<String> readXmlConfig(String path,String rootEl,String elName){
		//规范路径并且获取File对象
		File file = new File(URLDecoder.decode(path));
		List<String> values = new ArrayList<String>();
		//创建SAXReader对象
		SAXReader reader = new SAXReader();
		//获取xml的document对象
		Document doc = null;
		try {
			doc = reader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		//获取Document对象的根元素
		Element root = doc.getRootElement();
		//指定的元素集
		Element infoSet = null;
		if(root.getName().equals(rootEl)){
			//获取指定的元素集
			infoSet = root.element(elName);
		}else{
			return null;
		}


		List allInfo = infoSet.elements();
		for(Object el: allInfo){
			Element xmlEl = (Element)el;
			values.add(xmlEl.getTextTrim());
		}

		return values;
	}
}
