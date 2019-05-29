/*
 * Copyright 2019 Timothy Logan
 * Copyright 2019 Victor Velea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmu.mymadisonapp.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jmu.mymadisonapp.room.model.Term

@Dao
abstract class TermDao {

    @Transaction
    open suspend fun upsert(term: Term): Long =
        insertTerm(term).also { if (it == -1L) updateTerm(term) }

    @Transaction
    open suspend fun upsertTerms(vararg terms: Term): List<Long> =
        insertTerms(*terms).also {
            it.mapIndexed { index, l ->
                if (l == -1L) terms[index] else null
            }.filterNotNull()
                .takeIf { leftovers -> leftovers.isNotEmpty() }
                ?.let { leftovers ->
                    updateTerms(*leftovers.toTypedArray())
                }
        }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertTerm(term: Term): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertTerms(vararg terms: Term): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateTerm(term: Term)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateTerms(vararg terms: Term): Int

    @Delete
    abstract fun deleteTerm(term: Term)

    @Query("SELECT * FROM terms")
    abstract fun getAllTerms(): LiveData<List<Term>>

    @Query("SELECT * FROM terms WHERE semester=:semester AND year=:year")
    abstract fun getTerm(semester: String, year: Int): LiveData<Term>

}