package clientrest.com.clientrest.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import clientrest.com.clientrest.DataBase.DAO.DBHelper;
import clientrest.com.clientrest.DataBase.Entity.InferredDecisionAttributes;
import clientrest.com.clientrest.DataBase.Entity.Request;
import clientrest.com.clientrest.R;

public class Request_Items_Adapter extends RecyclerView.Adapter<Request_Items_Adapter.DataObjectHolder> {
    private Context context;
    private Request request;
    boolean flag_ContemInformacao;
    private EditText edtInformacaoResp;
    private boolean isHistory, isInferredMechanism = false;
    private List<InferredDecisionAttributes> mDataset;
    private static MyClickListener myClickListener;
    private TextView tvAtributoResp, tvRespostaResp, tvNivelResp, tvInserirResp;
    private TextView tvLocation, tvRetention, tvShared, tvInferred;
    private LinearLayout linearResp, linearNivel;
    private View viewRes, viewNivel;
    private static final String ALLOW = "Permitir";
    private static final String DENY = "Negar";
    private static final String NEGOTIATE = "Negociar";

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvAtributo;
        TextView tvResposta;
        TextView tvNivel;
        LinearLayout lnBackColor;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tvAtributo = itemView.findViewById(R.id.tvAtributo);
            tvResposta = itemView.findViewById(R.id.tvResposta);
            tvNivel = itemView.findViewById(R.id.tvNivel);
            lnBackColor = itemView.findViewById(R.id.lnBackColor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public Request_Items_Adapter(Request request, Context context) {
        this.context = context;
        this.request = request;
        this.isHistory = false;
        this.isInferredMechanism =false;
        mDataset = request.getInferredDecisionId().getInferredDecisionAttributesList();
    }

    public Request_Items_Adapter(Request request, Context context, boolean isInferredMechanism) {
        this.context = context;
        this.request = request;
        this.isHistory = true;
        this.isInferredMechanism = isInferredMechanism;
        mDataset = request.getInferredDecisionId().getInferredDecisionAttributesList();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_attributes_fragment, parent, false);
        context = view.getContext();
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        flag_ContemInformacao = true;
        RepaintItemView(holder, position);
        holder.tvAtributo.setText("Atributo: " + mDataset.get(position).getDataAttributes().getAttribute());
        flag_ContemInformacao = (mDataset.get(position).getTrustLevel() <= 0) ? false : true;

        if (flag_ContemInformacao) {
            holder.tvResposta.setText("Decisão Mecanismo: " + IntToStringDecision(mDataset.get(position).getState()));
            holder.tvNivel.setText("Nivel Confiança: " + mDataset.get(position).getTrustLevel().toString() + " %");
        } else {
            holder.tvResposta.setText("A informação solicitada não contêm na sua base de dados.");
            holder.tvNivel.setText("Nível Confiança: Inexistente");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(view.getContext());
                final View DialogView = factory.inflate(R.layout.data_attribute_response_dialog, null);
                final AlertDialog Dialog = new AlertDialog.Builder(view.getContext()).create();
                Dialog.setView(DialogView);

                addDialogInformation(DialogView, position);

                DialogView.findViewById(R.id.btnAutorizar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateInformation(flag_ContemInformacao, mDataset.get(position), R.integer.ACCEPT, edtInformacaoResp.getText().toString(), holder, Dialog);

                    }
                });
                DialogView.findViewById(R.id.btnNegociar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateInformation(flag_ContemInformacao, mDataset.get(position), R.integer.NEGOTIATE, edtInformacaoResp.getText().toString(), holder, Dialog);

                    }
                });
                DialogView.findViewById(R.id.btnNegar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateInformation(flag_ContemInformacao, mDataset.get(position), R.integer.DENY, edtInformacaoResp.getText().toString(), holder, Dialog);

                    }
                });
                Dialog.show();
            }
        });

    }

    private void updateInformation(boolean flag_ContemInformacao, InferredDecisionAttributes inferredDecisionAttributes, int resp, String information, DataObjectHolder holder, AlertDialog dialog) {
        DBHelper database = new DBHelper(context);
        if (flag_ContemInformacao) {
            database.saveUserDecision(request, inferredDecisionAttributes.getDataAttributes(), context.getResources().getInteger(resp), "");
            if (isHistory && isInferredMechanism ) {
                inferredDecisionAttributes.setState(context.getResources().getInteger(resp));
                database.saveOrUpdateTraining(request, inferredDecisionAttributes);
            }
            RefreshColor(holder, context.getResources().getInteger(resp));
            dialog.dismiss();
        } else {
            if (!information.trim().equals("") || context.getResources().getInteger(resp) ==2) {
                database.saveUserDecision(request, inferredDecisionAttributes.getDataAttributes(), context.getResources().getInteger(resp), information);
                if (isHistory && isInferredMechanism ) {
                    inferredDecisionAttributes.setState(context.getResources().getInteger(resp));
                    database.saveOrUpdateTraining(request, inferredDecisionAttributes);
                }

                RefreshColor(holder, context.getResources().getInteger(resp));
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Informação não pode ser em branco!!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateRequest() {
        DBHelper database = new DBHelper(context);
        request = database.getRequest(request.getRequestId());
    }


    private void RepaintItemView(DataObjectHolder holder, int posicao) {
        try {
            int codigo = 0;
            if (isHistory && isInferredMechanism) {
                codigo = request.getInferredDecisionId().getInferredDecisionAttributesList().get(posicao).getState();
            } else {
                codigo = request.getUserDecisionId().getUserDecisionAttributesList().get(posicao).getState();
            }
            RefreshColor(holder, codigo);
        } catch (IndexOutOfBoundsException c) {
        }
    }

    private void RefreshColor(DataObjectHolder holder, int idResult) {
        if (idResult == 1) {
            holder.lnBackColor.setBackgroundColor(Color.GREEN);
        } else if (idResult == 2) {
            holder.lnBackColor.setBackgroundColor(Color.RED);
        } else if (idResult == 3) {
            holder.lnBackColor.setBackgroundColor(Color.YELLOW);
        }
        holder.itemView.refreshDrawableState();
    }

    private void addDialogInformation(View DialogView, int position) {
        updateRequest();
        tvAtributoResp = DialogView.findViewById(R.id.tvAtributoResp);
        tvRespostaResp = DialogView.findViewById(R.id.tvRespostaResp);
        tvNivelResp = DialogView.findViewById(R.id.tvNivelResp);
        tvInserirResp = DialogView.findViewById(R.id.tvInserirResp);
        edtInformacaoResp = DialogView.findViewById(R.id.edtInformacaoResp);
        tvLocation = DialogView.findViewById(R.id.tvLocation);
        tvRetention = DialogView.findViewById(R.id.tvRetention);
        tvShared = DialogView.findViewById(R.id.tvShared);
        tvInferred = DialogView.findViewById(R.id.tvInferred);
        linearNivel = DialogView.findViewById(R.id.linearNivel);
        linearResp = DialogView.findViewById(R.id.linearResp);
        viewRes = DialogView.findViewById(R.id.viewResp);
        viewNivel = DialogView.findViewById(R.id.viewNivel);

        flag_ContemInformacao = (mDataset.get(position).getTrustLevel() <= 0) ? false : true;
        tvAtributoResp.setText(mDataset.get(position).getDataAttributes().getAttribute());
        String aux = (mDataset.get(position).getDataAttributes().getShared().equals(1)) ? "Sim" : "Não";
        tvShared.setText(aux);
        tvLocation.setText(getStringLocation(request.getLocation()));
        aux = (mDataset.get(position).getDataAttributes().getInferred().equals(1)) ? "Sim" : "Não";
        tvInferred.setText(aux);
        tvRetention.setText(getStringRetention(mDataset.get(position).getDataAttributes().getRetention()));

        if (flag_ContemInformacao) {
            linearResp.setVisibility(View.VISIBLE);
            linearNivel.setVisibility(View.VISIBLE);
            viewRes.setVisibility(View.VISIBLE);
            viewNivel.setVisibility(View.VISIBLE);

            edtInformacaoResp.setVisibility(View.GONE);
            tvInserirResp.setVisibility(View.GONE);

            tvRespostaResp.setText(IntToStringDecision(mDataset.get(position).getState()));
            tvNivelResp.setText(mDataset.get(position).getTrustLevel().toString() + " %");

        } else {
            linearResp.setVisibility(View.GONE);
            linearNivel.setVisibility(View.GONE);
            viewRes.setVisibility(View.GONE);
            viewNivel.setVisibility(View.GONE);

            tvInserirResp.setText("A informação solicitada não contêm na sua base de dados. \nInsira esta informação:");
            edtInformacaoResp.setVisibility(View.VISIBLE);
            tvInserirResp.setVisibility(View.VISIBLE);
            if (request.getUserDecisionId().getUserDecisionId() != 0) {
                for (int i = 0; i < request.getUserDecisionId().getUserDecisionAttributesList().size(); i++) {
                    if (request.getUserDecisionId().getUserDecisionAttributesList().get(i).getDataAtttributeId().getDataAttributesId() == mDataset.get(position).getDataAttributes().getDataAttributesId()) {

                        if (!request.getUserDecisionId().getUserDecisionAttributesList().get(i).getInformation().isEmpty()) {
                            edtInformacaoResp.setText(request.getUserDecisionId().getUserDecisionAttributesList().get(i).getInformation());
                        }
                    }
                }
            }
        }
    }

    private String getStringRetention(String retention){
        if(retention.equals("forever")){
            return "Para sempre";
        }else if(retention.equals("satisfied_purpose")){
            return "Ate satisfazer o seu propósito";
        }else{
            return "";
        }
    }

    private String getStringLocation(String location) {
        if (location.equals("semi_public")) {
            return "Semi público";
        } else if (location.equals("public")) {
            return "Público";
        } else if (location.equals("your_place")) {
            return "Seu local";
        } else if (location.equals("someone_else_place")) {
            return "Local de outra pessoa";
        } else if (location.equals("private")) {
            return "Privacdo";
        }else{
            return "";
        }
    }

    private String IntToStringDecision(int code) {
        if (code == context.getResources().getInteger(R.integer.ACCEPT)) {
            return ALLOW;
        } else {
            if (code == context.getResources().getInteger(R.integer.DENY)) {
                return DENY;
            } else {
                if (code == context.getResources().getInteger(R.integer.NEGOTIATE)) {
                    return NEGOTIATE;
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}