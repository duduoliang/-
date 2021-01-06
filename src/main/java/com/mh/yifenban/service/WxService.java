package com.mh.yifenban.service;

import com.mh.yifenban.entity.WXMenu;

import java.util.Map;

public interface WxService {
    //获得AccessToken
    String getAccessToken();
    //获得二维码参数
    Map<String,Object> getQrCode();
    //自定义菜单
    Integer createMenu(WXMenu wxMenu);

}
