/**
 * Copyright (C) 2016 Arturo Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.losextraditables.brokin.re_brokin.android.infrastructure.injector.module;

import android.content.Context;
import com.losextraditables.brokin.AndroidApplication;
import com.losextraditables.brokin.UIThread;
import com.losextraditables.brokin.brokin_old.db.DatabaseHelper;
import com.losextraditables.brokin.re_brokin.android.infrastructure.notifications.NotificationsManager;
import com.losextraditables.brokin.re_brokin.android.infrastructure.notifications.NotificationsTool;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock.DatabaseStockRepository;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock.LocalStockRepository;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock.RemoteStockRepository;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock.ServiceRemoteStockRepository;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.user.DatabaseUserRepository;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.user.UserRepository;
import com.losextraditables.brokin.re_brokin.android.infrastructure.tools.CrashReportTool;
import com.losextraditables.brokin.re_brokin.android.infrastructure.tools.CrashlyticsReportTool;
import com.losextraditables.brokin.re_brokin.core.infrastructure.executor.JobExecutor;
import com.losextraditables.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.losextraditables.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class ApplicationModule {

  private final AndroidApplication application;

  public ApplicationModule(AndroidApplication application) {
    this.application = application;
  }

  @Provides @Singleton Context provideContext() {
    return application;
  }

  @Provides @Singleton ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
    return jobExecutor;
  }

  @Provides @Singleton PostExecutionThread providePostExecutionThread(UIThread uiThread) {
    return uiThread;
  }

  @Provides DatabaseHelper provideDatabaseHelper() {
    return OpenHelperManager.getHelper(application, DatabaseHelper.class);
  }

  @Provides @Singleton RemoteStockRepository provideRemoteStockRepository(
      ServiceRemoteStockRepository serviceStockRepository) {
    return serviceStockRepository;
  }

  @Provides @Singleton LocalStockRepository provideLocalStockRepository(
      DatabaseStockRepository databaseStockRepository) {
    return databaseStockRepository;
  }

  @Provides @Singleton UserRepository provideUserRepository(
      DatabaseUserRepository databaseUserRepository) {
    return databaseUserRepository;
  }

  @Provides @Singleton CrashReportTool provideCrashReportTool() {
    return new CrashlyticsReportTool();
  }

  @Provides @Singleton NotificationsManager provideNotificationsManager() {
    return new NotificationsTool(application);
  }
}