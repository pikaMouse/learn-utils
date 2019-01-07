package com.example.pikamouse.learn_utils.tools.view.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jiangfeng
 * @date: 2019/1/4
 */
public class MonitorListItemBean {

    public String mTitle;
    public List<String> mList;
    public String mTag;

    private MonitorListItemBean() {

    }

    public static class Builder {

        private List<String> mList;
        private String mTitle;
        private String mTag;

        public Builder() {
            mList = new ArrayList<>();
            mTitle = "";
            mTag = "";
        }

        public Builder title(String text) {
            this.mTitle = text;
            return this;
        }

        public Builder tag(String text) {
            this.mTag = text;
            return this;
        }

        public Builder addItem(String text) {
            mList.add(text);
            return this;
        }

        public MonitorListItemBean build() {
            MonitorListItemBean config = new MonitorListItemBean();
            config.mList = this.mList;
            config.mTitle = this.mTitle;
            config.mTag = this.mTag;
            return config;
        }
    }

}
