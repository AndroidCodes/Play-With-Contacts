package com.example.androidcodes.contactsexample;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidcodes.contactsexample.adapter.ContactListAdapter;
import com.example.androidcodes.contactsexample.customviews.FloatingActionMenu;
import com.example.androidcodes.contactsexample.model.ContactDetails;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, View.OnClickListener {

    private static final int CONTACT_READ_WRITE_CODE = 1;

    private ArrayList<ContactDetails> contactDetailsList = null;

    private Activity activity;

    private FloatingActionMenu fam_options = null;

    private RecyclerView rv_contactList;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.
                FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_main);

        activity = MainActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        fam_options = (FloatingActionMenu) findViewById(R.id.fam_options);
        fam_options.setClosedOnTouchOutside(true);

        findViewById(R.id.fab_sync).setOnClickListener(this);

        findViewById(R.id.fab_create_contact).setOnClickListener(this);

        findViewById(R.id.fab_update_contact).setOnClickListener(this);

        findViewById(R.id.fab_delete_contact).setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(this);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        view.findViewById(R.id.tv_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(activity, "You Clicked Name .. ", Toast.LENGTH_SHORT).show();

            }
        });

        view.findViewById(R.id.tv_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(activity, "You Clicked Address .. ", Toast.LENGTH_SHORT).show();

            }
        });

        rv_contactList = (RecyclerView) findViewById(R.id.rv_contactList);
        rv_contactList.setLayoutManager(new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false));
        rv_contactList.setHasFixedSize(true);
        rv_contactList.setItemAnimator(new DefaultItemAnimator());

        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fatching Contacts...");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkReadWriteContactPermission();

        } else {

            progressDialog.show();

            Executors.newSingleThreadExecutor().execute(new Runnable() {

                @Override
                public void run() {

                    readContacts();

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    public void readContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        contactDetailsList = new ArrayList<>();
        contactDetailsList.clear();

        if (cur.getCount() > 0) {

            String name = "", phone = "";

            while (cur.moveToNext()) {

                ContactDetails contactDetails = new ContactDetails();

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.
                        DISPLAY_NAME));

                contactDetails.setName(name);

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.
                        HAS_PHONE_NUMBER))) > 0) {

                    System.out.println("CustomLogger --> name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {

                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Phone.NUMBER));

                        contactDetails.setNumber(phone);

                        System.out.println("CustomLogger --> phone : " + phone);

                    }

                    contactDetailsList.add(contactDetails);

                    //pCur.close();

                    // get email and type
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {

                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Email.DATA));

                        String emailType = emailCur.getString(emailCur.
                                getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        System.out.println("CustomLogger --> Email : " + email + " Email Type : " +
                                emailType);

                    }
                    //emailCur.close();

                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                            ContactsContract.Data.MIMETYPE + " = ?";

                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere,
                            noteWhereParams, null);
                    if (noteCur.moveToFirst()) {

                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Note.NOTE));

                        System.out.println("CustomLogger --> Note : " + note);

                    }
                    //noteCur.close();

                    //Get Postal Address....
                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                            ContactsContract.Data.MIMETYPE + " = ?";
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI, null, null, null,
                            null);
                    while (addrCur.moveToNext()) {

                        String poBox = addrCur.getString(addrCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.StructuredPostal.POBOX));
                        String street = addrCur.getString(addrCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.StructuredPostal.STREET));
                        String city = addrCur.getString(addrCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.StructuredPostal.CITY));
                        String state = addrCur.getString(addrCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.StructuredPostal.REGION));
                        String postalCode = addrCur.getString(addrCur.
                                getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.
                                        POSTCODE));
                        String country = addrCur.getString(addrCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.StructuredPostal.COUNTRY));
                        String type = addrCur.getString(addrCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.StructuredPostal.TYPE));

                        // Do something with these....

                    }
                    //addrCur.close();

                    // Get Instant Messenger.........
                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                            ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI, null, imWhere,
                            imWhereParams, null);
                    if (imCur.moveToFirst()) {

                        String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Im.DATA));
                        String imType = imCur.getString(imCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Im.TYPE));

                    }
                    //imCur.close();

                    // Get Organizations.........
                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                            ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, null, orgWhere,
                            orgWhereParams, null);
                    if (orgCur.moveToFirst()) {

                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Organization.DATA));

                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Organization.TITLE));

                    }
                    //orgCur.close();
                }
            }
        }

        if (contactDetailsList != null && !contactDetailsList.isEmpty()) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    progressDialog.dismiss();

                    rv_contactList.setAdapter(new ContactListAdapter(activity, contactDetailsList));

                }
            });
        }
    }

    private void checkReadWriteContactPermission() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.
                    READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(activity, "Got Read Permission", Toast.LENGTH_SHORT).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.
                        READ_CONTACTS}, CONTACT_READ_WRITE_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            readContacts();

        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Toast.makeText(activity, "Got Write Permission", Toast.LENGTH_SHORT).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.
                        WRITE_CONTACTS}, CONTACT_READ_WRITE_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case CONTACT_READ_WRITE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Executors.newSingleThreadExecutor().execute(new Runnable() {

                        @Override
                        public void run() {

                            readContacts();

                        }
                    });
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void displayContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.
                                CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(activity, "Name: " + name + ", Phone No: " + phoneNo,
                                Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }
            }
        }
    }

    private void createContact(String name, String phone) {
        ContentResolver cr = getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String existName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (existName.contains(name)) {
                    Toast.makeText(activity, "The contact name: " + name + " already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "accountname@gmail.com")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "com.google")
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());


        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(activity, "Created a new contact with name: " + name + " and Phone No: " + phone, Toast.LENGTH_SHORT).show();

    }

    private void updateContact(String name, String phone) {

        ContentResolver cr = getContentResolver();

        String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ? AND " +
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";
        String[] params = new String[]{name,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)};

        Cursor phoneCur = managedQuery(ContactsContract.Data.CONTENT_URI, null, where, params, null);

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        if ((null == phoneCur)) {
            createContact(name, phone);
        } else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).
                    withSelection(where, params).withValue(ContactsContract.CommonDataKinds.Phone.
                    DATA, phone).build());
        }

        phoneCur.close();

        try {

            cr.applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(activity, "Updated the phone number of 'Sample Name' to: " + phone,
                Toast.LENGTH_SHORT).show();

    }

    private void deleteContact(String name) {

        ContentResolver cr = getContentResolver();
        String where = ContactsContract.Data.DISPLAY_NAME + " = ? ";
        String[] params = new String[]{name};

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(where, params).build());

        try {

            cr.applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException | OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(activity, "Deleted the contact with name '" + name + "'", Toast.LENGTH_SHORT).
                show();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_sync:

                readContacts();

                break;

            case R.id.fab_create_contact:

                createContact("Temporery", "123457890");

                break;

            case R.id.fab_update_contact:

                updateContact("Temporery", "123457890");

                break;

            case R.id.fab_delete_contact:

                deleteContact("Temporery");

                break;

            default:

                break;

        }

        readContacts();

        fam_options.close(true);

    }
}
