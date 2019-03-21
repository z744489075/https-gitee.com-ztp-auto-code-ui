
package com.zengtengpeng.ui.servlet;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.utils.MyStringUtils;
import com.zengtengpeng.ui.constant.ParamConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = {"/auto-code-ui/ui/*", "/auto-code-ui/static/*"}, description = "页面控制器")
public class AutoCodeUiServlet extends HttpServlet {
    Logger logger = LoggerFactory.getLogger(AutoCodeUiServlet.class);

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {

            response.setHeader("Content-type", "text/html;charset=UTF-8");
            String contextPath = request.getContextPath();
            String requestURI = request.getRequestURI();
            AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
            if(!autoCodeConfig.getGlobalConfig().getAutoCode()){
                response.getWriter().println("autoCode=false.不能开启界面");
                return;
            }

            if (contextPath == null) { // root context
                contextPath = "";
            }
            String path = requestURI.substring(contextPath.length()+1);

            InputStream inputStream = AutoCodeUiServlet.class.getClassLoader().getResourceAsStream(path);
            if (path.endsWith(".tff")) {
                response.setHeader("Accept-Ranges", "bytes");
                response.setContentType("application/font-tff;charset=utf-8");
            } else if (path.endsWith(".woff")) {
                response.setHeader("Accept-Ranges", "bytes");
                response.setContentType("application/font-woff;charset=utf-8");
            } else if (path.endsWith(".png")) {
                response.setContentType("image/png;charset=utf-8");
            } else if (path.endsWith(".jpg")) {
                response.setContentType("image/jpg;charset=utf-8");
            } else if (path.endsWith(".css")) {
                response.setContentType("text/css;charset=utf-8");
            } else if (path.endsWith(".js")) {
                response.setContentType("text/javascript;charset=utf-8");
            }

            byte[] buff = new byte[1024];
            int len;
            while ((len = inputStream.read(buff)) != -1) {
                if (!path.endsWith(".jpg") && !path.endsWith(".png") && !path.endsWith(".jpge") &&
                        !path.endsWith(".tff") && !path.endsWith(".woff")) {
                    response.getWriter().write(new String(buff, 0, len, StandardCharsets.UTF_8));
                } else {
                    response.getOutputStream().write(buff, 0, len);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void init(ServletConfig config) {
    }
}
