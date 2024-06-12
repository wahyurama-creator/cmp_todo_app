package domain

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error

    /**
     * Return data from a [Success]
     * @throws ClassCastException if the current state is not [Success]
     */
    fun getSuccessData() = (this as Success).data
    fun getSuccessDataOrNull(): T? {
        return try {
            (this as Success).data
        } catch (e: Exception) {
            println("Error when get success data: ${e.message}")
            null
        }
    }

    /**
     * Return data from a [Success]
     * @throws ClassCastException if the current state is not [Success]
     */
    fun getErrorMessage() = (this as Error).message
    fun getErrorMessageOrEmpty(): String {
        return try {
            (this as Error).message
        } catch (e: Exception) {
            println("Error when get error message: ${e.message}")
            ""
        }
    }

    @Composable
    fun DisplayResult(
        onIdle: (@Composable () -> Unit)? = null,
        onLoading: (@Composable () -> Unit),
        onSuccess: (@Composable (T) -> Unit),
        onError: (@Composable (String) -> Unit),
        transitionSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
            fadeIn(tween(durationMillis = 300)) togetherWith
                fadeOut(tween(durationMillis = 300))
        }
    ) {
        AnimatedContent(
            targetState = this,
            transitionSpec = transitionSpec,
            label = "Animated State",
            contentKey = { it::class }
        ) { state ->
            when (state) {
                is Error -> onError(state.getErrorMessage())
                is Idle -> onIdle?.invoke()
                is Loading -> onLoading()
                is Success -> onSuccess(state.getSuccessData())
            }
        }
    }
}