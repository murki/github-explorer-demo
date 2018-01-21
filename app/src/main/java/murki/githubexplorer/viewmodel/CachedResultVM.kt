package murki.githubexplorer.viewmodel

data class CachedResultVM<out T>(val data: T, val isFromCache: Boolean)