package com.example.test.data.NutrtionData

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.test.network.UsdaApiService

class FoodPagingSource(
    private val apiService: UsdaApiService,
    private val query: String,
    private val apiKey: String,
    private val pageSize: Int = 20
) : PagingSource<Int, UsdaFood>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UsdaFood> {
        return try {
            // The page number to load, defaulting to the first page
            val pageNumber = params.key ?: 1
            val response = apiService.searchFoods(
                query = query,
                pageSize = pageSize,
                apiKey = apiKey,
                pageNumber = pageNumber
            )

            LoadResult.Page(
                data = response.foods,
                prevKey = if (pageNumber == 1) null else pageNumber - 1,
                nextKey = if (response.foods.isEmpty()) null else pageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // Provide a key for refreshing the list
    override fun getRefreshKey(state: PagingState<Int, UsdaFood>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}