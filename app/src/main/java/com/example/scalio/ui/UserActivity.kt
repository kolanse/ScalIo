package com.example.scalio.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.example.scalio.R
import com.example.scalio.data.model.User
import com.example.scalio.databinding.ActivityMainBinding
import com.example.scalio.ui.adapter.UsersAdapter
import com.example.scalio.ui.adapter.UsersLoadStateAdapter
import com.example.scalio.utils.*
import com.example.scalio.viewmodel.UiAction
import com.example.scalio.viewmodel.UiState
import com.example.scalio.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val usersViewModel: UserViewModel by viewModels()
    @Inject
    lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.searchBoxEt.requestFocus()
        binding.userRv.apply {
            itemAnimator = SpringAddItemAnimator()
            addItemDecoration(
                BottomMarginItemDecoration(resources.getDimensionPixelSize(R.dimen.grid_2))
            )
        }

        binding.bindState(
            uiState = usersViewModel.state,
            pagingData = usersViewModel.userPagingFlow,
            uiActions = usersViewModel.accept
        )
    }

    private fun ActivityMainBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<User>>,
        uiActions: (UiAction) -> Unit
    ) {

        val header = UsersLoadStateAdapter { usersAdapter.retry() }
        userRv.adapter = usersAdapter.withLoadStateFooter(
            footer = UsersLoadStateAdapter { usersAdapter.retry() }
        )

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )

        bindList(
            header = header,
            usersAdapter = usersAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun ActivityMainBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchBoxEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchBoxEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        searchIcon.setOnClickListener {
            updateRepoListFromInput(onQueryChanged)
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect{
                 searchBoxEt.setText(it)
                binding.searchBoxEt.requestFocus()
                binding.searchBoxEt.setSelection(binding.searchBoxEt.text.length)
                }
        }
    }

    private fun ActivityMainBinding.updateRepoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchBoxEt.text.trim().let {
            if (it.isNotEmpty()) {
                userRv.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun ActivityMainBinding.bindList(
        header: UsersLoadStateAdapter,
        usersAdapter: UsersAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<User>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {

        retryBtn.setOnClickListener { usersAdapter.retry() }

        userRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })

        // used with remote mediator
        val notLoading = usersAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest {
                userRv.doOnNextLayout {
          startPostponedEnterTransition()
                }
                usersAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) userRv.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            usersAdapter.loadStateFlow.collect { loadState ->

                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && usersAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && usersAdapter.itemCount == 0
                emptyListTv.isVisible = isListEmpty
                userRv.isVisible = loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                retryBtn.isVisible = loadState.mediator?.refresh is LoadState.Error && usersAdapter.itemCount == 0
                emptyListTv.isVisible = loadState.mediator?.refresh is LoadState.Error && usersAdapter.itemCount == 0
                if (loadState.source.refresh is LoadState.Loading) hideKeyboard(root, this@UserActivity)
                if (loadState.mediator?.refresh is LoadState.Error && usersAdapter.itemCount == 0) {
                    val errSt = loadState.mediator?.refresh as LoadState.Error
                    emptyListTv.text = errSt.error.localizedMessage
                }
                if (isListEmpty) {
                    emptyListTv.text = getString(R.string.no_result)
                    emptyListTv.isVisible = true
                }

                // Show error in snackbar
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    showSnackBar(" Error ${it.error.localizedMessage}", root)
                }
            }
        }
    }
}
