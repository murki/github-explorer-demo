package murki.githubexplorer.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import murki.githubexplorer.R
import murki.githubexplorer.viewmodel.RepoItemVM


class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_main, container, false)

        val recyclerView = view.findViewById(R.id.main_recycler_view) as RecyclerView

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = MainAdapter(listOf(RepoItemVM("name", "description")))

        return view
    }

}
