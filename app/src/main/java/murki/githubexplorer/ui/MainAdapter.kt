package murki.githubexplorer.ui

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import murki.githubexplorer.R
import murki.githubexplorer.databinding.RepoItemBinding
import murki.githubexplorer.viewmodel.RepoItemVM

class MainAdapter : ListAdapter<RepoItemVM, DataBindingHolder<RepoItemBinding>>(RepoItemDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DataBindingHolder<RepoItemBinding> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.repo_item, parent, false)
        return DataBindingHolder(view)
    }

    override fun onBindViewHolder(holder: DataBindingHolder<RepoItemBinding>?, position: Int) {
        holder?.binding?.viewModel = getItem(position)
    }

}
