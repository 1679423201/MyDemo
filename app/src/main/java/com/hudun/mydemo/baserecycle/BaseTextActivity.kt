package com.hudun.mydemo.baserecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.entity.node.BaseNode
import com.hudun.mydemo.R
import com.hudun.mydemo.databinding.ActivityBaseTextBinding

class BaseTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind:ActivityBaseTextBinding = ActivityBaseTextBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val adapter: MyBaseAdapter = MyBaseAdapter()
        bind.baseRecycle.adapter = adapter
        bind.baseRecycle.layoutManager = LinearLayoutManager(this)
        adapter.setList(getList())
    }

    private fun getList():MutableList<BaseNode>{
        val firstList: MutableList<BaseNode> = ArrayList()
        for (index in 0..4){
            val secondList: MutableList<BaseNode> = ArrayList()
            for (num in 0..9){
                val secondNode:SecondNode = SecondNode("子标题 = $num")
                secondList.add(secondNode)
            }
            val firstNode: FirstNode = FirstNode(secondList, "主标题 = $index")
            firstList.add(firstNode)
            firstNode.isExpanded = index == 0
        }
        return firstList
    }
}