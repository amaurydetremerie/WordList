package com.example.wordlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WordViewModel mWordViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ajout du RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Récupération du ViewModel
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        //Ajout d'un observer sur le LiveData de la query AllWord afin qu'il se mette automatiquement à jour
        mWordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable final List<Word> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
            }
        });

        //Récupération de notre bouton pour l'ajout d'un mot
        FloatingActionButton add = findViewById(R.id.add);
        //Ajout d'un ClickListener
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Création d'un intent
                Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
                //Appel de l'intent avec attente d'un résultat
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        //Récupération de notre bouton pour filtrer
        FloatingActionButton filter = findViewById(R.id.filter);
        //Ajout d'un ClickListener
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ouverture d'un AlertDialog suite à un appui sur le bouton filtre
                AlertDialog.Builder builder = new AlertDialog.Builder(recyclerView.getContext());
                builder.setTitle("Enter your string to filter");
                //Initialisation de variable nécessaire
                LayoutInflater l = LayoutInflater.from(recyclerView.getContext());
                View v = l.inflate(R.layout.sort, null);
                //Ajout de la vue au dialog
                builder.setView(v);

                //ajout du bouton SORT
                builder.setPositiveButton("SORT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Récupération du filtre
                        String filter = ((EditText) v.findViewById(R.id.editTextSort)).getText().toString();
                        //Gestion du filtre
                        mWordViewModel.getFiltered(filter).observe(MainActivity.this, new Observer<List<Word>>() {
                            @Override
                            public void onChanged(@Nullable final List<Word> words) {
                                // Update the cached copy of the words in the adapter.
                                adapter.setWords(words);
                            }
                        });
                        dialog.dismiss();
                    }
                });

                //ajout du bouton CANCEL
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Permet de revenir à la liste complete quand on ajoute un nouveau mot
                        mWordViewModel.getAllWords();
                        dialog.dismiss();
                    }
                });
                //Création et affichage du dialog
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //Ajout d'un listener long click sur un mot
        adapter.setOnItemClickListener(new WordListAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int positionRecyclerViewAndArray) {
                //Ouverture d'un AlertDialog suite à un LongClick
                AlertDialog.Builder builder = new AlertDialog.Builder(recyclerView.getContext());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                //ajout du bouton oui
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Récupération du Word
                        Word word = new Word(mWordViewModel.getAllWords().getValue().get(positionRecyclerViewAndArray).getWord());
                        //Suppression du mot
                        mWordViewModel.deleteWord(word);
                        dialog.dismiss();
                    }
                });

                //ajout du bouton non
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Ne fait rien
                        dialog.dismiss();
                    }
                });
                //Création et affichage du dialog
                AlertDialog alert = builder.create();
                alert.show();
                //Nécessaire pour l'interface
                return true;
            }
        });

        //Récupération de notre bouton pour rechercher
        FloatingActionButton search = findViewById(R.id.search);
        //Ajout d'un Click Listener
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ouverture d'un AlertDialog suite à un appui sur le bouton search
                AlertDialog.Builder builder = new AlertDialog.Builder(recyclerView.getContext());
                builder.setTitle("Enter your string to search");
                //Initialisation de variable nécessaire
                LayoutInflater l = LayoutInflater.from(recyclerView.getContext());
                View v = l.inflate(R.layout.search, null);
                //Ajout de la vue au dialog
                builder.setView(v);

                //ajout du bouton SORT
                builder.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Création d'un intent
                        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                        //Récupération du mot à chercher
                        String search = ((EditText) v.findViewById(R.id.editTextSearch)).getText().toString();
                        //Ajout du mot à l'Intent
                        intent.putExtra("search", search);
                        //Lancement de l'Intent
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                //ajout du bouton CANCEL
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Ne fait rien
                        dialog.dismiss();
                    }
                });
                //Création et affichage du dialog
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Récupération de donnée de l'Activity NewWord
        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //Récupération du Word
            Word word = new Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY));
            //Ajout du Word
            mWordViewModel.insert(word);
        } else {
            //Si le mot est vide
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}
