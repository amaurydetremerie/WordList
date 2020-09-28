package com.example.wordlist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Word word);

    //Query permettant de vider la table
    @Query("DELETE FROM word_table")
    void deleteAll();

    @Query("SELECT * from word_table ORDER BY word ASC")
    LiveData<List<Word>> getAlphabetizedWords();

    //Query permetant de sélectionner une liste de mot via un filtre.
    //La query renvoie une LiveData afin de pouvoir créer un observer
    @Query("SELECT * from word_table WHERE word LIKE :filter ORDER BY word ASC")
    LiveData<List<Word>> getFiltered(String filter);

    //Query permettant de supprimer un mot
    @Delete
    void deleteWord(Word word);
}