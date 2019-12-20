package com.cleanandcomfort.smtrick.user_laundryapp.Repository;

import com.cleanandcomfort.smtrick.user_laundryapp.CallBack.CallBack;
import com.cleanandcomfort.smtrick.user_laundryapp.Models.Requests;
import com.cleanandcomfort.smtrick.user_laundryapp.Models.UserServices;

import java.util.Map;

public interface LeedRepository {


    void deleteLeed(final String leedId, final CallBack callback);

    void updateLeed(final String leedId, final Map leedsMap, final CallBack callBack);

    void updateUser(final String userid, final Map leedsMap, final CallBack callBack);


    void readRequestUser(final String userId, final CallBack callback);

    void readActiveUser(final String userId, final CallBack callback);

    void updateRelative(final String leedId, final Map leedsMap, final CallBack callBack);

    void updateUserProfile(final String leedId, final Map leedsMap, final CallBack callBack);

    void readUserById(final String userId, final CallBack callback);

    void readServicesByUserId(final String userId, final CallBack callback);

    void readServicesByName(final String serviceName, final CallBack callback);

    void createUserServices(final UserServices serviceId, final CallBack callback);

    void sendRequest(final Requests request, final CallBack callback);

    void updateRequest(final String leedId, final Map leedsMap, final CallBack callBack);

    void readAllServices(final CallBack callback);

    void readAllRequests(final CallBack callback);
}
