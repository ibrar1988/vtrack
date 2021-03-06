package com.perigrine.businesscardverification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.perigrine.Helper.Common;
import com.perigrine.Helper.PrintJobMonitorService;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class PrintActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView wv_print;
    private Button btn_print;
    private PrintManager mgr = null;
    SharedPreferences sharedPreferences;
    String htmlDocument;
    String primary_color;
    String logo_url,visitor_imagePath;

    ImageView imageView_back,imageView_logo;
    TextView textView_toolbar_title;
    View layout_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        sharedPreferences = getSharedPreferences("new_theme", MODE_PRIVATE);
        logo_url = sharedPreferences.getString("org_Logo", "");
        primary_color =  sharedPreferences.getString("primary_Color", "#F00025");
        wv_print = (WebView) findViewById(R.id.wv_print);
        btn_print = (Button) findViewById(R.id.btn_print);
        btn_print.setOnClickListener(this);
        layout_toolbar = findViewById(R.id.layout_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(primary_color));
        setSupportActionBar(toolbar);
        imageView_back = (ImageView)layout_toolbar.findViewById(R.id.imageView_back);
        imageView_logo = (ImageView)layout_toolbar.findViewById(R.id.imageView_logo);
        textView_toolbar_title = (TextView) layout_toolbar.findViewById(R.id.textView_toolbar_title);
        textView_toolbar_title.setText(getResources().getString(R.string.title_activity_print));
        imageView_back.setVisibility(View.GONE);

        if(!logo_url.isEmpty()){
            Picasso.with(PrintActivity.this)
                    .load(logo_url).placeholder(R.drawable.default_card).into(imageView_logo);
        }
        mgr = (PrintManager) getSystemService(PRINT_SERVICE);
        String orglogo=sharedPreferences.getString("org_Logo", "");

        String imagepath="file:///storage/emulated/0/Pictures/ABBYY Cloud OCR SDK Demo App/ScanImage.jpg";
        /*Bitmap myBitmap = BitmapFactory.decodeFile(imagepath);
        //Bitmap resizedbitmap = Bitmap.createScaledBitmap(myBitmap, 150, 100, true);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String dataURL= "data:image/png;base64," + imgageBase64;*/
       // System.out.println("dataURL:::::"+dataURL);
        //webview.loadUrl(dataURL); //pass the bitmap base64 dataurl in URL parameter

       // htmlDocument = ("<html><body><header><h1>Title</h1></header><section><img src=\""+imagepath+"\" style=width:100px;height:120px;float:right; align=left><p>Content</p></section></body></html>" );
        if(VisitorDetailsForm.vm.getDepartmentName_viewVisitor()!=null) {
            htmlDocument = ("<html><body><style>" +
                    " h1{text-align: center;} \n" +
                    "\n" +
                    "p.big {\n" +
                    "    height:5;\n" +
                    "}" +
                    "</style> <div style='border: 1px solid black;padding : 15px;width:60%; margin:auto;'>" +
                    "<header>" +
                    "<img src=" + orglogo + " style=width:80px;height:80px;float:left;>" +
                    "<section><img src=\"" + imagepath + "\" style=width:100px;height:120px;float:right; align=left>" +
                    "<h1> Visitor </h1>" +
                    "</section></header>" +
                    "<table style=width:100%>" +
                    "  <tr>" +
                    "    <td>Visitor Name:</td>" +
                    "    <td>" + VisitorDetailsForm.vm.getName() + "</td>" +
                    "  </tr>" +
                    "  <tr>" +
                    "    <td>Visitor Company Name:</td>" +
                    "    <td>" + VisitorDetailsForm.vm.getCompany() + "</td>" +
                    "  </tr>" +
                    "  <tr>" +
                    "    <td>Date of visit:</td>" +
                    "    <td>" + Common.getCurrentDateTime("MMM dd, yyyy hh:mm:ss a") + "</td>" +
                    "  </tr>" +
                    "   <tr>" +
                    "    <td>Host Name:</td>" +
                    "    <td>" + VisitorDetailsForm.vm.getHostName() + "</td>" +
                    "  </tr>" +
                    "   <tr>" +
                    "<tr>" +
                    "<td>Department</td>" +
                    "<td>" + VisitorDetailsForm.vm.getDepartmentName_viewVisitor() + "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td>Visit Purpose</td>" +
                    "<td>" + VisitorDetailsForm.vm.getPurpose() + "</td>" +
                    "</tr>" +
                    "</table> <p class=\"big\">\n" +
                    "</p>\n" +
                    "\n" +
                    "<table style=width:100%>\n" +
                    "<tr> \n" +
                    "                    <td>Visitor</td> \n" +
                    "                    <td>Employee</td> \n" +
                    "<td>Security</td>\n" +
                    "                  </tr> \n" +
                    "</table></div>" +
                    "</body>" +
                    "</html>");
        }else {
            htmlDocument = ("<html><body><style>" +
                    " h1{text-align: center;} \n" +
                    "\n" +
                    "p.big {\n" +
                    "    height:5;\n" +
                    "}" +
                    "</style> <div style='border: 1px solid black;padding : 15px;width:60%; margin:auto;'>" +
                    "<header>" +
                    "<img src=" + orglogo + " style=width:80px;height:80px;float:left;>" +
                    "<section><img src=\"" + imagepath + "\" style=width:100px;height:120px;float:right; align=left>" +
                    "<h1> Visitor </h1>" +
                    "</section></header>" +
                    "<table style=width:100%>" +
                    "  <tr>" +
                    "    <td>Visitor Name:</td>" +
                    "    <td>" + VisitorDetailsForm.vm.getName() + "</td>" +
                    "  </tr>" +
                    "  <tr>" +
                    "    <td>Visitor Company Name:</td>" +
                    "    <td>" + VisitorDetailsForm.vm.getCompany() + "</td>" +
                    "  </tr>" +
                    "  <tr>" +
                    "    <td>Date of visit:</td>" +
                    "    <td>" + Common.getCurrentDateTime("MMM dd, yyyy hh:mm:ss a") + "</td>" +
                    "  </tr>" +
                    "   <tr>" +
                    "    <td>Host Name:</td>" +
                    "    <td>" + VisitorDetailsForm.vm.getHostName() + "</td>" +
                    "  </tr>" +
                    "   <tr>" +
                    "<tr>" +
                    "<td>Visit Purpose</td>" +
                    "<td>" + VisitorDetailsForm.vm.getPurpose() + "</td>" +
                    "</tr>" +
                    "</table> <p class=\"big\">\n" +
                    "</p>\n" +
                    "\n" +
                    "<table style=width:100%>\n" +
                    "<tr> \n" +
                    "                    <td>Visitor</td> \n" +
                    "                    <td>Employee</td> \n" +
                    "<td>Security</td>\n" +
                    "                  </tr> \n" +
                    "</table></div>" +
                    "</body>" +
                    "</html>");
        }
        //wv_print.loadData(htmlDocument,"text/HTML", "UTF-8");
        wv_print.loadDataWithBaseURL(imagepath,
                htmlDocument,
                "text/html",
                "utf-8",
                "");
        wv_print.getSettings().setAllowFileAccess(true);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_print) {
          //  printWebPage();
            Intent in = new Intent(this, HomeVistorsList.class);
            startActivity(in);
        }
    }

    private void printWebPage() {
        WebView print = prepPrintWebView("Web Page");
        print.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
    }


    private WebView prepPrintWebView(final String name) {
        WebView result = getWebView();

        result.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                print(name, view.createPrintDocumentAdapter(),
                        new PrintAttributes.Builder().build());
            }
        });

        return (result);
    }

    private WebView getWebView() {
        if (wv_print == null) {
            wv_print = new WebView(this);
        }
        return (wv_print);
    }

    private PrintJob print(String name, PrintDocumentAdapter adapter,
                           PrintAttributes attrs) {
        startService(new Intent(this, PrintJobMonitorService.class));
        return (mgr.print(name, adapter, attrs));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_home) {
            Intent in = new Intent(this, HomeVistorsList.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
