package org.bbt.kiakoa.fragment.LoanDetails;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import org.bbt.kiakoa.activity.LoanDetailsActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.ColorPickerDialog;
import org.bbt.kiakoa.dialog.DeleteLoanDialog;
import org.bbt.kiakoa.dialog.LoanContactDialog;
import org.bbt.kiakoa.dialog.LoanDateDialog;
import org.bbt.kiakoa.dialog.LoanItemDialog;
import org.bbt.kiakoa.dialog.PictureDialog;
import org.bbt.kiakoa.dialog.ReturnDateDialog;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;

/**
 * Activity displaying {@link LoanDetailsFragment}
 * Activity used only on normal layout (not large)
 *
 * @author Benoit Bousquet
 */
public class LoanDetailsFragment extends ListFragment implements LoanLists.OnLoanListsChangedListener, AdapterView.OnItemClickListener {

    /**
     * For log
     */
    private static final String TAG = "LoanDetailsFragment";

    /**
     * To get the result of the permission read contact request
     */
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    /**
     * To get the result of the permission write external storage request
     */
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 456;

    /**
     * Current loan
     */
    private Loan loan;

    /**
     * {@link MenuItem} to display contact card of the displayed loan
     */
    private MenuItem contactCardMenuItem;

    /**
     * {@link MenuItem} to delete the displayed loan
     */
    private MenuItem deleteMenuItem;

    /**
     * {@link MenuItem} to create a calendar event
     */
    private MenuItem createCalendarMenuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loan_details_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListAdapter(new LoanDetailsAdapter());
        getListView().setOnItemClickListener(this);
        getListView().setEmptyView(view.findViewById(R.id.empty_element));
    }

    @Override
    public void onResume() {
        super.onResume();
        LoanLists.getInstance().registerOnLoanListsChangedListener(this, TAG);
        updateView();
    }

    @Override
    public void onPause() {
        super.onPause();
        LoanLists.getInstance().unregisterOnLoanListsChangedListener(this, TAG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.loan_details, menu);
        // retrieve instance of contact menu item
        contactCardMenuItem = menu.findItem(R.id.action_contact_card);
        // retrieve instance of create calendar event menu item
        createCalendarMenuItem = menu.findItem(R.id.action_calendar_event);
        // retrieve instance of delete menu item
        deleteMenuItem = menu.findItem(R.id.action_delete);
        // update menu item visibility
        updateView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact_card:
                loan.displayContactCard(getContext());
                return true;
            case R.id.action_calendar_event:
                createCalendarEvent();
                return true;
            case R.id.action_delete:
                showDeleteLoanDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set the loan to display in this fragment
     *
     * @param loan loan to display
     */
    public void setLoan(Loan loan) {
        Log.d(TAG, "Displaying loan details : " + ((loan == null) ? "null" : loan.getItem()));
        this.loan = loan;
        updateView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        switch (position) {
            case 0:
                Log.i(TAG, "Item modification requested");
                showItemDialog();

                break;
            case 1:
                Log.i(TAG, "returned status change requested");
                loan.toggleReturnedStatus();
                LoanLists.getInstance().updateLoan(loan, getContext());
                break;
            case 2:
                Log.i(TAG, "Requesting picture for the item");
                // check for write external storage permission
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
                    showPictureDialog();
                }

                break;
            case 3:
                Log.i(TAG, "Loan date modification requested");
                showDateDialog();

                break;
            case 4:
                Log.i(TAG, "Loan date modification requested");
                showDateReturnDialog();

                break;
            case 5:
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
            case 6:
                Log.i(TAG, "Color modification requested");
                showColorPickerDialog();
                break;
        }
    }

    /**
     * Method to update view visibility
     */
    private void updateView() {
        ((LoanDetailsAdapter) getListAdapter()).setLoan(loan);

        // update menu item visibility depending on loan status
        if (createCalendarMenuItem != null) {
            createCalendarMenuItem.setVisible((loan != null) && (loan.getReturnDate() != -1) && (!loan.isReturned()));
        }
        if (contactCardMenuItem != null) {
            contactCardMenuItem.setVisible((loan != null) && (loan.hasContactId()));
        }
        if (deleteMenuItem != null) {
            deleteMenuItem.setVisible(loan != null);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
                    showPictureDialog();
                } else {
                    // permission denied
                    Log.i(TAG, "WRITE_EXTERNAL_STORAGE permission denied");
                }
                break;
        }
    }

    /**
     * Launch intent action to create a calendar event according to the return date of the loan
     */
    private void createCalendarEvent() {
        Log.i(TAG, "Calendar event creation requested");
        if (loan.getReturnDate() != -1) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(Events.CONTENT_URI)
                    .putExtra(Events.TITLE, loan.getItem())
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, loan.getReturnDate())
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
            startActivity(intent);
        } else {
            Log.e(TAG, "loan does not have a return date. Should not happen.");
        }
    }


    private void showColorPickerDialog() {
        Log.i(TAG, "Color selection requested");

        // Create and show the dialog.
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newInstance(loan);
        colorPickerDialog.show(getFragmentManager(), "color");
    }

    /**
     * Display dialog to set the loan contact
     */
    private void showContactDialog() {

        Log.i(TAG, "Contact selection requested");

        // Create and show the dialog.
        LoanContactDialog contactDialog = LoanContactDialog.newInstance(loan);
        contactDialog.show(getFragmentManager(), "contact");
    }

    /**
     * Purpose to user to change the item picture
     */
    private void showPictureDialog() {

        Log.i(TAG, "Picture selection requested");

        // Create and show the dialog
        PictureDialog newPictureDialog = PictureDialog.selectPicture(loan);
        newPictureDialog.show(getFragmentManager(), "loan_date");
    }

    /**
     * Display dialog to change the loan date
     */
    private void showDateDialog() {

        Log.i(TAG, "Loan date requested");

        // Create and show the dialog
        LoanDateDialog newLoanDateDialog = LoanDateDialog.newInstance(loan);
        newLoanDateDialog.show(getFragmentManager(), "loan_date");
    }

    /**
     * Display dialog to change the return date
     */
    private void showDateReturnDialog() {

        Log.i(TAG, "Return date requested");

        // Create and show the dialog
        ReturnDateDialog newReturnDateDialog = ReturnDateDialog.newInstance(loan);
        newReturnDateDialog.show(getFragmentManager(), "return_date");
    }

    /**
     * Display dialog to delete this loan
     */
    private void showDeleteLoanDialog() {

        Log.i(TAG, "Loan delete requested");

        // Create and show the dialog.
        DeleteLoanDialog newDialog = DeleteLoanDialog.newInstance(loan);
        newDialog.show(getFragmentManager(), "delete");
    }

    /**
     * Display dialog to enter a new item
     */
    private void showItemDialog() {

        Log.i(TAG, "Loan item requested");

        // Create and show the dialog.
        LoanItemDialog newItemDialog = LoanItemDialog.newInstance(loan);
        newItemDialog.show(getFragmentManager(), "item");
    }


    @Override
    public void onLoanListsChanged() {
        if (loan != null) {
            loan = LoanLists.getInstance().findLoanById(loan.getId());
            if (loan == null) {
                Log.e(TAG, "Cannot find loan when lists updated.");
                Activity activity = getActivity();
                if (activity instanceof LoanDetailsActivity) {
                    getActivity().finish();
                }
            }
        }
        updateView();
    }
}
