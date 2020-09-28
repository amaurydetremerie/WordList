package com.example.wordlist;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class WordRepository {

    private WordDao mWordDao;
    private LiveData<List<Word>> mAllWords;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getAlphabetizedWords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    //Appel de la query avec filtre et ajout du % afin de séléctionner que le début d'un mot (REGEX SQL)
    LiveData<List<Word>> getFiltered (String filter) {
        return mWordDao.getFiltered(filter + "%");
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }

    //Appel de la query Delete !!! PAS DANS LE THREAD PRINCIPAL !!!
    void deleteWord (Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.deleteWord(word);
        });
    }

    //Appel de l'API
    MutableLiveData<List<Word>> searchSimilar(String search){
        //Création du Retrofit
        //BaseURL = site sur lequel on va appeler l'API
        //addConverterFactory = interpréteur utilisé pour lire l'API (ici pour du JSON)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.datamuse.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Liaison entre Retrofit et notre interface qui contient la query de l'API
        DataMuseService data = retrofit.create(DataMuseService.class);

        //Appel à l'API
        Call<List<WordFromAPI>> call = data.listSimilarWords(search);

        //Création de la liste qui va être renvoyée
        MutableLiveData<List<Word>> list = new MutableLiveData<>();

        //GESTION DE MANIERE ASYNCHRONE
        call.enqueue(new Callback<List<WordFromAPI>>() {
            //Méthode qui va se lancer automatiquement à la réponse de l'API
            @Override
            public void onResponse(Call<List<WordFromAPI>> call, Response<List<WordFromAPI>> response)
            {
                //Gestion de la réponse
                if (response.isSuccessful()) {
                    //Récupération du corps de l'API
                    List<WordFromAPI> words = response.body();
                    //Création d'une liste qui sera mise dans le Mutable
                    List<Word> listAPI = new LinkedList<Word>();
                    //Traitement du corps de l'API
                    for (Word word : words){
                        //Création d'un Word sur base d'un WordFromAPI
                        listAPI.add(new Word(word.getWord()));
                    }
                    //Ajout de la liste dans la Mutable
                    list.setValue(listAPI);
                }
                else {
                    //Gestion de l'erreur
                    Log.e("WORD", "onResponse: " + response.errorBody());
                }
            }

            //En cas d'erreur de l'API
            @Override
            public void onFailure(Call<List<WordFromAPI>> call, Throwable t) {
                Log.e("WORD", "onFailure: " + t.getLocalizedMessage());
            }
        });

        //Renvoi de la Mutable afin de pouvoir créer notre observer
        return list;
    }
}