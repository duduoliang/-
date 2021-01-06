package com.mh.yifenban.controller;

import com.alibaba.fastjson.JSON;
import com.mh.yifenban.cache.RedisCacheManager;
import com.mh.yifenban.entity.Button;
import com.mh.yifenban.entity.WXMenu;
import com.mh.yifenban.service.WxService;
import com.mh.yifenban.utils.WXSignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MenuController {

    Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Resource
    private WxService wxService;
    @Resource
    private RedisCacheManager redisCacheManager;
    @Resource
    private WXSignUtil wxSignUtil;




    @GetMapping("/createMenu")
    @ResponseBody
    public String createMenu(){
        WXMenu wxMenu = new WXMenu();
        List<Button> buttonList = new ArrayList<>();
        //菜单1
        Button button1 = new Button();
        button1.setName("注册");
        button1.setType("click");
        button1.setKey("key1");
        buttonList.add(button1);

        //菜单2
        Button button2 = new Button();
        button2.setName("登录");
        button2.setType("click");
        button2.setKey("key2");
        buttonList.add(button2);

        wxMenu.setButton(buttonList);
        logger.info("菜单："+ JSON.toJSONString(wxMenu));
        Integer menu = wxService.createMenu(wxMenu);

        return JSON.toJSONString(menu);
    }



}
