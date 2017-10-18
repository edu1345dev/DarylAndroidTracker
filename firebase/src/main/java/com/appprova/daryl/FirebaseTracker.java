package com.appprova.daryl;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.HashMap;
import java.util.Map;

public class FirebaseTracker implements TrackerAdapter {

    private final FirebaseAnalytics tracker;

    public FirebaseTracker(FirebaseAnalytics tracker) {
        this.tracker = tracker;
    }

    @Override
    public void logPageView(final String name) {
        final Bundle bundle = new Bundle();
        bundle.putString("pageName", sanitize(name));
        this.tracker.logEvent("pageView", bundle);
        FirebaseCrash.log("pageView: " + sanitize(name));
    }

    @Override
    public void logEvent(final Map<String, Object> eventData) {
        final Map<String, Object> eventDataCopy = new HashMap<>(eventData);
        String eventName = (String) eventDataCopy.get(Constants.EVENT_NAME);
        eventDataCopy.remove(Constants.EVENT_NAME);

        Bundle bundle = new Bundle();
        for (Map.Entry<String, ?> entry : eventDataCopy.entrySet()) {
            if (entry.getValue() != null) {
                bundle.putString(entry.getKey(), this.sanitize(entry.getValue()));
            }
        }

        this.tracker.logEvent(this.sanitize(eventName), bundle);
        FirebaseCrash.log(EventToStringConverter.toString(eventDataCopy));
    }

    @Override
    public void setUserProperty(String key, Object value) {
        switch (key) {
            case Constants.USER_PROPERTY_ID:
                this.tracker.setUserId(value.toString());
            default:
                this.tracker.setUserProperty(key, this.sanitize(value));
        }
    }

    private String sanitize(final Object value) {
        return ("" + value)
                .replace("/", "_")
                .replace("-", "_")
                .replace(" ", "_");
    }

    @Override
    public void logException(Throwable e) {
        FirebaseCrash.report(e);
    }
}
