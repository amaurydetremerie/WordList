package com.example.wordlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private WordViewModel mWordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //Récupération du mot à rechercher dans l'API
        Intent intent = getIntent();
        String search = intent.getStringExtra("search");

        //Mise en place d'une RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerviewsearch);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Récupération du model
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        //Ajout d'un observer sur le MutableLiveData afin qu'il se mette automatiquement à jour
        mWordViewModel.searchSimilar(search).observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable final List<Word> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
            }
        });

        //Désactivation du LongClick
        adapter.setOnItemClickListener(new WordListAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int positionRecyclerViewAndArray) {
                return false;
            }
        });
    }
}
