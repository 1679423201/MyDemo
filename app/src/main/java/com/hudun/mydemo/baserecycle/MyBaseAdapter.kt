package com.hudun.mydemo.baserecycle

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.entity.node.BaseNode

/**
 * <pre>
 *      @ClassName MyBaseAdapter
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2022/1/18 10:54
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
class MyBaseAdapter constructor(): BaseNodeAdapter() {
    companion object{
        const val EXPAND_COLLAPSE_PAYLOAD = 110
    }
    init{
        addNodeProvider(FirstNodeProvider())
        addNodeProvider(SecondNodeProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        val node = data[position]
        if (node is FirstNode) {
            return 0
        } else if (node is SecondNode) {
            return 1
        }
        return -1
    }

}