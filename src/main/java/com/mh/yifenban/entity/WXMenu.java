package com.mh.yifenban.entity;


import lombok.Data;

import java.util.List;

/**
 * 微信外层按钮
 */
@Data
public class WXMenu {
    private List<Button> button;
}
