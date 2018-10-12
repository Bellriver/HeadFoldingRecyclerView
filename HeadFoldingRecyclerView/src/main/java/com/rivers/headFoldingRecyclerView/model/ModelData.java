package com.rivers.headFoldingRecyclerView.model;

/**
 * Created by jiangjiang.zhong on 2018/10/9.
 * Description:
 */

public class ModelData {
    public String title;
    public String content;
    public long time;
    public String name;

    public ModelData(String title,String name,String content,long time){
        this.title=title;
        this.name=name;
        this.content=content;
        this.time=time;
    }
}
