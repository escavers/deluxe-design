package com.deluxedesign.app.repository;

import androidx.lifecycle.LiveData;
import com.deluxedesign.app.domain.model.*;
import java.util.List;

public interface QuoteRepository {
  LiveData<List<Quote>> quotes(String userId);

  void saveQuote(Quote quote, Result<Quote> result);
}
