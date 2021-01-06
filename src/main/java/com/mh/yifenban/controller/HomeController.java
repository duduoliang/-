package com.mh.yifenban.controller;

import com.mh.yifenban.cache.RedisCacheManager;
import com.mh.yifenban.service.WxService;
import com.mh.yifenban.utils.WXSignUtil;
import com.mh.yifenban.utils.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;


@Controller
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Resource
    private WxService wxService;
    @Resource
    private RedisCacheManager redisCacheManager;
    @Resource
    private WXSignUtil wxSignUtil;


    /**
     * 首页
     * @return
     */

    @GetMapping("/")
    public String index(){

        return "index";
    }

    /**
     * 登陆页面
     * @return
     */
    @GetMapping("/login")
    public String login(){

        return "login";
    }

    /**
     * 用于检测扫码和关注状态
     * @return
     */
    @PostMapping("/checkLogin")
    @ResponseBody
    public Object checkLogin(String ticket){
        //如果redis中有ticket凭证则说明用户已扫码说明登陆成功
        if(redisCacheManager.hasKey(ticket)){
            //扫码通过则删除
            redisCacheManager.delete(ticket);
            return true;
        }
        return false;
    }


    /**
     * 登陆成功跳转
     * @return
     */
    @GetMapping("/success")
    @ResponseBody
    public String loginSuccess(){

        return "登陆成功";
    }

    /**
     * 获取二维码参数
     * @return
     */
    @GetMapping("/getQrCode")
    @ResponseBody
    public Object getQrCode(){

        return wxService.getQrCode();
    }

    /**
     *微信签名验证
     * @param request
     * @return
     */
    @GetMapping("/handleWxCheckSignature")
    @ResponseBody
    public String handleWxCheckSignature(HttpServletRequest request){
        logger.info("-----------验证微信信息开始---------");
        //微信加密签名
        String signature = request.getParameter("signature");
        //时间戳
        String timestamp = request.getParameter("timestamp");
        //随机数
        String norce = request.getParameter("norce");
        //随机字符串
        String echostr = request.getParameter("echostr");

        logger.info("signature is:"+signature+"timestamp is:"+timestamp+"norce is:"+norce);
        if(wxSignUtil.checkSignature(signature,timestamp,norce)){
            logger.info("-------------微信验证结束-----------------");
            return echostr;
        }else{

            return null;
        }
    }

    /**
     *接收微信推送事件
     * @param request
     * @return
     */
    @PostMapping("/handleWxCheckSignature")
    @ResponseBody
    public String handleWxEvent(HttpServletRequest request){

        try{
            ServletInputStream inputStream = request.getInputStream();
            Map<String, Object> map = XmlUtil.parseXML(inputStream);
            String userOpenId = (String) map.get("FromUserName");
            String event = (String) map.get("Event");
            if ("subscribe".equals(event)){
                // TODO:获取openid判断用户是否存在,不存在则获取新增用户,自己的业务

                //自己生成的二维码不管是关注还是扫码都能取到ticket凭证
                String ticket = (String) map.get("Ticket");
                redisCacheManager.set(ticket,ticket,10*60);

                logger.info("用户关注:{}",userOpenId);
            }else if("SCAN".equals(event)){
                //自己生成的二维码不管是关注还是扫码都能取到ticket凭证
                String ticket = (String) map.get("Ticket");
                redisCacheManager.set(ticket,ticket,10*60);
                logger.info("用户扫码:{}",userOpenId);
            }

            logger.info("接收参数:{}",map);

        }catch (IOException e){
            e.printStackTrace();
        }

        return "success";

    }
}
