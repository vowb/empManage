package com.studentsManager.Listener;

import com.studentsManager.util.GetConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;

public class ExcludedRequestListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        //排除的请求
        String ConfigPath = this.getClass().getClassLoader().getResource("resource/loginFilterExcludedRequests.xml").getPath();
        List<String> excludedReqs = GetConfig.readXmlConfig(ConfigPath,"excludedRequests-config","excludedRequests");
        System.out.println(excludedReqs+"这是配置的跳过登录检查的请求");

        //保存到session域属性中
        context.setAttribute("excludedReqs",excludedReqs);
    }
}
