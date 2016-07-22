package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock;

import com.extraditables.los.brokin.brokin_old.db.DatabaseHelper;
import com.extraditables.los.brokin.brokin_old.models.UserStockModel;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.tools.CrashReportTool;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import javax.inject.Inject;

public class DatabaseStockRepository implements LocalStockRepository {

  private final DatabaseHelper databaseHelper;
  private final CrashReportTool crashReportTool;

  @Inject public DatabaseStockRepository(DatabaseHelper databaseHelper,
      CrashReportTool crashReportTool) {
    this.databaseHelper = databaseHelper;
    this.crashReportTool = crashReportTool;
  }

  @Override public void setStock(UserStockModel userStockModel) {
    Dao dao;
    try {
      dao = databaseHelper.getUserStockModelDao();
      dao.create(userStockModel);
    } catch (SQLException e) {
      crashReportTool.logException(e);
    }
    OpenHelperManager.releaseHelper();
  }

  @Override public void remove(UserStockModel stock) {
    try {
      Dao dao = databaseHelper.getUserStockModelDao();
      dao.delete(stock);
    } catch (SQLException e) {
      crashReportTool.logException(e);
    }
    OpenHelperManager.releaseHelper();
  }
}
