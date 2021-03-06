// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.server;

import com.google.appinventor.server.flags.Flag;
import com.google.appinventor.server.storage.StorageIo;
import com.google.appinventor.server.storage.StorageIoInstanceHolder;
import com.google.appinventor.shared.rpc.user.Config;
import com.google.appinventor.shared.rpc.user.User;
import com.google.appinventor.shared.rpc.user.UserInfoService;

/**
 * Implementation of the user information service.
 *
 * <p>Note that this service must be state-less so that it can be run on
 * multiple servers.
 *
 */
public class UserInfoServiceImpl extends OdeRemoteServiceServlet implements UserInfoService {

  // Storage of user settings
  private final transient StorageIo storageIo = StorageIoInstanceHolder.INSTANCE;

  private static final long serialVersionUID = -7316312435338169166L;

  /**
   * Returns System Config, including user information record
   *
   */

  @Override
  public Config getSystemConfig(String sessionId) {
    Config config = new Config();
    User user = userInfoProvider.getUser();
    user.setSessionId(sessionId);
    storageIo.setUserSessionId(userInfoProvider.getUserId(), sessionId);
    Flag<String> rendezvousFlag = Flag.createFlag("use.rendezvousserver", "");
    if (!rendezvousFlag.get().equals("")) {
      config.setRendezvousServer(rendezvousFlag.get());
    }
    config.setUser(user);
    return config;
  }

  /**
   * Returns user information.
   *
   * (obsoleted by getSystemConfig())
   *
   * @return  user information record
   */

  @Override
  public User getUserInformation(String sessionId) {
    // This is a little evil here. We are fetching the User object
    // *and* side effecting it by storing the sessionId
    // A more pedagotically correct way would be to do the store
    // in a separate RPC. But that would add another round trip.
    User user = userInfoProvider.getUser();
    user.setSessionId(sessionId); // Store local copy
    // Store it in the data store
    storageIo.setUserSessionId(userInfoProvider.getUserId(), sessionId);
    return user;
  }

  /**
   * Returns user information based on userId.
   *
   * @return  user information record
   */
  @Override
  public User getUserInformationByUserId(String userId) {
    return storageIo.getUser(userId);
  }

  /**
   * Retrieves the user's settings.
   *
   * @return  user's settings
   */
  @Override
  public String loadUserSettings() {
    return storageIo.loadSettings(userInfoProvider.getUserId());
  }

  /**
   * Stores the user's settings.
   * @param settings  user's settings
   */
  @Override
  public void storeUserSettings(String settings) {
    storageIo.storeSettings(userInfoProvider.getUserId(), settings);
  }

  /**
   * Stores the user's name.
   * @param name  user's name
   */
  @Override
  public void storeUserName(String name) {
    storageIo.setUserName(userInfoProvider.getUserId(), name);
  }

  /**
   * Stores the user's link.
   * @param name  user's link
   */
  @Override
  public void storeUserLink(String link) {
    storageIo.setUserLink(userInfoProvider.getUserId(), link);
  }

  /**
   * Returns true if the current user has a user file with the given file name
   */
  @Override
  public boolean hasUserFile(String fileName) {
    return storageIo.getUserFiles(userInfoProvider.getUserId()).contains(fileName);
  }

  /**
   * Deletes the user file with the given file name
   */
  @Override
  public void deleteUserFile(String fileName) {
    storageIo.deleteUserFile(userInfoProvider.getUserId(), fileName);
  }
}
