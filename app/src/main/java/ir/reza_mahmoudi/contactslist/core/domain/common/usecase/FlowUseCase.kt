package ir.reza_mahmoudi.contactslist.core.domain.common.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(parameters: P): Flow<R> = withContext(coroutineDispatcher) {
        execute(parameters).flowOn(coroutineDispatcher)
    }

    protected abstract suspend fun execute(parameters: P): Flow<R>
}
