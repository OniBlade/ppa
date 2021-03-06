package clientrest.com.clientrest.Frament;

import android.content.Context;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import clientrest.com.clientrest.Activity.MainActivity;
import clientrest.com.clientrest.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Configuration_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Configuration_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Configuration_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText edtHost, edtPort;
    Button bntSalvarConf;
    Context context;

    private OnFragmentInteractionListener mListener;

    public Configuration_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Configuration_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Configuration_Fragment newInstance(String param1, String param2) {
        Configuration_Fragment fragment = new Configuration_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.configuration_fragment, container, false);
        context = view.getContext();

        edtHost = (EditText) view.findViewById(R.id.edtHostConf);
        edtPort = (EditText) view.findViewById(R.id.edtPortConf);
        bntSalvarConf = (Button) view.findViewById(R.id.bntSalvarConf);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("Conexao", Context.MODE_PRIVATE);
        edtHost.setText(sharedPreferences.getString("Host", ""));
        edtPort.setText(sharedPreferences.getString("Port", ""));

        bntSalvarConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Host", edtHost.getText().toString());
                editor.putString("Port", edtPort.getText().toString());

            }
        });

        return view;
    }

    private void fragmentJump() {
        Request_List_Fragment mFragment = new Request_List_Fragment();
        Bundle mBundle = new Bundle();
        mFragment.setArguments(mBundle);
        switchContent(R.id.content_main, mFragment);
    }

    public void switchContent(int id, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.switchContent(id, fragment, "Request_List_Fragment");
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
