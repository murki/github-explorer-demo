package murki.githubexplorer.ui

import android.support.v7.recyclerview.extensions.ListAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import murki.githubexplorer.databinding.RepoItemBinding
import murki.githubexplorer.viewmodel.RepoItemVM

class MainAdapter : ListAdapter<RepoItemVM, DataBindingHolder<RepoItemBinding>>(RepoItemDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingHolder<RepoItemBinding> {
        Log.d(CLASSNAME, "onCreateViewHolder() called. Inflating view for row. viewType=$viewType")
        val binding: RepoItemBinding = RepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataBindingHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingHolder<RepoItemBinding>, position: Int) {
        Log.d(CLASSNAME, "onBindViewHolder() called. Binding data to view. hasPendingBindings=${holder.binding.hasPendingBindings()}")
        holder.binding.viewModel = getItem(position)
        if (holder.binding.hasPendingBindings()) {
            holder.binding.executePendingBindings()
        }
    }

    companion object {
        private const val CLASSNAME = "MainAdapter"
    }

}
