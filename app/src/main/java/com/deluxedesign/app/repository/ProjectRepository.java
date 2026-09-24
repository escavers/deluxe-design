package com.deluxedesign.app.repository;

import androidx.lifecycle.LiveData;
import com.deluxedesign.app.domain.model.*;
import java.util.List;

public interface ProjectRepository {
  LiveData<List<Project>> projects(String userId);

  void saveProject(Project project, Result<Project> result);
}
