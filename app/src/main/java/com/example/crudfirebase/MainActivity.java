package com.example.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;

import com.example.crudfirebase.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //incorporado el webservice .json en la carpeta app
    //agregado dependencias en gradle
    //incorporado el permiso de internet
    EditText nomP, appP, correoP, passwordP;
    ListView listV_personas;
    FirebaseDatabase firebaseDatabase; //incorporado en el gradle app
    DatabaseReference databaseReference;
    private List<Persona> listPerson = new ArrayList<>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    Persona personaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomP = findViewById(R.id.txt_nombre);
        appP = findViewById(R.id.txt_apellido);
        correoP = findViewById(R.id.txt_mail);
        passwordP = findViewById(R.id.txt_pass);
        listV_personas = findViewById(R.id.iv_datosPersonas);
        inicializarFirebase();
        listarDatos();

        //recuperar datos
        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(position);
                nomP.setText(personaSelected.getNombre());
                appP.setText(personaSelected.getApellido());
                correoP.setText(personaSelected.getCorreo());
                passwordP.setText(personaSelected.getPassword());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear(); //limpiar caché
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Persona p =objSnapshot.getValue(Persona.class);
                    listPerson.add(p);

                    arrayAdapterPersona = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_list_item_1 ,listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //persistencia de datos- funciona solo en el caso de una activity
        databaseReference = firebaseDatabase.getReference();
    }


    //habilitar los menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();

        if (nombre.equals("") || apellido.equals("") || correo.equals("") || password.equals("")) {
            validacion(); //hacer a el metodo mas eficiente
        }
        else   {
            switch (item.getItemId()) {
                case R.id.icon_add: {
                    if (nombre.equals("") || apellido.equals("") || correo.equals("") || password.equals("")) {
                        validacion();
                    }
                    else {
                        Persona p = new Persona();
                        p.setUid(UUID.randomUUID().toString()); //generar id automatico
                        p.setNombre(nombre);
                        p.setApellido(apellido);
                        p.setCorreo(correo);
                        p.setPassword(password);
                        //setvalue: que cambie el objeto
                        //child: hijo id
                        databaseReference.child("Persona").child(p.getUid()).setValue(p); //hijo
                        Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    }
                }break;
                case R.id.icon_save:
                    if (nombre.equals("") || apellido.equals("") || correo.equals("") ||password.equals("")) {
                        validacion();
                    }
                    else {
                        Persona p = new Persona();
                        p.setUid(personaSelected.getUid());
                        p.setNombre(nomP.getText().toString().trim()); //ignora los espacios en blanco
                        p.setApellido(appP.getText().toString().trim());
                        p.setCorreo(correoP.getText().toString().trim());
                        p.setPassword(passwordP.getText().toString().trim());
                        databaseReference.child("Persona").child(p.getUid()).setValue(p);

                        Toast.makeText(this, "Guardar", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    }
                    break;
                case R.id.icon_delete:
                        Persona p = new Persona();
                        p.setUid(personaSelected.getUid());
                        databaseReference.child("Persona").child(p.getUid()).removeValue();
                        Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    break;
                default:break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarCajas() {
        nomP.setText("");
        appP.setText("");
        correoP.setText("");
        passwordP.setText("");
    }

    public void validacion() {
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();

        if (nombre.equals("")){
            nomP.setError("Requerido");
        } else if (apellido.equals("")) {
            appP.setError("Requerido");
        } else if (correo.equals("")) {
            correoP.setError("Requerido");
        } else if(password.equals("")) {
            passwordP.setError("Requerido");
        }
    }
}
