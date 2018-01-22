package murki.githubexplorer.ui

import android.support.v7.recyclerview.extensions.DiffCallback
import murki.githubexplorer.viewmodel.RepoItemVM

class RepoItemDiff : DiffCallback<RepoItemVM>() {
    override fun areItemsTheSame(oldItem: RepoItemVM, newItem: RepoItemVM): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RepoItemVM, newItem: RepoItemVM): Boolean {
        return oldItem == newItem
    }

}