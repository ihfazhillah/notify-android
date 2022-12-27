package com.ihfazh.notify.remote
import okio.IOException
import timber.log.Timber


enum class ResultStatus {
    Unknown,
    Success,
    NetworkException,
    RequestException,
    GeneralException
}

//class ApiResult<T>(
//    val result: T? = null,
//    val status: ResultStatus = ResultStatus.Unknown
//) {
//    val success = status == ResultStatus.Success
//}


sealed class ApiResult<T>{
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error<T>(val message: String? = null, val code: ResultStatus): ApiResult<T>()
}

suspend fun <T> safeApiRequest(
    apiFunction: suspend () -> T
): ApiResult<T> =
    try {
        val result = apiFunction()
        ApiResult.Success(result)
    } catch (ex: retrofit2.HttpException) {
        // check ex data
        ApiResult.Error(code=ResultStatus.RequestException)
    } catch (ex: IOException) {
        ApiResult.Error(code = ResultStatus.NetworkException)
    } catch (ex: Exception) {
        Timber.d(ex)
        ApiResult.Error(code = ResultStatus.GeneralException)
    }
