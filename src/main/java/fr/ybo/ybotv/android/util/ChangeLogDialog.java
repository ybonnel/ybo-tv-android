package fr.ybo.ybotv.android.util;

/*
 * Class to show a changelog dialog
 * (c) 2012 Martin van Zuilekom (http://martin.cubeactive.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ChangeLogDialog {
    static final private String TAG = "ChangeLogDialog";

    static final private String TITLE_CHANGELOG = "title_changelog";
    static final private String CHANGELOG_XML = "changelog";

    private Activity acctivity;

    public ChangeLogDialog(Activity context) {
        acctivity = context;
    }

    //Get the current app version
    public String getAppVersion() {
        try {
            PackageInfo info = acctivity.getPackageManager().getPackageInfo(acctivity.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    //Parse a the release tag and return html code
    private String parseReleaseTag(XmlResourceParser xml) throws XmlPullParserException, IOException {
        StringBuilder result = new StringBuilder("<h1>Version : ");
        result.append(xml.getAttributeValue(null, "version"));
        result.append("</h1><ul>");
        int eventType = xml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xml.getName().equals("change"))) {
            if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("change"))) {
                xml.next();
                result.append("<li>");
                result.append(xml.getText());
                result.append("</li>");
            }
            eventType = xml.next();
        }
        result.append("</ul>");
        return result.toString();
    }

    //CSS style for the html
    private String getStyle() {
        return
                "<style type=\"text/css\">"
                        + "h1 { margin-left: 0px; font-size: 12pt; }"
                        + "li { margin-left: 0px; font-size: 9pt;}"
                        + "ul { padding-left: 30px;}"
                        + "</style>";
    }

    //Get the changelog in html code, this will be shown in the dialog's webview
    private String getHTMLChangelog(int aResourceId, Resources aResource) {
        StringBuilder result = new StringBuilder("<html><head>");
        result.append(getStyle());
        result.append("</head><body>");
        XmlResourceParser xml = aResource.getXml(aResourceId);
        try {
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("release"))) {
                    result.append(parseReleaseTag(xml));

                }
                eventType = xml.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            xml.close();
        }
        result.append("</body></html>");
        return result.toString();
    }

    //Call to show the changelog dialog
    public void show() {
        //Get resources
        String packageName = acctivity.getPackageName();
        Resources resource;
        try {
            resource = acctivity.getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        //Get dialog title
        int resId = resource.getIdentifier(TITLE_CHANGELOG, "string", packageName);
        StringBuilder title = new StringBuilder(resource.getString(resId));
        title.append(" v");
        title.append(getAppVersion());

        //Get Changelog xml resource id
        resId = resource.getIdentifier(CHANGELOG_XML, "xml", packageName);
        //Create html change log
        String html = replaceHtmlAccents(getHTMLChangelog(resId, resource));

        //Get button strings
        String close = resource.getString(R.string.changelog_close);

        //Check for empty changelog
        if (html.length() == 0) {
            //Could not load change log, message user and exit void
            Toast.makeText(acctivity, "Could not load change log", Toast.LENGTH_SHORT).show();
            return;
        }

        //Create webview and load html
        WebView webView = new WebView(acctivity);
        webView.loadData(html, "text/html", null);
        AlertDialog.Builder builder = new AlertDialog.Builder(acctivity)
                .setTitle(title)
                .setView(webView)
                .setPositiveButton(close, new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    private static String replaceHtmlAccents(String html) {
        return html.replaceAll("@", "&");
    }

}