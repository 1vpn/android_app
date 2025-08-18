package com.one.vpnapp.handler

import com.tencent.mmkv.MMKV
import com.one.vpnapp.model.UserData
import com.one.vpnapp.model.Location
import com.google.gson.Gson

object MmkvManager {
    private const val KEY_USER_DATA = "USER_DATA"
    private const val HAS_GIVEN_RATING = "HAS_GIVEN_RATING"
    private const val KEY_SELECTED_LOCATION = "SELECTED_LOCATION"
    private val mmkv: MMKV = MMKV.mmkvWithID("1vpn_mmkv")
    private val gson = Gson()

    fun getUserData(): UserData? {
        val userDataJson = mmkv.decodeString(KEY_USER_DATA) ?: return null
        return gson.fromJson(userDataJson, UserData::class.java)
    }

    fun setUserData(userData: UserData) {
        mmkv.encode(KEY_USER_DATA, gson.toJson(userData))
    }

    fun removeUserData() {
        mmkv.remove(KEY_USER_DATA)
    }

    fun hasGivenRating(): Boolean {
        return mmkv.decodeBool(HAS_GIVEN_RATING, false)
    }

    fun setHasGivenRating(value: Boolean) {
        mmkv.encode(HAS_GIVEN_RATING, value)
    }

    fun getSelectedLocation(): Location? {
        val locationJson = mmkv.decodeString(KEY_SELECTED_LOCATION) ?: return null
        return gson.fromJson(locationJson, Location::class.java)
    }

    fun setSelectedLocation(location: Location) {
        mmkv.encode(KEY_SELECTED_LOCATION, gson.toJson(location))
    }

    fun removeSelectedLocation() {
        mmkv.remove(KEY_SELECTED_LOCATION)
    }
}