package com.deltaworks.damlink;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.deltaworks.damlink.model.TokenModel;
import com.deltaworks.damlink.retrofit.RetrofitLib;
import com.deltaworks.damlink.retrofit.RetrofitService;

import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Call;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.deltaworks.damlink", appContext.getPackageName());
    }
}


