package com.rohman.spbu.persistence.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rohman.spbu.model.Foto
import com.rohman.spbu.model.Manual
import com.rohman.spbu.model.Produk
import com.rohman.spbu.model.Template
import com.rohman.spbu.persistence.dao.FotoDao
import com.rohman.spbu.persistence.dao.ManualDao
import com.rohman.spbu.persistence.dao.ProductDao
import com.rohman.spbu.persistence.dao.TemplateDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Produk::class,Template::class, Manual::class,Foto::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun templateDao(): TemplateDao
    abstract fun manualDao(): ManualDao
    abstract fun fotoDao(): FotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            synchronized(this) {
                var instance =
                    INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "spbu_db"
                    )
                        .fallbackToDestructiveMigration().addCallback(
                            ProductCallback(
                                scope
                            )
                        ).addCallback(
                            TemplateCallback(scope)
                        )
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    private class TemplateCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    database.templateDao().insert(Template(null))
                }
            }
        }
    }

    private class ProductCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    database.productDao().insert(Produk(null))
                }
            }
        }
    }

}