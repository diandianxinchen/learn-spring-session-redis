package com.alazyboy.controller;

import com.alazyboy.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class LoginController {
    @RequestMapping("/login")
    public void login(ServletRequest request, ServletResponse response, String username, String password) throws IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        httpServletRequest.getSession().setAttribute(User.USER_ATTR,user);
        httpServletResponse.sendRedirect("welcome.do");
    }

    @RequestMapping("/welcome")
    @ResponseBody
    public String welcome(ServletRequest request,ServletResponse response) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Object user = httpServletRequest.getSession().getAttribute(User.USER_ATTR);
        if(user != null){
            String username = ((User)user).getUsername();
            String password = ((User)user).getPassword();
            return username + "/" + password + "  " + hostAddress;
        }
        return "please login first";
    }
}
