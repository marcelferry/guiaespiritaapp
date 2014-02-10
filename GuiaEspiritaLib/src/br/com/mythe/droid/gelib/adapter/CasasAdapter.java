package br.com.mythe.droid.gelib.adapter;

import java.util.List;

import org.jredfoot.sophielib.util.MapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.vo.CasaVO;

public class CasasAdapter extends ArrayAdapter<CasaVO> {
 
    int resource;
    String response;
    Context context;
    //Initialize adapter
    public CasasAdapter(Context context, int resource, List<CasaVO> items) {
        super(context, resource, items);
        this.resource=resource;
 
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout listView;
        //Get the current alert object
        CasaVO al = getItem(position);
 
        //Inflate the view
        if(convertView==null)
        {
            listView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, listView, true);
        }
        else
        {
            listView = (LinearLayout) convertView;
        }
        //Get the text boxes from the listitem.xml file
        TextView nome =(TextView)listView.findViewById(R.id.nome);
        TextView endereco =(TextView)listView.findViewById(R.id.endereco);
        TextView bairro =(TextView)listView.findViewById(R.id.bairro);
        TextView distance =(TextView)listView.findViewById(R.id.distancia);
 
        //Assign the appropriate data from our alert object above
        nome.setText(al.entidade);
        endereco.setText(al.endereco);
        bairro.setText(al.bairro);

        double distancia = new Double(al.distance);
        
        String novoTexto = MapUtils.getDistanceFormatada(distancia * 1000);
        distance.setText(novoTexto);
 
        return listView;
    }
 
}