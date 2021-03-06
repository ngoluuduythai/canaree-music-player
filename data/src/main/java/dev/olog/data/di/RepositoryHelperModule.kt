package dev.olog.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.ApplicationContext
import dev.olog.data.db.AppDatabase
import dev.olog.data.db.AppDatabaseMigrations.Migration_15_16
import dev.olog.data.db.AppDatabaseMigrations.Migration_16_17
import dev.olog.data.db.AppDatabaseMigrations.Migration_17_18
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class])
object RepositoryHelperModule {

    @Provides
    @Singleton
    @JvmStatic
    internal fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(Migration_15_16, Migration_16_17, Migration_17_18)
            .allowMainThreadQueries()
            .build()
    }

}