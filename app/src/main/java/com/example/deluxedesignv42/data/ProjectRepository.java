package com.example.deluxedesignv42.data;

import android.content.Context;
import com.example.deluxedesignv42.data.local.AppDatabase;
import com.example.deluxedesignv42.data.local.ProjectDao;
import com.example.deluxedesignv42.data.local.ProjectEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectRepository {
    private static ProjectRepository instance;
    private final ProjectDao projectDao;
    private final ExecutorService executorService;

    public interface OnProjectLoadedListener<T> {
        void onLoaded(T result);
    }

    private ProjectRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        projectDao = db.projectDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public static synchronized ProjectRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProjectRepository(context);
        }
        return instance;
    }

    public void getAllProjects(OnProjectLoadedListener<List<ProjectEntity>> listener) {
        executorService.execute(() -> {
            List<ProjectEntity> projects = projectDao.getAllProjects();
            listener.onLoaded(projects);
        });
    }

    public void getProjectById(int id, OnProjectLoadedListener<ProjectEntity> listener) {
        executorService.execute(() -> {
            ProjectEntity project = projectDao.getProjectById(id);
            listener.onLoaded(project);
        });
    }

    public void saveProject(ProjectEntity project, OnProjectLoadedListener<Long> listener) {
        executorService.execute(() -> {
            long newId = projectDao.insert(project);
            listener.onLoaded(newId);
        });
    }

    public void updateProject(ProjectEntity project, Runnable onComplete) {
        executorService.execute(() -> {
            projectDao.update(project);
            onComplete.run();
        });
    }

    public void getAllQuotes(OnProjectLoadedListener<List<com.example.deluxedesignv42.data.local.QuoteEntity>> listener) {
        executorService.execute(() -> {
            List<com.example.deluxedesignv42.data.local.QuoteEntity> quotes = projectDao.getAllQuotes();
            listener.onLoaded(quotes);
        });
    }

    public void saveQuote(com.example.deluxedesignv42.data.local.QuoteEntity quote, OnProjectLoadedListener<Long> listener) {
        executorService.execute(() -> {
            long newId = projectDao.insertQuote(quote);
            listener.onLoaded(newId);
        });
    }
}