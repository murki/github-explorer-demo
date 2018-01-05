package murki.githubexplorer.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

class DataBindingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding: ViewDataBinding = DataBindingUtil.bind(itemView)

}