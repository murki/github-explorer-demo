package murki.githubexplorer.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.mainRecyclerView

import murki.githubexplorer.R
import murki.githubexplorer.viewmodel.RepoItemVM


class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.adapter = MainAdapter(listOf(RepoItemVM("name", "description")))
    }
}
