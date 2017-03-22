package in.meegotech.invisiblewidgetspro.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.meegotech.invisiblewidgetspro.cache.AppsContract;
import in.meegotech.invisiblewidgetspro.extras.AppsAdapter;
import in.meegotech.invisiblewidgetspro.extras.RecyclerViewClickListener;
import in.meegotech.invisiblewidgetspro.extras.RecyclerViewEmptyViewSupport;
import in.meegotech.invisiblewidgetspro.utils.AppConstants;

/**
 * Created by shivam on 11/02/17.
 * <p>
 * Some of the code is taken from
 * http://stacktips.com/tutorials/android/how-to-get-list-of-installed-apps-in-android
 */

public class AppSelectorDialogFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(in.meegotech.invisiblewidgetspro.R.id.recyclerview_installed_apps)
    RecyclerViewEmptyViewSupport recyclerView;

    @BindView(in.meegotech.invisiblewidgetspro.R.id.empty_view)
    LinearLayout emptyView;

    private static final int CURSOR_LOADER_ID = 0;

    private AppsAdapter adapter;
    private Context mContext;
    private ArrayList<ApplicationInfo> applist;
    private Cursor mCursor;
    private AppsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(in.meegotech.invisiblewidgetspro.R.layout.fragment_dialog, container);

        ButterKnife.bind(this, view);

        mContext = getActivity();

        mAdapter = new AppsAdapter(mContext, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(emptyView);

        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getActivity(), new
                RecyclerViewClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if (position == 0)
                            ((AppSelectedListener) getActivity()).getSelectedAppPackage(AppConstants.PLACEHOLDER_WIDGET);
                        else {
                            mCursor.moveToPosition(position);
                            ((AppSelectedListener) getActivity()).getSelectedAppPackage
                                    (AppsContract.getColumnString(mCursor, AppsContract
                                            .AppColumns.PACKAGE_NAME));
                        }
                            dismiss();
                    }
                }));
        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        return view;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                mContext,
                AppsContract.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public interface AppSelectedListener {
        public void getSelectedAppPackage(String packageName);
    }
}
