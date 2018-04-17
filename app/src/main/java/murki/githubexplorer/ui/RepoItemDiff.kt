package murki.githubexplorer.ui

import android.support.v7.util.DiffUtil
import murki.githubexplorer.viewmodel.RepoItemVM

class RepoItemDiff : DiffUtil.ItemCallback<RepoItemVM>() {
    override fun areItemsTheSame(oldItem: RepoItemVM, newItem: RepoItemVM): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RepoItemVM, newItem: RepoItemVM): Boolean {
        return oldItem == newItem
    }

}