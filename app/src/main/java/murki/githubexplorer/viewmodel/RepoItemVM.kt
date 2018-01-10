package murki.githubexplorer.viewmodel

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator") // for bug: https://youtrack.jetbrains.com/issue/KT-19300
@Parcelize
data class RepoItemVM(val name: String, val description: String?) : Parcelable