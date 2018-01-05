package murki.githubexplorer.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

class DataBindingHolder<out T : ViewDataBinding>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding: T = DataBindingUtil.bind(itemView)

}