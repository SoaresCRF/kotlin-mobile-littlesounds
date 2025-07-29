package com.dev.soarescrf.littlesounds.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dev.soarescrf.littlesounds.core.database.dao.CategoryItemDao
import com.dev.soarescrf.littlesounds.core.database.entities.CategoryItemLocal

/**
 * Banco de dados Room da aplicação.
 *
 * Define as entidades e fornece acesso aos DAOs.
 */
@Database(
    entities = [CategoryItemLocal::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** DAO para operações na tabela CategoryItem */
    abstract fun categoryItemDao(): CategoryItemDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "app_db"

        /**
         * Retorna uma instância singleton da base de dados.
         *
         * Garante thread-safety usando double-checked locking com o bloco synchronized.
         *
         * @param context Contexto da aplicação para inicializar a instância.
         * @return Instância única de AppDatabase.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        /**
         * Cria uma nova instância do banco de dados Room.
         *
         * Inclui o fallback para destruição da base em caso de mudança de versão sem migração.
         *
         * @param context Contexto da aplicação.
         * @return Nova instância de AppDatabase.
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}
