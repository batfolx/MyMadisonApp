package com.jmu.mymadisonapp.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jmu.mymadisonapp.room.model.FormData
import java.text.Normalizer

@Dao
abstract class FormDataDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertFormData(formData: FormData): LiveData<FormData>


    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateFormData(formData: FormData): LiveData<FormData>


    @Delete
    abstract fun deleteFormData(formData: FormData)


    @Query("SELECT ICSID FROM FormData")
    abstract fun getICSID(): LiveData<FormData>


}