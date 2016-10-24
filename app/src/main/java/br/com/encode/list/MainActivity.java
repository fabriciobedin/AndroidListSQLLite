package br.com.encode.list;

import android.app.Activity;
import android.app.IntentService;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;
    private ArrayAdapter<String> itensAdapter;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;
    private AlertDialog.Builder dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            textoTarefa = (EditText) findViewById(R.id.texto_main_tarefa);
            botaoAdicionar = (Button) findViewById(R.id.bt_main_adicionar);
            listaTarefas = (ListView) findViewById(R.id.lista_main_tarefas);

            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR )");

            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });

            listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    confirmacaoRemoverTarefa(ids.get(position));

                }
            });

            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    private  void salvarTarefa(String texto){
        try {
            if(texto.equals("")){
                Toast.makeText(MainActivity.this, "Digite uma tarefa!", Toast.LENGTH_SHORT).show();
            }else {
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES('" + texto + "')");
                Toast.makeText(MainActivity.this, "Tarefa adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                textoTarefa.setText("");
            }
        }catch (Exception e){
            e.printStackTrace();

        }

    }
    private void recuperarTarefas(){
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            itensAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itens){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);
                    ((TextView) view).setTextColor(Color.parseColor("#949494"));
                    return view;
                }
            };

            listaTarefas.setAdapter(itensAdapter);

            cursor.moveToFirst();
            while(cursor != null){
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removerTarefa (Integer id){
        try {
            bancoDados.execSQL("DELETE FROM tarefas WHERE id="+id);
            Toast.makeText(MainActivity.this, "Tarefa apagada!", Toast.LENGTH_SHORT).show();
            recuperarTarefas();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void confirmacaoRemoverTarefa(final Integer id){
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Atenção!");
        dialog.setMessage("Você tem certeza que deseja apagar essa tarefa?");
        dialog.setCancelable(false);
        dialog.setIcon(android.R.drawable.ic_delete);
        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Nada alterado!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removerTarefa(id);
            }
        });
        dialog.create();
        dialog.show();
    }
}
