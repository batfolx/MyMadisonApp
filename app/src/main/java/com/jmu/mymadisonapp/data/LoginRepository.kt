package com.jmu.mymadisonapp.data

import com.jmu.mymadisonapp.data.model.LoggedInUser
import com.jmu.mymadisonapp.log
import com.timmahh.openph.data.Result
import okhttp3.ResponseBody

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(username: String, password: String): Result<ResponseBody?> {
        // handle login
        val result = dataSource.loginUser(username, password)

//        if (result is Success) {
//            setLoggedInUser(result.data)
//        }
        log("LoginResponse", "Response: ${result.data?.string()}")

        return result
    }

    private fun setLoggedInUser(loggedInUser: ResponseBody) {
//        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
