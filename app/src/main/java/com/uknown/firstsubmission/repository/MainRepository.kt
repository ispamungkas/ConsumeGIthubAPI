package com.uknown.firstsubmission.repositoryimport androidx.lifecycle.LiveDataimport androidx.lifecycle.MediatorLiveDataimport androidx.lifecycle.asLiveDataimport com.uknown.firstsubmission.local.ServiceDaoimport com.uknown.firstsubmission.local.Userimport com.uknown.firstsubmission.network.response.UserResponseimport com.uknown.firstsubmission.network.retorfit.Serviceimport com.uknown.firstsubmission.utils.AppExecutorimport com.uknown.firstsubmission.utils.Resourcesimport com.uknown.firstsubmission.utils.SettingPreferenceimport retrofit2.Responseimport retrofit2.awaitResponseclass MainRepository(    private val service: Service,    private val serviceDao: ServiceDao,    private val appExecutors: AppExecutor,    private val pref: SettingPreference) {    private val resouces = MediatorLiveData<Resources<UserResponse>>()    companion object {        private const val MAIN = "Main View Model"        private var instance: MainRepository? = null        fun getInstance(            service: Service,            serviceDao: ServiceDao,            appExecutors: AppExecutor,            pref: SettingPreference        ): MainRepository? = instance ?: synchronized(this) {            instance = MainRepository(service, serviceDao, appExecutors, pref)            instance        }    }    suspend fun getSpesificUserData(username: String): LiveData<Resources<UserResponse>> {        resouces.value = Resources.Loading        val call = service.searchUser(username)        val data: Response<UserResponse> = call.awaitResponse()        if (data.isSuccessful) {            if (data.body() != null) {                resouces.value = Resources.Success(data.body()!!)            }        }        return resouces    }    fun insertDataDao(user: User) {        appExecutors.networkIO.execute { serviceDao.insert(user) }    }    fun deleteDataDao(user: User) {        appExecutors.networkIO.execute { serviceDao.deleteData(user) }    }    fun getData(username: String): LiveData<User> {        return serviceDao.getDataUser(username)    }    fun getAllDataDao(): LiveData<List<User>> {        return serviceDao.getAllData()    }    fun getThemeSetting(): LiveData<Boolean> {        return pref.getThemeSetting().asLiveData()    }    suspend fun setThemeSetting(isDarkMode: Boolean) {        pref.saveThemeSetting(isDarkMode)    }}