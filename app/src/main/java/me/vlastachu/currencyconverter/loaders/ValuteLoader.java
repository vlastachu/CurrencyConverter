package me.vlastachu.currencyconverter.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import me.vlastachu.currencyconverter.model.ValCurs;
import me.vlastachu.currencyconverter.model.Valute;
import me.vlastachu.currencyconverter.util.XmlParser;

public class ValuteLoader extends AsyncTaskLoader<ValCurs> {
    private final static String TAG = ValuteLoader.class.getSimpleName();
    private final static String filename = "ValCurs.xml";
    private ValCurs valCurs = null;

    public ValuteLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (valCurs != null && !valCurs.isTooOld()) {
            deliverResult(valCurs);
        } else {
            forceLoad();
        }
    }

    @Override
    public ValCurs loadInBackground() {
        try {
            File file = new File(getContext().getFilesDir(), filename);
            if (file.exists()) {
                ValCurs valCurs = XmlParser.read(getContext().openFileInput(filename));
                if (!valCurs.isTooOld())
                    return valCurs;
            }
            URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp");
            URLConnection connection = url.openConnection();
            connection.connect();
            ValCurs valCurs = XmlParser.read(connection.getInputStream());
            if (valCurs.getValutes().size() < 1) {
                // our app not support this case
                Log.e(TAG, "no valutes");
                throw new Exception();
            }
            // little hack
            valCurs.getValutes().add(new Valute("", 643, "RUB", 1, "Российский рубль", 1f));
            // cache
            XmlParser.write(valCurs, getContext().openFileOutput(filename, Context.MODE_PRIVATE));
            return valCurs;
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL specification changed", e);
        } catch (IOException e) {
            Log.i(TAG, "no connection", e);
        } catch (Exception e) {
            Log.i(TAG, "not xml", e);
        }
        return null;
    }

    @Override
    public void deliverResult(ValCurs data) {
        valCurs = data;
        super.deliverResult(data);
    }
}
