package com.hudun.mydemo.baserecycle

import android.view.View
import android.widget.Toast
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hudun.mydemo.R

/**
 * <pre>
 *      @ClassName FirstNode
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2022/1/18 13:57
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
class FirstNodeProvider constructor(): BaseNodeProvider() {
    override val itemViewType: Int
        get() = 0
    override val layoutId: Int
        get() = R.layout.item_base_level_0

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val firstNode: FirstNode = item as FirstNode
        helper.setText(R.id.level_0_title, firstNode.title)
    }

    override fun onChildClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        super.onChildClick(helper, view, data, position)
    }
    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        getAdapter()?.expandOrCollapse(position = position,
            animate = true,
            notify = true,
            parentPayload = MyBaseAdapter.EXPAND_COLLAPSE_PAYLOAD
        )
    }
}