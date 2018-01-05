package murki.githubexplorer.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import murki.githubexplorer.R
import murki.githubexplorer.databinding.RepoItemBinding
import murki.githubexplorer.viewmodel.RepoItemVM

class MainAdapter(var dataset : List<RepoItemVM>) : RecyclerView.Adapter<DataBindingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DataBindingHolder {
        // create a new view
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.repo_item, parent, false)

        return DataBindingHolder(view)
    }

    override fun onBindViewHolder(holder: DataBindingHolder?, position: Int) {
        val binding = holder?.binding as RepoItemBinding
        binding.viewModel = dataset[0]
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
