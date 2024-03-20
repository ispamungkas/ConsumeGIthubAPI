package com.uknown.firstsubmission.localimport androidx.lifecycle.LiveDataimport androidx.room.Daoimport androidx.room.Deleteimport androidx.room.Insertimport androidx.room.OnConflictStrategyimport androidx.room.Queryimport androidx.room.Update@Daointerface ServiceDao {    @Insert(onConflict = OnConflictStrategy.IGNORE)    fun insert(user: User)    @Query("SELECT * FROM USER")    fun getAllData(): LiveData<List<User>>    @Update    fun updateData(user: User)    @Delete    fun deleteData(user: User)    @Query("SELECT * FROM USER WHERE USERNAME = :username")    fun getDataUser(username: String): LiveData<User>}