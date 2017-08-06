package org.bbt.kiakoa.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.LendContactDialog;
import org.bbt.kiakoa.dialog.LendDateDialog;
import org.bbt.kiakoa.dialog.LendItemDialog;
import org.bbt.kiakoa.model.Contact;
import org.bbt.kiakoa.model.Lend;
import org.bbt.kiakoa.model.LendLists;

import java.text.DateFormat;

/**
 * Activity displaying {@link LendDetailsFragment}
 * Activity used only on normal layout (not large)
 *
 * @author Benoit Bousquet
 */
public class LendDetailsFragment extends ListFragment implements LendItemDialog.OnLendItemSetListener, LendDateDialog.OnLendDateSetListener, LendContactDialog.OnLendContactSetListener {

    /**
     * For log
     */
    private static final String TAG = "LendDetailsFragment";

    /**
     * To get the result of the permission request
     */
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    /**
     * To get a picture for the item
     */
    private static final int REQUEST_CODE_GET_PICTURE = 234;

    /**
     * Current lend
     */
    private Lend lend;

    /**
     * Instance of used {@link LendDetailsListAdapter}
     */
    private LendDetailsListAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lend_details_list, container, false);

        // customize list
        ListView listView = view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty_element));
        adapter = new LendDetailsListAdapter();
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * Set the lend to display in this fragment
     *
     * @param lend lend to display
     */
    public void setLend(Lend lend) {
        this.lend = lend;
        updateView();
    }

    /**
     * Update the data of the view with current lend
     */
    private void updateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("lend", lend);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            setLend((Lend) savedInstanceState.getParcelable("lend"));
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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_picture_item, lend.getItem())), REQUEST_CODE_GET_PICTURE);
                break;
            case 2:
                Log.i(TAG, "Lend date modification requested");
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
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                Log.i(TAG, "Permission result received");
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_PICTURE:
                if(resultCode==Activity.RESULT_OK)
                {
                    Log.i(TAG, "Picture returned");
                    lend.setItemPicture(data.getData().toString());
                    updateLend();
                } else {
                    Log.i(TAG, "No picture returned");
                }
                break;
        }
    }

    /**
     * Display dialog to set the lend contact
     */
    private void showContactDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment contactDialog = getFragmentManager().findFragmentByTag("contact");
        if (contactDialog != null) {
            ft.remove(contactDialog);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        LendContactDialog newContactDialog = LendContactDialog.newInstance(lend.getContact());
        newContactDialog.setOnLendContactSetListener(this);
        newContactDialog.show(ft, "contact");
    }

    /**
     * Display dialog to change the lend date
     */
    private void showDateDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment lendDateDialog = getFragmentManager().findFragmentByTag("lend_date");
        if (lendDateDialog != null) {
            ft.remove(lendDateDialog);
        }

        // Create and show the dialog
        LendDateDialog newLendDateDialog = LendDateDialog.newInstance(lend.getLendDate());
        newLendDateDialog.setOnLendDateSetListener(this);
        newLendDateDialog.show(ft, "lend_date");
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
        LendItemDialog newItemDialog = LendItemDialog.newInstance(lend.getItem());
        newItemDialog.setOnLendItemSetListener(this);
        newItemDialog.show(ft, "item");
    }

    @Override
    public void onItemSet(String item) {
        Log.i(TAG, "New item : " + item);
        lend.setItem(item);
        updateLend();
    }

    @Override
    public void onDateSet(long lendDate) {
        Log.i(TAG, "New lend date : " + lendDate);
        lend.setLendDate(lendDate);
        updateLend();
    }

    @Override
    public void onContactSet(Contact contact) {
        Log.i(TAG, "New lend contact : " + contact);
        lend.setContact(contact);
        updateLend();
    }

    /**
     * Update lend and notify for changes
     */
    private void updateLend() {
        LendLists.getInstance().updateLend(lend, getContext());
        adapter.notifyDataSetChanged();
        LendLists.getInstance().notifyLendListsChanged();
    }

    /**
     * Adapter in charge of displaying {@link Lend} details.
     * Here is the details displayed :
     * <ol>
     * <li>item</li>
     * <li>lend date</li>
     * </ol>
     */
    private class LendDetailsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (lend == null) {
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
                view = getActivity().getLayoutInflater().inflate(R.layout.adapter_lend_detail_item, viewGroup, false);

                // Creates a ViewHolder
                holder = new LendDetailsListAdapter.ViewHolder();
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
                    holder.value.setText(lend.getItem());
                    holder.image.setVisibility(View.GONE);
                    break;
                case 1:
                    // lend item picture
                    holder.description.setText(R.string.picture);
                    holder.icon.setImageResource(R.drawable.ic_picture_24dp);
                    String pictureUri = lend.getItemPicture();
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
                    // lend date
                    holder.icon.setImageResource(R.drawable.ic_event_24dp);
                    holder.description.setText(R.string.lend_date);
                    holder.value.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(lend.getLendDate()));
                    holder.image.setVisibility(View.GONE);
                    break;
                case 3:
                    // lend contact
                    holder.description.setText(R.string.contact);
                    holder.image.setVisibility(View.GONE);
                    holder.icon.setImageResource(R.drawable.ic_contact_24dp);
                    // contact details
                    Contact contact = lend.getContact();
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
