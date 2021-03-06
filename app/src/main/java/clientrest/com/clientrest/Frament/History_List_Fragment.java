package clientrest.com.clientrest.Frament;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import clientrest.com.clientrest.Adapter.History_Items_Adapter;
import clientrest.com.clientrest.DataBase.DAO.DBHelper;
import clientrest.com.clientrest.DataBase.Entity.HistoryObject;
import clientrest.com.clientrest.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class History_List_Fragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_CODE = "CODE";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private int code =0;
    private OnListFragmentInteractionListener mListener;
    private TextView emptyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public History_List_Fragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static History_List_Fragment newInstance(int columnCount) {
        History_List_Fragment fragment = new History_List_Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            code =  getArguments().getInt(ARG_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<HistoryObject> dataset = getDataSet();
        View view = null;
        if (dataset.size() > 0) {
            view = inflater.inflate(R.layout.fragment_history_list, container, false);

            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                recyclerView.setAdapter(new History_Items_Adapter(getDataSet(), mListener, getContext(), code));
            }
        }else {
            view = inflater.inflate(R.layout.empty_layout, container, false);
            emptyView = view.findViewById(R.id.empty_view);
            if(code ==0) {
                emptyView.setText("Nenhum historico de inferência pelo mecanismo!");
            }else{
                emptyView.setText("Nenhum historico de inferência do usuário!");
            }
        }
        return view;
    }

    private List<HistoryObject> getDataSet() {
        List<HistoryObject> results;
        DBHelper dataBase = new DBHelper(getContext());
        if (code==0) {
            results = dataBase.getHistoryMechanism();
        }else{
            results = dataBase.getHistoryUser();
        }
        return results;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(HistoryObject item);
    }
}
