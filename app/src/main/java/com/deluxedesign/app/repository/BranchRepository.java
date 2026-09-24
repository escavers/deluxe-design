package com.deluxedesign.app.repository;

import androidx.lifecycle.LiveData;
import com.deluxedesign.app.domain.model.*;
import java.util.List;

public interface BranchRepository {
  LiveData<List<Branch>> branches();
}
