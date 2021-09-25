package com.example.ceditect.models

import androidx.room.*



@Dao
interface RatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(vararg rates: Rates)

    @Update
    suspend fun updateRates(vararg rates: Rates)

    @Delete
    suspend fun deleteRates(vararg rates: Rates)

    @Query("SELECT * FROM  rates WHERE uid = $CURRENT_RATES_ID")
    suspend fun loadAllRates(): List<Rates>


}