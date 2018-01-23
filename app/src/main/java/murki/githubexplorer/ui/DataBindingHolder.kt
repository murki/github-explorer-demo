package murki.githubexplorer.ui

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

data class DataBindingHolder<out T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)