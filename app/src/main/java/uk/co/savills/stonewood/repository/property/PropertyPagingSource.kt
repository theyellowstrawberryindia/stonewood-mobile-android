package uk.co.savills.stonewood.repository.property

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.storage.db.entity.property.PropertyWithNoAccessHistory
import uk.co.savills.stonewood.util.mapper.mapToModel
import java.io.IOException

class PropertyPagingSource(
    private val dbModelPagingSource: PagingSource<Int, PropertyWithNoAccessHistory>
) : PagingSource<Int, PropertyModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PropertyModel> {
        return when (val result = dbModelPagingSource.load(params)) {
            is LoadResult.Page -> {
                val properties = result.data

                LoadResult.Page(
                    data = properties.map(::mapToModel),
                    prevKey = null,
                    nextKey = result.nextKey
                )
            }

            is LoadResult.Error -> LoadResult.Error(result.throwable)

            is LoadResult.Invalid -> LoadResult.Error(IOException("${PropertyPagingSource::class.simpleName} failed with LoadResult.Invalid"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PropertyModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
