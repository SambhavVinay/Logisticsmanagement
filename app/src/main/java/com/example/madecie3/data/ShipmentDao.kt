package com.example.madecie3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ShipmentDao {
    @Query("SELECT * FROM shipments ORDER BY timestamp DESC")
    suspend fun getAllShipments(): List<ShipmentEntity>

    @Insert
    suspend fun insertShipment(shipment: ShipmentEntity)

    @Query("SELECT * FROM shipments ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestShipment(): ShipmentEntity?

    @Query("SELECT COUNT(*) FROM shipments")
    suspend fun getCount(): Int
}
