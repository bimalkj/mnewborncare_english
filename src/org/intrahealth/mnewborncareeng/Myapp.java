package org.intrahealth.mnewborncareeng;

import android.content.Context;

public class Myapp extends android.app.Application {

    private static Myapp instance;

    public Myapp() {
    	instance = this;
    }

    public static Context getContext() {
    	return instance;
    }


}
