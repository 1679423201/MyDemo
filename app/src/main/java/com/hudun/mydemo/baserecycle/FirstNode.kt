package com.hudun.mydemo.baserecycle

import android.icu.text.CaseMap
import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

/**
 * <pre>
 *      @ClassName FirstNode
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2022/1/18 16:04
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
class FirstNode constructor(): BaseExpandNode() {
    var childNodeList: MutableList<BaseNode>? = null
    var title: String? = null

    constructor(childNodeList:MutableList<BaseNode>, title: String):this(){
        this.childNodeList = childNodeList
        this.title = title;
        isExpanded = false
    }

    override val childNode: MutableList<BaseNode>?
        get() = childNodeList
}