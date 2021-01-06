package com.mh.yifenban.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mh.yifenban.cache.RedisCacheManager;
import com.mh.yifenban.entity.WXMenu;
import com.mh.yifenban.service.WxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxServiceImpl  implements WxService {

    Logger logger = LoggerFactory.getLogger(WxServiceImpl.class);

    @Value("${wx.gz.appid:''}")
    private String appid;
    @Value("${wx.gz.secret:''}")
    private String secret;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisCacheManager redisCacheManager;

    //获取access_token
    @Override
    public String getAccessToken() {
        //token
        String key = "abc123";

        //从redis缓存中获取token
        if(redisCacheManager.hasKey(key)){
            return (String) redisCacheManager.get(key);
        }
        String url =String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",appid,secret);
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        if(result.getStatusCode() == HttpStatus.OK){
            //网页状态值==200执行
            JSONObject jsonObject = JSON.parseObject(result.getBody());
            //获取微信返回给公众号的json数据包
            //获取到的凭证
            String access_token = jsonObject.getString("access_token");
            //获取到凭证有效时间
            long expires_in = jsonObject.getLong("expires_in");
            //将token，凭证，凭证有效时间放入缓存
            redisCacheManager.set(key,access_token,expires_in);

            return access_token;

        }

        return null;
    }

    @Override
    public Map<String, Object> getQrCode() {

        //获取临时二维码
        String url = String.format("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s",getAccessToken());
        ResponseEntity<String> result = restTemplate.postForEntity(url, "{\"expire_seconds\": 604800, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"test\"}}}", String.class);

        logger.info("二维码:{}",result.getBody());

        JSONObject jsonObject = JSON.parseObject(result.getBody());
        Map<String,Object> map = new HashMap<>();
        map.put("ticket",jsonObject.getString("ticket"));
        map.put("url",jsonObject.getString("url"));


        return map;
    }

    /**
     * 自定义菜单
     * @return
     */
    @Override
    public Integer createMenu(WXMenu wxMenu) {
        String url = String.format("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s",getAccessToken());

        ResponseEntity<String> result = restTemplate.postForEntity(url, JSON.toJSONString(wxMenu), String.class);

        JSONObject jsonObject = JSON.parseObject(result.getBody());

        return jsonObject.getInteger("errcode");
    }
}
