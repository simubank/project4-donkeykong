package com.levelup.td.tdlevelupquest.Utils;

import org.json.JSONObject;

/**
 * Created by Mikeb on 7/17/2018.
 */

public interface APICallback {
    void onResponse(boolean success, JSONObject object); // Params are self-defined and added to suit your needs.
}