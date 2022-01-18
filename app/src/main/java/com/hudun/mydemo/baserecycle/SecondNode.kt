package com.hudun.mydemo.baserecycle

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

/**
 * <pre>
 *      @ClassName SecondNode
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2022/1/18 16:05
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
class SecondNode constructor(): BaseNode() {
    var title: String? = null

    constructor(title: String):this(){
        this.title = title
    }

    override val childNode: MutableList<BaseNode>?
        get() = null
}