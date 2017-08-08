package org.bbt.kiakoa.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.LoanContactDialog;
import org.bbt.kiakoa.dialog.LoanDateDialog;
import org.bbt.kiakoa.dialog.LoanItemDialog;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

import java.text.DateFormat;

/**
 * Activity displaying {@link LoanDetailsFragment}
 * Activity used only on normal layout (not large)
 *
 * @author Benoit Bousquet
 */
public class LoanDetailsFragment extends ListFragment implements LoanItemDialog.OnLoanItemSetListener, LoanDateDialog.OnLoanDateSetListener, LoanContactDialog.OnLoanContactSetListener {

    /**
     * For log
     */
    private static final String TAG = "LoanDetailsFragment";

    /**
     * To get the result of the permission read contact request
     */
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    /**
     * To get a picture for the item
     */
    private static final int REQUEST_CODE_GET_PICTURE_GALLERY = 234;

    /**
     * To get a picture for the item
     */
    private static final int REQUEST_CODE_GET_PICTURE_CAMERA = 345;

    /**
     * To get the result of the permission write external storage request
     */
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 456;

    /**
     * Current loan
     */
    private Loan loan;

    /**
     * Instance of used {@link LoanDetailsListAdapter}
     */
    private LoanDetailsListAdapter adapter;

    /**
     * Intent to take a picture from camera
     */
    private final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_details_list, container, false);

        // customize list
        ListView listView = view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty_element));
        adapter = new LoanDetailsListAdapter();
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * Set the loan to display in this fragment
     *
     * @param loan loan to display
     */
    public void setLoan(Loan loan) {
        this.loan = loan;
        updateView();
    }

    /**
     * Update the data of the view with current loan
     */
    private void updateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("loan", loan);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            setLoan((Loan) savedInstanceState.getParcelable("loan"));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.

        switch (position) {
            case 0:
                Log.i(TAG, "Item modification requested");
                showItemDialog();

                break;
            case 1:
                Log.i(TAG, "Requesting picture for the item");
                PackageManager packageManager = getContext().getPackageManager();
                // to check if we can display camera activity
                boolean camera = false;
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                    Log.i(TAG, "Requesting picture : use camera");
                    if (takePictureIntent.resolveActivity(packageManager) != null) {

                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                // Show an explanation to the user *asynchronously*
                                Log.i(TAG, "WRITE_EXTERNAL_STORAGE permission not granted");
                                Toast.makeText(getContext(), R.string.write_external_storage_required, Toast.LENGTH_SHORT).show();
                            } else {
                                // No explanation needed, we can request the permission.
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            }
                        } else {
                            startActivityForResult(takePictureIntent, REQUEST_CODE_GET_PICTURE_CAMERA);
                        }
                        camera = true;

                    } else {
                        Log.w(TAG, "Camera request failed");
                    }

                }

                if (!camera){

                    Log.i(TAG, "Requesting picture : use gallery");
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_picture_item, loan.getItem())), REQUEST_CODE_GET_PICTURE_GALLERY);

                }
                break;
            case 2:
                Log.i(TAG, "Loan date modification requested");
                showDateDialog();

                break;
            case 3:
                Log.i(TAG, "Contact modification requested");

                // check permission to read contacts
                boolean showDialog = true;
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                        // Show an explanation to the user *asynchronously*
                        Log.i(TAG, "READ_CONTACTS permission not granted");
                    } else {
                        // No explanation needed, we can request the permission.
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                        showDialog = false;
                    }
                }

                if (showDialog) {
                    showContactDialog();
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                Log.i(TAG, "Permission result received for read contact");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted !
                    Log.i(TAG, "READ_CONTACTS permission granted");
                } else {
                    // permission denied
                    Log.i(TAG, "READ_CONTACTS permission denied");
                }
                // in all cases we display contact dialog
                showContactDialog();
                break;
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                Log.i(TAG, "Permission result received for write external storage");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted !
                    Log.i(TAG, "WRITE_EXTERNAL_STORAGE permission granted");
                    startActivityForResult(takePictureIntent, REQUEST_CODE_GET_PICTURE_CAMERA);
                    // launch camera
                } else {
                    // permission denied
                    Log.i(TAG, "WRITE_EXTERNAL_STORAGE permission denied");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_PICTURE_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    loan.setItemPicture(data.getData().toString());
                    updateLoan();
                } else {
                    Log.i(TAG, "No picture returned from gallery");
                }
                break;
            case REQUEST_CODE_GET_PICTURE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Picture returned from camera");
                    Bundle extras = data.getExtras();
                    String uri = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), (Bitmap) extras.get("data"), loan.getItem(), "");
                    if (uri != null) {
                        Log.i(TAG, "Image added to media store");
                        loan.setItemPicture(uri);
                        updateLoan();
                    } else {
                        Log.e(TAG, "Image not added to media store :-(");
                    }
                } else {
                    Log.w(TAG, "No picture returned from camera");
                }
                break;
        }
    }

    /**
     * Display dialog to set the loan contact
     */
    private void showContactDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment contactDialog = getFragmentManager().findFragmentByTag("contact");
        if (contactDialog != null) {
            ft.remove(contactDialog);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        LoanContactDialog newContactDialog = LoanContactDialog.newInstance(loan.getContact());
        newContactDialog.setOnLoanContactSetListener(this);
        newContactDialog.show(ft, "contact");
    }

    /**
     * Display dialog to change the loan date
     */
    private void showDateDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment loanDateDialog = getFragmentManager().findFragmentByTag("loan_date");
        if (loanDateDialog != null) {
            ft.remove(loanDateDialog);
        }

        // Create and show the dialog
        LoanDateDialog newLoanDateDialog = LoanDateDialog.newInstance(loan.getLoanDate());
        newLoanDateDialog.setOnLoanDateSetListener(this);
        newLoanDateDialog.show(ft, "loan_date");
    }

    /**
     * Display dialog to enter a new item
     */
    private void showItemDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment itemDialog = getFragmentManager().findFragmentByTag("item");
        if (itemDialog != null) {
            ft.remove(itemDialog);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        LoanItemDialog newItemDialog = LoanItemDialog.newInstance(loan.getItem());
        newItemDialog.setOnLoanItemSetListener(this);
        newItemDialog.show(ft, "item");
    }

    @Override
    public void onItemSet(String item) {
        Log.i(TAG, "New item : " + item);
        loan.setItem(item);
        updateLoan();
    }

    @Override
    public void onDateSet(long loanDate) {
        Log.i(TAG, "New loan date : " + loanDate);
        loan.setLoanDate(loanDate);
        updateLoan();
    }

    @Override
    public void onContactSet(Contact contact) {
        Log.i(TAG, "New loan contact : " + contact);
        loan.setContact(contact);
        updateLoan();
    }

    /**
     * Update loan and notify for changes
     */
    private void updateLoan() {
        LoanLists.getInstance().updateLoan(loan, getContext());
        adapter.notifyDataSetChanged();
        LoanLists.getInstance().notifyLoanListsChanged();
    }

    /**
     * Adapter in charge of displaying {@link Loan} details.
     * Here is the details displayed :
     * <ol>
     * <li>item</li>
     * <li>loan date</li>
     * </ol>
     */
    private class LoanDetailsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (loan == null) {
                return 0;
            } else {
                return 4;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.adapter_loan_detail_item, viewGroup, false);

                // Creates a ViewHolder
                holder = new LoanDetailsListAdapter.ViewHolder();
                holder.icon = view.findViewById(R.id.icon);
                holder.description = view.findViewById(R.id.description);
                holder.value = view.findViewById(R.id.value);
                holder.image = view.findViewById(R.id.image);

                view.setTag(holder);
            } else {
                // Get the ViewHolder
                holder = (ViewHolder) view.getTag();
            }

            // Bind the data efficiently with the holder.
            switch (i) {
                case 0:
                    // item
                    holder.icon.setImageResource(R.drawable.ic_item_24dp);
                    holder.description.setText(R.string.item);
                    holder.value.setText(loan.getItem());
                    holder.image.setVisibility(View.GONE);
                    break;
                case 1:
                    // loan item picture
                    holder.description.setText(R.string.picture);
                    holder.icon.setImageResource(R.drawable.ic_picture_24dp);
                    String pictureUri = loan.getItemPicture();
                    if (pictureUri != null) {
                        holder.image.setImageURI(Uri.parse(pictureUri));
                        holder.image.setVisibility(View.VISIBLE);
                        holder.value.setVisibility(View.INVISIBLE);
                    } else {
                        holder.image.setVisibility(View.GONE);
                        holder.value.setVisibility(View.VISIBLE);
                        holder.value.setText(R.string.no_picture);
                    }
                    break;
                case 2:
                    // loan date
                    holder.icon.setImageResource(R.drawable.ic_event_24dp);
                    holder.description.setText(R.string.loan_date);
                    holder.value.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(loan.getLoanDate()));
                    holder.image.setVisibility(View.GONE);
                    break;
                case 3:
                    // loan contact
                    holder.description.setText(R.string.contact);
                    holder.image.setVisibility(View.GONE);
                    holder.icon.setImageResource(R.drawable.ic_contact_24dp);
                    // contact details
                    Contact contact = loan.getContact();
                    if (contact != null) {
                        // set name if possible
                        holder.value.setText(contact.getName());
                        // set image if possible
                        String photoUri = contact.getPhotoUri();
                        if (photoUri != null) {
                            holder.image.setImageURI(Uri.parse(photoUri));
                            holder.image.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.value.setText(R.string.no_contact);
                    }
                    break;
            }

            return view;
        }

        private class ViewHolder {
            ImageView icon;
            TextView description;
            TextView value;
            ImageView image;
        }
    }
}
