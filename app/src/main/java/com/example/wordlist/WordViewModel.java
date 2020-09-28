package com.example.wordlist;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

//Classe permettant une liaison entre le repository et le Main.

public class WordViewModel extends AndroidViewModel {

    private WordRepository mRepository;

    public WordViewModel (Application application) {
        super(application);
        mRepository = new WordRepository(application);
    }

    LiveData<List<Word>> getFiltered (String filter) { return mRepository.getFiltered(filter); }

    LiveData<List<Word>> getAllWords() { return mRepository.getAllWords(); }

    public void insert(Word word) { mRepository.insert(word); }

    public void deleteWord (Word word) { mRepository.deleteWord(word); }

    public MutableLiveData<List<Word>> searchSimilar(String search) { return mRepository.searchSimilar(search); }

}