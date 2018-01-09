package murki.githubexplorer.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import murki.githubexplorer.R
import murki.githubexplorer.databinding.RepoItemBinding
import murki.githubexplorer.viewmodel.RepoItemVM

class MainAdapter(private var dataset : List<RepoItemVM>?) : RecyclerView.Adapter<DataBindingHolder<RepoItemBinding>>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DataBindingHolder<RepoItemBinding> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.repo_item, parent, false)
        return DataBindingHolder(view)
    }

    override fun onBindViewHolder(holder: DataBindingHolder<RepoItemBinding>?, position: Int) {
        holder?.binding?.viewModel = dataset?.get(position)
    }

    override fun getItemCount(): Int {
        return dataset?.size ?: 0
    }
}
