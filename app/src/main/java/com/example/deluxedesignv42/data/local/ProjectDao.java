package com.example.deluxedesignv42.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY dateLong DESC")
    List<ProjectEntity> getAllProjects();

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    ProjectEntity getProjectById(int id);

    @Insert
    long insert(ProjectEntity project);

    @Update
    void update(ProjectEntity project);

    // Métodos para cotizaciones adjuntos al DAO para consistencia de Room
    @Query("SELECT * FROM quotes ORDER BY dateLong DESC")
    List<QuoteEntity> getAllQuotes();

    @Insert
    long insertQuote(QuoteEntity quote);
}