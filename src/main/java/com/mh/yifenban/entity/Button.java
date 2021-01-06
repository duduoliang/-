package com.mh.yifenban.entity;

import lombok.Data;

import java.util.List;

/**
 * 微信内层按钮
 */
@Data
public class Button {
    private String type;

    private String name;

    private String key;

    private String url;

    private List<Button> sub_button;
}
