package com.hudun.mydemo.baserecycle

import android.view.View
import android.widget.Toast
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hudun.mydemo.R

/**
 * <pre>
 *      @ClassName ScendNode
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2022/1/18 13:57
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
class SecondNodeProvider(): BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.item_base_level_1

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val secondNode: SecondNode = item as SecondNode
        helper.setText(R.id.level_1_title, secondNode.title)
        val position = helper.bindingAdapterPosition
        addChildClickViewIds(R.id.turn_next_0)
        addChildClickViewIds(R.id.turn_next_1)
    }
    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        val secondNode: SecondNode = data as SecondNode
        Toast.makeText(context, secondNode.title, Toast.LENGTH_SHORT).show()
    }


    override fun onChildClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        when(view.id){
            R.id.turn_next_0 -> {
                Toast.makeText(context, "子跳转事件 = $position", Toast.LENGTH_SHORT).show()
            }
            R.id.turn_next_1 -> {
                Toast.makeText(context, "子点击事件 = $position", Toast.LENGTH_SHORT).show()
            }
        }

        super.onChildClick(helper, view, data, position)
    }
}