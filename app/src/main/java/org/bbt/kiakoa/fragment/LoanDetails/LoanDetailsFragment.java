package org.bbt.kiakoa.fragment.LoanDetails;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.bbt.kiakoa.LoanDetailsActivity;
import org.bbt.kiakoa.R;
import org.bbt.kiakoa.dialog.DeleteLoanDialog;
import org.bbt.kiakoa.dialog.LoanContactDialog;
import org.bbt.kiakoa.dialog.LoanDateDialog;
import org.bbt.kiakoa.dialog.LoanItemDialog;
import org.bbt.kiakoa.dialog.LoanStatusDialog;
import org.bbt.kiakoa.dialog.ReturnDateDialog;
import org.bbt.kiakoa.model.Loan;
import org.bbt.kiakoa.model.LoanLists;
import org.bbt.kiakoa.tools.ItemClickRecyclerAdapter;
import org.bbt.kiakoa.tools.SimpleDividerItemDecoration;

/**
 * Activity displaying {@link LoanDetailsFragment}
 * Activity used only on normal layout (not large)
 *
 * @author Benoit Bousquet
 */
public class LoanDetailsFragment extends Fragment implements ItemClickRecyclerAdapter.OnItemClickListener, LoanLists.OnLoanListsChangedListener {

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
     * Instance of used {@link LoanDetailsRecyclerAdapter}
     */
    private LoanDetailsRecyclerAdapter loanDetailsAdapter;

    /**
     * Intent to take a picture from camera
     */
    private final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    /**
     * {@link RecyclerView} displaying loan details
     */
    private RecyclerView recyclerView;

    /**
     * Displayed when no loan is is the list
     */
    private TextView emptyTextView;

    /**
     * {@link MenuItem} to return the displayed loan
     */
    private MenuItem returnMenuItem;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_details_list, container, false);

        // customize list
        recyclerView = view.findViewById(R.id.recycler);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        loanDetailsAdapter = new LoanDetailsRecyclerAdapter();
        loanDetailsAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(loanDetailsAdapter);

        emptyTextView = view.findViewById(R.id.empty_element);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.loan_details, menu);
        // retrieve instance of return menu item
        returnMenuItem = menu.findItem(R.id.action_return);
        // retrieve instance of create calendar event menu item
        createCalendarMenuItem = menu.findItem(R.id.action_calendar_event);
        // retrieve instance of contact card menu item
        contactCardMenuItem = menu.findItem(R.id.action_contact_card);
        // retrieve instance of delete menu item
        deleteMenuItem = menu.findItem(R.id.action_delete);
        // update menu item visibility
        updateView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_return:
                LoanLists.getInstance().saveReturned(loan, getContext());
                if (getActivity() instanceof LoanDetailsActivity) {
                    getActivity().finish();
                } else {
                    updateView();
                }
                Toast.makeText(getContext(), R.string.loan_returned, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_calendar_event:
                createCalendarEvent();
                return true;
            case R.id.action_contact_card:
                loan.displayContactCard(getContext());
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

    /**
     * Method to update view visibility
     */
    private void updateView() {
        loanDetailsAdapter.setLoan(loan);

        // set display if there is a loan to display
        if (loanDetailsAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }

        // update menu item visibility depending on loan status
        if (returnMenuItem != null) {
            returnMenuItem.setVisible((loan != null) && (!loan.isReturned()));
        }
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
                    if (data.getData() != null) {
                        loan.setItemPicture(data.getData().toString());
                        updateLoan();
                    }
                } else {
                    Log.i(TAG, "No picture returned from gallery");
                }
                break;
            case REQUEST_CODE_GET_PICTURE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Picture returned from camera");
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        if (bitmap != null) {
                            String uri = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, loan.getItem(), "");
                            if (uri != null) {
                                Log.i(TAG, "Image added to media store");
                                loan.setItemPicture(uri);
                                updateLoan();
                            } else {
                                Log.e(TAG, "Image not added to media store :-(");
                            }
                        } else {
                            Log.e(TAG, "Image not found :-(");
                        }
                    } else {
                        Log.e(TAG, "No extra found :-(");
                    }
                } else {
                    Log.w(TAG, "No picture returned from camera");
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
        LoanContactDialog newContactDialog = LoanContactDialog.newInstance(loan);
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
        LoanDateDialog newLoanDateDialog = LoanDateDialog.newInstance(loan);
        newLoanDateDialog.show(ft, "loan_date");
    }

    /**
     * Display dialog to change the return date
     */
    private void showDateReturnDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment returnDateDialog = getFragmentManager().findFragmentByTag("return_date");
        if (returnDateDialog != null) {
            ft.remove(returnDateDialog);
        }

        // Create and show the dialog
        ReturnDateDialog newReturnDateDialog = ReturnDateDialog.newInstance(loan);
        newReturnDateDialog.show(ft, "return_date");
    }

    /**
     * Display dialog to delete this loan
     */
    private void showDeleteLoanDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment dialog = getFragmentManager().findFragmentByTag("delete");
        if (dialog != null) {
            ft.remove(dialog);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DeleteLoanDialog newDialog = DeleteLoanDialog.newInstance(loan);
        newDialog.show(ft, "delete");
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
        LoanItemDialog newItemDialog = LoanItemDialog.newInstance(loan);
        newItemDialog.show(ft, "item");
    }

    /**
     * Display dialog to enter a new status
     */
    private void showStatusDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment statusDialog = getFragmentManager().findFragmentByTag("status");
        if (statusDialog != null) {
            ft.remove(statusDialog);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        LoanStatusDialog newItemDialog = LoanStatusDialog.newInstance(loan);
        newItemDialog.show(ft, "status");
    }

    /**
     * Update loan and notify for changes
     */
    private void updateLoan() {
        LoanLists.getInstance().updateLoan(loan, getContext());
        updateView();
        LoanLists.getInstance().notifyLoanListsChanged();
    }

    @Override
    public void onItemClick(int position) {

        switch (position) {
            case 0:
                Log.i(TAG, "Item modification requested");
                showItemDialog();

                break;
            case 1:
                Log.i(TAG, "Item status requested");
                showStatusDialog();

                break;
            case 2:
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

                if (!camera) {

                    Log.i(TAG, "Requesting picture : use gallery");
                    Intent intent = new Intent();
                    intent.setType("circleView/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_picture_item, loan.getItem())), REQUEST_CODE_GET_PICTURE_GALLERY);

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
        }
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
